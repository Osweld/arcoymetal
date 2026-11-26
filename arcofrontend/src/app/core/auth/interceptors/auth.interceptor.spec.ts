import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from '../services/auth.service';

describe('authInterceptor', () => {
  afterEach(() => {
    TestBed.inject(HttpTestingController).verify();
  });

  it('attaches Bearer token and withCredentials to /api requests', () => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        {
          provide: AuthService,
          useValue: { accessToken: () => 'jwt-token-123' },
        },
      ],
    });

    const http = TestBed.inject(HttpClient);
    const httpMock = TestBed.inject(HttpTestingController);

    http.get('/api/v1/users').subscribe();

    const req = httpMock.expectOne('/api/v1/users');
    expect(req.request.headers.get('Authorization')).toBe('Bearer jwt-token-123');
    expect(req.request.withCredentials).toBe(true);
    req.flush({});
  });

  it('does not attach Authorization header to non-/api requests', () => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        {
          provide: AuthService,
          useValue: { accessToken: () => 'jwt-token-123' },
        },
      ],
    });

    const http = TestBed.inject(HttpClient);
    const httpMock = TestBed.inject(HttpTestingController);

    http.get('/assets/logo.svg').subscribe();

    const req = httpMock.expectOne('/assets/logo.svg');
    expect(req.request.headers.has('Authorization')).toBe(false);
    expect(req.request.withCredentials).toBe(false);
    req.flush('');
  });

  it('refreshes silently and retries once on 401 for protected endpoints', () => {
    const refreshToken = vi.fn().mockReturnValue(
      of({
        accessToken: 'new-jwt-456',
        expiresInSeconds: 900,
        user: {
          id: 'u-1',
          employeeId: 'e-1',
          email: 'admin@arcoymetal.com.sv',
          roles: ['ADMINISTRATOR'],
          status: 'ACTIVE',
          mustChangePassword: false,
        },
      })
    );
    const clearSessionAndRedirectToLogin = vi.fn();

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        {
          provide: AuthService,
          useValue: {
            accessToken: () => 'expired-jwt-123',
            refreshToken,
            clearSessionAndRedirectToLogin,
          },
        },
      ],
    });

    const http = TestBed.inject(HttpClient);
    const httpMock = TestBed.inject(HttpTestingController);

    let result: unknown;
    http.get('/api/v1/employees').subscribe((res) => (result = res));

    const firstReq = httpMock.expectOne('/api/v1/employees');
    expect(firstReq.request.headers.get('Authorization')).toBe('Bearer expired-jwt-123');
    firstReq.flush({ status: 401 }, { status: 401, statusText: 'Unauthorized' });

    const retryReq = httpMock.expectOne('/api/v1/employees');
    expect(retryReq.request.headers.get('Authorization')).toBe('Bearer new-jwt-456');
    retryReq.flush({ ok: true });

    expect(refreshToken).toHaveBeenCalledTimes(1);
    expect(clearSessionAndRedirectToLogin).not.toHaveBeenCalled();
    expect(result).toEqual({ ok: true });
  });

  it('does not trigger refresh loop when /api/v1/auth/refresh or /login returns 401', () => {
    const refreshToken = vi.fn();
    const clearSessionAndRedirectToLogin = vi.fn();

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        {
          provide: AuthService,
          useValue: {
            accessToken: () => null,
            refreshToken,
            clearSessionAndRedirectToLogin,
          },
        },
      ],
    });

    const http = TestBed.inject(HttpClient);
    const httpMock = TestBed.inject(HttpTestingController);

    let failedStatus = 0;
    http.post('/api/v1/auth/refresh', {}).subscribe({
      error: (err) => (failedStatus = err.status),
    });

    const req = httpMock.expectOne('/api/v1/auth/refresh');
    req.flush({ status: 401 }, { status: 401, statusText: 'Unauthorized' });

    expect(failedStatus).toBe(401);
    expect(refreshToken).not.toHaveBeenCalled();
    expect(clearSessionAndRedirectToLogin).not.toHaveBeenCalled();
  });

  it('clears session and redirects to /login when silent refresh fails after 401', () => {
    const refreshToken = vi.fn().mockReturnValue(throwError(() => ({ status: 401 })));
    const clearSessionAndRedirectToLogin = vi.fn();

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        {
          provide: AuthService,
          useValue: {
            accessToken: () => 'expired-jwt',
            refreshToken,
            clearSessionAndRedirectToLogin,
          },
        },
      ],
    });

    const http = TestBed.inject(HttpClient);
    const httpMock = TestBed.inject(HttpTestingController);

    let errored = false;
    http.get('/api/v1/employees').subscribe({
      error: () => (errored = true),
    });

    const req = httpMock.expectOne('/api/v1/employees');
    req.flush({ status: 401 }, { status: 401, statusText: 'Unauthorized' });

    expect(errored).toBe(true);
    expect(refreshToken).toHaveBeenCalledTimes(1);
    expect(clearSessionAndRedirectToLogin).toHaveBeenCalledTimes(1);
  });
});
