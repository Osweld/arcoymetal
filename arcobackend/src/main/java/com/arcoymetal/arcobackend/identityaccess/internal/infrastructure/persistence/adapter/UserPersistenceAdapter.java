package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.adapter;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.UserRepository;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.User;
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
        springDataUserRepository.save(userPersistenceMapper.toEntity(user));
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmployeeId(UUID employeeId) {
        return springDataUserRepository.existsByEmployeeId(employeeId);
    }

}
