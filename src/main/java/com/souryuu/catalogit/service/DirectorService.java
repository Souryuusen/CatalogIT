package com.souryuu.catalogit.service;

import com.souryuu.catalogit.entity.Director;
import com.souryuu.catalogit.entity.Movie;
import com.souryuu.catalogit.repository.DirectorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class DirectorService {

    private DirectorRepository repository;

    public DirectorService(DirectorRepository repository) {
        this.repository = repository;
    }

    public Director save(Director director) {
        return this.repository.save(director);
    }

    public List<Director> findAll() {
        return repository.findAll();
    }

    public List<Director> findDirectorsOfMovie(Movie movie) {
        return repository.findDirectorByDirectedMoviesContains(movie);
    }

    public boolean directorExistInDB(Director director) {
        return this.repository.countDirectorByNameIsIgnoreCase(director.getName()) > 0;
    }
}
