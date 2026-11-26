package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.mapper;

import java.util.HashSet;

import org.springframework.stereotype.Component;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.User;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Email;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.EmployeeId;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.PasswordHash;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserId;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.entity.UserEntity;

@Component
public class UserPersistenceMapper {

    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.getUserId().value(),
                user.getEmployeeId().value(),
                user.getPasswordHash().value(),
                user.getEmail().value(),
                new HashSet<>(user.getRoles()),
                user.getUserStatus(),
                user.isMustChangePassword(),
                user.getTokenVersion(),
                user.getFailedLoginAttempts()
        );
    }

    public User toDomain(UserEntity entity) {
        return User.reconstitute(
                new UserId(entity.getId()),
                new EmployeeId(entity.getEmployeeId()),
                new PasswordHash(entity.getPasswordHash()),
                new Email(entity.getEmail()),
                entity.getRoles(),
                entity.getStatus(),
                entity.isMustChangePassword(),
                entity.getTokenVersion(),
                entity.getFailedLoginAttempts()
        );
    }
}
