package com.arcoymetal.arcobackend.identityaccess.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.UserAlreadyHasRoleException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.UserDoesNotHaveRoleException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.UserMustHaveAtLeastOneRoleException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Email;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.EmployeeId;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.PasswordHash;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Role;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserId;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserStatus;

class UserTest {

    private static final PasswordHash VALID_HASH = new PasswordHash("a".repeat(60));
    private static final Email VALID_EMAIL = new Email("admin@arcoymetal.com.sv");

    @Test
    void shouldCreateActiveUserWithDefensiveCopyAndUnmodifiableRolesView() {
        UserId userId = UserId.generate();
        EmployeeId employeeId = EmployeeId.generate();
        Set<Role> inputRoles = new HashSet<>(Set.of(Role.ADMINISTRATOR));

        User user = User.create(userId, employeeId, VALID_HASH, VALID_EMAIL, inputRoles);
        inputRoles.clear();

        assertThat(user.getUserId()).isEqualTo(userId);
        assertThat(user.getEmployeeId()).isEqualTo(employeeId);
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getRoles()).containsExactly(Role.ADMINISTRATOR);
        assertThatThrownBy(() -> user.getRoles().add(Role.MANAGER))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldRejectEmptyOrNullRolesOrNullRoleElement() {
        UserId userId = UserId.generate();
        EmployeeId employeeId = EmployeeId.generate();

        assertThatNullPointerException()
                .isThrownBy(() -> User.create(userId, employeeId, VALID_HASH, VALID_EMAIL, null))
                .withMessage("Roles cannot be null");

        assertThatThrownBy(() -> User.create(userId, employeeId, VALID_HASH, VALID_EMAIL, Set.of()))
                .isInstanceOf(UserMustHaveAtLeastOneRoleException.class);

        Set<Role> rolesWithNull = new HashSet<>();
        rolesWithNull.add(null);
        assertThatNullPointerException()
                .isThrownBy(() -> User.create(userId, employeeId, VALID_HASH, VALID_EMAIL, rolesWithNull))
                .withMessage("Role cannot be null");
    }

    @Test
    void shouldAssignAndRemoveRolesWhilePreservingAtLeastOneRoleInvariant() {
        User user = User.create(
                UserId.generate(),
                EmployeeId.generate(),
                VALID_HASH,
                VALID_EMAIL,
                Set.of(Role.ADMINISTRATOR)
        );

        // Assign second role works even when created from immutable Set.of(...)
        user.assignRole(Role.MANAGER);
        assertThat(user.getRoles()).containsExactlyInAnyOrder(Role.ADMINISTRATOR, Role.MANAGER);

        // Duplicate role rejected
        assertThatThrownBy(() -> user.assignRole(Role.MANAGER))
                .isInstanceOf(UserAlreadyHasRoleException.class)
                .hasMessageContaining("MANAGER");

        // Remove one role when two exist succeeds
        user.removeRole(Role.MANAGER);
        assertThat(user.getRoles()).containsExactly(Role.ADMINISTRATOR);

        // Removing non-assigned role throws UserDoesNotHaveRoleException
        assertThatThrownBy(() -> user.removeRole(Role.MANAGER))
                .isInstanceOf(UserDoesNotHaveRoleException.class)
                .hasMessageContaining("MANAGER");

        // Removing last role throws UserMustHaveAtLeastOneRoleException AND preserves the role
        assertThatThrownBy(() -> user.removeRole(Role.ADMINISTRATOR))
                .isInstanceOf(UserMustHaveAtLeastOneRoleException.class);
        assertThat(user.getRoles()).containsExactly(Role.ADMINISTRATOR);
    }

    @Test
    void shouldReconstituteUserWithNonActiveStatus() {
        User user = User.reconstitute(
                UserId.generate(),
                EmployeeId.generate(),
                VALID_HASH,
                VALID_EMAIL,
                Set.of(Role.MANAGER),
                UserStatus.LOCKED
        );

        assertThat(user.getUserStatus()).isEqualTo(UserStatus.LOCKED);
        assertThat(user.getRoles()).containsExactly(Role.MANAGER);
    }
}
