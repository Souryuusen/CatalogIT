package com.souryuu.catalogit;

import com.souryuu.catalogit.entity.Director;
import com.souryuu.catalogit.entity.Movie;
import com.souryuu.catalogit.entity.MovieBuilderImplementation;
import com.souryuu.catalogit.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MovieService movieService;

    public DataInitializer(MovieService movieService) {
        this.movieService = movieService;
    }

    @Override
    public void run(String... args) throws Exception {
        addRandomMovies(5);
    }

    private void addRandomMovies(int amount) {
        for(int i = 1; i <= amount; i++) {
            Movie m = new MovieBuilderImplementation().withTitle("Test Movie: " + (i)).withReleaseDate("" + (1970+i))
                            .withRuntime("1h " + i + "m " + i + "s").withLanguage("Polish")
                            .withImdbUrl("www.imdb.com/" + i).withCoverUrl("www.img.com/" + i)
                            .withDirectors(new Director("John " + i + " Wicked")).build();
            System.out.println(m);
//            movieService.save(m);
        }
    }
}
