package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.mapper;

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
        UserEntity entity = new UserEntity();
        entity.setId(user.getUserId().value());
        entity.setEmployeeId(user.getEmployeeId().value());
        entity.setPasswordHash(user.getPasswordHash().value());
        entity.setEmail(user.getEmail().value());
        entity.setRoles(user.getRoles());
        entity.setStatus(user.getUserStatus());
        return entity;
    }

    public User toDomain(UserEntity entity) {
        return User.reconstitute(
                new UserId(entity.getId()),
                new EmployeeId(entity.getEmployeeId()),
                new PasswordHash(entity.getPasswordHash()),
                new Email(entity.getEmail()),
                entity.getRoles(),
                entity.getStatus()
        );
    }

}
