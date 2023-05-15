package com.souryuu.imdbscrapper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Optional;

public class PageContentRetriever {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36";

    public static Optional<Document> retrievePageContent(String url) {
        Optional<Document> htmlContent = Optional.empty();
        try {
            Connection.Response response = Jsoup.connect(url).userAgent(USER_AGENT)
                    .referrer("www.google.com").followRedirects(true).execute();
            if(response.statusCode() == 200) {
                htmlContent = Optional.of(response.parse());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return htmlContent;
    }
}
