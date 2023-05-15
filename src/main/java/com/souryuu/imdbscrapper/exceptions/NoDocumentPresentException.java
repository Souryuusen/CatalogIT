package com.souryuu.imdbscrapper.exceptions;

public class NoDocumentPresentException extends RuntimeException{

    public NoDocumentPresentException() {
        super();
    }

    public NoDocumentPresentException(String msg) {
        super(msg);
    }

}
