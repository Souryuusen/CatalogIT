package com.souryuu.catalogit.entity.application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MovieDataExtractorTest {

    MovieDataExtractor test;

    @BeforeEach
    public void beforeEach() {
        test = new MovieDataExtractor();
    }

    @Test
    public void whenLinkIsNull_thenValidLinkThrowIllegalExceptionArgument() {
        assertThrowsExactly(IllegalArgumentException.class, () -> test.validSource(null));
        assertThrowsExactly(IllegalArgumentException.class, () -> new MovieDataExtractor(null));
    }

    @Test
    public void whenLinkIsValid_thenReturnTrue() {
        String testUrl = "https://www.imdb.com/title/tt0120611/";
        assertTrue(test.validSource(testUrl));
    }

    @Test
    public void whenLinkIsNotValid_thenReturnFalse() {
        String[] testUrls = {"htps://www.imdb.com/title/tt0120611/" ,"https://ww.imdb.com/title/tt0120611/",
                "https://www.imdb/title/tt0120611/", "https://www.imdb.com/titl/tt0120611/", "https://www.imdb.com/title/"};
        for(String testedValue : testUrls) {
            assertFalse(test.validSource(testedValue), "Url:\t" + testedValue + "\tReturned True Instead Of False!");
        }
    }

}
