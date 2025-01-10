package com.sparta.yobaeats.domain.review.controller;

import com.sparta.yobaeats.global.security.entity.CustomUserDetails;
import com.sparta.yobaeats.domain.review.dto.request.ReviewCreateReq;
import com.sparta.yobaeats.domain.review.dto.response.ReviewReadInfoListRes;
import com.sparta.yobaeats.domain.review.service.ReviewService;
import com.sparta.yobaeats.global.util.UriBuilderUtil;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Void> createReview(
        @RequestBody @Valid ReviewCreateReq reviewReq,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getId();
        Long StoreId = reviewService.createReview(userId, reviewReq);
        URI uri = UriBuilderUtil.create("/api/reviews/{reviewId}", StoreId);

        return ResponseEntity.created(uri).build();
    }

    @GetMapping
    public ResponseEntity<ReviewReadInfoListRes> readReviews(
        @RequestParam Long storeId,
        @RequestParam(required = false) Integer startStar,
        @RequestParam(required = false) Integer endStar
    ) {
        ReviewReadInfoListRes listRes = new ReviewReadInfoListRes(
            reviewService.readReviews(storeId, startStar, endStar));
        return ResponseEntity.ok(listRes);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
        @PathVariable Long reviewId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getId();
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }
}
