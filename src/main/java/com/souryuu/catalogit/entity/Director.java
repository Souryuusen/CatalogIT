package com.souryuu.catalogit.entity;

import com.google.common.base.Objects;
import com.souryuu.catalogit.utility.ScraperUtility;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.util.Set;

@Entity
@Table(name = "Directors")
@NoArgsConstructor
@ToString(includeFieldNames = false, onlyExplicitlyIncluded = true)
public class Director {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "director_id") @Getter
    private long directorID;

    @Column(name = "name", nullable = false, unique = true) @Getter @ToString.Include
    @NaturalId
    private String name;

    @Setter @Getter
    @ManyToMany(mappedBy = "directors")
    private Set<Movie> directedMovies;

    public Director(String name) {
        // Arguments Check
        if(name == null) throw new IllegalArgumentException("Director Name Cannot Be Null!!");
        this.setName(name);
    }

    protected void setName(String name) {
        this.name = ScraperUtility.formatToCamelCase(name.trim());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Director)) return false;
        Director director = (Director) o;
        return Objects.equal(getName(), director.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}
