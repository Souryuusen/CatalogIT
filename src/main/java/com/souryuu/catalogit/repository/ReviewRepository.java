package com.souryuu.catalogit.repository;

import com.souryuu.catalogit.entity.database.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Review getReviewByCreationDataEquals(ZonedDateTime date);

    Review getReviewByReviewID(long reviewID);

    void deleteReviewByReviewID(long reviewID);

}
