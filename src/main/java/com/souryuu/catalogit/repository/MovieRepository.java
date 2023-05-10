package com.souryuu.catalogit.repository;

import com.souryuu.catalogit.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    public Movie getMovieByImdbUrlEqualsIgnoreCase(String imdbUrl);

    @Query("SELECT m FROM Movie m " +
            "JOIN FETCH m.directors " +
            "JOIN FETCH m.writers " +
            "JOIN FETCH m.reviews " +
            "WHERE m.movieID = :id")
    public Movie getMovieByIdWithAll(@Param("id") long id);

}
