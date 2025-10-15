package com.safedrive.exception;

public class DuplicateCredentialException extends RuntimeException {
    public DuplicateCredentialException(String message) {
        super(message);
    }
}
