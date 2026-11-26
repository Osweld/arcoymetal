import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { describe, expect, it, vi } from 'vitest';
import { UserFormComponent } from './user-form.component';
import { UserService } from '../../services/user.service';
import { EmployeeService } from '../../../employee/services/employee.service';
import { PageResult } from '../../../../shared/models/pagination.model';
import { EmployeeDetails } from '../../../employee/models/employee.model';

describe('UserFormComponent', () => {
  const mockEmployeesPage: PageResult<EmployeeDetails> = {
    content: [
      {
        employeeId: 'emp-uuid-1',
        firstName: 'Carlos',
        lastName: 'Mendoza',
        email: 'carlos@arcoymetal.com.sv',
        phoneNumber: '71234567',
        gender: 'MALE',
        birthDate: '1995-03-10',
        addressLine: 'Col. Escalón',
        districtId: 'AH0101',
        dui: '012345678',
        nit: null,
        isss: null,
        nup: null,
        pensionFundInstitution: 'AFP_CONFIA',
        emergencyContactName: null,
        emergencyContactPhone: null,
        employmentStatus: 'WITHOUT_PERIOD',
        currentJobTitle: null,
        createdAt: '2026-11-24T12:00:00Z',
      },
    ],
    page: 0,
    size: 100,
    totalElements: 1,
    totalPages: 1,
    first: true,
    last: true,
  };

  it('loads paginated employees using employeeId and submits valid CreateUserRequest', () => {
    const createUser = vi.fn().mockReturnValue(of({ userId: 'usr-uuid-1' }));
    const getEmployeesPage = vi.fn().mockReturnValue(of(mockEmployeesPage));

    TestBed.configureTestingModule({
      imports: [UserFormComponent],
      providers: [
        provideRouter([]),
        { provide: UserService, useValue: { createUser } },
        { provide: EmployeeService, useValue: { getEmployeesPage } },
      ],
    });

    const router = TestBed.inject(Router);
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    const fixture = TestBed.createComponent(UserFormComponent);
    fixture.detectChanges();

    const component = fixture.componentInstance;
    expect(getEmployeesPage).toHaveBeenCalledWith(0, 100);
    expect(component.employees()).toHaveLength(1);

    component.userForm.setValue({
      employeeId: 'emp-uuid-1',
      email: 'carlos@arcoymetal.com.sv',
      password: 'Password123!',
    });
    component.onSubmit();

    expect(createUser).toHaveBeenCalledWith({
      employeeId: 'emp-uuid-1',
      email: 'carlos@arcoymetal.com.sv',
      password: 'Password123!',
      roles: ['MANAGER'],
    });
    expect(navigateSpy).toHaveBeenCalledWith(['/users']);
  });

  it('formats ApiErrorResponse validation and conflict messages on submit error', () => {
    const createUser = vi.fn().mockReturnValue(
      throwError(() => ({
        status: 409,
        error: {
          timestamp: '2026-11-24T12:00:00Z',
          status: 409,
          message: 'User with employeeId emp-uuid-1 already exists.',
          errors: null,
        },
      }))
    );

    TestBed.configureTestingModule({
      imports: [UserFormComponent],
      providers: [
        provideRouter([]),
        { provide: UserService, useValue: { createUser } },
        { provide: EmployeeService, useValue: { getEmployeesPage: () => of(mockEmployeesPage) } },
      ],
    });

    const fixture = TestBed.createComponent(UserFormComponent);
    fixture.detectChanges();

    const component = fixture.componentInstance;
    component.userForm.setValue({
      employeeId: 'emp-uuid-1',
      email: 'carlos@arcoymetal.com.sv',
      password: 'Password123!',
    });
    component.onSubmit();

    expect(component.serverError()).toBe('User with employeeId emp-uuid-1 already exists.');
  });
});
