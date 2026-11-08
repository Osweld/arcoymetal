import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { UserService } from '../../services/user.service';
import { EmployeeService } from '../../../employee/services/employee.service';
import { CreateUserRequest, ROLE_LABELS, Role } from '../../models/user.model';
import { EmployeeDetails } from '../../../employee/models/employee.model';

@Component({
  selector: 'app-user-form',
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="mx-auto max-w-2xl space-y-6">
      <!-- Header -->
      <div>
        <a
          routerLink="/users"
          class="inline-flex items-center gap-1 text-xs font-semibold text-amber-700 hover:text-amber-800 mb-1"
        >
          &larr; Volver a Cuentas de Usuario
        </a>
        <h2 class="text-xl sm:text-2xl font-bold text-slate-900">
          Crear Cuenta de Usuario
        </h2>
        <p class="text-sm text-slate-500">
          Vincula un empleado existente de <code class="font-mono text-xs">Employee</code> con sus credenciales de acceso y roles en <code class="font-mono text-xs">IdentityAccess</code>.
        </p>
      </div>

      @if (serverError()) {
        <div
          role="alert"
          aria-live="assertive"
          class="rounded-xl border border-rose-300 bg-rose-50 p-4 text-sm text-rose-900"
        >
          <p class="font-bold">No se pudo crear la cuenta de usuario</p>
          <p class="mt-1 text-xs text-rose-800">{{ serverError() }}</p>
        </div>
      }

      <form
        [formGroup]="userForm"
        (ngSubmit)="onSubmit()"
        method="POST"
        action="/api/v1/users"
        novalidate
        class="space-y-6"
      >
        <fieldset class="rounded-xl border border-slate-200 bg-white p-6 shadow-2xs space-y-5">
          <legend class="px-2 text-base font-bold text-slate-900">
            Datos de Vinculación y Credenciales
          </legend>

          <!-- Selección de Empleado -->
          <div>
            <label for="user-employeeId" class="block text-sm font-semibold text-slate-800 mb-1.5">
              Empleado a vincular <span class="text-rose-600">*</span>
            </label>
            <select
              id="user-employeeId"
              name="employeeId"
              formControlName="employeeId"
              (change)="onEmployeeSelected($any($event.target).value)"
              required
              [attr.aria-describedby]="showError('employeeId') ? 'err-employeeId' : 'help-employeeId'"
              class="block w-full rounded-lg border border-slate-300 bg-white px-3.5 py-2.5 text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
            >
              <option value="">Selecciona un empleado registrado...</option>
              @for (emp of employees(); track emp.id) {
                <option [value]="emp.id">
                  {{ emp.firstName }} {{ emp.lastName }} &mdash; {{ emp.jobTitle }} (DUI: {{ emp.dui }})
                </option>
              }
            </select>
            <span id="help-employeeId" class="mt-1 block text-[11px] text-slate-500">
              El backend verifica la existencia mediante el contrato público <code class="font-mono">EmployeeExistenceVerifier</code>.
            </span>
            @if (showError('employeeId')) {
              <p id="err-employeeId" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                Debes seleccionar un empleado válido.
              </p>
            }
          </div>

          <!-- Correo de Acceso -->
          <div>
            <label for="user-email" class="block text-sm font-semibold text-slate-800 mb-1.5">
              Correo electrónico de acceso <span class="text-rose-600">*</span>
            </label>
            <input
              id="user-email"
              name="email"
              type="email"
              formControlName="email"
              autocomplete="username"
              autocorrect="off"
              autocapitalize="off"
              spellcheck="false"
              maxlength="254"
              required
              [attr.aria-describedby]="showError('email') ? 'err-user-email' : null"
              class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
            />
            @if (showError('email')) {
              <p id="err-user-email" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                Ingresa un correo electrónico válido (entre 5 y 254 caracteres).
              </p>
            }
          </div>

          <!-- Contraseña -->
          <div>
            <div class="flex items-center justify-between mb-1.5">
              <label for="user-password" class="block text-sm font-semibold text-slate-800">
                Contraseña inicial <span class="text-rose-600">*</span>
              </label>
              <button
                type="button"
                (click)="togglePassword()"
                [attr.aria-pressed]="showPassword()"
                aria-label="Mostrar u ocultar contraseña"
                aria-describedby="user-password-warning"
                class="text-xs font-semibold text-amber-700 hover:text-amber-800 cursor-pointer"
              >
                {{ showPassword() ? 'Ocultar contraseña' : 'Mostrar contraseña' }}
              </button>
              <span id="user-password-warning" class="visually-hidden">
                Advertencia: esto mostrará la contraseña en pantalla.
              </span>
            </div>
            <input
              id="user-password"
              name="password"
              [type]="showPassword() ? 'text' : 'password'"
              formControlName="password"
              autocomplete="new-password"
              minlength="8"
              maxlength="20"
              required
              [attr.aria-describedby]="showError('password') ? 'err-user-password' : 'help-user-password'"
              class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
            />
            <span id="help-user-password" class="mt-1 block text-[11px] text-slate-500">
              Entre 8 y 20 caracteres. Se almacenará cifrada con <code class="font-mono">BCryptPasswordHasher</code>.
            </span>
            @if (showError('password')) {
              <p id="err-user-password" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                La contraseña debe tener entre 8 y 20 caracteres.
              </p>
            }
          </div>

          <!-- Roles (Checkboxes multi-select) -->
          <div>
            <span class="block text-sm font-semibold text-slate-800 mb-2">
              Roles asignados (RBAC) <span class="text-rose-600">*</span>
            </span>
            <div class="grid grid-cols-1 gap-3 sm:grid-cols-2">
              @for (roleOption of availableRoles; track roleOption.value) {
                <label
                  class="flex items-start gap-3 rounded-xl border p-3.5 cursor-pointer transition-colors"
                  [class.border-amber-500]="selectedRoles().includes(roleOption.value)"
                  [class.bg-amber-50/60]="selectedRoles().includes(roleOption.value)"
                  [class.border-slate-300]="!selectedRoles().includes(roleOption.value)"
                >
                  <input
                    type="checkbox"
                    name="roles"
                    [value]="roleOption.value"
                    [checked]="selectedRoles().includes(roleOption.value)"
                    (change)="onToggleRole(roleOption.value, $any($event.target).checked)"
                    class="mt-0.5 h-4 w-4 accent-amber-600 rounded shrink-0"
                  />
                  <div>
                    <div class="text-sm font-bold text-slate-900">{{ roleOption.label }}</div>
                    <div class="text-xs text-slate-500">{{ roleOption.description }}</div>
                  </div>
                </label>
              }
            </div>
            @if (submitAttempted() && selectedRoles().length === 0) {
              <p role="alert" class="mt-1.5 text-xs font-medium text-rose-600">
                El usuario debe tener al menos un rol asignado (regla de dominio: UserMustHaveAtLeastOneRole).
              </p>
            }
          </div>
        </fieldset>

        <div class="flex items-center justify-end gap-3">
          <a
            routerLink="/users"
            class="rounded-xl border border-slate-300 bg-white px-5 py-2.5 text-sm font-semibold text-slate-700 hover:bg-slate-50"
          >
            Cancelar
          </a>
          <button
            type="submit"
            [disabled]="isSubmitting()"
            class="rounded-xl bg-amber-500 px-6 py-2.5 text-sm font-bold text-slate-950 shadow-xs hover:bg-amber-400 disabled:opacity-60 cursor-pointer"
          >
            {{ isSubmitting() ? 'Creando usuario...' : 'Crear Cuenta de Usuario' }}
          </button>
        </div>
      </form>
    </div>
  `,
})
export class UserFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly employeeService = inject(EmployeeService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly employees = signal<EmployeeDetails[]>([]);
  readonly selectedRoles = signal<Role[]>(['MANAGER']);
  readonly showPassword = signal(false);
  readonly isSubmitting = signal(false);
  readonly submitAttempted = signal(false);
  readonly serverError = signal<string | null>(null);

  readonly availableRoles: { value: Role; label: string; description: string }[] = [
    {
      value: 'ADMINISTRATOR',
      label: ROLE_LABELS.ADMINISTRATOR,
      description: 'Acceso completo a configuración, usuarios, empleados, inventario y planillas.',
    },
    {
      value: 'MANAGER',
      label: ROLE_LABELS.MANAGER,
      description: 'Gestión operativa de personal, consultas e inventario.',
    },
  ];

  readonly userForm = this.fb.nonNullable.group({
    employeeId: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email, Validators.minLength(5), Validators.maxLength(254)]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(20)]],
  });

  ngOnInit(): void {
    const preselectedEmployeeId = this.route.snapshot.queryParamMap.get('employeeId') ?? '';

    this.employeeService.getAllEmployees().subscribe({
      next: (list) => {
        this.employees.set(list);
        if (preselectedEmployeeId) {
          this.userForm.controls.employeeId.setValue(preselectedEmployeeId);
          this.onEmployeeSelected(preselectedEmployeeId);
        }
      },
      error: () => {
        this.serverError.set(
          'No se pudo cargar la lista de empleados desde GET /api/v1/employees.'
        );
      },
    });
  }

  onEmployeeSelected(employeeId: string): void {
    const emp = this.employees().find((e) => e.id === employeeId);
    if (emp && !this.userForm.controls.email.dirty) {
      this.userForm.controls.email.setValue(emp.email);
    }
  }

  onToggleRole(role: Role, checked: boolean): void {
    this.selectedRoles.update((current) => {
      if (checked) {
        return current.includes(role) ? current : [...current, role];
      }
      return current.filter((r) => r !== role);
    });
  }

  togglePassword(): void {
    this.showPassword.update((v) => !v);
  }

  showError(controlName: 'employeeId' | 'email' | 'password'): boolean {
    const control = this.userForm.controls[controlName];
    return control.invalid && (control.touched || this.submitAttempted());
  }

  onSubmit(): void {
    this.submitAttempted.set(true);
    this.serverError.set(null);

    if (this.userForm.invalid || this.selectedRoles().length === 0) {
      this.userForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    const raw = this.userForm.getRawValue();
    const payload: CreateUserRequest = {
      employeeId: raw.employeeId,
      email: raw.email.trim(),
      password: raw.password,
      roles: this.selectedRoles(),
    };

    this.userService.createUser(payload).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.router.navigate(['/users']);
      },
      error: (err) => {
        this.isSubmitting.set(false);
        this.serverError.set(
          err?.error?.message ||
            'No se pudo crear el usuario. Verifica que el empleado no tenga ya una cuenta asignada y que el correo sea único.'
        );
      },
    });
  }
}
