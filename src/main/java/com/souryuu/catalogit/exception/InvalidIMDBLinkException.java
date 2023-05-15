package com.souryuu.catalogit.exception;

public class InvalidIMDBLinkException extends RuntimeException{

    public InvalidIMDBLinkException(String providedLink) {
        super("Non-Valid Value Of Provided URL Detected: \"" + providedLink + "\"");
    }

}
