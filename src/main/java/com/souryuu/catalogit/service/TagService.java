package com.souryuu.catalogit.service;

import com.souryuu.catalogit.entity.database.Tag;
import com.souryuu.catalogit.repository.TagRepository;
import org.springframework.stereotype.Service;

@Service
public class TagService {

    private final TagRepository repository;

    public TagService(TagRepository repository) {
        this.repository = repository;
    }

    public Tag findOrCreateNew(String tagName) {
        return (!existsByTagName(tagName)) ? save(new Tag(tagName)) : findTagByTagName(tagName);
    }

    public Tag save(Tag tag) {
        return this.repository.save(tag);
    }

    public boolean existsByTagName(String tagName) {
        return this.repository.existsByTagNameIgnoreCase(tagName);
    }

    public Tag findTagByTagName(String tagName) {
        return this.repository.findTagByTagNameIgnoreCase(tagName);
    }
}
