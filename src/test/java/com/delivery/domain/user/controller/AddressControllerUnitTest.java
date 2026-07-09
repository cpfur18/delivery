package com.delivery.domain.user.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.delivery.domain.auth.controller.AuthController;
import com.delivery.domain.user.dto.request.CreateAddressRequest;
import com.delivery.domain.user.dto.response.AddressResponse;
import com.delivery.domain.user.service.AddressService;
import com.delivery.global.security.jwt.JwtRequestFilter;
import com.delivery.global.security.jwt.JwtUtil;
import com.delivery.testconfig.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AddressControllerUnitTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockitoBean private AddressService addressService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private JwtRequestFilter jwtRequestFilter;

    private final UUID addressId = UUID.randomUUID();


    @Test
    @WithMockCustomUser(id = 1L, role = "CUSTOMER")
    @DisplayName("로그인 유저가 배송지 생성에 성공한다.")
    void createAddress_success() throws Exception {
        // given
        CreateAddressRequest request = new CreateAddressRequest("주소1", "상세주소1", true);
        AddressResponse response = new AddressResponse (addressId, request.address(), request.addressDetail(), request.isDefault());

        given(addressService.createAddress(1L, request)).willReturn(response);

        // when & then
        mockMvc.perform(
                post("/api/v1/users/me/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());


    }

//    @Test
//    @DisplayName("배송지 등록 실패 - 유효성 체크")
//
//
//    @Test
//    @DisplayName("배송지 업데이트 실패 - 유효성 체크")
//
//
//    @Test
//    @DisplayName("")

    void getAddressList() {
    }

    @Test
    @DisplayName("")
    void getAddress() {}

    @Test
    @DisplayName("")
    void updateAddress() {}

    @Test
    @DisplayName("")
    void deleteAddress() {}
}
