package com.souryuu.imdbscrapper;

import com.souryuu.imdbscrapper.entity.MovieData;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.*;

public class IMDBScrapper {

    private boolean processingDone = false;
    private File jobFile;
    private List<String> scrapeURLS = new ArrayList<>();
    private List<MovieData> scrapedData = new ArrayList<>();


    public IMDBScrapper(File jobFile) {
        this.jobFile = jobFile;
    }

    public File getJobFile() {
        return jobFile;
    }

    public void setJobFile(File jobFile) {
        this.jobFile = jobFile;
    }

    public List<String> getScrapeURLS() {
        return scrapeURLS;
    }

    public void setScrapeURLS(List<String> scrapeURLS) {
        this.scrapeURLS = scrapeURLS;
    }

    public List<MovieData> getScrapedData() {
        return scrapedData;
    }

    public void setScrapedData(List<MovieData> scrapedData) {
        this.scrapedData = scrapedData;
    }

    public boolean isProcessingDone() {
        return processingDone;
    }

    public void setProcessingDone(boolean processingDone) {
        this.processingDone = processingDone;
    }

    private void loadJobFile(){
        try {
            Scanner sc = new Scanner(getJobFile());
            while (sc.hasNext()) scrapeURLS.add(sc.nextLine());
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void multithreadedScrape(int threadCount) {
        loadJobFile();
        // Thread Job Parameters - amount of operations and divided urls
        List<List<String>> threadLinks = new ArrayList<>(threadCount);
        for(int i = 0; i < threadCount; i++) threadLinks.add(new ArrayList<>());

        int index = 0;
        for(int i  = 0; i < getScrapeURLS().size(); i++) {
            threadLinks.get(index).add(getScrapeURLS().get(i));
            index++;
            if(index == threadCount) index = 0;
        }

        ArrayList<Callable> tasks = new ArrayList<>(threadCount);
        for(int i = 0; i < threadCount; i++) {
            tasks.add(new ScrappingTask(threadLinks.get(i)));
        }

        List<Future<Optional<List<MovieData>>>> futures = new ArrayList<>();
        // Launching processing of jobs
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for(Callable c : tasks) {
            futures.add(executor.submit(c));
        }
        for(Future<Optional<List<MovieData>>> future : futures) {
            try {
                if(future.get().isPresent()) {
                    List<MovieData> retrievedData = future.get().get();
                    retrievedData.stream().forEach(md -> scrapedData.add(md));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        setProcessingDone(true);
    }

}
