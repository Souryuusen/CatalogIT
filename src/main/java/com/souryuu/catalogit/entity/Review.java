package com.souryuu.catalogit.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.common.base.Objects;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "REVIEWS")
@NoArgsConstructor @RequiredArgsConstructor
@ToString
public class Review{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter @Getter
    @Column(name = "review_id")
    private long reviewID;

    @Getter @Setter @NonNull
    @Column(name = "rating", nullable = false)
    private int rating;

    @Getter @Setter @NonNull
    @Column(name = "review")
    private String review;

    @Getter @Setter @NonNull
    @Column(name = "creation_time")
    private ZonedDateTime creationData;

    @Getter @Setter @NonNull @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "movieID", nullable = false)
    private Movie reviewedMovie;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review1 = (Review) o;
        return getRating() == review1.getRating()
                && Objects.equal(getReview(), review1.getReview()) && Objects.equal(getCreationData(), review1.getCreationData());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getRating(), getReview(), getCreationData());
    }
}
