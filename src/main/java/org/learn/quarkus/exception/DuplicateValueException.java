package org.learn.quarkus.exception;

public class DuplicateValueException extends Exception {
    public DuplicateValueException(String message) {
        super(message);
    }
}
