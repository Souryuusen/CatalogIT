package com.souryuu.catalogit.entity;

import com.souryuu.catalogit.entity.interfaces.MovieBuilder;
import lombok.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class MovieBuilderImplementation implements MovieBuilder {
    @Setter(AccessLevel.PRIVATE) @Getter(AccessLevel.PRIVATE)
    private String title;
    @Setter(AccessLevel.PRIVATE) @Getter(AccessLevel.PRIVATE)
    private String releaseDate;
    @Setter(AccessLevel.PRIVATE) @Getter(AccessLevel.PRIVATE)
    private String language;
    @Setter(AccessLevel.PRIVATE) @Getter(AccessLevel.PRIVATE)
    private String runtime;
    @Setter(AccessLevel.PRIVATE) @Getter(AccessLevel.PRIVATE)
    private String imdbUrl;
    @Setter(AccessLevel.PRIVATE) @Getter(AccessLevel.PRIVATE)
    private String coverUrl;
    @Setter(AccessLevel.PRIVATE) @Getter(AccessLevel.PRIVATE)
    Set<Director> directorSet = new HashSet<>();

    @Override
    public MovieBuilder withTitle(String title) {
        if(title != null && title.length() > 0) setTitle(title);
        return this;
    }

    @Override
    public MovieBuilder withDirectors(Director... directors) {
        Arrays.asList(directors).stream().filter(director -> director != null).forEach(director -> directorSet.add(director));
        return this;
    }

    @Override
    public MovieBuilder withReleaseDate(String releaseDate) {
        if(releaseDate != null && releaseDate.length() > 0) setReleaseDate(releaseDate);
        return this;
    }

    @Override
    public MovieBuilder withLanguage(String language) {
        if(language != null && language.length() > 0) setLanguage(language);
        return this;
    }

    @Override
    public MovieBuilder withRuntime(String runtime) {
        if(runtime != null && runtime.length() > 0) setRuntime(runtime);
        return this;
    }

    @Override
    public MovieBuilder withImdbUrl(String imdbUrl) {
        if(imdbUrl != null && imdbUrl.length() > 0) setImdbUrl(imdbUrl);
        return this;
    }

    @Override
    public MovieBuilder withCoverUrl(String coverUrl) {
        if(coverUrl != null && coverUrl.length() > 0) setCoverUrl(coverUrl);
        return this;
    }

    @Override
    public Movie build() {
        Movie m = new Movie();
        m.setTitle(getTitle());
        m.setLanguage(getLanguage());
        m.setImdbUrl(getImdbUrl());
        m.setRuntime(getRuntime());
        m.setReleaseDate(getReleaseDate());
        m.setCoverUrl(getCoverUrl());
        m.setDirectors(directorSet);
        return m;
    }
}
