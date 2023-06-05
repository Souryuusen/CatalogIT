package com.souryuu.catalogit.repository;

import com.souryuu.catalogit.entity.database.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    boolean existsByGenreNameIgnoreCase(String genreName);

    Genre findGenreByGenreNameIgnoreCase(String genreName);

}
