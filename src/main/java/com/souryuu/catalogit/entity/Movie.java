package com.souryuu.catalogit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Objects;
import com.souryuu.catalogit.exception.InvalidIMDBLinkException;
import com.souryuu.imdbscrapper.entity.MovieData;
import com.souryuu.imdbscrapper.entity.ProductionDetailKeys;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    @JsonIgnoreProperties({"directors", "writers", "reviews", "genres", "tags"})
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
    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "MOVIE_GENRES",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    @Getter @Setter
    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "MOVIE_TAGS",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

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
        this.setGenres(data.getGenres().stream().map(Genre::new).collect(Collectors.toSet()));
        this.setTags(data.getTags().stream().map(Tag::new).collect(Collectors.toSet()));
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

    public boolean addGenre(Genre genre) {
        return getGenres().add(genre);
    }

    public boolean removeGenre(Genre genre) {
        return getGenres().remove(genre);
    }

    public boolean addTag(Tag tag) {
        return getTags().add(tag);
    }

    public boolean removeTag(Tag tag) {
        return getTags().remove(tag);
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

    public static class Builder {
        private String title;
        private String imdbUrl;
        private String coverUrl;
        private String runtime;
        private String language;
        private String releaseDate;
        private String countryOfOrigin;

        private Set<Director> directors;
        private Set<Writer> writers;
        private Set<Review> reviews;
        private Set<Genre> genres;
        private Set<Tag> tags;

        public Movie build() {
            Movie m = new Movie(imdbUrl);
            m.setTitle(this.title);
            m.setCoverUrl(this.coverUrl);
            m.setRuntime(this.runtime);
            m.setLanguage(this.language);
            m.setReleaseDate(this.releaseDate);
            m.setCountryOfOrigin(this.countryOfOrigin);
            m.setWriters(this.writers);
            m.setDirectors(this.directors);
            m.setReviews(this.reviews);
            m.setGenres(this.genres);
            m.setTags(this.tags);
            return m;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withImdbUrl(String imdbUrl) {
            this.imdbUrl = imdbUrl;
            return this;
        }

        public Builder withCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
            return this;
        }

        public Builder withRuntime(String runtime) {
            this.runtime = runtime;
            return this;
        }

        public Builder withLanguage(String language) {
            this.language = language;
            return this;
        }

        public Builder withReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public Builder withCountryOfOrigin(String countryOfOrigin) {
            this.countryOfOrigin = countryOfOrigin;
            return this;
        }

        public Builder withDirectors(Set<Director> directors) {
            this.directors = new HashSet<>();
            this.directors.addAll(directors);
            return this;
        }

        public Builder withWriters(Set<Writer> writers) {
            this.writers = new HashSet<>();
            this.writers.addAll(writers);
            return this;
        }

        public Builder withReviews(Set<Review> reviews) {
            this.reviews = new HashSet<>();
            this.reviews.addAll(reviews);
            return this;
        }

        public Builder withGenres(Set<Genre> genres) {
            this.genres = new HashSet<>();
            this.genres.addAll(genres);
            return this;
        }

        public Builder withTags(Set<Tag> tags) {
            this.tags = new HashSet<>();
            this.tags.addAll(tags);
            return this;
        }

    }
}
