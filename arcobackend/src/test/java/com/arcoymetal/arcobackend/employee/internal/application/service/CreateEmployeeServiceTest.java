package com.arcoymetal.arcobackend.employee.internal.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.arcoymetal.arcobackend.employee.internal.application.command.CreateEmployeeCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeRepository;
import com.arcoymetal.arcobackend.employee.internal.domain.ContractType;
import com.arcoymetal.arcobackend.employee.internal.domain.Employee;
import com.arcoymetal.arcobackend.employee.internal.domain.EmployeeId;
import com.arcoymetal.arcobackend.employee.internal.domain.Gender;
import com.arcoymetal.arcobackend.employee.internal.domain.PensionFundInstitution;

@ExtendWith(MockitoExtension.class)
public class CreateEmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private CreateEmployeeService createEmployeeService;

    @Test
    @DisplayName("Should throw exception when command is null")
    void shouldThrowExceptionWhenCommandIsNull() {

        assertThatThrownBy(() -> createEmployeeService.createEmployee(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CreateEmployeeCommand cannot be null");

        verifyNoInteractions(employeeRepository);

    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        CreateEmployeeCommand createEmployeeCommand = validCommand();

        when(employeeRepository.existsByEmail(createEmployeeCommand.email())).thenReturn(true);

        assertThatThrownBy(() -> createEmployeeService.createEmployee(createEmployeeCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("An employee with the provided email already exists.");

        verify(employeeRepository).existsByEmail(createEmployeeCommand.email());
        verify(employeeRepository, never()).existsByDui(createEmployeeCommand.dui());
        verify(employeeRepository, never()).save(any());

    }

    @Test
    @DisplayName("Should throw exception when DUI already exists")
    void shouldThrowExceptionWhenDuiAlreadyExists() {
        CreateEmployeeCommand createEmployeeCommand = validCommand();

        when(employeeRepository.existsByEmail(createEmployeeCommand.email())).thenReturn(false);
        when(employeeRepository.existsByDui(createEmployeeCommand.dui())).thenReturn(true);

        assertThatThrownBy(() -> createEmployeeService.createEmployee(createEmployeeCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("An employee with the provided DUI already exists.");

        verify(employeeRepository).existsByEmail(createEmployeeCommand.email());
        verify(employeeRepository).existsByDui(createEmployeeCommand.dui());
        verify(employeeRepository, never()).save(any());

    }

    @Test
    @DisplayName("Should create employee successfully when command is valid and email and DUI do not exist")
    void shouldCreateEmployeeSuccessfullyWhenCommandIsValid() {
        CreateEmployeeCommand createEmployeeCommand = validCommand();

        when(employeeRepository.existsByEmail(createEmployeeCommand.email())).thenReturn(false);
        when(employeeRepository.existsByDui(createEmployeeCommand.dui())).thenReturn(false);

        EmployeeId employeeId = createEmployeeService.createEmployee(createEmployeeCommand);

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeCaptor.capture());

        verify(employeeRepository).existsByEmail(createEmployeeCommand.email());
        verify(employeeRepository).existsByDui(createEmployeeCommand.dui());
        verify(employeeRepository).save(any());

         Employee savedEmployee = employeeCaptor.getValue();

         assertThat(employeeId).isNotNull();
            assertThat(savedEmployee.getEmployeeId()).isEqualTo(employeeId);
            assertThat(savedEmployee.getName().firstName()).isEqualTo(createEmployeeCommand.firstName());
            assertThat(savedEmployee.getName().lastName()).isEqualTo(createEmployeeCommand.lastName());
            assertThat(savedEmployee.getEmail().value()).isEqualTo(createEmployeeCommand.email());
            assertThat(savedEmployee.getPhoneNumber().value()).isEqualTo(createEmployeeCommand.phoneNumber());
            assertThat(savedEmployee.getGender()).isEqualTo(createEmployeeCommand.gender());
            assertThat(savedEmployee.getBirthDate().value()).isEqualTo(createEmployeeCommand.birthDate());
            assertThat(savedEmployee.getAddress().addressLine().value()).isEqualTo(createEmployeeCommand.addressLine());
            assertThat(savedEmployee.getAddress().districtId().value()).isEqualTo(createEmployeeCommand.districtId());
            assertThat(savedEmployee.getJobTitle().value()).isEqualTo(createEmployeeCommand.jobTitle());
            assertThat(savedEmployee.getBaseSalary().value()).isEqualTo(createEmployeeCommand.baseSalary());
            assertThat(savedEmployee.getContractType()).isEqualTo(createEmployeeCommand.contractType());
            assertThat(savedEmployee.getDui().value()).isEqualTo(createEmployeeCommand.dui());
            assertThat(savedEmployee.getNit().value()).isEqualTo(createEmployeeCommand.nit());
            assertThat(savedEmployee.getIsss().value()).isEqualTo(createEmployeeCommand.isss());
            assertThat(savedEmployee.getNup().value()).isEqualTo(createEmployeeCommand.nup());
            assertThat(savedEmployee.getPensionFundInstitution()).isEqualTo(createEmployeeCommand.pensionFundInstitution());
            assertThat(savedEmployee.getHireDate().value()).isEqualTo(createEmployeeCommand.hireDate());        
    }

    @Test
    @DisplayName("Should create employee successfully when command is valid and email and DUI do not exist, even with null fields")
    void shouldCreateEmployeeSuccessfullyWhenCommandIsValidWithNullFields() {
        CreateEmployeeCommand createEmployeeCommand = validCommandWithNullFields();

       when(employeeRepository.existsByEmail(createEmployeeCommand.email())).thenReturn(false);
        when(employeeRepository.existsByDui(createEmployeeCommand.dui())).thenReturn(false);

        EmployeeId employeeId = createEmployeeService.createEmployee(createEmployeeCommand);

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeCaptor.capture());

        verify(employeeRepository).existsByEmail(createEmployeeCommand.email());
        verify(employeeRepository).existsByDui(createEmployeeCommand.dui());
        verify(employeeRepository).save(any());

         Employee savedEmployee = employeeCaptor.getValue();

         assertThat(employeeId).isNotNull();
            assertThat(savedEmployee.getEmployeeId()).isEqualTo(employeeId);
            assertThat(savedEmployee.getName().firstName()).isEqualTo(createEmployeeCommand.firstName());
            assertThat(savedEmployee.getName().lastName()).isEqualTo(createEmployeeCommand.lastName());
            assertThat(savedEmployee.getEmail().value()).isEqualTo(createEmployeeCommand.email());
            assertThat(savedEmployee.getPhoneNumber().value()).isEqualTo(createEmployeeCommand.phoneNumber());
            assertThat(savedEmployee.getGender()).isEqualTo(createEmployeeCommand.gender());
            assertThat(savedEmployee.getBirthDate().value()).isEqualTo(createEmployeeCommand.birthDate());
            assertThat(savedEmployee.getAddress().addressLine().value()).isEqualTo(createEmployeeCommand.addressLine());
            assertThat(savedEmployee.getAddress().districtId().value()).isEqualTo(createEmployeeCommand.districtId());
            assertThat(savedEmployee.getJobTitle().value()).isEqualTo(createEmployeeCommand.jobTitle());
            assertThat(savedEmployee.getBaseSalary().value()).isEqualTo(createEmployeeCommand.baseSalary());
            assertThat(savedEmployee.getContractType()).isEqualTo(createEmployeeCommand.contractType());
            assertThat(savedEmployee.getDui().value()).isEqualTo(createEmployeeCommand.dui());
            assertThat(savedEmployee.getNit()).isNull();
            assertThat(savedEmployee.getIsss()).isNull();
            assertThat(savedEmployee.getNup()).isNull();
            assertThat(savedEmployee.getPensionFundInstitution()).isEqualTo(createEmployeeCommand.pensionFundInstitution());
            assertThat(savedEmployee.getHireDate().value()).isEqualTo(createEmployeeCommand.hireDate()); 
    }

    private CreateEmployeeCommand validCommand() {
        return new CreateEmployeeCommand(
                "John",
                "Doe",
                "john.doe@example.com",
                "78654321",
                Gender.MALE,
                LocalDate.of(1990, 1, 1),
                "Colonia el Escalon, Calle 1, casa 1",
                "SS01",
                "Software Engineer",
                new BigDecimal("5000.00"),
                ContractType.FULL_TIME,
                "123456789",
                "12341234561231",
                "123456789",
                "123456789",
                PensionFundInstitution.AFP_CONFIA,
                LocalDate.of(2020, 1, 1)

        );

    }

    private CreateEmployeeCommand validCommandWithNullFields() {
        return new CreateEmployeeCommand(
                "John",
                "Doe",
                "john.doe@example.com",
                "78654321",
                Gender.MALE,
                LocalDate.of(1990, 1, 1),
                "Colonia el Escalon, Calle 1, casa 1",
                "SS01",
                "Software Engineer",
                new BigDecimal("5000.00"),
                ContractType.FULL_TIME,
                "123456789",
                null, // NIT
                null, // ISSS
                null, // NUP
                PensionFundInstitution.AFP_CONFIA,
                LocalDate.of(2020, 1, 1)

        );

    }
}
