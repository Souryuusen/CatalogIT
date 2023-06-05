package com.souryuu.catalogit.entity.interfaces;

import com.souryuu.catalogit.entity.enums.MovieDataParameters;

import java.util.List;
import java.util.Map;

public interface ExtractedData {

    Map<ExtractedDataParameter, List<String>> retrieveExtractedData();

}
