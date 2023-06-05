package com.souryuu.catalogit.exception;

public class NoDocumentException extends RuntimeException {

    public NoDocumentException(String message) {
        super(message);
    }

    public NoDocumentException(String message, Throwable cause) {
        super(message, cause);
    }

}
