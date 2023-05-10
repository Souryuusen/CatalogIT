package com.souryuu.catalogit;

import com.souryuu.catalogit.entity.*;
import com.souryuu.catalogit.service.DirectorService;
import com.souryuu.catalogit.service.MovieService;
import com.souryuu.catalogit.service.ReviewService;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

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
        addRandomMovies(2);

        try{
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        fetchMovies();
    }

    private void addRandomMovies(int amount) {
        for(int i = 1; i <= amount; i++) {
            Movie m = new MovieBuilderImplementation().withTitle("Test Movie: " + (i)).withReleaseDate("" + (1970+i))
                            .withRuntime("1h " + i + "m " + i + "s").withLanguage("Polish")
                            .withImdbUrl("www.imdb.com/" + i).withCoverUrl("www.img.com/" + i).build();
            m.addDirector(new Director("John " + i + " Wicked"));
            m.addWriter(new Writer("Writer " + i + " Test"));
            movieService.save(m);
            Review review = new Review(55, "To jest Recenzja... " + i + "!!", ZonedDateTime.now(), m);
            reviewService.save(review);
        }
    }

    @Transactional
    private void fetchMovies() {
        Movie m = movieService.getMovieWithAll(1l);

        List<Director> directors = directorService.findDirectorsOfMovie(m);

        for(Director d : directors) System.out.println(d);
        System.out.println(m);

        System.out.println("");
    }
}
