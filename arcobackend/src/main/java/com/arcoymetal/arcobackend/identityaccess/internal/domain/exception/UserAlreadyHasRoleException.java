package com.arcoymetal.arcobackend.identityaccess.internal.domain.exception;

public class UserAlreadyHasRoleException extends RuntimeException {
    
    public UserAlreadyHasRoleException(String role) {
        super("User already has the role: " + role);
    }

}
