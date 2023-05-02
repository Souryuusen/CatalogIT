package com.souryuu.catalogit.service;

import com.souryuu.catalogit.entity.Writer;
import com.souryuu.catalogit.repository.WriterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WriterService {

    private WriterRepository repository;

    public WriterService(WriterRepository repository) {
        this.repository = repository;
    }

    public Writer save(Writer writer) {
        return this.repository.save(writer);
    }

    public List<Writer> findAll() {
        return repository.findAll();
    }

    public boolean writerExistInDB(Writer writer) {
        return this.repository.countWriterByNameIsIgnoreCase(writer.getName()) > 0;
    }


}
