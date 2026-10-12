package com.evangalinmart.repository;

import com.evangalinmart.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
    boolean existsByProductIdAndCustomerEmailIgnoreCase(Long productId, String customerEmail);
}
