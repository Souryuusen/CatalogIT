package com.souryuu.catalogit.repository;

import com.souryuu.catalogit.entity.database.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    boolean existsByTagNameIgnoreCase(String tagName);

    Tag findTagByTagNameIgnoreCase(String tagName);

}
