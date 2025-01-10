package com.sparta.yobaeats.domain.order.dto.request;

import com.sparta.yobaeats.domain.menu.entity.Menu;
import com.sparta.yobaeats.domain.order.dto.OrderValidationMessage;
import com.sparta.yobaeats.domain.order.entity.Order;
import com.sparta.yobaeats.domain.order.entity.OrderStatus;
import com.sparta.yobaeats.domain.store.entity.Store;
import com.sparta.yobaeats.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderCreateReq(

        @NotNull(message = OrderValidationMessage.STOREID_NOTNULL_MESSAGE)
        Long storeId,

        @NotNull(message = OrderValidationMessage.MENUID_NOTNULL_MESSAGE)
        Long menuId
) {
    /**
     * OrderCreateReq를 사용하여 Order 엔티티를 생성하는 메서드
     *
     * @param store 주문할 가게의 Store 엔티티
     * @param menu  주문할 메뉴의 Menu 엔티티
     * @param user   주문을 생성하는 사용자 User 엔티티
     * @return 생성된 Order 객체
     *         - 주문 상태는 기본적으로 'ORDER_REQUESTED'로 설정됩니다.
     */
    public Order toEntity(Store store, Menu menu, User user) {
        return Order.builder()
                .store(store)
                .menu(menu)
                .user(user)
                .orderStatus(OrderStatus.ORDER_REQUESTED) // 주문 상태 설정
                .build();
    }
}