import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/auth/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="min-h-screen bg-slate-950 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div class="sm:mx-auto sm:w-full sm:max-w-md px-4">
        <div class="flex justify-center">
          <div
            class="flex h-28 w-28 items-center justify-center rounded-full bg-white p-1.5 shadow-xl ring-2 ring-cyan-400/40 overflow-hidden"
          >
            <img
              src="images/logo-full.jpg"
              alt="Logo de Arco &amp; Metal Mecánica"
              class="h-full w-full rounded-full object-contain"
            />
          </div>
        </div>
        <h1 class="mt-5 text-center text-2xl font-extrabold tracking-tight text-white">
          Arco &amp; Metal Mecánica
        </h1>
        <p class="mt-1.5 text-center text-sm text-slate-400">
          Metapán, Santa Ana &bull; Sistema Web Administrativo
        </p>
      </div>

      <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md px-4">
        <div class="rounded-2xl bg-slate-900 border border-slate-800 py-8 px-6 shadow-xl sm:px-10">
          <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" method="POST" novalidate class="space-y-6">
            <div>
              <label for="login-email" class="block text-sm font-semibold text-slate-200 mb-1.5">
                Correo electrónico institucional <span class="text-amber-400">*</span>
              </label>
              <input
                id="login-email"
                name="email"
                type="email"
                formControlName="email"
                autocomplete="username"
                autocorrect="off"
                autocapitalize="off"
                spellcheck="false"
                required
                (blur)="markTouched('email')"
                [attr.aria-describedby]="showError('email') ? 'login-email-error' : null"
                class="block w-full rounded-xl border border-slate-700 bg-slate-800/90 px-3.5 py-3 text-base text-white placeholder-slate-500 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/30"
              />
              @if (showError('email')) {
                <p id="login-email-error" role="alert" class="mt-1.5 text-xs font-medium text-rose-400">
                  Ingresa un correo electrónico válido (ejemplo: usuario&#64;arcoymetal.com.sv).
                </p>
              }
            </div>

            <div>
              <div class="flex items-center justify-between mb-1.5">
                <label for="login-password" class="block text-sm font-semibold text-slate-200">
                  Contraseña <span class="text-amber-400">*</span>
                </label>
                <button
                  type="button"
                  (click)="togglePasswordVisibility()"
                  [attr.aria-pressed]="showPassword()"
                  aria-label="Mostrar u ocultar contraseña"
                  aria-describedby="login-toggle-warning"
                  class="text-xs font-semibold text-amber-400 hover:text-amber-300"
                >
                  {{ showPassword() ? 'Ocultar' : 'Mostrar' }}
                </button>
                <span id="login-toggle-warning" class="visually-hidden">
                  Advertencia: esto mostrará tu contraseña en pantalla.
                </span>
              </div>
              <input
                id="login-password"
                name="password"
                [type]="showPassword() ? 'text' : 'password'"
                formControlName="password"
                autocomplete="current-password"
                minlength="8"
                required
                (blur)="markTouched('password')"
                [attr.aria-describedby]="showError('password') ? 'login-password-error' : null"
                class="block w-full rounded-xl border border-slate-700 bg-slate-800/90 px-3.5 py-3 text-base text-white placeholder-slate-500 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/30"
              />
              @if (showError('password')) {
                <p id="login-password-error" role="alert" class="mt-1.5 text-xs font-medium text-rose-400">
                  La contraseña debe tener al menos 8 caracteres.
                </p>
              }
            </div>

            <div class="rounded-xl border border-slate-800 bg-slate-950/60 p-3.5 text-xs text-slate-400 leading-relaxed">
              <strong class="text-amber-400 font-semibold">Nota de integración JWT:</strong>
              Esta vista e interceptor (<code class="font-mono text-slate-300">authInterceptor</code>) están listos para consumir el endpoint <code class="font-mono text-slate-300">POST /api/v1/auth/login</code> y la cookie <code class="font-mono text-slate-300">HttpOnly</code> de Refresh Token.
            </div>

            <div class="space-y-3">
              <button
                type="submit"
                [disabled]="isSubmitting()"
                class="flex w-full justify-center rounded-xl bg-amber-500 px-4 py-3 text-sm font-bold text-slate-950 shadow-sm hover:bg-amber-400 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-amber-400 disabled:opacity-60 transition-colors cursor-pointer"
              >
                {{ isSubmitting() ? 'Verificando credenciales...' : 'Iniciar sesión en el sistema' }}
              </button>

              <a
                routerLink="/dashboard"
                class="flex w-full justify-center rounded-xl border border-slate-700 bg-slate-800/60 px-4 py-2.5 text-xs font-semibold text-slate-300 hover:bg-slate-800 hover:text-white transition-colors"
              >
                Volver al Panel General
              </a>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly showPassword = signal(false);
  readonly isSubmitting = signal(false);
  readonly submitAttempted = signal(false);

  readonly loginForm = this.fb.nonNullable.group({
    email: ['admin@arcoymetal.com.sv', [Validators.required, Validators.email]],
    password: ['Admin1234!', [Validators.required, Validators.minLength(8)]],
  });

  togglePasswordVisibility(): void {
    this.showPassword.update((v) => !v);
  }

  markTouched(controlName: 'email' | 'password'): void {
    this.loginForm.controls[controlName].markAsTouched();
  }

  showError(controlName: 'email' | 'password'): boolean {
    const control = this.loginForm.controls[controlName];
    return control.invalid && (control.touched || this.submitAttempted());
  }

  onSubmit(): void {
    this.submitAttempted.set(true);
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    const { email, password } = this.loginForm.getRawValue();
    this.authService.login({ email, password }).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.isSubmitting.set(false);
      },
    });
  }
}
