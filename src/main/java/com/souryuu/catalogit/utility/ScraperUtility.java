package com.souryuu.catalogit.utility;

import com.souryuu.imdbscrapper.*;
import com.souryuu.imdbscrapper.entity.MovieData;

public class ScraperUtility {

    public static MovieData scrapeData(String url) {
        MovieDataExtractor mde = new MovieDataExtractor(url);
        return mde.extract();
    }

    public static String formatToCamelCase(String input) {
        return MovieDataExtractor.formatToCamelCase(input);
    }

}
