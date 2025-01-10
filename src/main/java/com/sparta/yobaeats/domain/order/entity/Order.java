package com.sparta.yobaeats.domain.order.entity;

import com.sparta.yobaeats.domain.common.BaseEntity;
import com.sparta.yobaeats.domain.menu.entity.Menu;
import com.sparta.yobaeats.domain.store.entity.Store;
import com.sparta.yobaeats.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Enumerated(EnumType.STRING) // Enum을 DB에 저장할 때 문자열로 저장
    @Column(name = "status", nullable = false)
    @ColumnDefault("'PENDING'")
    private OrderStatus orderStatus;

    @Builder
    public Order(Long id, User user, Store store, Menu menu, OrderStatus orderStatus) {
        this.id = id;
        this.user = user;
        this.store = store;
        this.menu = menu;
        this.orderStatus = orderStatus != null ? orderStatus : OrderStatus.PENDING;
    }

    /**
     * 주문 상태를 다음 상태로 변경하는 메서드
     * 현재 주문 상태에 따라 다음 상태로 전환합니다.
     * 상태 전환은 OrderStatus Enum의 nextStatus() 메서드를 통해 처리됩니다.
     */
    public void changeStatusToNext() {
        this.orderStatus = this.orderStatus.nextStatus();  // nextStatus 호출하여 상태 전환
    }
}