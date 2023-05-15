package com.souryuu.catalogit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Objects;
import com.souryuu.catalogit.exception.InvalidIMDBLinkException;
import com.souryuu.imdbscrapper.entity.MovieData;
import com.souryuu.imdbscrapper.entity.ProductionDetailKeys;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.util.Set;

@Entity
@Table(name = "MOVIES")
@NoArgsConstructor
@ToString(exclude = {"directors", "writers", "reviews"})
public class Movie{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "movie_id") @Getter
    private long movieID;
    @Column(name = "title", nullable = false) @Getter @Setter @NonNull
    private String title;
    @Column(name = "imdb_url", unique = true) @Getter @Setter(AccessLevel.PACKAGE) @NonNull
    @NaturalId
    private String imdbUrl;
    @Column(name = "cover_url") @Getter @Setter
    private String coverUrl;
    @Column(name = "runtime") @Getter @Setter
    private String runtime;
    @Column(name = "language") @Getter @Setter
    private String language;
    @Column(name = "release_date") @Getter @Setter
    private String releaseDate;
    @Column(name = "country_of_origin") @Getter @Setter
    private String countryOfOrigin;

    @Getter @Setter
    @ManyToMany(cascade = {CascadeType.MERGE})
    @JsonIgnoreProperties({"directors", "writers", "reviews"})
    @JoinTable(
            name = "MOVIE_DIRECTORS",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id")
    )
    private Set<Director> directors;

    @Getter @Setter
    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "MOVIE_WRITERS",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "writer_id")
    )
    private Set<Writer> writers;

    @Getter @Setter
    @OneToMany(mappedBy = "reviewedMovie", cascade = {CascadeType.MERGE})
    private Set<Review> reviews;

    public Movie(String imdbUrl) {
        // Arguments Check
        if(imdbUrl == null || !validateImdbLink(imdbUrl)) throw new InvalidIMDBLinkException(imdbUrl);
        this.imdbUrl = imdbUrl.trim().toLowerCase();
    }

    public Movie(String imdbUrl, MovieData data) {
        this(imdbUrl);
        // Initialize Fields
        this.setTitle(data.getTitle());
        this.setCoverUrl(data.getCoverURL());
        this.setRuntime(data.getRuntime());
        this.setReleaseDate(data.getProductionDetails().get(ProductionDetailKeys.RELEASE_DATE));
        this.setCountryOfOrigin(data.getProductionDetails().get(ProductionDetailKeys.COUNTRY_OF_ORIGIN));
        this.setLanguage(data.getProductionDetails().get(ProductionDetailKeys.LANGUAGE));
    }

    public boolean addWriter(Writer writer) {
        return getWriters().add(writer);
    }

    public boolean removeWriter(Writer writer) {
        return getWriters().remove(writer);
    }

    public boolean addDirector(Director director) {
        return getDirectors().add(director);
    }

    public boolean removeDirector(Director director) {
        return getDirectors().remove(director);
    }

    public boolean addReview(Review review) {
        return getReviews().add(review);
    }

    public boolean removeReview(Review review) {
        return getReviews().remove(review);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Movie)) return false;
        Movie movie = (Movie) o;
        return Objects.equal(imdbUrl, movie.imdbUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(imdbUrl);
    }

    /**
     * @author Grzegorz Lach
     * @since v0.0.1
     * @param link IMDB Link that will be validated for containing of "https://www.imdb.com/" at beginning.
     * @return validation result of provided link
     */
    public static boolean validateImdbLink(String link) {
        if(link != null && link.trim().toLowerCase().startsWith("https://www.imdb.com/")) {
            return true;
        } else {
            return false;
        }
    }
}
