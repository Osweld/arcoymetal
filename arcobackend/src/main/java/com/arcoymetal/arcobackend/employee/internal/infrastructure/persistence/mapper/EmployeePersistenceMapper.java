package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.arcoymetal.arcobackend.employee.internal.domain.Address;
import com.arcoymetal.arcobackend.employee.internal.domain.AddressLine;
import com.arcoymetal.arcobackend.employee.internal.domain.BaseSalary;
import com.arcoymetal.arcobackend.employee.internal.domain.BirthDate;
import com.arcoymetal.arcobackend.employee.internal.domain.DUI;
import com.arcoymetal.arcobackend.employee.internal.domain.DistrictId;
import com.arcoymetal.arcobackend.employee.internal.domain.Email;
import com.arcoymetal.arcobackend.employee.internal.domain.Employee;
import com.arcoymetal.arcobackend.employee.internal.domain.EmployeeId;
import com.arcoymetal.arcobackend.employee.internal.domain.HireDate;
import com.arcoymetal.arcobackend.employee.internal.domain.ISSS;
import com.arcoymetal.arcobackend.employee.internal.domain.JobTitle;
import com.arcoymetal.arcobackend.employee.internal.domain.NIT;
import com.arcoymetal.arcobackend.employee.internal.domain.NUP;
import com.arcoymetal.arcobackend.employee.internal.domain.Name;
import com.arcoymetal.arcobackend.employee.internal.domain.PhoneNumber;
import com.arcoymetal.arcobackend.employee.internal.domain.TerminationDate;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmployeeEntity;

@Component 
public class EmployeePersistenceMapper {

    public EmployeeEntity toEntity(Employee employee) {
         EmployeeEntity entity = new EmployeeEntity();

            entity.setId(employee.getEmployeeId().value());
            entity.setFirstName(employee.getName().firstName());
            entity.setLastName(employee.getName().lastName());
            entity.setEmail(employee.getEmail().value());
            entity.setPhoneNumber(employee.getPhoneNumber().value());
            entity.setGender(employee.getGender());
            entity.setBirthDate(employee.getBirthDate().value());
            entity.setAddressLine(employee.getAddress().addressLine().value());
            entity.setDistrictId(employee.getAddress().districtId().value());
            entity.setJobTitle(employee.getJobTitle().value());
            entity.setBaseSalary(employee.getBaseSalary().value());
            entity.setContractType(employee.getContractType());
            entity.setDui(employee.getDui().value());
            entity.setNit(employee.getNit() != null ? employee.getNit().value() : null);
            entity.setIsss(employee.getIsss() != null ? employee.getIsss().value() : null);
            entity.setNup(employee.getNup() != null ? employee.getNup().value() : null);
            entity.setPensionFundInstitution(employee.getPensionFundInstitution());
            entity.setHireDate(employee.getHireDate().value());
            entity.setTerminationDate(employee.getTerminationDate() != null ? employee.getTerminationDate().value() : null);
            entity.setStatus(employee.getStatus());
            return entity;


    }

    public Employee toDomain(EmployeeEntity entity) {
        return Employee.reconstitute(
                new EmployeeId(entity.getId()),
                new Name(entity.getFirstName(), entity.getLastName()),
                new Email(entity.getEmail()),
                new PhoneNumber(entity.getPhoneNumber()),
                entity.getGender(),
                new BirthDate(entity.getBirthDate()),
                new Address(
                        new AddressLine(entity.getAddressLine()),
                        new DistrictId(entity.getDistrictId())),
                new JobTitle(entity.getJobTitle()),
                new BaseSalary(entity.getBaseSalary()),
                entity.getContractType(),
                new DUI(entity.getDui()),
                entity.getNit() != null ? new NIT(entity.getNit()) : null,
                entity.getIsss() != null ? new ISSS(entity.getIsss()) : null,
                entity.getNup() != null ? new NUP(entity.getNup()) : null,
                entity.getPensionFundInstitution(),
                new HireDate(entity.getHireDate()),
                entity.getTerminationDate() != null ? new TerminationDate(entity.getTerminationDate()) : null,
                entity.getStatus());
    }

}
