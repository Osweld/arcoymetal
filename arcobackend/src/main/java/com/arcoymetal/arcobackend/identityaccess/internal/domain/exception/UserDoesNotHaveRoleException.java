package com.arcoymetal.arcobackend.identityaccess.internal.domain.exception;

public class UserDoesNotHaveRoleException extends RuntimeException {
    
    public UserDoesNotHaveRoleException(String role) {
        super("User does not have the role: " + role);
    }

}
