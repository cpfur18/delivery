package com.delivery.domain.order.controller;

import com.delivery.common.RestApiResponse;
import com.delivery.domain.order.dto.request.OrderCreateRequest;
import com.delivery.domain.order.dto.response.OrderCreateResponse;
import com.delivery.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 고객 주문 생성
    @PostMapping
    public ResponseEntity<RestApiResponse<OrderCreateResponse>> createOrder(
            @Valid @RequestBody OrderCreateRequest request
    ){
        // TODO: Spring Security/JWT 적용 후 인증 객체에서 로그인 사용자 ID 가져오기

        Long currentUserId = 1L;

        OrderCreateResponse response = orderService.createOrder(request, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestApiResponse.success(
                        HttpStatus.CREATED,
                        "주문이 생성되었습니다.",
                        response
                ));

    }

}
