package com.souryuu.catalogit.repository;

import com.souryuu.catalogit.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    public Movie getMovieByImdbUrlEqualsIgnoreCase(String imdbUrl);

    public Movie getMovieByMovieID(long id);

}
