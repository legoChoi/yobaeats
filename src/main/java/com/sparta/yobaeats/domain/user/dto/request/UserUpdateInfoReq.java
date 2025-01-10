package com.sparta.yobaeats.domain.user.dto.request;

import com.sparta.yobaeats.domain.user.dto.UserValidationMessage;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateInfoReq(
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+])[a-zA-Z\\d!@#$%^&*()\\-_=+]*$",
        message = UserValidationMessage.INVALID_PASSWORD_MESSAGE
    )
    @Size(min = UserValidationMessage.PASSWORD_MIN, message = UserValidationMessage.PASSWORD_MIN_MESSAGE)
    String password,

    @Size(
        min = UserValidationMessage.NICKNAME_MIN,
        max = UserValidationMessage.NICKNAME_MAX,
        message = UserValidationMessage.NICKNAME_RANGE_MESSAGE
    )
    String nickName
) {

}