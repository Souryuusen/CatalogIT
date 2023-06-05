package com.souryuu.catalogit.service;

import com.souryuu.catalogit.entity.database.Review;
import com.souryuu.catalogit.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class ReviewService {

    private final ReviewRepository repository;

    public ReviewService(ReviewRepository repository) {
        this.repository = repository;
    }

    public Review save(Review review) {
        return this.repository.save(review);
    }

    public void delete(Review review) {
        this.repository.delete(review);
    }

    public Review getByReviewID(long reviewID) {
        return this.repository.getReviewByReviewID(reviewID);
    }

    public Review getByCreationDataEquals(ZonedDateTime date) {
        return this.repository.getReviewByCreationDataEquals(date);
    }

    @Transactional
    public void deleteByReviewID(long reviewID) {
        this.repository.deleteReviewByReviewID(reviewID);
    }
}
