import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { UserService } from './user.service';
import { CreateUserRequest, PageResult, UserSummary } from '../models/user.model';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('requests paginated users from GET /api/v1/users', () => {
    const mockPage: PageResult<UserSummary> = {
      content: [
        {
          id: 'u-1',
          employeeId: 'e-1',
          email: 'admin@arcoymetal.com.sv',
          roles: ['ADMINISTRATOR'],
          status: 'ACTIVE',
          createdAt: '2026-11-24T12:00:00Z',
        },
      ],
      page: 0,
      size: 10,
      totalElements: 1,
      totalPages: 1,
      first: true,
      last: true,
    };

    let received: PageResult<UserSummary> | undefined;
    service.getUsers(0, 10).subscribe((res) => (received = res));

    const req = httpMock.expectOne('/api/v1/users?page=0&size=10');
    expect(req.request.method).toBe('GET');
    req.flush(mockPage);

    expect(received).toEqual(mockPage);
  });

  it('fetches a single user by id from GET /api/v1/users/:id', () => {
    const mockUser: UserSummary = {
      id: 'u-1',
      employeeId: 'e-1',
      email: 'admin@arcoymetal.com.sv',
      roles: ['ADMINISTRATOR'],
      status: 'ACTIVE',
      createdAt: '2026-11-24T12:00:00Z',
    };

    let received: UserSummary | undefined;
    service.getUserById('u-1').subscribe((res) => (received = res));

    const req = httpMock.expectOne('/api/v1/users/u-1');
    expect(req.request.method).toBe('GET');
    req.flush(mockUser);

    expect(received).toEqual(mockUser);
  });

  it('creates a user via POST /api/v1/users with 201 Created response', () => {
    const payload: CreateUserRequest = {
      employeeId: 'e-1',
      email: 'manager@arcoymetal.com.sv',
      password: 'Password123!',
      roles: ['MANAGER'],
    };

    let receivedId: string | undefined;
    service.createUser(payload).subscribe((res) => (receivedId = res.userId));

    const req = httpMock.expectOne('/api/v1/users');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({ userId: 'u-2' }, { status: 201, statusText: 'Created' });

    expect(receivedId).toBe('u-2');
  });

  it('fetches linked employee IDs from GET /api/v1/users/linked-employee-ids', () => {
    let received: string[] | undefined;
    service.getLinkedEmployeeIds().subscribe((res) => (received = res));

    const req = httpMock.expectOne('/api/v1/users/linked-employee-ids');
    expect(req.request.method).toBe('GET');
    req.flush(['e-1', 'e-2']);

    expect(received).toEqual(['e-1', 'e-2']);
  });

  it('fetches user by employeeId from GET /api/v1/users/by-employee/:employeeId', () => {
    const mockUser: UserSummary = {
      id: 'u-1',
      employeeId: 'e-1',
      email: 'admin@arcoymetal.com.sv',
      roles: ['ADMINISTRATOR'],
      status: 'ACTIVE',
      createdAt: '2026-11-24T12:00:00Z',
    };

    let received: UserSummary | undefined;
    service.getUserByEmployeeId('e-1').subscribe((res) => (received = res));

    const req = httpMock.expectOne('/api/v1/users/by-employee/e-1');
    expect(req.request.method).toBe('GET');
    req.flush(mockUser);

    expect(received).toEqual(mockUser);
  });

  it('sends search and status query params to GET /api/v1/users', () => {
    service.getUsers(0, 10, { search: ' admin ', status: 'DISABLED' }).subscribe();

    const req = httpMock.expectOne('/api/v1/users?page=0&size=10&search=admin&status=DISABLED');
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

  it('updates a user via PUT /api/v1/users/:id', () => {
    const payload = {
      email: 'updated@arcoymetal.com.sv',
      roles: ['ADMINISTRATOR', 'MANAGER'] as ('ADMINISTRATOR' | 'MANAGER')[],
      status: 'ACTIVE' as const,
    };

    let receivedId: string | undefined;
    service.updateUser('u-1', payload).subscribe((res) => (receivedId = res.userId));

    const req = httpMock.expectOne('/api/v1/users/u-1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(payload);
    req.flush({ userId: 'u-1' });

    expect(receivedId).toBe('u-1');
  });
});
