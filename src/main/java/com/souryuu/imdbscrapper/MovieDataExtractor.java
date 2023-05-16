package com.souryuu.imdbscrapper;

import com.souryuu.imdbscrapper.entity.MovieData;
import com.souryuu.imdbscrapper.entity.ProductionDetailKeys;
import com.souryuu.imdbscrapper.exceptions.NoDocumentPresentException;
import com.souryuu.imdbscrapper.interfaces.Extractable;
import com.souryuu.imdbscrapper.interfaces.ProductionData;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.stream.Collectors;

public class MovieDataExtractor implements Extractable {

    MovieData retrievedData = null;

    private Document content;

    private String contentURL = "";

    public MovieDataExtractor(String url) {
        this.contentURL = url;
        while(this.contentURL.toLowerCase().contains("?")) {
            this.contentURL = this.contentURL.substring(0, this.contentURL.indexOf("?"));
        }
    }

    public MovieData getRetrievedData() {
        return retrievedData;
    }

    public Document getContent() {
        return content;
    }

    public String getContentURL() {
        return contentURL;
    }

    /**
     * @author Grzegorz Lach
     * @return  ProductionData Object that contains all basic information about movie (in this case it is MovieData)
     */
    public MovieData extract() {
        Optional<Document> retrievedDocument = PageContentRetriever.retrievePageContent(getContentURL());
        // Verification Of Retrieved Document
        if(!retrievedDocument.isPresent())
            throw new NoDocumentPresentException("No Document Created From Parsing " + getContentURL() + " URL.");
        content=retrievedDocument.get();
        // Start Of Retrieval (Scrapping) Process
        extractData(getContent());

        return retrievedData;
    }

    /**
     * @author Grzegorz Lach
     * @since 0.0.1
     * @param document JSOUP Document containing html page from IMDB url link
     * @return String value that represents full title of production
     */
    private String retrieveTitleData(Document document) {
        final String TITLE_ELEMENT_XPATH_ALT = "//*[@id=\"__next\"]/main/div/section[1]/section/div[3]/section/section/div[2]/div[1]/h1/span";
        final String TITLE_ELEMENT_XPATH = "//*[@id=\"__next\"]/main/div/section[1]/section/div[3]/section/section/div[2]/div[1]/div";
        // HTML Element JSOUP Retrieval
        Element titleElement = document.selectXpath(TITLE_ELEMENT_XPATH).first();
        if(titleElement == null) {
            titleElement = document.selectXpath(TITLE_ELEMENT_XPATH_ALT).first();
        }
        String title = titleElement.ownText();
        if(title.startsWith("Original title:")) {
            title = title.replaceFirst("Original title:", "").trim();
        }
        // Return Value
        return title;
    }

    /**
     * @author Grzegorz Lach
     * @param document JSOUP Document containing html page from IMDB url link
     * @return List of persons mentioned on directors part of IMDB HTML page content
     */
    private Set<String> retrieveDirectors(Document document) {
        final String DIRECTORS_ELEMENT_XPATH = "//*[@id=\"__next\"]/main/div/section[1]/section/div[3]/section/section/div[3]/div[2]/div[1]/section/div[2]/div/ul/li[1]/div/ul/li/a";

        Set<String> directorSet = new HashSet<>();
        Elements elements = document.selectXpath(DIRECTORS_ELEMENT_XPATH);
        for(Element e : elements) {
            // TODO: Extend person information about director (age, production count etc...)
            directorSet.add(e.ownText());
        }
        return directorSet;
    }

    /**
     * @author Grzegorz Lach
     * @param document JSOUP Document containing html page from IMDB url link
     * @return List of persons mentioned on writers part of IMDB HTML page content
     */
    private Set<String> retrieveWriters(Document document) {
        final String WRITERS_ELEMENT_XPATH = "//*[@id=\"__next\"]/main/div/section[1]/section/div[3]/section/section/div[3]/div[2]/div[1]/section/div[2]/div/ul/li[2]/div/ul/li/a";

        Set<String> writerSet = new HashSet<>();
        Elements elements = document.selectXpath(WRITERS_ELEMENT_XPATH);
        for(Element e : elements) {
            // TODO: Extend person information about writer (age, production count etc...)
            writerSet.add(e.ownText());
        }
        return writerSet;
    }

    /**
     * @author Grzegorz Lach
     * @param document JSOUP Document containing html page from IMDB url link
     * @return List genres mentioned on genre scroller part of IMDB HTML page content
     */
    private Set<String> retrieveGenres(Document document) {
        Set<String> genresSet = new HashSet<>();
        String s = document.toString().substring(document.toString().indexOf("<script id=\"__NEXT_DATA__\" type=\"application/json\">{\"props\":{\"pageProps\":{"));
        s = s.substring(s.indexOf("},\"genres\":"), s.indexOf("\"plot\""));
        for(String s1 : s.split(",")) {
            if(s1.contains("\"id\":")) {
                String tmp = s1.substring(s1.indexOf("\":\"")+3, s1.lastIndexOf("\""));
                genresSet.add(tmp);
            }
        }
        return genresSet;
    }

    /**
     * @author Grzegorz Lach
     * @param document JSOUP Document containing html page from IMDB url link
     * @return Optional URL link of IMDB page production cover
     */
    private Optional<String> retrieveProductionCoverURL(Document document) {
        final String MAIN_PAGE_COVER_ELEMENT_XPATH = "//*[@id=\"__next\"]/main/div/section[1]/section/div[3]/section/section/div[3]/div[1]/div[1]/div/a";
        final String COVER_ELEMENT_XPATH = "//*[@id=\"__next\"]/main/div[2]/div[3]/div/img";

        Optional<String> optionalCoverURL = Optional.empty();

        Element coverElement = document.selectXpath(MAIN_PAGE_COVER_ELEMENT_XPATH).first();

        String coverURL = coverElement.attr("abs:href");
        Optional<Document> coverDocumentOptional = PageContentRetriever.retrievePageContent(coverURL);
        if(coverDocumentOptional.isPresent()) {
            Document coverDocument = coverDocumentOptional.get();
            Elements coverElements = coverDocument.selectXpath(COVER_ELEMENT_XPATH);
            for(Element e : coverElements) {
                if(e.attr("data-image-id").endsWith("-curr")) {
                    coverElement = e;
                    break;
                }
            }
            String retrievedURL = coverElement.attr("src");
            optionalCoverURL = Optional.of(retrievedURL);
        }
        return optionalCoverURL;
    }

    /**
     * @author Grzegorz Lach
     * @since 0.0.1
     * @param document JSOUP Document containing html page from IMDB url link
     * @return List of all tags from given production on IMDB url link
     */
    private Optional<List<String>> retrieveAllTags(Document document) {
        final String TAGS_URL_EXTENSION = "keywords";
        final String TAGS_ELEMENTS_XPATH = "//*[@id=\"keywords_content\"]/table/tbody/tr/td[1]/div[1]/a | //*[@id=\"keywords_content\"]/table/tbody/tr/td[2]/div[1]/a";
        Optional<List<String>> resultOptional = Optional.empty();

        Optional<Document> tagDocumentOptional = PageContentRetriever.retrievePageContent(document.baseUri() + TAGS_URL_EXTENSION);
        if(tagDocumentOptional.isPresent()) {
            Document tagDocument = tagDocumentOptional.get();
            Elements tagElements = tagDocument.selectXpath(TAGS_ELEMENTS_XPATH);
            ArrayList<String> tagList = new ArrayList<>(tagElements.size());
            for(Element e : tagElements) {
                tagList.add(formatToCamelCase(e.ownText()));
            }
            resultOptional = Optional.of(tagList);
        }
        return resultOptional;
    }

    /**
     * @author Grzegorz Lach
     * @since 0.0.1
     * @param document JSOUP Document containing html page from IMDB url link
     * @return Map of production deteail (Release Date, Country Of Origin, Language) for provided IMDB url document
     */
    private Optional<Map<ProductionDetailKeys, String>> retrieveProductionDetails(Document document) {
        final String RELEASE_DATE_EXTENSION = "releaseinfo";
        final String RELEASE_DATE_COUNTRY_XPATH = "//*[@id=\"__next\"]/main/div/section/div/section/div/div[1]/section[1]/div[2]/ul/li/a[1]";
        final String RELEASE_DATE_XPATH = "//*[@id=\"__next\"]/main/div/section/div/section/div/div[1]/section[1]/div[2]/ul/li/div/ul/li/span";
        final String COUNTY_OF_ORIGIN_SELECTOR = "[href*=/search/title/?country_of_origin=]";
        final String LANGUAGE_SELECTOR = "[href*=/search/title?title_type=feature&primary_language]";

        Optional<Map<ProductionDetailKeys, String>> resultOptional;
        Map<ProductionDetailKeys, String> productionDetailMap = new HashMap<>();
        // Production Release Date Selection
        Optional<Document> releaseDocumentOptional = PageContentRetriever.retrievePageContent(document.baseUri() + RELEASE_DATE_EXTENSION);
        if(releaseDocumentOptional.isPresent()) {
            Element releaseDateCountryElement = releaseDocumentOptional.get().selectXpath(RELEASE_DATE_COUNTRY_XPATH).first();
            Element releaseDateElement = releaseDocumentOptional.get().selectXpath(RELEASE_DATE_XPATH).first();
            String productionDate = releaseDateCountryElement.ownText() + ", " + releaseDateElement.ownText();
            productionDetailMap.put(ProductionDetailKeys.RELEASE_DATE, formatToCamelCase(productionDate));
        }
        // Production Country Of Origin Selection
        Elements countryOfOriginElements = document.select(COUNTY_OF_ORIGIN_SELECTOR);
        if(countryOfOriginElements != null) {
            String countries = "";
            for(Element e : countryOfOriginElements) {
                if(!countries.equalsIgnoreCase("")) {
                    countries += ", ";
                }
                countries += formatToCamelCase(e.ownText());
            }
            productionDetailMap.put(ProductionDetailKeys.COUNTRY_OF_ORIGIN, countries);
        }
        // Production Language Selection
        Elements languageElements = document.select(LANGUAGE_SELECTOR);
        if(languageElements != null) {
            String langs = "";
            for(Element e : languageElements) {
                if(!langs.equalsIgnoreCase("")) {
                    langs += ", ";
                }
                langs += formatToCamelCase(e.ownText());
            }
            productionDetailMap.put(ProductionDetailKeys.LANGUAGE, langs);
        }
        resultOptional = Optional.of(productionDetailMap);
        return resultOptional;
    }

    /**
     * @author Grzegorz Lach
     * @since 0.0.1
     * @param document JSOUP Document containing html page from IMDB url link
     * @return Map of production deteail (Release Date, Country Of Origin, Language) for provided IMDB url document
     */
    private Optional<String> retrieveRunTime(Document document) {
        final String TECHNICAL_EXTENSION = "technical";
        final String RUNTIME_XPATH = "//*[@id=\"__next\"]/main/div/section/div/section/div/div[1]/section[1]/div/ul/li[1]/div/ul/li/span[1]";
        Optional<String> runtimeOptional = Optional.empty();

        Optional<Document> technicalDocument = PageContentRetriever.retrievePageContent(document.baseUri() + TECHNICAL_EXTENSION);
        if(technicalDocument.isPresent()) {
            Element test = technicalDocument.get().selectXpath(RUNTIME_XPATH).first();
            runtimeOptional = Optional.of(test.ownText());
        }
        return runtimeOptional;
    }

    /**
     * @author Grzegorz Lach
     * @since 0.0.1
     * @param document JSOUP Document containing html page from IMDB url link
     * @return ProductionData Object that contains all basic information about movie (in this case it is MovieData)
     */
    @Override
    public ProductionData extractData(Document document) {
        retrievedData = new MovieData();
        // Title Retrieval
        String retrievedTitle = retrieveTitleData(document);
        // Directors Retrieval
        Set<String> directors = retrieveDirectors(document);
        // Writers Retrieval
        Set<String> writers = retrieveWriters(document);
        // Genres Retrieval
        Set<String> genres = retrieveGenres(document);
        // Cover URL Retrieval
        Optional<String> coverURL = retrieveProductionCoverURL(document);
        // Tag List Retrieval
        Optional<List<String>> tagListOptional = retrieveAllTags(document);
        // Retrieve Production Detail
        Optional<Map<ProductionDetailKeys, String>> productionDetailsOptional = retrieveProductionDetails(document);
        // Retrieve Run Time
        Optional<String> runtimeOptional = retrieveRunTime(document);
        // Setting MovieData
        retrievedData.setTitle(retrievedTitle);
        for(String s : directors) {
            retrievedData.addDirector(s);
        }
        for(String s : writers) {
            retrievedData.addWriters(s);
        }
        for(String s : genres) {
            retrievedData.addGenre(s);
        }
        if (coverURL.isPresent()) retrievedData.setCoverURL(coverURL.get());
        if (tagListOptional.isPresent()) tagListOptional.get().forEach(t -> retrievedData.addTag(t));
        if (productionDetailsOptional.isPresent()) retrievedData.setProductionDetails(productionDetailsOptional.get());
        if (runtimeOptional.isPresent()) retrievedData.setRuntime(runtimeOptional.get());
        // Return
        return retrievedData;
    }

    private String formatToCamelCase(String input) {
        return Arrays.asList(input.split(" "))
                .stream().map(s -> s.toLowerCase())
                .map(s -> s.replaceFirst(s.substring(0,1), s.substring(0,1).toUpperCase()))
                .collect(Collectors.joining(" "));
    }
}
