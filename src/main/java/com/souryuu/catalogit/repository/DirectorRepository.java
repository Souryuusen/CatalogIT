package com.souryuu.catalogit.repository;

import com.souryuu.catalogit.entity.database.Director;
import com.souryuu.catalogit.entity.database.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {

    Director getDirectorByNameEqualsIgnoreCase(String name);

    List<Director> findDirectorByDirectedMoviesContains(Movie movie);

    boolean existsByNameIgnoreCase(String name);
}
