package com.stockpilot.client.dart;

import lombok.Getter;

@Getter
public class DartApiException extends RuntimeException {

    private final boolean rateLimited;
    private final String dartStatus;

    public DartApiException(String message, String dartStatus, boolean rateLimited) {
        super(message);
        this.dartStatus = dartStatus;
        this.rateLimited = rateLimited;
    }

    public DartApiException(String message, Throwable cause) {
        super(message, cause);
        this.dartStatus = null;
        this.rateLimited = false;
    }
}
