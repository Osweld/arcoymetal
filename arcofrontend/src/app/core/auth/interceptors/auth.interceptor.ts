import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * Interceptor funcional para adjuntar el Access Token JWT y credenciales (cookies HttpOnly)
 * hacia los endpoints del backend (/api/v1/...).
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.accessToken();

  if (!req.url.startsWith('/api')) {
    return next(req);
  }

  const headers: Record<string, string> = {};
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const clonedRequest = req.clone({
    setHeaders: headers,
    withCredentials: true,
  });

  return next(clonedRequest);
};
