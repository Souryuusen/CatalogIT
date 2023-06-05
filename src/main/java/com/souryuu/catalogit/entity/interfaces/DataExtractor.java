package com.souryuu.catalogit.entity.interfaces;

public interface DataExtractor {

    boolean validSource(String link);

    ExtractedData extractData();

}
