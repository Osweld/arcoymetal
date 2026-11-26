package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.PageQuery;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.PageResult;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.UserSummary;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.UserQueryRepository;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.UserNotFoundException;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.entity.UserEntity;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.repository.SpringDataUserRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryAdapter implements UserQueryRepository {


    private final SpringDataUserRepository springDataUserRepository;

    @Override
    public PageResult<UserSummary> findAll(PageQuery pageQuery) {

        Pageable pageable = PageRequest.of(
            pageQuery.page(),
            pageQuery.size(),
        Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<UserEntity> userPage = springDataUserRepository.findAll(pageable);

        List<UserSummary> content = userPage.getContent()
        .stream()
        .map(this::toUserSummary).toList();

        return new PageResult<>(
            content,
            userPage.getNumber(),
            userPage.getSize(),
            userPage.getTotalElements(),
            userPage.getTotalPages(),
            userPage.isFirst(),
            userPage.isLast()
        );
     
    }

    private UserSummary toUserSummary(UserEntity userEntity) {
        return new UserSummary(
            userEntity.getId(),
            userEntity.getEmployeeId(),
            userEntity.getEmail(),
            userEntity.getRoles(),
            userEntity.getStatus(),
            userEntity.getCreatedAt()
        );
    }

    @Override
    public UserSummary findById(UUID userId) {
        UserEntity userEntity = springDataUserRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        return toUserSummary(userEntity);
    }

    @Override
    public UserSummary findByEmployeeId(UUID employeeId) {
        UserEntity userEntity = springDataUserRepository.findByEmployeeId(employeeId)
            .orElseThrow(() -> new UserNotFoundException(employeeId));

        return toUserSummary(userEntity);
    }

    @Override
    public List<UUID> findAllLinkedEmployeeIds() {
        return springDataUserRepository.findAllLinkedEmployeeIds();
    }

}
