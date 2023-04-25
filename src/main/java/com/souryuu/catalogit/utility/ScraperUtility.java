package com.souryuu.catalogit.utility;

import com.souryuu.imdbscrapper.*;
import com.souryuu.imdbscrapper.entity.MovieData;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ScraperUtility {

    @SneakyThrows
    public static MovieData scrapeData(String url) {
        MovieDataExtractor mde = new MovieDataExtractor(url);
        return mde.extract();
    }

    public static String formatToCamelCase(String input) {
        return Arrays.asList(input.split(" "))
                .stream().map(s -> s.toLowerCase())
                .map(s -> s.replaceFirst(s.substring(0,1), s.substring(0,1).toUpperCase()))
                .collect(Collectors.joining(" "));
    }

}
