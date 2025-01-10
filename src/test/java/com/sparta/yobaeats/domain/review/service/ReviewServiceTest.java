package com.sparta.yobaeats.domain.review.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import com.sparta.yobaeats.domain.order.entity.Order;
import com.sparta.yobaeats.domain.order.entity.OrderStatus;
import com.sparta.yobaeats.domain.order.service.OrderService;
import com.sparta.yobaeats.domain.review.dto.request.ReviewCreateReq;
import com.sparta.yobaeats.domain.review.dto.response.ReviewReadInfoRes;
import com.sparta.yobaeats.domain.review.entity.Review;
import com.sparta.yobaeats.domain.review.repository.ReviewRepository;
import com.sparta.yobaeats.domain.store.entity.Store;
import com.sparta.yobaeats.domain.store.service.StoreService;
import com.sparta.yobaeats.domain.user.entity.User;
import com.sparta.yobaeats.domain.user.entity.UserRole;
import com.sparta.yobaeats.domain.user.service.UserService;
import com.sparta.yobaeats.global.exception.ConflictException;
import com.sparta.yobaeats.global.exception.InvalidException;
import com.sparta.yobaeats.global.exception.NotFoundException;
import com.sparta.yobaeats.global.exception.UnauthorizedException;
import com.sparta.yobaeats.global.exception.error.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserService userService;
    @Mock
    private StoreService storeService;
    @Mock
    private OrderService orderService;
    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Order order;
    private Store store;
    private Review review;
    private ReviewCreateReq reviewCreateReq;

    @BeforeEach
    void setUp() {
        user = new User(1L, "user1@mail.com", "12345!", "유저1", UserRole.ROLE_USER);
        store = Store.builder()
            .id(1L)
            .name("오늘파스타")
            .starRate(4.5)
            .build();

        order = Order.builder()
            .id(3L)
            .user(user)
            .store(store)
            .orderStatus(OrderStatus.DELIVERED)
            .build();
        review = new Review(1L, user, store, order, "맨날 먹어도 맛있어요.", 5);
        reviewCreateReq = new ReviewCreateReq(order.getId(), 5, "오늘 또 먹었어요.");
    }

    // 리뷰 생성 관련
    @DisplayName("주문에 이미 리뷰가 존재하면, '이미 리뷰가 작성된 주문입니다.' 출력")
    @Test
    void createDuplicateReview() {
        // given
        given(userService.findUserById(user.getId())).willReturn(user);
        given(orderService.findOrderById(reviewCreateReq.orderId())).willReturn(order);
        given(reviewRepository.existsByOrder(order)).willReturn(true);

        // when & then
        ConflictException exception = assertThrows(ConflictException.class,
            () -> reviewService.createReview(user.getId(), reviewCreateReq));

        assertEquals(ErrorCode.DUPLICATE_REVIEW, exception.getErrorCode());
    }

    @DisplayName("로그인한 유저와 주문한 유저가 다르면, '접근 권한이 없습니다.' 출력")
    @Test
    void validateUserFail() {
        // given
        Long differUserId = 2L;
        User differUser = new User(differUserId, "user2@mail.com", "123456!", "유저2",
            UserRole.ROLE_USER);
        given(userService.findUserById(differUserId)).willReturn(differUser);
        given(orderService.findOrderById(reviewCreateReq.orderId())).willReturn(order);

        // when & then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
            () -> reviewService.createReview(differUserId, reviewCreateReq));

        assertEquals(ErrorCode.UNAUTHORIZED_USER, exception.getErrorCode());
    }

    @DisplayName("주문 상태가 배달완료가 아니면, '배달 완료 상태가 아닙니다.' 출력")
    @Test
    void createReviewWithInvalidOrderStatus() {
        // given
        Order deliveringOrder = Order.builder().id(4L).user(user).store(store)
            .orderStatus(OrderStatus.DELIVERING).build();
        given(userService.findUserById(user.getId())).willReturn(user);
        given(orderService.findOrderById(reviewCreateReq.orderId())).willReturn(deliveringOrder);

        // when & then
        InvalidException exception = assertThrows(InvalidException.class,
            () -> reviewService.createReview(user.getId(), reviewCreateReq));
        assertEquals(ErrorCode.INVALID_ORDER_STATUS, exception.getErrorCode());
    }

    @DisplayName("리뷰 생성 성공")
    @Test
    void createReviewSuccess() {
        // given
        given(userService.findUserById(user.getId())).willReturn(user);
        given(orderService.findOrderById(reviewCreateReq.orderId())).willReturn(order);
        given(reviewRepository.existsByOrder(order)).willReturn(false);

        // when
        Long storeId = reviewService.createReview(user.getId(), reviewCreateReq);

        // then
        assertEquals(store.getId(), storeId);
    }

    @DisplayName("리뷰 생성 시 가게 평균 별점이 업데이트 됨")
    @Test
    void createReviewUpdateStoreStarRate() {
        // given
        given(userService.findUserById(user.getId())).willReturn(user);
        given(orderService.findOrderById(reviewCreateReq.orderId())).willReturn(order);
        given(reviewRepository.existsByOrder(order)).willReturn(false);
        given(reviewRepository.getAverageStarRate(store.getId())).willReturn(4.5);

        // when
        reviewService.createReview(user.getId(), reviewCreateReq);

        // then
        assertEquals(4.5, store.getStarRate());
    }

    // 리뷰 조회 관련
    @DisplayName("시작 별점이 끝 별점보다 크다면, '별점 범위가 유효하지 않습니다.' 출력")
    @Test
    void findReviewsWithInvalidStarRange() {
        // given
        int startStar = 5;
        int endStar = 3;

        // when & then
        InvalidException exception = assertThrows(InvalidException.class,
            () -> reviewService.readReviews(store.getId(), startStar, endStar));

        assertEquals(ErrorCode.INVALID_STAR_RANGE, exception.getErrorCode());
    }

    @DisplayName("해당 별점 범위의 리뷰가 없으면, '해당 별점의 리뷰가 없습니다.' 출력")
    @Test
    void findNoReviewsInStarRange() {
        // given
        int startStar = 1;
        int endStar = 2;
        given(reviewRepository.findReviewsByStoreIdAndStar(store.getId(), startStar,
            endStar)).willReturn(new ArrayList<>());

        // when & then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> reviewService.readReviews(store.getId(), startStar, endStar));
        assertEquals(ErrorCode.STAR_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("가게 리뷰 조회 성공")
    @Test
    void findReviewsByStoreSuccess() {
        // given
        int startStar = 3;
        int endStar = 5;
        List<Review> reviews = List.of(review);
        given(reviewRepository.findReviewsByStoreIdAndStar(store.getId(), startStar,
            endStar)).willReturn(reviews);

        // when
        List<ReviewReadInfoRes> reviewReadInfoResList = reviewService.readReviews(store.getId(),
            startStar, endStar);

        // then
        assertFalse(reviewReadInfoResList.isEmpty());
    }

    @DisplayName("존재하지 않는 리뷰 삭제 시도시, '리뷰가 존재하지 않습니다.' 출력")
    @Test
    void deleteNotExistReview() {
        // given
        Long reviewId = 918L;
        given(reviewRepository.findByIdAndIsDeletedFalse(reviewId)).willReturn(Optional.empty());

        // when & then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> reviewService.deleteReview(reviewId, user.getId()));
        assertEquals(ErrorCode.REVIEW_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("리뷰 삭제 성공")
    @Test
    void deleteReviewSuccess() {
        // given
        Long reviewId = review.getId();
        given(reviewRepository.findByIdAndIsDeletedFalse(reviewId)).willReturn(Optional.of(review));
        doNothing().when(userService).validateUser(user.getId(), user.getId());

        // when
        reviewService.deleteReview(reviewId, user.getId());

        // then
        assertTrue(review.isDeleted());
    }
}