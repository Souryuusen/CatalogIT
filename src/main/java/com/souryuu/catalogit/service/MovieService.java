package com.souryuu.catalogit.service;

import com.souryuu.catalogit.entity.Movie;
import com.souryuu.catalogit.entity.Writer;
import com.souryuu.catalogit.repository.MovieRepository;
import jakarta.transaction.Transactional;
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
        // Persist Movie
        return this.repository.save(movie);
    }

    public List<Movie> findAll() {
        return this.repository.findAll();
    }

    public Movie getMovieWithAll(long movieId) {
        return this.repository.getMovieByIdWithAll(movieId);
    }

    public Movie getMovieByImdbUrl(String imdbUrl) {
        return this.repository.getMovieByImdbUrlEqualsIgnoreCase(imdbUrl);
    }

}
