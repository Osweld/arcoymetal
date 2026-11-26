import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router, provideRouter } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { AuthService, AuthSessionResponse } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let router: Router;

  const sampleSession: AuthSessionResponse = {
    accessToken: 'jwt-access-123',
    expiresInSeconds: 900,
    user: {
      id: 'u-1',
      employeeId: 'e-1',
      email: 'admin@arcoymetal.com.sv',
      roles: ['ADMINISTRATOR'],
      status: 'ACTIVE',
      mustChangePassword: false,
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('starts unauthenticated and stores Access Token only in memory on login', () => {
    expect(service.isAuthenticated()).toBe(false);
    expect(service.isAdmin()).toBe(false);

    service
      .login({ email: 'admin@arcoymetal.com.sv', password: 'Password123!' })
      .subscribe();

    const req = httpMock.expectOne('/api/v1/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.withCredentials).toBe(true);
    req.flush(sampleSession);

    expect(service.isAuthenticated()).toBe(true);
    expect(service.isAdmin()).toBe(true);
    expect(service.accessToken()).toBe('jwt-access-123');
    expect(service.currentUser()?.email).toBe('admin@arcoymetal.com.sv');
  });

  it('handles 401 silently during initializeSession when no cookie exists', async () => {
    const initPromise = service.initializeSession();

    const req = httpMock.expectOne('/api/v1/auth/refresh');
    expect(req.request.method).toBe('POST');
    expect(req.request.withCredentials).toBe(true);
    req.flush({ status: 401 }, { status: 401, statusText: 'Unauthorized' });

    await initPromise;
    expect(service.isAuthenticated()).toBe(false);
    expect(service.accessToken()).toBeNull();
  });

  it('serializes concurrent refreshToken calls into a single POST /api/v1/auth/refresh', () => {
    let token1 = '';
    let token2 = '';

    service.refreshToken().subscribe((res) => (token1 = res.accessToken));
    service.refreshToken().subscribe((res) => (token2 = res.accessToken));

    const req = httpMock.expectOne('/api/v1/auth/refresh');
    req.flush(sampleSession);

    expect(token1).toBe('jwt-access-123');
    expect(token2).toBe('jwt-access-123');
  });

  it('calls POST /api/v1/auth/logout, clears memory state, and redirects to /login', () => {
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    service.login({ email: 'admin@arcoymetal.com.sv', password: 'Password123!' }).subscribe();
    httpMock.expectOne('/api/v1/auth/login').flush(sampleSession);
    expect(service.isAuthenticated()).toBe(true);

    service.logout();
    const logoutReq = httpMock.expectOne('/api/v1/auth/logout');
    expect(logoutReq.request.method).toBe('POST');
    expect(logoutReq.request.withCredentials).toBe(true);
    logoutReq.flush(null, { status: 204, statusText: 'No Content' });

    expect(service.isAuthenticated()).toBe(false);
    expect(service.accessToken()).toBeNull();
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });
});
