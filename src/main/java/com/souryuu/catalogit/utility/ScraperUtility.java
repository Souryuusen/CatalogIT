package com.souryuu.catalogit.utility;

import com.souryuu.catalogit.entity.application.MovieData;
import com.souryuu.catalogit.entity.application.MovieDataExtractor;

public abstract class ScraperUtility {

    public static MovieData scrapeData(String url) {
        MovieDataExtractor mde = new MovieDataExtractor(url);
        return (MovieData) mde.extractData();
    }

    public static void main(String[] args) {
        System.out.println(formatToCamelCase("dfasfas_fsadfsa-hrt wq!@$$aqr"));
    }

    public static String formatToCamelCase(String input) {
        if (input == null || input.trim().length() == 0) {
            throw new IllegalArgumentException("Input String Cannot Be null or empty !!");
        }
        String in = input.toLowerCase();

        boolean isSpecial = true;
        int startIndex = 0;
        int endIndex = 0;

        StringBuilder sb = new StringBuilder(in.length());
        for(int i = 0; i < input.length(); i++) {
            if(in.substring(i, i+1).matches("[^a-zA-z0-9]|[\\_]")  || i == in.length()-1 || i == in.length()){
                isSpecial = true;
                endIndex = i == 0 ? 0 : i-1;
                endIndex = i == in.length()-1 ? i+1 : i;
                if(endIndex > in.length()-1) endIndex = in.length()-1;
            } else {
                if(isSpecial) {
                    startIndex = i;
                    isSpecial = false;
                }
            }
            if(isSpecial || (!isSpecial && i == in.length())) {
                String t = in.substring(startIndex, endIndex+1);
                if(!t.equalsIgnoreCase("")) {
                    if(t.matches("^[a-zA-Z].*+")) {
                        sb.append(t.replaceFirst(t.substring(0, 1), t.substring(0, 1).toUpperCase()));
                    } else {
                        sb.append(t);
                    }
                }
                startIndex = i+1;
            }
        }
        return sb.toString();
    }

}
