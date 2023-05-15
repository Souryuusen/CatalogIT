package com.souryuu.imdbscrapper.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TV_SERIES")
public class TVSerie {
    @Id
    @GeneratedValue
    private long seriesID;
    @Column(name = "TITLE") private String title;
    @Column(name = "IMDB_LINK") String imdbLink;
    @Column(name = "REVIEW") String review;

    @Column(name = "RATING") int rating;
    @Column(name = "SEASON_COUNT") int seasonCount;

    public long getSeriesID() {
        return this.seriesID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImdbLink() {
        return imdbLink;
    }

    public void setImdbLink(String imdbLink) {
        this.imdbLink = imdbLink;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getSeasonCount() {
        return seasonCount;
    }

    public void setSeasonCount(int seasonCount) {
        this.seasonCount = seasonCount;
    }
}
