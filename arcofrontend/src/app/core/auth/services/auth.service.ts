import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, of, tap } from 'rxjs';
import { Role } from '../../../features/identity-access/models/user.model';

export interface AuthenticatedUser {
  id: string;
  employeeId?: string;
  fullName: string;
  email: string;
  roles: Role[];
}

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface AuthTokenResponse {
  accessToken: string;
  user: AuthenticatedUser;
}

const STORAGE_USER_KEY = 'arco_auth_user';
const STORAGE_TOKEN_KEY = 'arco_access_token';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly _accessToken = signal<string | null>(this.loadStoredToken());
  private readonly _currentUser = signal<AuthenticatedUser | null>(this.loadStoredUser());

  readonly accessToken = this._accessToken.asReadonly();
  readonly currentUser = this._currentUser.asReadonly();
  readonly isAuthenticated = computed(() => this._currentUser() !== null);

  constructor() {
    // Mientras el endpoint /api/v1/auth/login está pendiente en el backend,
    // inicializamos una sesión administrativa de desarrollo por defecto si no existe.
    if (!this._currentUser()) {
      const defaultDevUser: AuthenticatedUser = {
        id: '00000000-0000-0000-0000-000000000001',
        fullName: 'Administrador General',
        email: 'admin@arcoymetal.com.sv',
        roles: ['ADMINISTRATOR'],
      };
      this._currentUser.set(defaultDevUser);
    }
  }

  /**
   * Inicia sesión. Preparado para conectar con POST /api/v1/auth/login cuando
   * el módulo JWT esté activo en Spring Security.
   */
  login(credentials: LoginCredentials): Observable<AuthTokenResponse> {
    const simulatedResponse: AuthTokenResponse = {
      accessToken: 'dev-jwt-preview-token',
      user: {
        id: '00000000-0000-0000-0000-000000000001',
        fullName: credentials.email.split('@')[0].replace('.', ' '),
        email: credentials.email,
        roles: ['ADMINISTRATOR'],
      },
    };

    return of(simulatedResponse).pipe(
      tap((response) => {
        this._accessToken.set(response.accessToken);
        this._currentUser.set(response.user);
        sessionStorage.setItem(STORAGE_TOKEN_KEY, response.accessToken);
        sessionStorage.setItem(STORAGE_USER_KEY, JSON.stringify(response.user));
      })
    );
  }

  logout(): void {
    this._accessToken.set(null);
    this._currentUser.set(null);
    sessionStorage.removeItem(STORAGE_TOKEN_KEY);
    sessionStorage.removeItem(STORAGE_USER_KEY);
    this.router.navigate(['/login']);
  }

  hasRole(role: Role): boolean {
    return this._currentUser()?.roles.includes(role) ?? false;
  }

  private loadStoredToken(): string | null {
    if (typeof sessionStorage === 'undefined') return null;
    return sessionStorage.getItem(STORAGE_TOKEN_KEY);
  }

  private loadStoredUser(): AuthenticatedUser | null {
    if (typeof sessionStorage === 'undefined') return null;
    const raw = sessionStorage.getItem(STORAGE_USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as AuthenticatedUser;
    } catch {
      return null;
    }
  }
}
