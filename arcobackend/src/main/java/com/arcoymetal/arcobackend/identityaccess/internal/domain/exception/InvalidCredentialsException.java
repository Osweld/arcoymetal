package com.arcoymetal.arcobackend.identityaccess.internal.domain.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Credenciales inválidas.");
    }
}
