package com.arcoymetal.arcobackend.employee.internal.application.dto;

public record PageQuery(
    int page,
    int size
) {
    public PageQuery {
        if (page < 0) {
            throw new IllegalArgumentException(
            "Page index must not be less than zero!"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
            "Page size must be between 1 and 100!"
            );
        }
    }
}
