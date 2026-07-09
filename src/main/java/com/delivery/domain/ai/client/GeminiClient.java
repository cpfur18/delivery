package com.delivery.domain.ai.client;

import com.delivery.domain.ai.dto.gemini.GeminiGenerateContentRequest;
import com.delivery.domain.ai.dto.gemini.GeminiGenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

// Gemini generateContent API를 호출하는 역할만 담당함 (infra 계층 성격).
// 글자수 검증, 프롬프트에 문구 삽입, 로그 저장 같은 비즈니스 로직은 여기 없음 -
// 그건 이 클래스를 호출하는 AiService의 책임.
@Component
public class GeminiClient {

    // Gemini가 응답을 안 주면 요청 스레드가 무한 대기하지 않도록 타임아웃을 둠 -
    // 이 호출이 호출자(MenuService.createMenu)의 DB 트랜잭션 안에서 일어나므로,
    // 타임아웃이 없으면 커넥션을 물고 무한정 기다릴 수 있음.
    private static final int CONNECT_TIMEOUT_MILLIS = 5_000;
    private static final int READ_TIMEOUT_MILLIS = 15_000;

    // RestClient: Spring Boot 3.2+에 내장된 동기 HTTP 클라이언트.
    // RestTemplate의 최신 대체제 - Builder로 baseUrl 등 공통 설정을 미리 잡아두고 재사용함.
    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GeminiClient(
            RestClient.Builder restClientBuilder,
            @Value("${gemini.base-url}") String baseUrl,
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String model) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
        requestFactory.setReadTimeout(READ_TIMEOUT_MILLIS);

        this.restClient = restClientBuilder.baseUrl(baseUrl).requestFactory(requestFactory).build();
        this.apiKey = apiKey;
        this.model = model;
    }

    // prompt는 AiService이 글자수 검증 + "50자 이하로" 문구 삽입까지 끝낸 최종 텍스트.
    // 실패(4xx/5xx, 네트워크 오류) 시 RestClientException이 그대로 던져짐 -
    // 여기서 잡지 않고 호출자(AiService)가 잡아서 AI_GENERATION_FAILED로 변환하고
    // 실패 로그를 남기도록 함. API 키가 URL 쿼리에 들어가므로 요청/응답을 로깅하지 않음.
    public String generateContent(String prompt) {
        GeminiGenerateContentResponse response =
                restClient
                        .post()
                        .uri("/v1beta/models/{model}:generateContent?key={key}", model, apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(GeminiGenerateContentRequest.ofText(prompt))
                        .retrieve()
                        .body(GeminiGenerateContentResponse.class);

        return response.firstText();
    }
}
