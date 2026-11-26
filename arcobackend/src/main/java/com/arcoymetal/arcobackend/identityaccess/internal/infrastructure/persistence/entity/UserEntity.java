package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Role;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserStatus;
import com.arcoymetal.arcobackend.shared.infrastructure.persistence.entity.AuditableEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEntity extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "employee_id", nullable = false, unique = true)
    private UUID employeeId;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private UserStatus status;

    @Column(name = "must_change_password", nullable = false)
    private boolean mustChangePassword;

    @Column(name = "token_version", nullable = false)
    private long tokenVersion;

    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts;

    public void updateFrom(
            String email,
            String passwordHash,
            Set<Role> roles,
            UserStatus status,
            boolean mustChangePassword,
            long tokenVersion,
            int failedLoginAttempts
    ) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles.clear();
        this.roles.addAll(roles);
        this.status = status;
        this.mustChangePassword = mustChangePassword;
        this.tokenVersion = tokenVersion;
        this.failedLoginAttempts = failedLoginAttempts;
    }
}
