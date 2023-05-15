package com.souryuu.catalogit.entity;

import com.google.common.base.Objects;
import com.souryuu.catalogit.utility.ScraperUtility;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.util.Set;

@Entity
@Table(name = "Writers")
@NoArgsConstructor
@ToString(includeFieldNames = false, onlyExplicitlyIncluded = true)
public class Writer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "writer_id") @Getter
    private long writerID;

    @Column(name = "name", nullable = false, unique = true) @Getter @ToString.Include
    @NaturalId
    private String name;

    public Writer(String name) {
        // Arguments Check
        if(name == null) throw new IllegalArgumentException("Writer Name Cannot Be Null!!");
        this.setName(name);
    }

    protected void setName(String name) {
        this.name = ScraperUtility.formatToCamelCase(name.trim());
    }

    @ManyToMany(mappedBy = "writers")
    private Set<Movie> writtenMovies;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Writer)) return false;
        Writer writer = (Writer) o;
        return Objects.equal(getName(), writer.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}
