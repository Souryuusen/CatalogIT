package com.souryuu.catalogit.entity.application;

import com.souryuu.catalogit.entity.database.Movie;
import com.souryuu.catalogit.entity.enums.MovieDataParameters;
import com.souryuu.catalogit.entity.interfaces.ExtractedData;
import com.souryuu.catalogit.entity.interfaces.ExtractedDataParameter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class MovieData implements ExtractedData {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private Map<ExtractedDataParameter, List<String>> rawExtractedData = new HashMap<>();

    @Override
    public Map<ExtractedDataParameter, List<String>> retrieveExtractedData() {
        return getRawExtractedData();
    }

    public final void setDataParameter(MovieDataParameters parameter, String value) {
        if(getRawExtractedData().get(parameter) == null) {
            getRawExtractedData().put(parameter, new ArrayList<>());
        }
        List<String> parameterValues = getRawExtractedData().get(parameter);
        parameterValues.add(value);
        getRawExtractedData().put(parameter, parameterValues);
    }

    public final void setDataParameter(MovieDataParameters parameter, List<String> values) {
        if(getRawExtractedData().get(parameter) == null) {
            getRawExtractedData().put(parameter, new ArrayList<>());
        }
        List<String> parameterValues = getRawExtractedData().get(parameter);
        parameterValues.addAll(values);
        getRawExtractedData().put(parameter, parameterValues);
    }

    public String getTitle() {
        return getRawExtractedData().get(MovieDataParameters.TITLE).get(0);
    }

    public Optional<String> getCoverURL() {
        return Optional.of(getRawExtractedData().get(MovieDataParameters.COVER_URL).get(0));
    }

    public Optional<String> getRuntime() {
        return Optional.of(getRawExtractedData().get(MovieDataParameters.RUNTIME).get(0));
    }

    public Optional<String> getReleaseDate() {
        return Optional.of(getRawExtractedData().get(MovieDataParameters.RELEASE_DATE).get(0));
    }

    public Optional<String> getCountryOfOrigin() {
        return Optional.of(getRawExtractedData().get(MovieDataParameters.COUNTRY_OF_ORIGIN).get(0));
    }

    public Optional<String> getLanguage() {
        return Optional.of(getRawExtractedData().get(MovieDataParameters.LANGUAGE).get(0));
    }

    public Optional<List<String>> getDirectors() {
        return Optional.of(getRawExtractedData().getOrDefault(MovieDataParameters.DIRECTOR, new ArrayList<>()));
    }

    public Optional<List<String>> getWriters() {
        return Optional.of(getRawExtractedData().getOrDefault(MovieDataParameters.WRITER, new ArrayList<>()));
    }

    public Optional<List<String>> getGenres() {
        return Optional.of(getRawExtractedData().getOrDefault(MovieDataParameters.GENRE, new ArrayList<>()));
    }

    public Optional<List<String>> getTags() {
        return Optional.of(getRawExtractedData().getOrDefault(MovieDataParameters.TAG, new ArrayList<>()));
    }
}
