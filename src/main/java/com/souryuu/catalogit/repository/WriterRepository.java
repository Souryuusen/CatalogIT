package com.souryuu.catalogit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.souryuu.catalogit.entity.Writer;
import org.springframework.stereotype.Repository;

@Repository
public interface WriterRepository extends JpaRepository<Writer, Long> {

    int countWriterByNameIsIgnoreCase(String name);

}
