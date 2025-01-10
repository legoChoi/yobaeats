package com.sparta.yobaeats.domain.menu.entity;

import com.sparta.yobaeats.domain.store.entity.Store;
import com.sparta.yobaeats.global.exception.ConflictException;
import com.sparta.yobaeats.global.exception.CustomRuntimeException;
import com.sparta.yobaeats.global.exception.error.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Store store;

    @Column(name = "menu_name", nullable = false, length = 30)
    private String menuName;

    @Column(name = "menu_price", nullable = false)
    private Integer menuPrice;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    @Builder
    public Menu(Long id, Store store, String menuName, Integer menuPrice, String description, boolean isDeleted) {
        this.id = id;
        this.store = store;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.description = description;
        this.isDeleted = isDeleted;
    }

    /**
     * 메뉴 정보를 업데이트하는 메서드
     *
     * @param menuName 업데이트할 메뉴 이름
     * @param menuPrice 업데이트할 메뉴 가격
     * @param description 업데이트할 메뉴 설명
     */
    public void update(String menuName, Integer menuPrice, String description) {
        updateName(menuName); // 이름 업데이트
        updatePrice(menuPrice); // 가격 업데이트
        updateDescription(description); // 설명 업데이트
    }

    /**
     * 메뉴 삭제 처리 메서드
     *
     * 이미 삭제된 메뉴일 경우 예외를 발생시킴
     */
    public void delete() {
        if (this.isDeleted) {
            throw new ConflictException(ErrorCode.MENU_ALREADY_DELETED);
        }
    }

    /**
     * 메뉴 삭제 상태를 변경하는 메서드
     *
     * 이 메서드는 메뉴를 삭제 상태로 표시합니다.
     */
    public void markAsDeleted() {
        this.isDeleted = true;
    }

    /**
     * 메뉴 이름을 업데이트하는 메서드
     *
     * @param newName 새로운 메뉴 이름
     */
    private void updateName(String newName) {
        if (newName != null) {
            this.menuName = newName; // 새로운 이름으로 업데이트
        }
    }

    /**
     * 메뉴 가격을 업데이트하는 메서드
     *
     * @param newPrice 새로운 메뉴 가격
     */
    private void updatePrice(Integer newPrice) {
        if (newPrice != null) {
            this.menuPrice = newPrice; // 새로운 가격으로 업데이트
        }
    }

    /**
     * 메뉴 설명을 업데이트하는 메서드
     *
     * @param newDescription 새로운 메뉴 설명
     */
    private void updateDescription(String newDescription) {
        if (newDescription != null) {
            this.description = newDescription; // 새로운 설명으로 업데이트
        }
    }
}