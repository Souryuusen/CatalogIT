package com.souryuu.catalogit.entity.database;

import com.google.common.base.Objects;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "GENRES")
@NoArgsConstructor @RequiredArgsConstructor @ToString
public class Genre {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    @Getter
    private long genreID;

    @Getter @Setter @ToString.Include
    @Column(name = "genre_name") @NonNull
    @NaturalId
    private String genreName;

    @Getter @Setter @ToString.Exclude
    @Column(name = "genre_description")
    private String genreDescription;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre)) return false;
        Genre genre = (Genre) o;
        return Objects.equal(getGenreName(), genre.getGenreName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getGenreName());
    }
}
