package com.sparta.yobaeats.domain.menu.dto;

import org.springframework.http.HttpStatus;

public class MenuValidationMessage {

    public static final String STOREID_NOTNULL_MESSAGE = "스토어 아이디를 입력해주세요.";
    public static final int NAME_MAX = 30;
    public static final String NAME_MAX_MESSAGE = "메뉴명은 30글자를 넘을 수 없습니다.";
    public static final String NAME_BLANK_MESSAGE = "메뉴명을 입력해주세요.";
    public static final String PRICE_NOTNULL_MESSAGE = "메뉴가격을 입력해주세요.";
    public static final String PRICE_VALIDATION_MESSAGE = "메뉴 가격은 음수가 될 수 없습니다.";
    public static final int DESCRIPTION_MAX = 100;
    public static final String DESCRIPTION_MAX_MESSAGE = "메뉴 설명은 100글자를 넘을 수 없습니다.";
    public static final String DESCRIPTION_BLANK_MESSAGE  = "메뉴 설명을 입력해주세요.";
}