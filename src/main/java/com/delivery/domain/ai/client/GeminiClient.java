package com.delivery.domain.ai.client;

import com.delivery.domain.ai.dto.gemini.GeminiErrorResponse;
import com.delivery.domain.ai.dto.gemini.GeminiGenerateContentRequest;
import com.delivery.domain.ai.dto.gemini.GeminiGenerateContentResponse;
import com.delivery.domain.ai.dto.gemini.GeminiResponseException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.json.JsonMapper;

// Gemini generateContent API를 호출하는 역할만 담당함 (infra 계층 성격).
// 글자수 검증, 프롬프트에 문구 삽입, 로그 저장 같은 비즈니스 로직은 여기 없음 -
// 그건 이 클래스를 호출하는 AiService의 책임.
@Component
public class GeminiClient {

    // Gemini가 응답을 안 주면 요청 스레드가 무한 대기하지 않도록 타임아웃을 둠.
    // 실제로 Gemini 응답이 16초 넘게 걸리는 경우가 있어(직접 curl로 확인, server-timing
    // dur=16098) read timeout을 30초로 넉넉하게 잡음 - 너무 타이트하면 정상 응답도
    // 타임아웃으로 오탐하게 됨.
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(30);

    // 배치(새벽 스케줄러) 전용 타임아웃 - 응답을 기다리는 사용자가 없으므로 30초보다
    // 넉넉하게 잡아, 정상 응답인데도 조금 느린 경우(24~29초대)까지 놓치지 않도록 함.
    // Menu의 동기 호출(generateContent)에는 영향 없음 - 별도 RestClient를 씀.
    private static final Duration BATCH_READ_TIMEOUT = Duration.ofSeconds(60);

    // RestClient: Spring Boot 3.2+에 내장된 동기 HTTP 클라이언트.
    // RestTemplate의 최신 대체제 - Builder로 baseUrl 등 공통 설정을 미리 잡아두고 재사용함.
    private final RestClient restClient;
    private final RestClient batchRestClient;
    private final JsonMapper jsonMapper;
    private final String apiKey;
    private final String model;

    public GeminiClient(
            RestClient.Builder restClientBuilder,
            JsonMapper jsonMapper,
            @Value("${gemini.base-url}") String baseUrl,
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String model) {
        this.restClient =
                restClientBuilder
                        .baseUrl(baseUrl)
                        .requestFactory(buildRequestFactory(READ_TIMEOUT))
                        .build();
        // Menu 쪽 restClientBuilder(주입받은 빈)와 독립적인 설정이 필요해 별도 builder로 생성
        this.batchRestClient =
                RestClient.builder()
                        .baseUrl(baseUrl)
                        .requestFactory(buildRequestFactory(BATCH_READ_TIMEOUT))
                        .build();
        this.jsonMapper = jsonMapper;
        this.apiKey = apiKey;
        this.model = model;
    }

    // ClientHttpRequestFactoryBuilder.jdk()는 RestClient가 팩토리 미지정 시 쓰는
    // 기본 구현(JDK HttpClient)과 동일함 - SimpleClientHttpRequestFactory(구식
    // HttpURLConnection 기반)는 Gemini 응답의 Content-Type을 application/octet-stream으로
    // 잘못 처리하는 문제가 있어서 타임아웃만 얹은 이 방식으로 교체함.
    private static ClientHttpRequestFactory buildRequestFactory(Duration readTimeout) {
        return ClientHttpRequestFactoryBuilder.jdk()
                .build(
                        HttpClientSettings.defaults()
                                .withConnectTimeout(CONNECT_TIMEOUT)
                                .withReadTimeout(readTimeout));
    }

    // prompt는 AiService이 글자수 검증 + "50자 이하로" 문구 삽입까지 끝낸 최종 텍스트.
    // 실패(4xx/5xx) 시 응답 본문을 파싱해 Gemini가 준 실제 사유(error.message)만 뽑아
    // GeminiResponseException으로 다시 던짐 - AiService가 잡아서 AI_GENERATION_FAILED로
    // 변환하고 실패 로그를 남기도록 함. 응답 자체가 없는 순수 네트워크 오류(타임아웃 등)는
    // 파싱할 본문이 없으므로 RestClientException 그대로 전파됨(AiService가 동일하게 처리).
    // API 키가 URL 쿼리에 들어가므로 요청/응답을 로깅하지 않음.
    public String generateContent(String prompt) {
        return callGenerateContent(restClient, prompt);
    }

    // 응답을 기다리는 사용자가 없는 배치(새벽 스케줄러) 호출 전용 - 타임아웃만 더 길게 잡은
    // 별도 RestClient를 쓴다. AiService.summarizeStoreReviews에서만 사용.
    public String generateContentForBatch(String prompt) {
        return callGenerateContent(batchRestClient, prompt);
    }

    private String callGenerateContent(RestClient client, String prompt) {
        try {
            GeminiGenerateContentResponse response =
                    client.post()
                            .uri("/v1beta/models/{model}:generateContent?key={key}", model, apiKey)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(GeminiGenerateContentRequest.ofText(prompt))
                            .retrieve()
                            .body(GeminiGenerateContentResponse.class);

            return response.firstText();
        } catch (RestClientResponseException e) {
            throw new GeminiResponseException(extractErrorMessage(e));
        }
    }

    private String extractErrorMessage(RestClientResponseException e) {
        try {
            GeminiErrorResponse errorResponse =
                    jsonMapper.readValue(e.getResponseBodyAsString(), GeminiErrorResponse.class);
            // "404 NOT_FOUND: {message}" 형태 - HTTP 응답의 reason phrase는 HTTP/2에서
            // 아예 없을 수 있어(상태줄 자체가 없는 프로토콜) 대신 Gemini가 JSON으로 직접
            // 주는 code/status를 그대로 씀 - 프로토콜과 무관하게 항상 채워져 있음.
            GeminiErrorResponse.Error error = errorResponse.error();
            return error.code() + " " + error.status() + ": " + error.message();
        } catch (Exception parseFailure) {
            // Gemini가 항상 { "error": {...} } 포맷을 지킨다는 보장은 없으므로,
            // 파싱 자체가 실패하면 원래 예외 메시지로 안전하게 폴백.
            return e.getMessage();
        }
    }
}
