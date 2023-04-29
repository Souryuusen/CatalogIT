package com.souryuu.catalogit.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "MOVIES")
@NoArgsConstructor @RequiredArgsConstructor
@ToString @EqualsAndHashCode
public class Movie {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "movie_id") @Getter
    private long movieID;
    @Column(name = "title", nullable = false) @Getter @Setter @NonNull
    private String title;
    @Column(name = "imdb_url", unique = true) @Getter @Setter
    private String imdbUrl;
    @Column(name = "cover_url") @Getter @Setter
    private String coverUrl;
    @Column(name = "runtime") @Getter @Setter
    private String runtime;
    @Column(name = "language") @Getter @Setter
    private String language;
    @Column(name = "release_date") @Getter @Setter
    private String releaseDate;

    @Getter @Setter
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "MOVIE_DIRECTORS",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id")
    )
    private Set<Director> directors;

    @Getter @Setter
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "MOVIE_WRITERS",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "writer_id")
    )
    private Set<Writer> writers;

    @Getter @Setter
    @OneToMany(mappedBy = "reviewedMovie")
    private Set<Review> reviews;
}
