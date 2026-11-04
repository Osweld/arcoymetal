package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.adapter;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeQueryRepository;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository.SpringDataEmployeeRepository;

@Repository
public class EmployeeQueryRepositoryAdapter implements EmployeeQueryRepository {

    private final SpringDataEmployeeRepository springDataEmployeeRepository;

    public EmployeeQueryRepositoryAdapter(SpringDataEmployeeRepository springDataEmployeeRepository) {
        this.springDataEmployeeRepository = springDataEmployeeRepository;
    }

    @Override
    public List<EmployeeDetails> findAllEmployeeDetails() {
        return springDataEmployeeRepository.findAllProjectedBy()
                .stream()
                .map(projection -> new EmployeeDetails(
                        projection.getId(),
                        projection.getFirstName(),
                        projection.getLastName(),
                        projection.getEmail(),
                        projection.getPhoneNumber(),
                        projection.getGender(),
                        projection.getBirthDate(),
                        projection.getAddressLine(),
                        projection.getDistrictId(),
                        projection.getJobTitle(),
                        projection.getBaseSalary(),
                        projection.getContractType(),
                        projection.getDui(),
                        projection.getNit(),
                        projection.getIsss(),
                        projection.getNup(),
                        projection.getPensionFundInstitution(),
                        projection.getHireDate(),
                        projection.getTerminationDate(),
                        projection.getStatus()
                ))
                .toList();
    }

    @Override
    public EmployeeDetails findEmployeeDetailsById(String employeeId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findEmployeeDetailsById'");
    }



}
