package com.souryuu.catalogit.entity.database;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class MovieTest {

    private final static String TEST_URL = "test_url";

    @Test
    public static void whenAllFieldsAreFilledDescriptionMatches() {
        Movie testMovie = new Movie();
        testMovie.setImdbUrl("test_url");
    }

}
