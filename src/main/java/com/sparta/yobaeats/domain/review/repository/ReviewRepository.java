package com.sparta.yobaeats.domain.review.repository;

import com.sparta.yobaeats.domain.order.entity.Order;
import com.sparta.yobaeats.domain.review.entity.Review;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryQuerydsl {

    boolean existsByOrder(Order order);

    Optional<Review> findByIdAndIsDeletedFalse(Long reviewId);

    @Query("SELECT ROUND(AVG(r.star),1) FROM Review r WHERE r.store.id = :storeId AND r.isDeleted = false")
    Double getAverageStarRate(@Param("storeId") Long storeId);
}
