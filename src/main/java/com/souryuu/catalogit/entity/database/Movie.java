package com.souryuu.catalogit.entity.database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Objects;
import com.souryuu.catalogit.entity.application.MovieData;
import com.souryuu.catalogit.exception.InvalidIMDBLinkException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    @Column(name = "creation_time")
    private ZonedDateTime creationData;

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
        data.getCoverURL().ifPresent(this::setCoverUrl);
        data.getRuntime().ifPresent(this::setRuntime);
        data.getReleaseDate().ifPresent(this::setReleaseDate);
        data.getCountryOfOrigin().ifPresent(this::setCountryOfOrigin);
        data.getLanguage().ifPresent(this::setLanguage);
        data.getGenres().ifPresent(s -> s.stream().map(Genre::new).forEach(this::addGenre));
        data.getTags().ifPresent(t -> t.stream().map(Tag::new).forEach(this::addTag));
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
        if(getGenres() == null) {
            this.genres = new HashSet<>();
        }
        return getGenres().add(genre);
    }

    public boolean removeGenre(Genre genre) {
        if(getGenres() == null) {
            return false;
        }
        return getGenres().remove(genre);
    }

    public boolean addTag(Tag tag) {
        if(getTags() == null) {
            this.tags = new HashSet<>();
        }
        return getTags().add(tag);
    }

    public boolean removeTag(Tag tag) {
        return getTags().remove(tag);
    }

    /**
     * @author  Grzegorz Lach
     * @since v0.0.2
     * @return Summary of object fields values for export to *.txt feature
     */
    public String createExportSummary() {
        StringBuilder sb = new StringBuilder();
        // Movie Title
        sb.append(getTitle() + "\n");
        // Movie IMDB Url
        sb.append("\tURL: " + getImdbUrl() + "\n");
        // Movie Cover URL
        sb.append("\tCover URL: " + getCoverUrl() + "\n");
        // Movie Meta-Info
        sb.append("\tDetails: " + getRuntime() + ", " + getReleaseDate() + ", " + getCountryOfOrigin());
        // Movie Genre
        sb.append("\n\tGenre: ");
        var genreList = getGenres().stream().toList();
        for(int i = 0; i < genreList.size(); i++) {
            sb.append(genreList.get(i).getGenreName());
            if (i != genreList.size() - 1) sb.append(", ");
        }
        // Movie Tags
        sb.append("\n\tTags: ");
        var tagList = getTags().stream().toList();
        for(int i = 0; i < tagList.size(); i++) {
            sb.append(tagList.get(i).getTagName());
            if (i != tagList.size()-1) sb.append(", ");
        }
        // Movie Writers
        sb.append("\n\tWriters: ");
        var writerList = getWriters().stream().toList();
        for(int i = 0; i < writerList.size(); i++) {
            sb.append(writerList.get(i).getName());
            if (i != writerList.size()-1) sb.append(", ");
        }
        // Movie Directors
        sb.append("\n\tDirectors: ");
        var directorList = getDirectors().stream().toList();
        for(int i = 0; i < directorList.size(); i++) {
            sb.append(directorList.get(i).getName());
            if (i != directorList.size()-1) sb.append(", ");
        }
        // Movie Reviews
        sb.append("\n\tReviews:\n");
        var reviewList = getReviews().stream().toList();
        for(int i = 0; i < reviewList.size(); i++) {
            Review r = reviewList.get(i);
            sb.append("\t\tReview #" + (i+1) + ": "
                    + r.getCreationData().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss nnnn"))
                        + ", Rating: " + (r.getRating()/10.0) + "\n");
            sb.append("\t\t\t" + "\"" + r.getReview() + "\"\n");
        }
        return sb.toString();
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

    public static class MovieIdComparator implements Comparator<Movie> {
        @Override
        public int compare(Movie o1, Movie o2) {
            return (int) (o1.getMovieID() - o2.getMovieID());
        }
    }

    public static class MovieTitleComparator implements Comparator<Movie> {
        @Override
        public int compare(Movie o1, Movie o2) {
            if( o1.getTitle().equals(o2.getTitle()) )
                return 0;
            if( o1.getTitle() == null)
                return 1;
            if( o2.getTitle() == null )
                return -1;
            return o1.getTitle().compareTo(o2.getTitle());
        }
    }
}
