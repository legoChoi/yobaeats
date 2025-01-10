package com.sparta.yobaeats.domain.review.dto;

public class ReviewValidationMessage {

    public static final String ORDER_ID_BLANK_MESSAGE = "orderId를 입력해 주세요.";
    public static final String STAR_BLANK_MESSAGE = "별점을 입력해 주세요.";
    public static final String STAR_MAX_MESSAGE = "별점은 최대 5점까지 입력할 수 있습니다.";
    public static final String STAR_MIN_MESSAGE = "별점은 최소 1점부터 입력할 수 있습니다.";
    public static final int STAR_MAX = 5;
    public static final int STAR_MIN = 1;
    public static final String REVIEW_BLANK_MESSAGE = "리뷰 내용을 입력해 주세요.";
    public static final String REVIEW_MAX_MESSAGE = "리뷰 내용은 20자 이하로 입력해 주세요.";
    public static final int REVIEW_MAX = 20;
}
