package com.souryuu.catalogit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "Writers")
@NoArgsConstructor @RequiredArgsConstructor
@ToString @EqualsAndHashCode
public class Writer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "writer_id") @Getter
    private long writerID;

    @Column(name = "name", nullable = false, unique = true) @Getter @Setter @NonNull
    private String name;

    @ManyToMany(mappedBy = "writers", fetch = FetchType.EAGER)
    private Set<Movie> writtenMovies;
}
