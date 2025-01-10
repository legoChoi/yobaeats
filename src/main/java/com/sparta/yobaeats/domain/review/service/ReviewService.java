package com.sparta.yobaeats.domain.review.service;

import com.sparta.yobaeats.domain.order.entity.Order;
import com.sparta.yobaeats.domain.order.entity.OrderStatus;
import com.sparta.yobaeats.domain.order.service.OrderService;
import com.sparta.yobaeats.domain.review.dto.request.ReviewCreateReq;
import com.sparta.yobaeats.domain.review.dto.response.ReviewReadInfoRes;
import com.sparta.yobaeats.domain.review.entity.Review;
import com.sparta.yobaeats.domain.review.repository.ReviewRepository;
import com.sparta.yobaeats.domain.store.entity.Store;
import com.sparta.yobaeats.domain.user.entity.User;
import com.sparta.yobaeats.domain.user.service.UserService;
import com.sparta.yobaeats.global.exception.ConflictException;
import com.sparta.yobaeats.global.exception.InvalidException;
import com.sparta.yobaeats.global.exception.NotFoundException;
import com.sparta.yobaeats.global.exception.UnauthorizedException;
import com.sparta.yobaeats.global.exception.error.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final OrderService orderService;

    @Transactional
    public Long createReview(Long userId, ReviewCreateReq reviewCreateReq) {
        User user = userService.findUserById(userId);
        Order order = orderService.findOrderById(reviewCreateReq.orderId());
        Store store = order.getStore();
        // 리뷰 중복 작성 검증
        if (reviewRepository.existsByOrder(order)) {
            throw new ConflictException(ErrorCode.DUPLICATE_REVIEW);
        }
        // 사용자 검증
        if (!user.getId().equals(order.getUser().getId())) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_USER);
        }
        // 주문 상태 검증
        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new InvalidException(ErrorCode.INVALID_ORDER_STATUS);
        }

        reviewRepository.save(reviewCreateReq.to(user, order, store));

        Double averageStarRate = reviewRepository.getAverageStarRate(store.getId());
//        store.updateStarRate(averageStarRate != null ? averageStarRate : 0.0);

        return store.getId();
    }

    public List<ReviewReadInfoRes> readReviews(
        Long storeId, Integer startStar, Integer endStar
    ) {
        if (startStar != null && endStar != null && startStar > endStar) {
            throw new InvalidException(ErrorCode.INVALID_STAR_RANGE);
        }

        List<Review> reviewList = reviewRepository
            .findReviewsByStoreIdAndStar(storeId, startStar, endStar);

        if (reviewList.isEmpty()) {
            throw new NotFoundException(ErrorCode.REVIEW_NOT_FOUND);
        }

        return reviewList.stream()
            .map(ReviewReadInfoRes::from)
            .toList();
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findByIdAndIsDeletedFalse(reviewId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));

        userService.validateUser(review.getUser().getId(), userId);

        review.softDelete();
    }
}