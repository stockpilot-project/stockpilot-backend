package com.stockpilot.global.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "Invalid input value"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "Internal server error"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C003", "Method not allowed"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C004", "Resource not found"),

    // Stock
    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "Stock not found"),
    SECTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "S002", "Sector not found"),
    DUPLICATE_STOCK(HttpStatus.CONFLICT, "S003", "Stock already exists"),

    // Watchlist
    WATCHLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "W001", "Watchlist not found"),
    WATCHLIST_ITEM_DUPLICATE(HttpStatus.CONFLICT, "W002", "Stock already in watchlist"),
    WATCHLIST_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "W003", "Stock not in watchlist"),

    // Knowledge (Investment Terms / Economic Indicators / Disclosures)
    INVESTMENT_TERM_NOT_FOUND(HttpStatus.NOT_FOUND, "K001", "Investment term not found"),
    INVESTMENT_TERM_DUPLICATE(HttpStatus.CONFLICT, "K002", "Investment term already exists"),
    TERM_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "K003", "Term category not found"),
    INDICATOR_NOT_FOUND(HttpStatus.NOT_FOUND, "K101", "Economic indicator not found"),
    INDICATOR_SOURCE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "K102", "Economic indicator source unavailable"),
    DISCLOSURE_NOT_FOUND(HttpStatus.NOT_FOUND, "K201", "Disclosure not found"),
    DART_RATE_LIMITED(HttpStatus.TOO_MANY_REQUESTS, "K202", "DART API rate limit exceeded"),
    DART_CORP_NOT_FOUND(HttpStatus.NOT_FOUND, "K203", "DART corp mapping not found"),

    // External API
    EXTERNAL_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "E001", "External API call failed");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
