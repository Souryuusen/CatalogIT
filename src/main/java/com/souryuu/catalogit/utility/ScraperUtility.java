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
                .map(s -> {
                    String tmp = s.replaceAll("\\W", "");
                    String tmp2 = tmp.replaceFirst(tmp.substring(0,1), tmp.substring(0,1).toUpperCase());
                    return s.replace(tmp, tmp2);
                }).collect(Collectors.joining(" "));
    }

}
