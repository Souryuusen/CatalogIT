package com.souryuu.catalogit.service;

import com.souryuu.catalogit.entity.Review;
import com.souryuu.catalogit.repository.ReviewRepository;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepository repository;

    public ReviewService(ReviewRepository repository) {
        this.repository = repository;
    }

    public Review save(Review review) {
        return this.repository.save(review);
    }
}
