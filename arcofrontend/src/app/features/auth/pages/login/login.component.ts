import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/auth/services/auth.service';
import { extractApiErrorMessage } from '../../../../shared/models/api-error.model';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
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
          @if (authError()) {
            <div
              role="alert"
              aria-live="assertive"
              class="mb-6 rounded-xl border border-rose-500/40 bg-rose-950/60 p-3.5 text-xs font-medium text-rose-200"
            >
              {{ authError() }}
            </div>
          }

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
                placeholder="usuario@arcoymetal.com.sv"
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
                  class="text-xs font-semibold text-amber-400 hover:text-amber-300 cursor-pointer"
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
                required
                (blur)="markTouched('password')"
                [attr.aria-describedby]="showError('password') ? 'login-password-error' : null"
                class="block w-full rounded-xl border border-slate-700 bg-slate-800/90 px-3.5 py-3 text-base text-white placeholder-slate-500 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/30"
              />
              @if (showError('password')) {
                <p id="login-password-error" role="alert" class="mt-1.5 text-xs font-medium text-rose-400">
                  Ingresa tu contraseña.
                </p>
              }
            </div>

            <button
              type="submit"
              [disabled]="isSubmitting()"
              class="flex w-full justify-center rounded-xl bg-amber-500 px-4 py-3 text-sm font-bold text-slate-950 shadow-sm hover:bg-amber-400 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-amber-400 disabled:opacity-60 transition-colors cursor-pointer"
            >
              {{ isSubmitting() ? 'Verificando credenciales...' : 'Iniciar sesión en el sistema' }}
            </button>
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
  readonly authError = signal<string | null>(null);

  readonly loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
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
    this.authError.set(null);

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    const { email, password } = this.loginForm.getRawValue();
    this.authService.login({ email: email.trim(), password }).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.isSubmitting.set(false);
        this.authError.set(
          extractApiErrorMessage(err, 'No se pudo iniciar sesión. Verifica tus credenciales.')
        );
      },
    });
  }
}
