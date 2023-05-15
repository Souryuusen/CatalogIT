package com.souryuu.catalogit.service;

import com.souryuu.catalogit.entity.Movie;
import com.souryuu.catalogit.entity.Writer;
import com.souryuu.catalogit.repository.MovieRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.List;

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
        List<Movie> movieList = this.repository.findAll();
        movieList.forEach(movie -> Hibernate.initialize(movie.getReviews()));
        return movieList;
    }

    public Movie getMovieWithInitialization(long movieId) {
        Movie movie = this.repository.getMovieByMovieID(movieId);
        Hibernate.initialize(movie.getDirectors());
        Hibernate.initialize(movie.getWriters());
        Hibernate.initialize(movie.getReviews());
        return movie;
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
        return movie;
    }
}
