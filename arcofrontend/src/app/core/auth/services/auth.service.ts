import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import {
  Observable,
  catchError,
  finalize,
  firstValueFrom,
  of,
  shareReplay,
  tap,
} from 'rxjs';
import { Role, UserStatus } from '../../../features/identity-access/models/user.model';

export interface AuthenticatedUser {
  id: string;
  employeeId: string;
  email: string;
  roles: Role[];
  status: UserStatus;
  mustChangePassword: boolean;
}

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface AuthSessionResponse {
  accessToken: string;
  expiresInSeconds: number;
  user: AuthenticatedUser;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly authUrl = '/api/v1/auth';

  private readonly _accessToken = signal<string | null>(null);
  private readonly _currentUser = signal<AuthenticatedUser | null>(null);
  private refreshInProgress$: Observable<AuthSessionResponse> | null = null;

  readonly accessToken = this._accessToken.asReadonly();
  readonly currentUser = this._currentUser.asReadonly();
  readonly isAuthenticated = computed(
    () => this._currentUser() !== null && this._accessToken() !== null
  );
  readonly isAdmin = computed(() => this.hasRole('ADMINISTRATOR'));

  /**
   * Restaura silenciosamente la sesión desde la cookie HttpOnly al iniciar/recargar la app.
   * Si no existe cookie o responde 401, limpia el estado sin propagar error para que
   * authGuard redirija limpiamente a /login.
   */
  async initializeSession(): Promise<void> {
    await firstValueFrom(
      this.refreshToken().pipe(
        catchError(() => {
          this.clearSessionState();
          return of(null);
        })
      )
    );
  }

  login(credentials: LoginCredentials): Observable<AuthSessionResponse> {
    return this.http
      .post<AuthSessionResponse>(`${this.authUrl}/login`, credentials, {
        withCredentials: true,
      })
      .pipe(tap((session) => this.applySession(session)));
  }

  /**
   * Renueva el Access Token en memoria usando la cookie HttpOnly.
   * Serializa llamadas concurrentes (ej. múltiples 401 simultáneos) en una única petición HTTP.
   */
  refreshToken(): Observable<AuthSessionResponse> {
    if (!this.refreshInProgress$) {
      this.refreshInProgress$ = this.http
        .post<AuthSessionResponse>(
          `${this.authUrl}/refresh`,
          {},
          { withCredentials: true }
        )
        .pipe(
          tap((session) => this.applySession(session)),
          finalize(() => {
            this.refreshInProgress$ = null;
          }),
          shareReplay(1)
        );
    }
    return this.refreshInProgress$;
  }

  fetchMe(): Observable<AuthenticatedUser> {
    return this.http
      .get<AuthenticatedUser>(`${this.authUrl}/me`, { withCredentials: true })
      .pipe(tap((user) => this._currentUser.set(user)));
  }

  logout(): void {
    this.http
      .post<void>(`${this.authUrl}/logout`, {}, { withCredentials: true })
      .pipe(
        catchError(() => of(void 0)),
        finalize(() => {
          this.clearSessionState();
          this.router.navigate(['/login']);
        })
      )
      .subscribe();
  }

  clearSessionState(): void {
    this._accessToken.set(null);
    this._currentUser.set(null);
  }

  clearSessionAndRedirectToLogin(): void {
    this.clearSessionState();
    this.router.navigate(['/login']);
  }

  hasRole(role: Role): boolean {
    return this._currentUser()?.roles.includes(role) ?? false;
  }

  private applySession(session: AuthSessionResponse): void {
    this._accessToken.set(session.accessToken);
    this._currentUser.set(session.user);
  }
}
