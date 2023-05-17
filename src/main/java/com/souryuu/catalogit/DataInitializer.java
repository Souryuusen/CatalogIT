package com.souryuu.catalogit;

import com.souryuu.catalogit.entity.*;
import com.souryuu.catalogit.service.DirectorService;
import com.souryuu.catalogit.service.MovieService;
import com.souryuu.catalogit.service.ReviewService;
import com.souryuu.catalogit.utility.ScraperUtility;
import jakarta.persistence.Persistence;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MovieService movieService;
    private final ReviewService reviewService;
    private final DirectorService directorService;

    public DataInitializer(MovieService movieService, ReviewService reviewService, DirectorService directorService) {
        this.movieService = movieService;
        this.reviewService = reviewService;
        this.directorService = directorService;
    }

    @Override
    public void run(String... args) throws Exception {
//        addRandomMovies(2);
//
//        try{
//            Thread.sleep(5000);
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }
//
//        fetchMovies();
    }

    private void addRandomMovies(int amount) {
        /*for(int i = 1; i <= amount; i++) {

            String link = "www.imdb.com/" + (i+50);
            Movie fetchedMovie = movieService.getMovieByImdbUrlWithAll(link);

            if(fetchedMovie == null) {
                Movie m = new MovieBuilderImplementation().withTitle("Test Movie: " + (i)).withReleaseDate("" + (1970 + i))
                        .withRuntime("1h " + i + "m " + i + "s").withLanguage("Polish")
                        .withImdbUrl(link).withCoverUrl("www.img.com/" + i).build();
                m.addDirector(new Director("John " + i + " Wicked"));
                m.addWriter(new Writer("Writer " + i + " Test"));
                Review review = new Review(55, "To jest Recenzja... " + i + "!!", ZonedDateTime.now(), m);
                Review review2 = new Review(55, "To jest Recenzja... " + i + 1 + "!!", ZonedDateTime.now(), m);
                Review review3 = new Review(55, "To jest Recenzja... " + i + 2 + "!!", ZonedDateTime.now(), m);
                m.addReview(review);
                m.addReview(review2);
                m.addReview(review3);
                movieService.save(m);
            } else {
                fetchedMovie.addDirector(new Director("John " + (i+100) + " Wicked"));
                movieService.save(fetchedMovie);
            }
//            reviewService.save(review);
        }*/
    }

    @Transactional
    private void fetchMovies() {
        Movie m = movieService.getMovieWithInitialization(1l);

        List<Director> directors = directorService.findDirectorsOfMovie(m);

        for(Director d : directors) {
            System.out.println(d);
            for(Movie movie : d.getDirectedMovies()) {
                System.out.println(movie);
            }
        }

        System.out.println("");
    }

    public static void main(String[] args) {
        String input = "\\\\(ancient\\\\)";
        String in2 = input.replaceAll("\\W","");
        String x = in2.replaceFirst(in2.substring(0,1), in2.substring(0,1).toUpperCase());
        String result = input.replace(in2, x);
        System.out.println(result);
        //        Arrays.asList(input.split(" "))
//                .stream().map(s -> s.toLowerCase())
//                .map(s ->
//                    s.replaceFirst(s.substring(0,1), s.substring(0,1).toUpperCase()))
//                .collect(Collectors.joining(" "));
    }
}
