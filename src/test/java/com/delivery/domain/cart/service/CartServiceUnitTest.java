package com.delivery.domain.cart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.delivery.domain.cart.dto.response.CartResponse;
import com.delivery.domain.cart.entity.Cart;
import com.delivery.domain.cart.entity.CartItem;
import com.delivery.domain.cart.repository.CartItemRepository;
import com.delivery.domain.cart.repository.CartRepository;
import com.delivery.domain.menu.entity.MenuEntity;
import com.delivery.domain.menu.exception.MenuException;
import com.delivery.domain.menu.repository.MenuRepository;
import com.delivery.domain.user.entity.Role;
import com.delivery.global.exception.BusinessException;
import com.delivery.global.security.config.CustomUserDetails;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
class CartServiceUnitTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private MenuRepository menuRepository;

    @InjectMocks private CartService cartService;

    @Nested
    @DisplayName("내 장바구니 조회")
    class GetMyCart {

        @Test
        @DisplayName("장바구니가 없으면 빈 장바구니를 반환한다")
        void getMyCart_returns_empty_when_cart_does_not_exist() {
            CustomUserDetails userDetails = createUserDetails(1L);

            when(cartRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

            CartResponse response = cartService.getMyCart(userDetails);

            assertThat(response.userId()).isEqualTo(1L);
            assertThat(response.items()).isEmpty();
            assertThat(response.totalAmount()).isZero();
        }
    }

    @Nested
    @DisplayName("장바구니 항목 추가")
    class AddCartItem {

        @Test
        @DisplayName("장바구니가 없으면 새 장바구니를 만들고 항목을 추가한다")
        void addCartItem_creates_cart_when_cart_does_not_exist() {
            CustomUserDetails userDetails = createUserDetails(1L);
            UUID menuId = UUID.randomUUID();
            UUID storeId = UUID.randomUUID();
            MenuEntity menu = new MenuEntity(storeId, "짜장면", "기본", 7000);
            Cart cart = createCart(1L, storeId);
            CartItem cartItem = CartItem.create(cart, menuId, 2, 7000L);

            when(menuRepository.findByMenuIdAndDeletedAtIsNull(menuId)).thenReturn(Optional.of(menu));
            when(cartRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);
            when(cartItemRepository.findByCartAndMenuIdAndDeletedAtIsNull(cart, menuId))
                    .thenReturn(Optional.empty());
            when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
            when(cartItemRepository.findAllByCartAndDeletedAtIsNullOrderByCreatedAtAsc(cart))
                    .thenReturn(List.of(cartItem));

            CartResponse response = cartService.addCartItem(userDetails, menuId, 2);

            assertThat(response.storeId()).isEqualTo(storeId);
            assertThat(response.totalQuantity()).isEqualTo(2);
            assertThat(response.totalAmount()).isEqualTo(14000L);
        }

        @Test
        @DisplayName("같은 메뉴를 다시 담으면 수량을 합산한다")
        void addCartItem_increases_quantity_when_same_menu_exists() {
            CustomUserDetails userDetails = createUserDetails(1L);
            UUID menuId = UUID.randomUUID();
            UUID storeId = UUID.randomUUID();
            MenuEntity menu = new MenuEntity(storeId, "짬뽕", "매운맛", 9000);
            Cart cart = createCart(1L, storeId);
            CartItem cartItem = CartItem.create(cart, menuId, 1, 9000L);

            when(menuRepository.findByMenuIdAndDeletedAtIsNull(menuId)).thenReturn(Optional.of(menu));
            when(cartRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(cart));
            when(cartItemRepository.findByCartAndMenuIdAndDeletedAtIsNull(cart, menuId))
                    .thenReturn(Optional.of(cartItem));
            when(cartItemRepository.findAllByCartAndDeletedAtIsNullOrderByCreatedAtAsc(cart))
                    .thenReturn(List.of(cartItem));

            CartResponse response = cartService.addCartItem(userDetails, menuId, 2);

            assertThat(cartItem.getQuantity()).isEqualTo(3);
            assertThat(response.totalQuantity()).isEqualTo(3);
            verify(cartItemRepository, never()).save(any(CartItem.class));
        }

        @Test
        @DisplayName("다른 가게 메뉴는 같은 장바구니에 담을 수 없다")
        void addCartItem_fails_when_store_is_different() {
            CustomUserDetails userDetails = createUserDetails(1L);
            UUID menuId = UUID.randomUUID();
            MenuEntity menu = new MenuEntity(UUID.randomUUID(), "볶음밥", "기본", 8000);
            Cart cart = createCart(1L, UUID.randomUUID());

            when(menuRepository.findByMenuIdAndDeletedAtIsNull(menuId)).thenReturn(Optional.of(menu));
            when(cartRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(cart));

            assertThatThrownBy(() -> cartService.addCartItem(userDetails, menuId, 1))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("숨김 메뉴는 장바구니에 추가할 수 없다")
        void addCartItem_fails_when_menu_is_hidden() {
            CustomUserDetails userDetails = createUserDetails(1L);
            UUID menuId = UUID.randomUUID();
            MenuEntity menu = new MenuEntity(UUID.randomUUID(), "숨김메뉴", "비공개", 5000);
            menu.updateHidden(true);

            when(menuRepository.findByMenuIdAndDeletedAtIsNull(menuId)).thenReturn(Optional.of(menu));

            assertThatThrownBy(() -> cartService.addCartItem(userDetails, menuId, 1))
                    .isInstanceOf(MenuException.class);
        }
    }

    @Nested
    @DisplayName("장바구니 항목 수정")
    class UpdateCartItem {

        @Test
        @DisplayName("다른 고객의 장바구니 항목은 수정할 수 없다")
        void updateCartItem_fails_when_cart_item_owned_by_another_user() {
            CustomUserDetails userDetails = createUserDetails(1L);
            Cart cart = createCart(2L, UUID.randomUUID());
            CartItem cartItem = CartItem.create(cart, UUID.randomUUID(), 1, 5000L);

            when(cartItemRepository.findByCartItemIdAndDeletedAtIsNull(cartItem.getCartItemId()))
                    .thenReturn(Optional.of(cartItem));

            assertThatThrownBy(
                            () ->
                                    cartService.updateCartItem(
                                            userDetails, cartItem.getCartItemId(), 3))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("장바구니 항목 삭제")
    class DeleteCartItem {

        @Test
        @DisplayName("마지막 항목을 삭제하면 장바구니도 소프트 삭제한다")
        void deleteCartItem_deletes_cart_when_last_item_removed() {
            CustomUserDetails userDetails = createUserDetails(1L);
            Cart cart = createCart(1L, UUID.randomUUID());
            CartItem cartItem = CartItem.create(cart, UUID.randomUUID(), 1, 5000L);

            when(cartItemRepository.findByCartItemIdAndDeletedAtIsNull(cartItem.getCartItemId()))
                    .thenReturn(Optional.of(cartItem));
            when(cartItemRepository.countByCartAndDeletedAtIsNull(cart)).thenReturn(0L);

            cartService.deleteCartItem(userDetails, cartItem.getCartItemId());

            assertThat(cartItem.isDeleted()).isTrue();
            assertThat(cart.isDeleted()).isTrue();
        }
    }

    private CustomUserDetails createUserDetails(Long userId) {
        return CustomUserDetails.builder()
                .id(userId)
                .username("customer")
                .authorities(
                        Set.of(new SimpleGrantedAuthority(Role.CUSTOMER.getAuthority())))
                .build();
    }

    private Cart createCart(Long userId, UUID storeId) {
        return Cart.create(userId, storeId);
    }
}
