package com.souryuu.imdbscrapper;

import com.souryuu.imdbscrapper.entity.MovieData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

public class ScrappingTask implements Callable<Optional<List<MovieData>>> {

    private final ArrayList<String> scrappingUrls = new ArrayList<>();

    public ScrappingTask(String url) {
        this.scrappingUrls.add(url);
    }

    public ScrappingTask(List<String> urls) {
        this.scrappingUrls.addAll(urls);
    }

    @Override
    public Optional<List<MovieData>> call() {
        ArrayList<MovieData> resultList = new ArrayList<>(scrappingUrls.size());
        scrappingUrls.forEach(url -> {
            MovieDataExtractor mde = new MovieDataExtractor(url);
            mde.extract();
            resultList.add(mde.getRetrievedData());
        });
        Optional<List<MovieData>> resultOptional = Optional.of(resultList);
        return resultOptional;
    }
}
