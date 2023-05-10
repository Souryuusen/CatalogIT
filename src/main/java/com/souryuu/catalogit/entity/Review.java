package com.souryuu.catalogit.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "REVIEWS")
@NoArgsConstructor @RequiredArgsConstructor
@ToString
public class Review extends BaseEntity{

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

}
