package com.souryuu.catalogit.exception;

public class ImdbLinkParsingException extends RuntimeException{

    public ImdbLinkParsingException(String parsedLine) {
        super("Exception During Parsing Of IMDB Link At Line:\t" + parsedLine);
    }

}
