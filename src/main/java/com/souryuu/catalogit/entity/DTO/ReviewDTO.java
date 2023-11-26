package com.souryuu.catalogit.entity.DTO;

import com.souryuu.catalogit.entity.database.Review;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class ReviewDTO {

    private double convertedRating;
    private long reviewID;
    private String formattedCreationDate;
    private String reviewBody;

    public ReviewDTO(Review review) {
        setReviewID(review.getReviewID());
        setConvertedRating(review.getRating()/10.0);
        setFormattedCreationDate(review.getCreationData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        setReviewBody(review.getReview());
    }

}