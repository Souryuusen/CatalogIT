package com.souryuu.catalogit.service;

import com.souryuu.catalogit.entity.database.Director;
import com.souryuu.catalogit.entity.database.Writer;
import com.souryuu.catalogit.repository.WriterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WriterService {

    private WriterRepository repository;

    public WriterService(WriterRepository repository) {
        this.repository = repository;
    }

    public Writer findOrCreateNew(String writerName) {
        return (!existsByNameIgnoreCase(writerName)) ? save(new Writer(writerName)) : getWriterByNameEqualsIgnoreCase(writerName);
    }

    public Writer save(Writer writer) {
        return this.repository.save(writer);
    }

    public List<Writer> findAll() {
        return repository.findAll();
    }

    public Writer getWriterByNameEqualsIgnoreCase(String name) {
        return this.repository.getWriterByNameEqualsIgnoreCase(name);
    }

    public boolean existsByNameIgnoreCase(String name) {
        return this.repository.existsByNameIgnoreCase(name);
    }


}
