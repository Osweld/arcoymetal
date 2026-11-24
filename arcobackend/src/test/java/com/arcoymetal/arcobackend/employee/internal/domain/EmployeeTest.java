package com.arcoymetal.arcobackend.employee.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.Address;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.AddressLine;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.BirthDate;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.DUI;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.DistrictId;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Email;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Employee;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.EmployeeId;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Gender;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.ISSS;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.NIT;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.NUP;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Name;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.PensionFundInstitution;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.PhoneNumber;

class EmployeeTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-06-15T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void shouldCreateEmployeeWithAllFields() {
        EmployeeId id = EmployeeId.generate();
        Employee employee = Employee.create(
                id,
                new Name("Carlos", "Mendoza"),
                new Email("carlos.mendoza@arcoymetal.com"),
                new PhoneNumber("71234567"),
                Gender.MALE,
                new BirthDate(LocalDate.of(1995, 3, 10)),
                new Address(new AddressLine("Col. Escalón #123"), new DistrictId("0614")),
                new DUI("01234567-8"),
                new NIT("0614-100395-101-2"),
                new ISSS("12345678-9"),
                new NUP("98765432-1"),
                PensionFundInstitution.AFP_CONFIA,
                FIXED_CLOCK
        );

        assertThat(employee.getEmployeeId()).isEqualTo(id);
        assertThat(employee.getName().fullName()).isEqualTo("Carlos Mendoza");
        assertThat(employee.getDui().value()).isEqualTo("012345678");
        assertThat(employee.getNit().value()).isEqualTo("06141003951012");
        assertThat(employee.getIsss().value()).isEqualTo("123456789");
        assertThat(employee.getNup().value()).isEqualTo("987654321");
    }

    @Test
    void shouldCreateEmployeeWithOptionalIdentifiersNull() {
        Employee employee = Employee.create(
                EmployeeId.generate(),
                new Name("Ana", "López"),
                new Email("ana.lopez@arcoymetal.com"),
                new PhoneNumber("72345678"),
                Gender.FEMALE,
                new BirthDate(LocalDate.of(2008, 6, 15)), // exactly 18 years old on 2026-06-15
                new Address(new AddressLine("Calle Principal #5"), new DistrictId("0614")),
                new DUI("023456789"),
                null,
                null,
                null,
                PensionFundInstitution.AFP_CRECER,
                FIXED_CLOCK
        );

        assertThat(employee.getNit()).isNull();
        assertThat(employee.getIsss()).isNull();
        assertThat(employee.getNup()).isNull();
    }

    @Test
    void shouldRejectEmployeeUnder18YearsOld() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Employee.create(
                        EmployeeId.generate(),
                        new Name("Luis", "Pérez"),
                        new Email("luis@arcoymetal.com"),
                        new PhoneNumber("70001111"),
                        Gender.MALE,
                        new BirthDate(LocalDate.of(2008, 6, 16)), // 17 years old
                        new Address(new AddressLine("Av. Norte #10"), new DistrictId("0614")),
                        new DUI("034567890"),
                        null,
                        null,
                        null,
                        PensionFundInstitution.ISP,
                        FIXED_CLOCK
                ))
                .withMessage("Employee must be at least 18 years old");
    }

    @Test
    void shouldRejectFutureBirthDate() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Employee.create(
                        EmployeeId.generate(),
                        new Name("Luis", "Pérez"),
                        new Email("luis@arcoymetal.com"),
                        new PhoneNumber("70001111"),
                        Gender.MALE,
                        new BirthDate(LocalDate.of(2026, 6, 16)),
                        new Address(new AddressLine("Av. Norte #10"), new DistrictId("0614")),
                        new DUI("034567890"),
                        null,
                        null,
                        null,
                        PensionFundInstitution.ISP,
                        FIXED_CLOCK
                ))
                .withMessage("Birth date cannot be in the future");
    }

    @Test
    void shouldRejectNullBirthDateOrClockOnCreateAndUpdate() {
        assertThatNullPointerException()
                .isThrownBy(() -> Employee.create(
                        EmployeeId.generate(),
                        new Name("Ana", "López"),
                        new Email("ana@arcoymetal.com"),
                        new PhoneNumber("72345678"),
                        Gender.FEMALE,
                        null,
                        new Address(new AddressLine("Calle 1"), new DistrictId("0614")),
                        new DUI("023456789"),
                        null, null, null,
                        PensionFundInstitution.AFP_CONFIA,
                        FIXED_CLOCK
                ))
                .withMessage("Birth date cannot be null");

        assertThatNullPointerException()
                .isThrownBy(() -> Employee.update(
                        EmployeeId.generate(),
                        new Name("Ana", "López"),
                        new Email("ana@arcoymetal.com"),
                        new PhoneNumber("72345678"),
                        Gender.FEMALE,
                        new BirthDate(LocalDate.of(1990, 1, 1)),
                        new Address(new AddressLine("Calle 1"), new DistrictId("0614")),
                        new DUI("023456789"),
                        null, null, null,
                        PensionFundInstitution.AFP_CONFIA,
                        null
                ))
                .withMessage("Clock cannot be null");
    }

    @Test
    void shouldUpdateAndReconstituteEmployee() {
        EmployeeId id = EmployeeId.generate();
        Employee updated = Employee.update(
                id,
                new Name("Marta", "Gómez"),
                new Email("marta@arcoymetal.com"),
                new PhoneNumber("75556666"),
                Gender.FEMALE,
                new BirthDate(LocalDate.of(1992, 8, 20)),
                new Address(new AddressLine("Residencial Los Robles"), new DistrictId("0614")),
                new DUI("098765432"),
                null, null, null,
                PensionFundInstitution.AFP_CONFIA,
                FIXED_CLOCK
        );

        Employee reconstituted = Employee.reconstitute(
                id,
                updated.getName(),
                updated.getEmail(),
                updated.getPhoneNumber(),
                updated.getGender(),
                updated.getBirthDate(),
                updated.getAddress(),
                updated.getDui(),
                null, null, null,
                updated.getPensionFundInstitution()
        );

        assertThat(updated.getEmployeeId()).isEqualTo(id);
        assertThat(reconstituted.getEmail()).isEqualTo(updated.getEmail());
    }
}
