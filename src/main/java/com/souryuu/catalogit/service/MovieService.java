package com.souryuu.catalogit.service;

import com.souryuu.catalogit.entity.Movie;
import com.souryuu.catalogit.repository.MovieRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MovieService {

    private final MovieRepository repository;

    public MovieService(MovieRepository repository) {
        this.repository = repository;
    }

    public Movie save(Movie movie) {
        return this.repository.save(movie);
    }

}
