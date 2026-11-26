import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

const AUTH_BYPASS_REFRESH_URLS = [
  '/api/v1/auth/login',
  '/api/v1/auth/refresh',
  '/api/v1/auth/logout',
];

/**
 * Adjunta el Access Token JWT en memoria y withCredentials: true hacia /api/**.
 * Ante 401 en endpoints protegidos, intenta una única renovación silenciosa (serializada
 * en AuthService) y reintenta la petición original; excluye /auth/login, /refresh y /logout
 * para evitar bucles infinitos.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  if (!req.url.startsWith('/api')) {
    return next(req);
  }

  const token = authService.accessToken();
  const headers: Record<string, string> = {};
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const clonedRequest = req.clone({
    setHeaders: headers,
    withCredentials: true,
  });

  const isAuthLifecycleEndpoint = AUTH_BYPASS_REFRESH_URLS.some((url) =>
    req.url.startsWith(url)
  );

  return next(clonedRequest).pipe(
    catchError((error: unknown) => {
      if (
        error instanceof HttpErrorResponse &&
        error.status === 401 &&
        !isAuthLifecycleEndpoint
      ) {
        return authService.refreshToken().pipe(
          switchMap((session) => {
            const retriedRequest = req.clone({
              setHeaders: {
                Authorization: `Bearer ${session.accessToken}`,
              },
              withCredentials: true,
            });
            return next(retriedRequest);
          }),
          catchError((refreshError) => {
            authService.clearSessionAndRedirectToLogin();
            return throwError(() => refreshError);
          })
        );
      }

      return throwError(() => error);
    })
  );
};
