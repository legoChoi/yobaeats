package com.sparta.yobaeats.domain.order.service;

import com.sparta.yobaeats.domain.auth.entity.UserDetailsCustom;
import com.sparta.yobaeats.domain.menu.entity.Menu;
import com.sparta.yobaeats.domain.menu.service.MenuService;
import com.sparta.yobaeats.domain.order.dto.request.OrderCreateReq;
import com.sparta.yobaeats.domain.order.dto.request.OrderUpdateReq;
import com.sparta.yobaeats.domain.order.entity.Order;
import com.sparta.yobaeats.domain.order.entity.OrderStatus;
import com.sparta.yobaeats.domain.order.repository.OrderRepository;
import com.sparta.yobaeats.domain.store.entity.Store;
import com.sparta.yobaeats.domain.store.service.StoreService;
import com.sparta.yobaeats.domain.user.entity.User;
import com.sparta.yobaeats.domain.user.entity.UserRole;
import com.sparta.yobaeats.domain.user.service.UserService;
import com.sparta.yobaeats.global.exception.CustomRuntimeException;
import com.sparta.yobaeats.global.exception.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StoreService storeService;

    @Mock
    private MenuService menuService;

    @Mock
    private UserService userService;

    private User user;
    private UserDetailsCustom userDetails;
    private Store store;
    private Menu menu;

    @BeforeEach
    void setUp() {
        // 공통적으로 사용되는 객체 초기화
        user = User.builder()
                .id(1L)
                .email("user@example.com")
                .password("password")
                .nickName("username")
                .role(UserRole.ROLE_USER)
                .build();
        userDetails = new UserDetailsCustom(user);

        store = Store.builder()
                .id(1L)
                .name("가게 이름")
                .minOrderPrice(5000)
                .starRate(0.0)
                .isDeleted(false)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(21, 0))
                .user(user)
                .build();

        menu = new Menu(1L, store, "메뉴 이름", 10000, "메뉴 설명", false);
    }

    // 주문 생성 성공 테스트
    @Test
    @WithMockUser(roles = "USER")
    void 주문_생성_성공() {
        // given
        OrderCreateReq orderCreateReq = new OrderCreateReq(store.getId(), menu.getId());

        // Mock 설정
        when(storeService.findStoreById(store.getId())).thenReturn(store);
        when(menuService.findMenuById(menu.getId())).thenReturn(menu);
        when(orderRepository.save(any(Order.class))).thenReturn(new Order(1L, user, store, menu, OrderStatus.PENDING));

        // when
        Long orderId = orderService.createOrder(orderCreateReq, userDetails);

        // then
        assertNotNull(orderId);
        verify(orderRepository).save(any(Order.class));
    }

    // 주문 조회 성공 테스트
    @Test
    void 주문_조회_성공() {
        // given
        Order order = new Order(1L, user, store, menu, OrderStatus.PENDING);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // when
        Order foundOrder = orderService.findOrderById(order.getId());

        // then
        assertEquals(order.getId(), foundOrder.getId());
        assertEquals(user.getId(), foundOrder.getUser().getId());
    }

    // 주문 조회 실패 테스트
    @Test
    void 주문_조회_실패() {
        // given
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // when & then
        CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () -> {
            orderService.findOrderById(orderId);
        });
        assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
    }

    // 주문 상태 변경 성공 테스트
    @Test
    @WithMockUser(roles = "OWNER")
    void 주문_상태_변경_성공() {
        // given
        User owner = User.builder()
                .id(1L)
                .email("owner@example.com")
                .password("password")
                .nickName("ownerUsername")
                .role(UserRole.ROLE_OWNER)
                .build();

        UserDetailsCustom ownerDetails = new UserDetailsCustom(owner);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                ownerDetails,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_OWNER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 주문 객체 생성
        Order order = new Order(1L, owner, store, menu, OrderStatus.PENDING); // 올바른 소유자 연결

        // Mock 설정
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(storeService.findStoreById(store.getId())).thenReturn(store);

        OrderUpdateReq orderUpdateReq = new OrderUpdateReq(OrderStatus.ORDER_REQUESTED);

        // when
        orderService.updateOrderStatus(order.getId(), orderUpdateReq, ownerDetails);

        // then
        assertEquals(OrderStatus.ORDER_REQUESTED, order.getOrderStatus());
        verify(orderRepository).findById(order.getId());
        verify(storeService).findStoreById(store.getId());
    }

    // 주문 생성 실패 테스트 - 스토어가 존재하지 않음
    @Test
    @WithMockUser(roles = "USER")
    void 주문_생성_실패_스토어_존재하지않음() {
        // given
        when(storeService.findStoreById(store.getId())).thenThrow(new CustomRuntimeException(ErrorCode.STORE_NOT_FOUND));
        OrderCreateReq orderCreateReq = new OrderCreateReq(store.getId(), menu.getId());

        // when & then
        CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () -> {
            orderService.createOrder(orderCreateReq, userDetails);
        });
        assertEquals(ErrorCode.STORE_NOT_FOUND, exception.getErrorCode());
    }
}