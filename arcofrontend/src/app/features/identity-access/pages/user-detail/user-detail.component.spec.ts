import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { describe, expect, it, vi } from 'vitest';
import { UserDetailComponent } from './user-detail.component';
import { UserService } from '../../services/user.service';
import { EmployeeService } from '../../../employee/services/employee.service';
import { UserSummary } from '../../models/user.model';

describe('UserDetailComponent', () => {
  const sampleUser: UserSummary = {
    id: 'usr-1',
    employeeId: 'emp-1',
    email: 'admin@arcoymetal.com.sv',
    roles: ['ADMINISTRATOR'],
    status: 'ACTIVE',
    createdAt: '2026-11-24T12:00:00Z',
  };

  it('loads user and updates email, roles, and status via PUT /api/v1/users/:id', () => {
    const updateUser = vi.fn().mockReturnValue(of({ userId: 'usr-1' }));

    TestBed.configureTestingModule({
      imports: [UserDetailComponent],
      providers: [
        provideRouter([]),
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => 'usr-1' } } } },
        {
          provide: UserService,
          useValue: {
            getUserById: () => of(sampleUser),
            updateUser,
          },
        },
        {
          provide: EmployeeService,
          useValue: {
            getEmployeeById: () =>
              of({
                employeeId: 'emp-1',
                firstName: 'Carlos',
                lastName: 'Mendoza',
                dui: '012345678',
              }),
          },
        },
      ],
    });

    const fixture = TestBed.createComponent(UserDetailComponent);
    fixture.detectChanges();

    const component = fixture.componentInstance;
    expect(component.editForm.controls.email.value).toBe('admin@arcoymetal.com.sv');
    expect(component.selectedRoles()).toEqual(['ADMINISTRATOR']);

    component.toggleRole('MANAGER');
    component.editForm.patchValue({
      email: 'carlos.admin@arcoymetal.com.sv',
      status: 'ACTIVE',
    });
    component.onSubmit();

    expect(updateUser).toHaveBeenCalledWith('usr-1', {
      email: 'carlos.admin@arcoymetal.com.sv',
      roles: ['ADMINISTRATOR', 'MANAGER'],
      status: 'ACTIVE',
    });
  });

  it('shows conflict error when backend rejects modifying the last active administrator', () => {
    const updateUser = vi.fn().mockReturnValue(
      throwError(() => ({
        status: 409,
        error: {
          timestamp: '2026-11-25T20:00:00Z',
          status: 409,
          message:
            'Cannot disable, lock, or remove the ADMINISTRATOR role from the last active administrator.',
          errors: null,
        },
      }))
    );

    TestBed.configureTestingModule({
      imports: [UserDetailComponent],
      providers: [
        provideRouter([]),
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => 'usr-1' } } } },
        {
          provide: UserService,
          useValue: {
            getUserById: () => of(sampleUser),
            updateUser,
          },
        },
        {
          provide: EmployeeService,
          useValue: {
            getEmployeeById: () => throwError(() => ({ status: 404 })),
          },
        },
      ],
    });

    const fixture = TestBed.createComponent(UserDetailComponent);
    fixture.detectChanges();

    const component = fixture.componentInstance;
    component.editForm.patchValue({ status: 'DISABLED' });
    component.onSubmit();

    expect(component.serverError()).toBe(
      'Cannot disable, lock, or remove the ADMINISTRATOR role from the last active administrator.'
    );
  });
});
