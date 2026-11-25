import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { describe, expect, it, vi } from 'vitest';
import { EmployeeDetailComponent } from './employee-detail.component';
import { EmployeeService } from '../../services/employee.service';
import { EmploymentPeriodService } from '../../services/employment-period.service';
import { LocationService } from '../../services/location.service';
import { EmployeeDetails } from '../../models/employee.model';

describe('EmployeeDetailComponent', () => {
  const sampleEmployee: EmployeeDetails = {
    employeeId: 'emp-1',
    firstName: 'Roberto',
    lastName: 'Díaz',
    email: 'roberto@arcoymetal.com.sv',
    phoneNumber: '76667777',
    gender: 'MALE',
    birthDate: '1988-11-05',
    addressLine: 'Pasaje Central #8',
    districtId: 'AH0101',
    dui: '077777771',
    nit: null,
    isss: null,
    nup: null,
    pensionFundInstitution: 'AFP_CONFIA',
    createdAt: '2026-11-24T12:00:00Z',
  };

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

  it('creates an EmploymentPeriod and handles 409 conflict when an active period already exists', () => {
    const createEmploymentPeriod = vi
      .fn()
      .mockReturnValueOnce(of({ employmentPeriodId: 'ep-uuid-1' }))
      .mockReturnValueOnce(
        throwError(() => ({
          status: 409,
          error: {
            timestamp: '2026-11-24T12:00:00Z',
            status: 409,
            message: 'Employee with ID emp-1 already has an active employment period',
            errors: null,
          },
        }))
      );

    TestBed.configureTestingModule({
      imports: [EmployeeDetailComponent],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => 'emp-1' } } },
        },
        {
          provide: EmployeeService,
          useValue: {
            getEmployeeById: () => of(sampleEmployee),
            updateEmployee: () => of({ employeeId: 'emp-1' }),
          },
        },
        { provide: EmploymentPeriodService, useValue: { createEmploymentPeriod } },
        {
          provide: LocationService,
          useValue: {
            getHierarchy: () => of(mockHierarchy),
            resolveDistrict: () => ({
              department: mockHierarchy[0],
              municipality: mockHierarchy[0].municipalities[0],
              district: mockHierarchy[0].municipalities[0].districts[0],
            }),
          },
        },
      ],
    });

    const fixture = TestBed.createComponent(EmployeeDetailComponent);
    fixture.detectChanges();

    const component = fixture.componentInstance;

    // Verify edit form pre-selects department, municipality, and district in the DOM
    component.toggleEditMode();
    fixture.detectChanges();
    const deptSelect = fixture.nativeElement.querySelector('#edit-dept') as HTMLSelectElement;
    const munSelect = fixture.nativeElement.querySelector('#edit-mun') as HTMLSelectElement;
    const distSelect = fixture.nativeElement.querySelector('#edit-dist') as HTMLSelectElement;
    expect(deptSelect.value).toBe('AH');
    expect(munSelect.value).toBe('AH01');
    expect(distSelect.value).toBe('AH0101');

    component.toggleEmploymentPeriodForm();
    component.employmentPeriodForm.setValue({
      startDate: '2026-01-15',
      jobTitle: 'Soldador de Estructuras',
      contractType: 'FULL_TIME',
      baseSalary: '650.50',
    });

    component.onCreateEmploymentPeriod();

    expect(createEmploymentPeriod).toHaveBeenCalledWith({
      employeeId: 'emp-1',
      startDate: '2026-01-15',
      jobTitle: 'Soldador de Estructuras',
      contractType: 'FULL_TIME',
      baseSalary: 650.5,
    });
    expect(component.lastCreatedPeriod()?.employmentPeriodId).toBe('ep-uuid-1');

    // Second attempt triggers 409 conflict
    component.toggleEmploymentPeriodForm();
    component.employmentPeriodForm.setValue({
      startDate: '2026-02-01',
      jobTitle: 'Soldador II',
      contractType: 'FULL_TIME',
      baseSalary: '700.00',
    });
    component.onCreateEmploymentPeriod();

    expect(component.actionError()).toBe(
      'Employee with ID emp-1 already has an active employment period'
    );
  });
});
