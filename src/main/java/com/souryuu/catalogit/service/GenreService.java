package com.souryuu.catalogit.service;

import com.souryuu.catalogit.entity.Genre;
import com.souryuu.catalogit.repository.GenreRepository;
import org.springframework.stereotype.Service;

@Service
public class GenreService {

    private final GenreRepository repository;

    public GenreService(GenreRepository repository) {
        this.repository = repository;
    }

    public Genre save(Genre genre) {
        return this.repository.save(genre);
    }

    public boolean existsByGenreName(String genreName) {
        return this.repository.existsByGenreNameIgnoreCase(genreName);
    }

    public Genre findGenreByGenreName(String genreName) {
        return this.repository.findGenreByGenreNameIgnoreCase(genreName);
    }

}
