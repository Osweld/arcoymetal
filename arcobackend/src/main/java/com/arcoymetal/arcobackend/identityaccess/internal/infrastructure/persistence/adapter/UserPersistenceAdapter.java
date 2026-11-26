package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.adapter;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.UserRepository;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.User;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.UserNotFoundException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Role;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserStatus;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.entity.UserEntity;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.repository.SpringDataUserRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepository {

    private final SpringDataUserRepository springDataUserRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    @Override
    public void save(User user) {
        springDataUserRepository.findById(user.getUserId().value())
                .ifPresentOrElse(
                        existing -> {
                            existing.updateFrom(
                                    user.getEmail().value(),
                                    user.getPasswordHash().value(),
                                    user.getRoles(),
                                    user.getUserStatus()
                            );
                            springDataUserRepository.save(existing);
                        },
                        () -> springDataUserRepository.save(userPersistenceMapper.toEntity(user))
                );
    }

    @Override
    public User findById(UUID userId) {
        UserEntity entity = springDataUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return userPersistenceMapper.toDomain(entity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmployeeId(UUID employeeId) {
        return springDataUserRepository.existsByEmployeeId(employeeId);
    }

    @Override
    public long countActiveByRole(Role role) {
        return springDataUserRepository.countByStatusAndRole(UserStatus.ACTIVE, role);
    }

}
