package com.delivery.domain.order.controller;

import com.delivery.common.RestApiResponse;
import com.delivery.domain.order.dto.request.OrderCreateRequest;
import com.delivery.domain.order.dto.response.OrderCreateResponse;
import com.delivery.domain.order.dto.response.OrderDetailResponse;
import com.delivery.domain.order.dto.response.OrderListResponse;
import com.delivery.domain.order.dto.response.OrderStatusResponse;
import com.delivery.domain.order.enums.OrderStatus;
import com.delivery.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 고객 주문 생성
    @PostMapping("/orders")
    public ResponseEntity<RestApiResponse<OrderCreateResponse>> createOrder(
            @Valid @RequestBody OrderCreateRequest request
    ){
        // TODO: Spring Security/JWT 적용 후 인증 객체에서 로그인 사용자 ID 가져오기

        Long currentUserId = 2L;

        OrderCreateResponse response = orderService.createOrder(request, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestApiResponse.success(
                        HttpStatus.CREATED,
                        "주문이 생성되었습니다.",
                        response
                ));

    }

    // 주문 단건 조회
    @GetMapping("/orders/{orderId}")
    // TODO: Spring Security/JWT 연동 후 역할 기반 접근 권한 적용
//    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'MANAGER', 'MASTER')")
    public ResponseEntity<RestApiResponse<OrderDetailResponse>> getOrder(
            @PathVariable UUID orderId
            ){
        // TODO: Spring Security/JWT 적용 후 인증 객체에서 로그인 사용자 ID 추출
        Long currentUserId = 1L;

        OrderDetailResponse response = orderService.getOrder(orderId, currentUserId);

        return ResponseEntity.ok(
                RestApiResponse.success(
                        HttpStatus.OK,
                        "주문 조회에 성공했습니다.",
                        response
                )
        );

    }

    // 고객 본인 주문 내역 조회
    @GetMapping("/orders/me")
    // TODO: Spring Security/JWT 연동 후 역할 기반 접근 권한 적용
    // @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<RestApiResponse<OrderListResponse>> getMyOrders(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false)
            OrderStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "createdAt,desc")
            String sort
    ) {
        // TODO: Spring Security/JWT 적용 후 인증 객체에서 로그인 사용자 ID 추출
        Long currentUserId = 1L;

        OrderListResponse response = orderService.getMyOrders(
                currentUserId,
                startDate,
                endDate,
                status,
                page,
                size,
                sort
        );

        return ResponseEntity.ok(
                RestApiResponse.success(
                        HttpStatus.OK,
                        "주문 내역 조회에 성공했습니다.",
                        response
                )
        );
    }


    // 가게 주문 내역 조회
    @GetMapping("/stores/{storeId}/orders")
    // TODO: Spring Security/JWT 연동 후 OWNER, MANAGER, MASTER(역할 기반 접근) 권한 적용
    // @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    public ResponseEntity<RestApiResponse<OrderListResponse>> getStoreOrders(
            @PathVariable UUID storeId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false)
            OrderStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "createdAt,desc")
            String sort
    ) {
        OrderListResponse response = orderService.getStoreOrders(
                storeId,
                startDate,
                endDate,
                status,
                page,
                size,
                sort
        );

        return ResponseEntity.ok(
                RestApiResponse.success(
                        HttpStatus.OK,
                        "가게 주문 내역 조회에 성공했습니다.",
                        response
                )
        );
    }

    // 관리자 주문 삭제
    @DeleteMapping("/admin/orders/{orderId}")
    // TODO: Spring Security/JWT 연동 후 관리자 권한 적용
    // @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    public ResponseEntity<RestApiResponse<Void>> deleteOrder(
            @PathVariable UUID orderId
    ) {
        // TODO: Spring Security/JWT 연동 후 인증 객체에서 관리자 ID 추출
        Long currentAdminId = 1L;

        // 관리자 ID와 삭제 대상 주문 ID를 Service에 전달
        orderService.deleteOrder(orderId, currentAdminId);

        return ResponseEntity.ok(
                RestApiResponse.success(
                        HttpStatus.OK,
                        "주문이 삭제되었습니다.",
                        null
                )
        );
    }


    // 주문 상태 변경 7가지
    // 고객 주문 취소 or 가게 주문 거절
    // 가게 주문 수락 -> 조리중 -> 배달 중 -> 배달 완료 -> 주문 최종 완료(리뷰 가능)

    // 고객 주문 취소
    @PatchMapping("/orders/{orderId}/cancel")
    // TODO: Spring Security/JWT 연동 후 CUSTOMER 권한 적용
    // @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<RestApiResponse<OrderStatusResponse>> cancelOrder(
            @PathVariable UUID orderId
    ) {
        // TODO: JWT 적용 후 인증 객체에서 로그인 고객 ID 추출
        Long currentUserId = 1L;

        OrderStatusResponse response =
                orderService.cancelOrder(orderId, currentUserId);

        return ResponseEntity.ok(
                RestApiResponse.success(
                        HttpStatus.OK,
                        "주문이 취소되었습니다.",
                        response
                )
        );
    }

    // 가게 주문 거절
    @PatchMapping("/stores/{storeId}/orders/{orderId}/reject")
    // TODO: Spring Security/JWT 연동 후 OWNER 권한 적용
    // @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<RestApiResponse<OrderStatusResponse>> rejectOrder(
            @PathVariable UUID storeId,
            @PathVariable UUID orderId
    ) {
        // TODO: JWT 적용 후 인증 객체에서 로그인 가게 사장 ID 추출
        Long currentUserId = 1L;

        OrderStatusResponse response =
                orderService.changeStoreOrderStatus(
                        storeId,
                        orderId,
                        OrderStatus.REJECTED,
                        currentUserId
                );

        return ResponseEntity.ok(
                RestApiResponse.success(
                        HttpStatus.OK,
                        "주문이 거절되었습니다.",
                        response
                )
        );
    }


    // 가게 주문 수락
    @PatchMapping("/stores/{storeId}/orders/{orderId}/accept")
    // TODO: Spring Security/JWT 연동 후 OWNER 권한 적용
    // @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<RestApiResponse<OrderStatusResponse>> acceptOrder(
            @PathVariable UUID storeId,
            @PathVariable UUID orderId
    ) {
        Long currentUserId = 1L;

        OrderStatusResponse response =
                orderService.changeStoreOrderStatus(
                        storeId,
                        orderId,
                        OrderStatus.ACCEPTED,
                        currentUserId
                );

        return ResponseEntity.ok(
                RestApiResponse.success(
                        HttpStatus.OK,
                        "주문이 수락되었습니다.",
                        response
                )
        );
    }

    // 가게 조리 중 변경
    @PatchMapping("/stores/{storeId}/orders/{orderId}/cook")
    // TODO: Spring Security/JWT 연동 후 OWNER 권한 적용
    // @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<RestApiResponse<OrderStatusResponse>> startCooking(
            @PathVariable UUID storeId,
            @PathVariable UUID orderId
    ) {
        Long currentUserId = 1L;

        OrderStatusResponse response =
                orderService.changeStoreOrderStatus(
                        storeId,
                        orderId,
                        OrderStatus.COOKING,
                        currentUserId
                );

        return ResponseEntity.ok(
                RestApiResponse.success(
                        HttpStatus.OK,
                        "주문 상태가 조리 중으로 변경되었습니다.",
                        response
                )
        );
    }

    // 배달 중 변경
    @PatchMapping("/stores/{storeId}/orders/{orderId}/deliver")
    // TODO: Spring Security/JWT 연동 후 OWNER 권한 적용
    // @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<RestApiResponse<OrderStatusResponse>> startDelivery(
            @PathVariable UUID storeId,
            @PathVariable UUID orderId
    ) {
        Long currentUserId = 1L;

        OrderStatusResponse response =
                orderService.changeStoreOrderStatus(
                        storeId,
                        orderId,
                        OrderStatus.DELIVERING,
                        currentUserId
                );

        return ResponseEntity.ok(
                RestApiResponse.success(
                        HttpStatus.OK,
                        "주문 상태가 배달 중으로 변경되었습니다.",
                        response
                )
        );
    }

    // 배달 완료 변경
    @PatchMapping("/stores/{storeId}/orders/{orderId}/delivered")
    // TODO: Spring Security/JWT 연동 후 OWNER 권한 적용
    // @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<RestApiResponse<OrderStatusResponse>> completeDelivery(
            @PathVariable UUID storeId,
            @PathVariable UUID orderId
    ) {
        Long currentUserId = 1L;

        OrderStatusResponse response =
                orderService.changeStoreOrderStatus(
                        storeId,
                        orderId,
                        OrderStatus.DELIVERED,
                        currentUserId
                );

        return ResponseEntity.ok(
                RestApiResponse.success(
                        HttpStatus.OK,
                        "주문 상태가 배달 완료로 변경되었습니다.",
                        response
                )
        );
    }


    // 고객 주문 최종 완료
    @PatchMapping("/orders/{orderId}/complete")
    // TODO: Spring Security/JWT 연동 후 CUSTOMER 권한 적용
    // @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<RestApiResponse<OrderStatusResponse>> completeOrder(
            @PathVariable UUID orderId
    ) {
        // TODO: JWT 적용 후 인증 객체에서 로그인 고객 ID 추출
        Long currentUserId = 1L;

        OrderStatusResponse response =
                orderService.completeOrder(orderId, currentUserId);

        return ResponseEntity.ok(
                RestApiResponse.success(
                        HttpStatus.OK,
                        "주문이 최종 완료되었습니다.",
                        response
                )
        );
    }


}
