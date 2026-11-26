package com.arcoymetal.arcobackend.employee.internal.domain.exception;

import java.util.UUID;

public class OnlyLatestEmploymentChangeCanBeRemovedException extends RuntimeException {

    public OnlyLatestEmploymentChangeCanBeRemovedException(UUID changeId) {
        super("Only the latest employment change can be removed; change " + changeId + " is not the latest");
    }
}
