package com.souryuu.catalogit.repository;

import com.souryuu.catalogit.entity.database.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import com.souryuu.catalogit.entity.database.Writer;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WriterRepository extends JpaRepository<Writer, Long> {

    Writer getWriterByNameEqualsIgnoreCase(String name);

    int countWriterByNameIsIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Writer> findWriterByWrittenMoviesContains(Movie Movie);
}
