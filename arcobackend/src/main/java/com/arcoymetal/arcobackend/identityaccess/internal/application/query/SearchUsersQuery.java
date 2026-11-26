package com.arcoymetal.arcobackend.identityaccess.internal.application.query;

import java.util.Objects;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.PageQuery;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserStatus;

public record SearchUsersQuery(
        PageQuery pageQuery,
        String search,
        UserStatus status
) {
    public SearchUsersQuery {
        Objects.requireNonNull(pageQuery, "PageQuery cannot be null");
        if (search != null) {
            String trimmed = search.trim();
            search = trimmed.isEmpty() ? null : trimmed;
        }
    }
}
