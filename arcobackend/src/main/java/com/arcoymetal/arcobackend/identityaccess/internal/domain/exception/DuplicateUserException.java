package com.arcoymetal.arcobackend.identityaccess.internal.domain.exception;

public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException(String message) {
        super(message);
    }
}
