package com.souryuu.catalogit.service;

import com.souryuu.catalogit.entity.Director;
import com.souryuu.catalogit.repository.DirectorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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

    public boolean directorExistInDB(Director director) {
        return this.repository.countDirectorByNameIsIgnoreCase(director.getName()) > 0;
    }
}
