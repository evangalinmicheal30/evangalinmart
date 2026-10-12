package com.evangalinmart.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String reviewerName;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, length = 1200)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Review() {}

    public Review(Long productId, String customerEmail, String reviewerName, int rating, String comment) {
        this.productId = productId;
        this.customerEmail = customerEmail;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getCustomerEmail() { return customerEmail; }
    public String getReviewerName() { return reviewerName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
