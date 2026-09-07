package com.lineacademy.coinappback.exception;

public class UpbitApiException extends RuntimeException {
    public UpbitApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpbitApiException(String message) {
        super(message);
    }
}
