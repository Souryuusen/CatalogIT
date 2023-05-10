package com.souryuu.catalogit.entity.interfaces;

import com.souryuu.catalogit.entity.Director;
import com.souryuu.catalogit.entity.Movie;
import com.souryuu.catalogit.entity.Review;
import com.souryuu.catalogit.entity.Writer;

public interface MovieBuilder {
    MovieBuilder withTitle(String title);
    MovieBuilder withDirectors(Director... directors);
    MovieBuilder withWriters(Writer... writers);
    MovieBuilder withReviews(Review... reviews);
    MovieBuilder withReleaseDate(String releaseDate);
    MovieBuilder withRuntime(String runtime);
    MovieBuilder withLanguage(String language);
    MovieBuilder withImdbUrl(String imdbUrl);
    MovieBuilder withCoverUrl(String coverUrl);
    Movie build();
}
