package com.stockpilot.client.bok;

public class BokApiException extends RuntimeException {

    public BokApiException(String message) {
        super(message);
    }

    public BokApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
