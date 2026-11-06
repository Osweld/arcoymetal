package com.arcoymetal.arcobackend.identityaccess.internal.domain.exception;

public class UserMustHaveAtLeastOneRoleException extends RuntimeException {
    
    public UserMustHaveAtLeastOneRoleException() {
        super("User must have at least one role.");
    }

}
