package com.sparta.yobaeats.domain.menu.dto.request;

import com.sparta.yobaeats.domain.menu.dto.MenuValidationMessage;
import com.sparta.yobaeats.domain.menu.entity.Menu;
import com.sparta.yobaeats.domain.store.entity.Store;
import com.sparta.yobaeats.global.exception.CustomRuntimeException;
import com.sparta.yobaeats.global.exception.error.ErrorCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MenuCreateReq(

        @NotNull(message = MenuValidationMessage.STOREID_NOTNULL_MESSAGE)
        Long storeId,

        @Size(max = MenuValidationMessage.NAME_MAX, message = MenuValidationMessage.NAME_MAX_MESSAGE)
        @NotBlank(message = MenuValidationMessage.NAME_BLANK_MESSAGE)
        String menuName,

        @NotNull(message = MenuValidationMessage.PRICE_NOTNULL_MESSAGE)
        Integer menuPrice,

        @Size(max = MenuValidationMessage.DESCRIPTION_MAX, message = MenuValidationMessage.DESCRIPTION_MAX_MESSAGE)
        @NotBlank(message = MenuValidationMessage.DESCRIPTION_BLANK_MESSAGE)
        String description
) {
    /**
     * MenuCreateReq를 사용하여 Menu 엔티티 생성
     *
     * @param store 메뉴가 속한 Store 엔티티
     * @return Menu 객체
     */
    public Menu toEntity(Store store) {
        return Menu.builder()
                .store(store)
                .menuName(menuName)
                .menuPrice(menuPrice)
                .description(description)
                .build();
    }
}
