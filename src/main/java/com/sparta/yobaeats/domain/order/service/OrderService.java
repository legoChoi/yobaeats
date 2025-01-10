package com.sparta.yobaeats.domain.order.service;

import com.sparta.yobaeats.global.security.entity.CustomUserDetails;
import com.sparta.yobaeats.domain.menu.entity.Menu;
import com.sparta.yobaeats.domain.order.dto.request.OrderCreateReq;
import com.sparta.yobaeats.domain.order.dto.request.OrderUpdateReq;
import com.sparta.yobaeats.domain.order.entity.Order;
import com.sparta.yobaeats.domain.order.repository.OrderRepository;
import com.sparta.yobaeats.domain.store.entity.Store;
import com.sparta.yobaeats.domain.store.service.StoreService;
import com.sparta.yobaeats.domain.menu.service.MenuService;
import com.sparta.yobaeats.domain.user.entity.User;
import com.sparta.yobaeats.domain.user.entity.UserRole;
import com.sparta.yobaeats.domain.user.service.UserService;
import com.sparta.yobaeats.global.exception.CustomRuntimeException;
import com.sparta.yobaeats.global.exception.error.ErrorCode;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final StoreService storeService;
    private final MenuService menuService;
    private final UserService userService;

    /**
     * 주문 생성 메서드
     *
     * 주어진 주문 생성 요청 DTO를 사용하여 새로운 주문을 생성합니다.
     * 스토어와 메뉴 정보를 확인하고, 현재 인증된 사용자 정보를 기반으로 주문을 저장합니다.
     *
     * @param orderCreateReq 주문 생성 요청 DTO
     * @return 생성된 주문의 ID
     */
    public Long createOrder(OrderCreateReq orderCreateReq, CustomUserDetails userDetails) {
        // 스토어 조회
        Store store = storeService.findStoreById(orderCreateReq.storeId());

        // 스토어가 존재하지 않는 경우 예외 처리
        if (store == null) {
            throw new CustomRuntimeException(ErrorCode.STORE_NOT_FOUND);
        }

        // 메뉴 조회
        Menu menu = menuService.findMenuById(orderCreateReq.menuId());

        // 인증된 사용자 ID 가져오기
        Long userId = userDetails.getId();
        // 사용자 객체 조회
        User user = userService.findUserById(userId); // User 객체를 가져옴

        // 주문 엔티티 생성
        Order order = orderRepository.save(orderCreateReq.toEntity(store, menu, user));

        // 주문이 성공적으로 생성되면 주문 ID를 반환
        return order.getId();
    }

    /**
     * 주문 상태 업데이트 메서드
     *
     * 주어진 주문 ID와 주문 업데이트 요청 DTO를 사용하여 주문 상태를 업데이트합니다.
     * 주문의 현재 상태와 요청된 상태가 일치하는지 확인 후 상태를 변경합니다.
     *
     * @param orderId        주문 ID
     * @param orderUpdateReq 주문 상태 업데이트 요청 DTO
     */
    public void updateOrderStatus(Long orderId, OrderUpdateReq orderUpdateReq, CustomUserDetails userDetails) {
        // 주문 조회
        Order order = findOrderById(orderId);

        // 스토어 ID 가져오기
        Long storeId = order.getStore().getId(); // 주문과 연관된 스토어 ID 가져오기

        // 소유자 체크
        checkIfOwner(userDetails);

        // 스토어 조회
        Store store = storeService.findStoreById(storeId);

        // 인증된 사용자 ID 가져오기
        Long userId = userDetails.getId();

        // 사용자 객체 조회
        User user = userService.findUserById(userId); // User 객체를 가져옴

        // 상태 변경
        order.changeStatusToNext();
    }

    /**
     * 현재 로그인된 사용자가 사장님(ROLE_OWNER)인지 확인하는 메서드
     *
     * 현재 인증된 사용자의 권한을 확인하고, ROLE_OWNER인지 검증합니다.
     * 권한이 없는 경우 예외를 발생시킵니다.
     */
    private void checkIfOwner(CustomUserDetails userDetails) {
        // 인증되지 않은 사용자 확인
        if (userDetails == null) {
            throw new CustomRuntimeException(ErrorCode.INVALID_USER_ROLE); // 인증되지 않은 사용자
        }

        // 사용자의 권한을 확인하고 ROLE_OWNER인지 검증
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new CustomRuntimeException(ErrorCode.INVALID_USER_ROLE))
                .getAuthority();

        // ROLE_OWNER인 경우만 권한 허용
        if (!UserRole.ROLE_OWNER.name().equals(role)) {
            throw new CustomRuntimeException(ErrorCode.INVALID_USER_ROLE);  // 권한이 없는 경우 예외 처리
        }
    }

    /**
     * 주문 ID로 주문을 찾는 메서드입니다.
     *
     * @param orderId 찾고자 하는 주문의 ID
     * @return 주문 객체
     */
    public Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomRuntimeException(ErrorCode.ORDER_NOT_FOUND)); // 주문이 없을 경우 예외 처리
    }
}
