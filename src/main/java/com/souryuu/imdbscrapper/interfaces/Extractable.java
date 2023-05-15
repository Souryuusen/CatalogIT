package com.souryuu.imdbscrapper.interfaces;

import org.jsoup.nodes.Document;

public interface Extractable {
    ProductionData extractData(Document document);
}
