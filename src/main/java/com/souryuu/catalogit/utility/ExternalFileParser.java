package com.souryuu.catalogit.utility;

import com.souryuu.imdbscrapper.MovieDataExtractor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Data @NoArgsConstructor
public class ExternalFileParser {

    public void parseTxtFile() {

    }

    public static void main(String[] args) {
        ExternalFileParser efp = new ExternalFileParser();
        String test = "Dhoom\t\t\t\t\t\t8\t\thttps://www.imdb.com/title/tt0422091/\tindyjska zabójcza broń";
        List<String> movieData = efp.parseMovieLine(test);
        if(movieData.size() == 4) {
            MovieDataExtractor mde = new MovieDataExtractor(movieData.get(2).trim());
        }
        System.out.println(movieData);

    }

    public List<String> parseMovieLine(String inputString) {
        return Arrays.stream(inputString.split("\t")).map(s -> s.trim()).filter(s -> !s.equalsIgnoreCase(""))
                .toList();
    }

}
