package com.souryuu.catalogit.repository;

import com.souryuu.catalogit.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {
    List<Director> findDirectorByNameIsIgnoreCase(String name);
    int countDirectorByNameIsIgnoreCase(String name);
}
