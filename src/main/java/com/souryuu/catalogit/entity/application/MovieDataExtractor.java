package com.souryuu.catalogit.entity.application;

import com.souryuu.catalogit.entity.enums.MovieDataParameters;
import com.souryuu.catalogit.entity.interfaces.DataExtractor;
import com.souryuu.catalogit.entity.interfaces.ExtractedData;
import com.souryuu.catalogit.exception.NoDocumentException;
import com.souryuu.catalogit.exception.NoElementSelectedFromPageException;
import com.souryuu.catalogit.utility.ScraperUtility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.IOException;
import java.util.*;

@NoArgsConstructor
public class MovieDataExtractor implements DataExtractor {

    @NonNull
    @Getter
    private String movieLink;

    @Getter @Setter
    private MovieData extractedData;

    public MovieDataExtractor(String link) {
        if(!validSource(link)) {
            throw new IllegalArgumentException("Provided Link " + link + " Is Not A Valid Movie Link");
        }
        setMovieLink(link);
    }

    public void setMovieLink(String link) {
        if(link == null || link.equalsIgnoreCase("")) {
            throw new IllegalArgumentException("Provided Link " + link + " Is Not A Valid Movie Link");
        }
        if(link.contains("?")) {
            link = link.substring(0, link.indexOf("?"));
        }
        this.movieLink = link;
    }

    @Override
    public boolean validSource(String link) {
        final String VALID_START = "https://www.imdb.com/title/";
        if(link != null && link.trim().toLowerCase().startsWith(VALID_START) && link.length() > VALID_START.length()) {
            return true;
        } else {
            if(link == null || link.length() == 0) {
                throw new IllegalArgumentException("Validated URL Cannot Be Null Or Empty! Source:\t" + link);
            }
            return false;
        }
    }

    @Override
    public ExtractedData extractData() {
        Optional<Document> sourceDocumentOptional = getPageContent(getMovieLink());
        if(sourceDocumentOptional.isPresent()) {
            MovieData extractedData = new MovieData();
            //
            Document sourceDocument = sourceDocumentOptional.get();
            // Title Data
            extractedData.setDataParameter(MovieDataParameters.TITLE, extractTitle(sourceDocument));
            // Directors Data
            extractedData.setDataParameter(MovieDataParameters.DIRECTOR, extractDirectors(sourceDocument));
            // Writers Data
            extractedData.setDataParameter(MovieDataParameters.WRITER, extractWriters(sourceDocument));
            // Genres Data
            extractedData.setDataParameter(MovieDataParameters.GENRE, extractGenres(sourceDocument));
            // Tags Data
            extractedData.setDataParameter(MovieDataParameters.TAG, extractTags(sourceDocument));
            // Cover URL Data
            extractedData.setDataParameter(MovieDataParameters.COVER_URL, extractCoverUrl(sourceDocument));
            // Runtime Data
            extractedData.setDataParameter(MovieDataParameters.RUNTIME, extractRuntime(sourceDocument));
            // Release Date Data
            extractedData.setDataParameter(MovieDataParameters.RELEASE_DATE, extractReleaseDate(sourceDocument));
            // Country Of Origin Data
            extractedData.setDataParameter(MovieDataParameters.COUNTRY_OF_ORIGIN, extractCountryOfOrigin(sourceDocument));
            // Language Data
            extractedData.setDataParameter(MovieDataParameters.LANGUAGE, extractLanguage(sourceDocument));
            return extractedData;
        } else {
            throw new NoDocumentException("No Document Present From URL:\t" + getMovieLink());
        }
    }

    private String extractTitle(Document document) {
        // SELECTORS
        final String[] TITLE_SELECTORS = {
                "//*[@id=\"__next\"]/main/div/section[1]/section/div[3]/section/section/div[2]/div[1]/h1/span",
                "//*[@id=\"__next\"]/main/div/section[1]/section/div[3]/section/section/div[2]/div[1]/div"};
        // Retrieving Title Element -> Try Until Found Or Error
        Element titleElement = null;
        for(String selector : TITLE_SELECTORS) {
            titleElement = document.selectXpath(selector).first();
            if(titleElement != null) break;
        }
        // Parsing Of Returned Element Data
        if(titleElement != null) {
            String title = titleElement.ownText();
            if (title.startsWith("Original title:")) {
                title = title.replaceFirst("Original title:", "").trim();
            }
            // Return Value
            return title;
        } else {
            throw new NoElementSelectedFromPageException("Error Finding Element Containing Movie Title");
        }
    }

    private List<String> extractDirectors(Document document) {
        // Selection Of a elements contained in div/ul/li
        // that is direct sibling to element containing
        // text "Director" which is contained inside ul/li
        // with role attribute equals to "presentation"
        final String DIRECTORS_SELECTOR = "ul li[role=presentation] :contains(Director) + div ul li a";
        // Retrieval Of Elements
        Elements elements = document.select(DIRECTORS_SELECTOR);
        // Parsing DISTINCT Director Names Formatted To CamelCase From Obtained Elements
        List<String> parsedDirectorsList = elements.eachText().stream()
                .map(ScraperUtility::formatToCamelCase)
                .distinct()
                .toList();
        // TODO: Extend person information about director (age, production count etc...)
        return parsedDirectorsList;
    }

    private List<String> extractWriters(Document document) {
        // Selection Of a elements contained in div/ul/li
        // that is direct sibling to element containing
        // text "Writer" which is contained inside ul/li
        // with role attribute equals to "presentation"
        final String WRITERS_SELECTOR = "ul li[role=presentation] :contains(Writer) + div ul li a";
        List<String> parsedWriterList = document.select(WRITERS_SELECTOR).eachText().stream()
                .map(ScraperUtility::formatToCamelCase)
                .distinct()
                .toList();
        // TODO: Extend person information about writer (age, production count etc...)
        return parsedWriterList;
    }

    private List<String> extractGenres(Document document) {
        // Selectors
        final String START_POINT_SELECTOR = "<script id=\"__NEXT_DATA__\" type=\"application/json\">{\"props\":{\"pageProps\":{";
        final String BEGIN_PART = "},\"genres\":";
        final String END_PART = "\"plot\"";
        final String GENRE_IDENTIFIER = "\"id\":";
        //
        ArrayList<String> parsedGenreList = new ArrayList<>();
        //
        String genresDocumentPart = document.toString().substring(document.toString().indexOf(START_POINT_SELECTOR));
        genresDocumentPart = genresDocumentPart.substring(genresDocumentPart.indexOf(BEGIN_PART), genresDocumentPart.indexOf(END_PART));
        for(String genreString : genresDocumentPart.split(",")) {
            if(genreString.contains(GENRE_IDENTIFIER)) {
                String genre = genreString.substring(genreString.indexOf("\":\"")+3, genreString.lastIndexOf("\""));
                parsedGenreList.add(ScraperUtility.formatToCamelCase(genre));
            }
        }
        return parsedGenreList;
    }

    private List<String> extractTags(Document document) {
        // Selectors
        final String TAGS_URL_EXTENSION = "keywords";
        final String test = "div.ipc-metadata-list-summary-item__c > div > a";
        //
        ArrayList<String> parsedTags = new ArrayList<>();
        //
        Optional<Document> tagDocumentOptional = getPageContent(document.baseUri() + TAGS_URL_EXTENSION);
        if(tagDocumentOptional.isPresent()) {
            Document tagDocument = tagDocumentOptional.get();
            Elements tagElements = tagDocument.select(test);
            for(Element e : tagElements) {
                parsedTags.add(ScraperUtility.formatToCamelCase(e.ownText()));
            }
        }
        return parsedTags;
    }

    private String extractCoverUrl(Document document) {
        // Selectors
        final String MAIN_PAGE_COVER_ELEMENT_XPATH = "//*[@id=\"__next\"]/main/div/section[1]/section/div[3]/section/section/div[3]/div[1]/div[1]/div/a";
        final String COVER_ELEMENT_XPATH = "//*[@id=\"__next\"]/main/div[2]/div[3]/div/img";
        //
        String coverUrl = "";
        //
        Element coverElement = document.selectXpath(MAIN_PAGE_COVER_ELEMENT_XPATH).first();
        if(coverElement != null) {
            coverUrl = coverElement.attr("abs:href");
            Optional<Document> coverDocumentOptional = getPageContent(coverUrl);
            if (coverDocumentOptional.isPresent()) {
                Document coverDocument = coverDocumentOptional.get();
                Elements coverElements = coverDocument.selectXpath(COVER_ELEMENT_XPATH);
                for (Element e : coverElements) {
                    if (e.attr("data-image-id").endsWith("-curr")) {
                        coverUrl = e.attr("src");
                        break;
                    }
                }
            }
        }
        return coverUrl;
    }

    private String extractRuntime(Document document) {
        // Selectors
        final String TECHNICAL_EXTENSION = "technical";
        final String RUNTIME_XPATH = "//*[@id=\"__next\"]/main/div/section/div/section/div/div[1]/section[1]/div/ul/li[1]/div/ul/li/span[1]";
        //
        String runtime = "";
        //
        Optional<Document> technicalDocument = getPageContent(document.baseUri() + TECHNICAL_EXTENSION);
        if(technicalDocument.isPresent()) {
            Element test = technicalDocument.get().selectXpath(RUNTIME_XPATH).first();
            if(test != null) {
                runtime = test.ownText();
            }
        }
        return runtime;
    }

    private String extractReleaseDate(Document document) {
        // Selectors
        final String RELEASE_DATE_EXTENSION = "releaseinfo";
        final String RELEASE_DATE_COUNTRY_XPATH = "//*[@id=\"__next\"]/main/div/section/div/section/div/div[1]/section[1]/div[2]/ul/li/a[1]";
        final String RELEASE_DATE_XPATH = "//*[@id=\"__next\"]/main/div/section/div/section/div/div[1]/section[1]/div[2]/ul/li/div/ul/li/span";
        //
        String releaseDate = "";
        //
        Optional<Document> releaseDocumentOptional = getPageContent(document.baseUri() + RELEASE_DATE_EXTENSION);
        if(releaseDocumentOptional.isPresent()) {
            Document releaseDocument = releaseDocumentOptional.get();
            Element releaseDateCountryElement = releaseDocument.selectXpath(RELEASE_DATE_COUNTRY_XPATH).first();
            Element releaseDateElement = releaseDocument.selectXpath(RELEASE_DATE_XPATH).first();
            if(releaseDateElement != null && releaseDateCountryElement != null) {
                String parsedData = releaseDateCountryElement.ownText() + ", " + releaseDateElement.ownText();
                releaseDate = ScraperUtility.formatToCamelCase(parsedData);
            }
        }
        return releaseDate;
    }

    private String extractCountryOfOrigin(Document document) {
        // Selectors
        final String COUNTY_OF_ORIGIN_SELECTOR = "[href*=/search/title/?country_of_origin=]";
        //
        String countryOfOrigin = "";
        // Production Country Of Origin Selection
        Elements countryOfOriginElements = document.select(COUNTY_OF_ORIGIN_SELECTOR);
        countryOfOrigin = buildStringFromElementsText(countryOfOriginElements);
        return countryOfOrigin;
    }

    private String extractLanguage(Document document) {
        // Selectors
        final String LANGUAGE_SELECTOR = "[href*=/search/title?title_type=feature&primary_language]";
        //
        String language = "";
        // Production Language Selection
        Elements languageElements = document.select(LANGUAGE_SELECTOR);
        language = buildStringFromElementsText(languageElements);
        return language;
    }

    private String buildStringFromElementsText(Elements elements) {
        if(elements != null) {
            StringBuilder sb = new StringBuilder();
            for (Element e : elements) {
                if (sb.length() != 0) {
                    sb.append(", ");
                }
                sb.append(ScraperUtility.formatToCamelCase(e.ownText()));
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    public static Optional<Document> getPageContent(String url) {
        if(url == null || url.length() == 0) {
            throw new IllegalArgumentException("Page URL Cannot Be Null Or Empty! Source:\t" + url);
        }

        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36";

        Optional<Document> htmlContent = Optional.empty();
        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .referrer("www.google.com")
                    .followRedirects(true).execute();
            if(response.statusCode() == 200) {
                htmlContent = Optional.of(response.parse());
            }
        } catch (IOException e) {
            // TODO: Add Logic For Error Handling
            e.printStackTrace();
        }
        return htmlContent;
    }
}
