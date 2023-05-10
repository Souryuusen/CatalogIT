package com.souryuu.catalogit.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "Writers")
@NoArgsConstructor @RequiredArgsConstructor
@ToString(includeFieldNames = false, onlyExplicitlyIncluded = true)
public class Writer extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "writer_id") @Getter
    private long writerID;

    @Column(name = "name", nullable = false, unique = true) @Getter @Setter @NonNull @ToString.Include
    private String name;

    @ManyToMany(mappedBy = "writers")
    private Set<Movie> writtenMovies;
}
