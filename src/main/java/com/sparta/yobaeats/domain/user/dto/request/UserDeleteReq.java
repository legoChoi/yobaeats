package com.sparta.yobaeats.domain.user.dto.request;

import com.sparta.yobaeats.domain.user.dto.UserValidationMessage;
import jakarta.validation.constraints.NotNull;

public record UserDeleteReq(
    @NotNull(message = UserValidationMessage.PASSWORD_BLANK_MESSAGE)
    String password
) {
}