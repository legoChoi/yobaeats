package com.sparta.yobaeats.domain.review.dto.request;

import com.sparta.yobaeats.domain.order.entity.Order;
import com.sparta.yobaeats.domain.review.dto.ReviewValidationMessage;
import com.sparta.yobaeats.domain.review.entity.Review;
import com.sparta.yobaeats.domain.store.entity.Store;
import com.sparta.yobaeats.domain.user.entity.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewCreateReq(
    @NotNull(message = ReviewValidationMessage.ORDER_ID_BLANK_MESSAGE)
    Long orderId,

    @NotNull(message = ReviewValidationMessage.STAR_BLANK_MESSAGE)
    @Min(value = ReviewValidationMessage.STAR_MIN, message = ReviewValidationMessage.STAR_MIN_MESSAGE)
    @Max(value = ReviewValidationMessage.STAR_MAX, message = ReviewValidationMessage.STAR_MAX_MESSAGE)
    int star,

    @NotBlank(message = ReviewValidationMessage.REVIEW_BLANK_MESSAGE)
    @Size(max = ReviewValidationMessage.REVIEW_MAX, message = ReviewValidationMessage.REVIEW_MAX_MESSAGE)
    String contents
) {

    public Review to(User user, Order order, Store store) {
        return Review.builder()
            .user(user)
            .order(order)
            .store(store)
            .contents(this.contents)
            .star(this.star)
            .build();
    }
}
