package com.souryuu.catalogit.service;

import com.souryuu.catalogit.entity.database.Movie;
import com.souryuu.catalogit.repository.MovieRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MovieService {

    private final MovieRepository repository;

    public MovieService(MovieRepository repository, DirectorService directorService, WriterService writerService, ReviewService reviewService) {
        this.repository = repository;
    }

    public Movie save(Movie movie) {
        return this.repository.save(movie);
    }

    public List<Movie> findAll() {
        return this.repository.findAll();
    }

    public List<Movie> findAllWithReviews() {
        List<Movie> movieList = this.findAll();
        movieList.forEach(movie -> Hibernate.initialize(movie.getReviews()));
        return movieList;
    }

    public List<Movie> findAllByTitleContaining(String partTitle) {
        return this.repository.findAllByTitleContainsIgnoreCase(partTitle);
    }

    public List<Movie> findAllByTitleContainingWithReviews(String partTitle) {
        List<Movie> movieList = findAllByTitleContaining(partTitle);
        movieList.forEach(m -> Hibernate.initialize(m.getReviews()));
        return movieList;
    }

    public List<Movie> findAllById(long id) {
        return this.repository.findAllByMovieIDEquals(id);
    }

    public List<Movie> findAllByIdWithReviews(long id) {
        List<Movie> movieList = findAllById(id);
        movieList.forEach(m -> Hibernate.initialize(m.getReviews()));
        return movieList;
    }

    public List<Movie> findAllByUrlContains(String partUrl) {
        return this.repository.findAllByImdbUrlContainsIgnoreCase(partUrl);
    }

    public List<Movie> findAllByUrlContainsWithReviews(String partUrl) {
        List<Movie> movieList = findAllByUrlContains(partUrl);
        movieList.forEach(m -> Hibernate.initialize(m.getReviews()));
        return movieList;
    }

    public Movie getMovieWithInitialization(long movieId) {
        Movie movie = this.repository.getMovieByMovieID(movieId);
        Hibernate.initialize(movie.getDirectors());
        Hibernate.initialize(movie.getWriters());
        Hibernate.initialize(movie.getReviews());
        Hibernate.initialize(movie.getGenres());
        Hibernate.initialize(movie.getTags());
        return movie;
    }

    public Movie getReferenceByID(long id){
        return this.repository.getReferenceById(id);
    }

    public Movie getMovieByIdWithReviews(long movieID) {
        Movie movie = this.repository.getMovieByMovieID(movieID);
        Hibernate.initialize(movie.getReviews());
        return movie;
    }

    public Movie getMovieByImdbUrl(String imdbUrl) {
        return this.repository.getMovieByImdbUrlEqualsIgnoreCase(imdbUrl);
    }

    public Movie getMovieByImdbUrlWithInitialization(String imdbUrl) {
        Movie movie = this.repository.getMovieByImdbUrlEqualsIgnoreCase(imdbUrl);
        Hibernate.initialize(movie.getDirectors());
        Hibernate.initialize(movie.getWriters());
        Hibernate.initialize(movie.getReviews());
        Hibernate.initialize(movie.getGenres());
        Hibernate.initialize(movie.getTags());
        return movie;
    }

    public Movie getMovieByTitleWithInitialization(String title) {
        Movie movie = this.repository.getMovieByTitleEqualsIgnoreCase(title);
        Hibernate.initialize(movie.getDirectors());
        Hibernate.initialize(movie.getWriters());
        Hibernate.initialize(movie.getReviews());
        Hibernate.initialize(movie.getGenres());
        Hibernate.initialize(movie.getTags());
        return movie;
    }

    public Set<Movie> getAllMovies() {
        return this.repository.findAll().stream().collect(Collectors.toSet());
    }

    public Set<Movie> getAllMoviesWithInitialization() {
        Set<Movie> moviesSet = this.repository.findAll().stream().collect(Collectors.toSet());
        for(Movie m : moviesSet) {
            Hibernate.initialize(m.getDirectors());
            Hibernate.initialize(m.getWriters());
            Hibernate.initialize(m.getReviews());
            Hibernate.initialize(m.getGenres());
            Hibernate.initialize(m.getTags());
        }
        return moviesSet;
    }

    public boolean deleteMovieById(long id) {
        this.repository.deleteById(id);
        return !existsById(id);
    }

    public boolean existsById(long id) {
        return this.repository.existsById(id);
    }
}
