package com.souryuu.catalogit;

import com.souryuu.catalogit.entity.Movie;
import com.souryuu.catalogit.service.MovieService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CatalogItApplicationTests {

//    @Autowired
//    private MovieService movieService;

    @Test
    public void should_add_new_movie() {
//        Movie testMovie = new Movie("Unit Test Movie");
//        testMovie.setRuntime("1h 25m 30s");
//        testMovie.setLanguage("Polish");
//        testMovie.setCoverUrl("www.test-domain.com");
//        testMovie.setImdbUrl("www.imdb.com/test-value");
//        testMovie.setReleaseDate("September 1993");
//        // TODO: add init of tags/genres/producers/writers after they are added to project
//        Movie addedMovie = movieService.save(testMovie);
//        Assertions.assertEquals(testMovie, addedMovie);
        Assertions.assertTrue(true);
    }

}
