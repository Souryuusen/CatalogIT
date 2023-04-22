package com.souryuu.catalogit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Set;

@Entity
@Table(name = "Directors")
@NoArgsConstructor @RequiredArgsConstructor
@ToString @EqualsAndHashCode
public class Director {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "director_id") @Getter
    private long directorID;

    @Column(name = "name", nullable = false, unique = true) @Getter @Setter @NonNull
    private String name;

    @ManyToMany(mappedBy = "directors")
    private Set<Movie> directedMovies;
}
