package com.souryuu.catalogit.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "Directors")
@NoArgsConstructor @RequiredArgsConstructor
@ToString(includeFieldNames = false, onlyExplicitlyIncluded = true)
public class Director extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "director_id") @Getter
    private long directorID;

    @Column(name = "name", nullable = false, unique = true) @Getter @Setter @NonNull @ToString.Include
    private String name;

    @ManyToMany(mappedBy = "directors")
    private Set<Movie> directedMovies;
}
