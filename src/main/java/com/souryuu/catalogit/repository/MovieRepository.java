package com.souryuu.catalogit.repository;

import com.souryuu.catalogit.entity.database.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Movie getMovieByImdbUrlEqualsIgnoreCase(String imdbUrl);

    Movie getMovieByMovieID(long id);

    List<Movie> findAllByMovieIDEquals(long id);
    List<Movie> findAllByTitleContainsIgnoreCase(String title);
    List<Movie> findAllByImdbUrlContainsIgnoreCase(String link);
}
