package com.arcoymetal.arcobackend.identityaccess.internal.application.dto;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {

}
