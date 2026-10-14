package org.example.exception;

public class BrokenLineException extends RuntimeException {
    public BrokenLineException(String message) {
        super(message);
    }
}
