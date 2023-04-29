package com.souryuu.catalogit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "REVIEWS")
@NoArgsConstructor @RequiredArgsConstructor
public class Review {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter @Getter
    @Column(name = "review_id")
    private long reviewID;

    @Getter @Setter @NonNull
    @Column(name = "rating", nullable = false)
    private byte rating;

    @Getter @Setter @NonNull
    @Column(name = "review")
    private String review;

    @Getter @Setter @NonNull
    @ManyToOne
    @JoinColumn(name = "movieID", nullable = false)
    private Movie reviewedMovie;

}
