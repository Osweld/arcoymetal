import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { describe, expect, it, vi } from 'vitest';
import { EmployeeFormComponent } from './employee-form.component';
import { EmployeeService } from '../../services/employee.service';
import { LocationService } from '../../services/location.service';

describe('EmployeeFormComponent', () => {
  const mockHierarchy = [
    {
      id: 'AH',
      name: 'Ahuachapán',
      municipalities: [
        {
          id: 'AH01',
          name: 'Ahuachapán Norte',
          districts: [{ id: 'AH0101', name: 'Atiquizaya' }],
        },
      ],
    },
  ];

  it('submits CreateEmployeeRequest without employment fields and normalizes blank optional IDs to null', () => {
    const createEmployee = vi.fn().mockReturnValue(of({ employeeId: 'emp-new-1' }));

    TestBed.configureTestingModule({
      imports: [EmployeeFormComponent],
      providers: [
        provideRouter([]),
        { provide: EmployeeService, useValue: { createEmployee } },
        { provide: LocationService, useValue: { getHierarchy: () => of(mockHierarchy) } },
      ],
    });

    const router = TestBed.inject(Router);
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    const fixture = TestBed.createComponent(EmployeeFormComponent);
    fixture.detectChanges();

    const component = fixture.componentInstance;
    component.employeeForm.setValue({
      firstName: 'Ana',
      lastName: 'López',
      email: 'ana@arcoymetal.com.sv',
      phoneNumber: '71234567',
      gender: 'FEMALE',
      birthDate: '1994-05-20',
      addressLine: 'Calle Principal #5',
      districtId: 'AH0101',
      dui: '02345678-9',
      nit: '',
      isss: '',
      nup: '',
      pensionFundInstitution: 'AFP_CONFIA',
      emergencyContactName: 'María López',
      emergencyContactPhone: '70009999',
    });

    component.onSubmit();

    expect(createEmployee).toHaveBeenCalledWith({
      firstName: 'Ana',
      lastName: 'López',
      email: 'ana@arcoymetal.com.sv',
      phoneNumber: '71234567',
      gender: 'FEMALE',
      birthDate: '1994-05-20',
      addressLine: 'Calle Principal #5',
      districtId: 'AH0101',
      dui: '02345678-9',
      nit: null,
      isss: null,
      nup: null,
      pensionFundInstitution: 'AFP_CONFIA',
      emergencyContactName: 'María López',
      emergencyContactPhone: '70009999',
    });
    expect(navigateSpy).toHaveBeenCalledWith(['/employees', 'emp-new-1']);
  });

  it('displays formatted ApiErrorResponse on 409 conflict', () => {
    const createEmployee = vi.fn().mockReturnValue(
      throwError(() => ({
        status: 409,
        error: {
          timestamp: '2026-11-24T12:00:00Z',
          status: 409,
          message: 'An employee with the provided DUI already exists.',
          errors: null,
        },
      }))
    );

    TestBed.configureTestingModule({
      imports: [EmployeeFormComponent],
      providers: [
        provideRouter([]),
        { provide: EmployeeService, useValue: { createEmployee } },
        { provide: LocationService, useValue: { getHierarchy: () => of(mockHierarchy) } },
      ],
    });

    const fixture = TestBed.createComponent(EmployeeFormComponent);
    fixture.detectChanges();

    const component = fixture.componentInstance;
    component.employeeForm.setValue({
      firstName: 'Ana',
      lastName: 'López',
      email: 'ana@arcoymetal.com.sv',
      phoneNumber: '71234567',
      gender: 'FEMALE',
      birthDate: '1994-05-20',
      addressLine: 'Calle Principal #5',
      districtId: 'AH0101',
      dui: '02345678-9',
      nit: '',
      isss: '',
      nup: '',
      pensionFundInstitution: 'AFP_CONFIA',
      emergencyContactName: '',
      emergencyContactPhone: '',
    });

    component.onSubmit();

    expect(component.serverError()).toBe('An employee with the provided DUI already exists.');
  });
});
