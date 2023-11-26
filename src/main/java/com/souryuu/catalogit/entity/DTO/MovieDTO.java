package com.souryuu.catalogit.entity.DTO;

import com.souryuu.catalogit.entity.database.Movie;
import com.souryuu.catalogit.entity.database.Review;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import lombok.Data;
import org.hibernate.Hibernate;

import java.time.ZonedDateTime;

@Data
public class MovieDTO {

    private DoubleProperty avgRating = new SimpleDoubleProperty(-1);
    private int reviewsCount;
    private long movieID;
    private String averageRating;
    private String title;
    private String imdbUrl;
    private String coverUrl;
    private String runtime;
    private String language;
    private String releaseDate;
    private String countryOfOrigin;
    private ZonedDateTime creationDate;

    public MovieDTO(Movie m) {
        if(m != null && m.getMovieID() != 0 && Hibernate.isInitialized(m.getReviews())) {
            setMovieID(m.getMovieID());
            setTitle(m.getTitle());
            setImdbUrl(m.getImdbUrl());
            setCoverUrl(m.getCoverUrl());
            setRuntime(m.getRuntime());
            setLanguage(m.getLanguage());
            setReleaseDate(m.getReleaseDate());
            setCountryOfOrigin(m.getCountryOfOrigin());
            setReviewsCount(m.getReviews().size());
            setCreationDate(m.getCreationData());
            // Calculating Average Rating From Reviews
            double avg = 0;
            if (m.getReviews().size() > 0) {
                for (Review r : m.getReviews()) avg += r.getRating();
                avg /= m.getReviews().size();
                avg /= 10;
                avgRating.setValue(avg);
                setAverageRating(String.format("%.2f", avg));
            } else {
                setAverageRating("-");
            }
        }
    }

    public DoubleProperty avgRatingProperty() {
        return avgRating;
    }

    public final double getAvgRating() {
        return avgRatingProperty().get();
    }

    public final void setAvgRating(double avgRating) {
        avgRatingProperty().set(avgRating);
    }
}