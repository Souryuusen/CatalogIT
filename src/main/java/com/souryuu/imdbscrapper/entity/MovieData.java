package com.souryuu.imdbscrapper.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.souryuu.imdbscrapper.interfaces.ProductionData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieData implements ProductionData {

    private String title = "";
    private String coverURL = "";
    private String runtime = "";
    private List<String> directors = new ArrayList<>();
    private List<String> writers = new ArrayList<>();
    private List<String> genres = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private Map<ProductionDetailKeys, String> productionDetails = new HashMap<>();

    public MovieData() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addDirector(String director) {
        this.directors.add(director);
    }

    public void addWriters(String writer) {
        this.writers.add(writer);
    }

    public void addGenre(String genre) {
        this.genres.add(genre);
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public List<String> getDirectors() {
        return this.directors;
    }

    public List<String> getWriters() {
        return this.writers;
    }

    public List<String> getGenres() {
        return this.genres;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public void setProductionDetails(Map<ProductionDetailKeys, String> map) {
        this.productionDetails.putAll(map);
    }

    public Map<ProductionDetailKeys, String> getProductionDetails() {
        return this.productionDetails;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String obtainJSONString() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        om.enable(SerializationFeature.INDENT_OUTPUT);
        return om.writeValueAsString(this);
    }
}
