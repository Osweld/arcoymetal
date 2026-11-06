package com.arcoymetal.arcobackend.identityaccess.internal.domain;

import java.util.Objects;
import java.util.Set;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.UserAlreadyHasRoleException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.UserDoesNotHaveRoleException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.UserMustHaveAtLeastOneRoleException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Email;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.EmployeeId;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.PasswordHash;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Role;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserId;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserStatus;

public class User {

    private final UserId userId;
    private final EmployeeId employeeId;
    private PasswordHash passwordHash;
    private Email email;
    private final Set<Role> roles;
    private UserStatus userStatus;

    private User(UserId userId, EmployeeId employeeId, PasswordHash passwordHash, Email email, Set<Role> roles,
            UserStatus userStatus) {
        this.userId = Objects.requireNonNull(userId, "UserId cannot be null");
        this.employeeId = Objects.requireNonNull(employeeId, "EmployeeId cannot be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "PasswordHash cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.roles = Objects.requireNonNull(roles, "Roles cannot be null");
        this.userStatus = Objects.requireNonNull(userStatus, "UserStatus cannot be null");
    }

    public static User create(UserId userId, EmployeeId employeeId, PasswordHash passwordHash, Email email,
            Set<Role> roles) {
        if (roles.isEmpty()) {
            throw new UserMustHaveAtLeastOneRoleException();
        }
        return new User(userId, employeeId, passwordHash, email, roles, UserStatus.ACTIVE);
    }

    public static User reconstitute(UserId userId, EmployeeId employeeId, PasswordHash passwordHash, Email email, Set<Role> roles,
            UserStatus userStatus) {
        return new User(userId, employeeId, passwordHash, email, roles, userStatus);
    }

    public void assignRole(Role role) {
        Objects.requireNonNull(role, "Role cannot be null");

        if (!this.roles.add(role)) {
            throw new UserAlreadyHasRoleException(role.toString());
        }
    }

    public void removeRole(Role role) {
        Objects.requireNonNull(role, "Role cannot be null");

        if (!this.roles.remove(role)) {
            throw new UserDoesNotHaveRoleException(role.toString());
        }

        if (this.roles.isEmpty()) {
            throw new UserMustHaveAtLeastOneRoleException();
        }
    }

    public UserId getUserId() {
        return userId;
    }

    public EmployeeId getEmployeeId() {
        return employeeId;
    }

    public PasswordHash getPasswordHash() {
        return passwordHash;
    }

    public Email getEmail() {
        return email;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }



    
    


}
