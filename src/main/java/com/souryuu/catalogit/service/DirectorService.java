package com.souryuu.catalogit.service;

import com.souryuu.catalogit.entity.database.Director;
import com.souryuu.catalogit.entity.database.Movie;
import com.souryuu.catalogit.repository.DirectorRepository;
import com.souryuu.catalogit.utility.ScraperUtility;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class DirectorService {

    private DirectorRepository repository;

    public DirectorService(DirectorRepository repository) {
        this.repository = repository;
    }

    //##################################################################################################################

    public Director findOrCreateNew(String name) {
        return (!existsByNameIgnoreCase(name)) ? save(new Director(name)) : getDirectorByNameEqualsIgnoreCase(name);
    }

    //##################################################################################################################

    public Director save(Director director) {
        return this.repository.save(director);
    }

    public Director getDirectorByNameEqualsIgnoreCase(String name) {
        return this.repository.getDirectorByNameEqualsIgnoreCase(name);
    }

    public List<Director> findAll() {
        return repository.findAll();
    }

    public List<Director> findDirectorsOfMovie(Movie movie) {
        List<Director> retrievedList = repository.findDirectorByDirectedMoviesContains(movie);
        retrievedList.stream().forEach(d -> Hibernate.initialize(d.getDirectedMovies()));
        return retrievedList;
    }

    public boolean existsByNameIgnoreCase(String name) {
        return this.repository.existsByNameIgnoreCase(name);
    }
}
