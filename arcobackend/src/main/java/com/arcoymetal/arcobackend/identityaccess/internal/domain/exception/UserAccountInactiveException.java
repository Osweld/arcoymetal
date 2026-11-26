package com.arcoymetal.arcobackend.identityaccess.internal.domain.exception;

public class UserAccountInactiveException extends RuntimeException {

    public UserAccountInactiveException(String message) {
        super(message);
    }
}
