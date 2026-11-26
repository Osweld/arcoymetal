package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.Address;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.AddressLine;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.BirthDate;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.DUI;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.DistrictId;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Email;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.EmergencyContact;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Employee;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.EmployeeId;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.ISSS;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.NIT;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.NUP;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Name;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.PhoneNumber;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmployeeEntity;


@Component
public class EmployeePersistenceMapper {

    public EmployeeEntity toEntity(Employee employee) {
        return new EmployeeEntity(
            employee.getEmployeeId().value(),
            employee.getName().firstName(),
            employee.getName().lastName(),
            employee.getEmail().value(),
            employee.getPhoneNumber().value(),
            employee.getGender(),
            employee.getBirthDate().value(),
            employee.getAddress().addressLine().value(),
            employee.getAddress().districtId().value(),
            employee.getDui().value(),
            employee.getNit() != null ? employee.getNit().value() : null,
            employee.getIsss() != null ? employee.getIsss().value() : null,
            employee.getNup() != null ? employee.getNup().value() : null,
            employee.getPensionFundInstitution(),
            employee.getEmergencyContact() != null ? employee.getEmergencyContact().name() : null,
            employee.getEmergencyContact() != null ? employee.getEmergencyContact().phoneNumber().value() : null
        );
    }

    public Employee toDomain(EmployeeEntity entity) {
        EmergencyContact emergencyContact = (entity.getEmergencyContactName() != null
                && entity.getEmergencyContactPhone() != null)
                ? new EmergencyContact(
                        entity.getEmergencyContactName(),
                        new PhoneNumber(entity.getEmergencyContactPhone()))
                : null;

        return Employee.reconstitute(
            new EmployeeId(entity.getId()),
            new Name(entity.getFirstName(), entity.getLastName()),
            new Email(entity.getEmail()),
            new PhoneNumber(entity.getPhoneNumber()),
            entity.getGender(),
            new BirthDate(entity.getBirthDate()),
            new Address(
                new AddressLine(entity.getAddressLine()),
                new DistrictId(entity.getDistrictId())
            ),
            new DUI(entity.getDui()),
            entity.getNit() != null ? new NIT(entity.getNit()) : null,
            entity.getIsss() != null ? new ISSS(entity.getIsss()) : null,
            entity.getNup() != null ? new NUP(entity.getNup()) : null,
            entity.getPensionFundInstitution(),
            emergencyContact
        );
    }
}