import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { EmployeeService } from './employee.service';
import { CreateEmployeeRequest, EmployeeDetails, UpdateEmployeeRequest } from '../models/employee.model';
import { PageResult } from '../../../shared/models/pagination.model';

describe('EmployeeService', () => {
  let service: EmployeeService;
  let httpMock: HttpTestingController;

  const sampleEmployee: EmployeeDetails = {
    employeeId: 'emp-1',
    firstName: 'Carlos',
    lastName: 'Mendoza',
    email: 'carlos@arcoymetal.com.sv',
    phoneNumber: '71234567',
    gender: 'MALE',
    birthDate: '1995-03-10',
    addressLine: 'Col. Escalón #123',
    districtId: 'AH0101',
    dui: '012345678',
    nit: null,
    isss: null,
    nup: null,
    pensionFundInstitution: 'AFP_CONFIA',
    createdAt: '2026-11-24T12:00:00Z',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(EmployeeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('requests paginated employees from GET /api/v1/employees', () => {
    const mockPage: PageResult<EmployeeDetails> = {
      content: [sampleEmployee],
      page: 0,
      size: 20,
      totalElements: 1,
      totalPages: 1,
      first: true,
      last: true,
    };

    let received: PageResult<EmployeeDetails> | undefined;
    service.getEmployeesPage(0, 20).subscribe((res) => (received = res));

    const req = httpMock.expectOne('/api/v1/employees?page=0&size=20');
    expect(req.request.method).toBe('GET');
    req.flush(mockPage);

    expect(received).toEqual(mockPage);
  });

  it('sends search and employmentStatus query params to GET /api/v1/employees', () => {
    service
      .getEmployeesPage(0, 10, { search: ' 01234567-8 ', employmentStatus: 'ACTIVE' })
      .subscribe();

    const req = httpMock.expectOne(
      '/api/v1/employees?page=0&size=10&search=01234567-8&employmentStatus=ACTIVE'
    );
    expect(req.request.method).toBe('GET');
    req.flush({
      content: [],
      page: 0,
      size: 10,
      totalElements: 0,
      totalPages: 0,
      first: true,
      last: true,
    });
  });

  it('fetches employee details from GET /api/v1/employees/:id', () => {
    let received: EmployeeDetails | undefined;
    service.getEmployeeById('emp-1').subscribe((res) => (received = res));

    const req = httpMock.expectOne('/api/v1/employees/emp-1');
    expect(req.request.method).toBe('GET');
    req.flush(sampleEmployee);

    expect(received).toEqual(sampleEmployee);
  });

  it('creates an employee via POST /api/v1/employees', () => {
    const payload: CreateEmployeeRequest = {
      firstName: 'Carlos',
      lastName: 'Mendoza',
      email: 'carlos@arcoymetal.com.sv',
      phoneNumber: '71234567',
      gender: 'MALE',
      birthDate: '1995-03-10',
      addressLine: 'Col. Escalón #123',
      districtId: 'AH0101',
      dui: '01234567-8',
      nit: null,
      isss: null,
      nup: null,
      pensionFundInstitution: 'AFP_CONFIA',
    };

    let receivedId: string | undefined;
    service.createEmployee(payload).subscribe((res) => (receivedId = res.employeeId));

    const req = httpMock.expectOne('/api/v1/employees');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({ employeeId: 'emp-1' }, { status: 201, statusText: 'Created' });

    expect(receivedId).toBe('emp-1');
  });

  it('updates an employee via PUT /api/v1/employees/:id', () => {
    const payload: UpdateEmployeeRequest = {
      firstName: 'Carlos Alberto',
      lastName: 'Mendoza',
      email: 'carlos@arcoymetal.com.sv',
      phoneNumber: '79998888',
      gender: 'MALE',
      birthDate: '1995-03-10',
      addressLine: 'Col. Escalón #456',
      districtId: 'AH0101',
      dui: '012345678',
      nit: null,
      isss: null,
      nup: null,
      pensionFundInstitution: 'AFP_CRECER',
    };

    let receivedId: string | undefined;
    service.updateEmployee('emp-1', payload).subscribe((res) => (receivedId = res.employeeId));

    const req = httpMock.expectOne('/api/v1/employees/emp-1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(payload);
    req.flush({ employeeId: 'emp-1' });

    expect(receivedId).toBe('emp-1');
  });
});
