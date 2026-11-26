import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { catchError, of, switchMap } from 'rxjs';
import { UserService } from '../../services/user.service';
import { EmployeeService } from '../../../employee/services/employee.service';
import {
  ROLE_LABELS,
  Role,
  USER_STATUS_LABELS,
  UpdateUserRequest,
  UserStatus,
  UserSummary,
} from '../../models/user.model';
import { EmployeeDetails } from '../../../employee/models/employee.model';
import { extractApiErrorMessage } from '../../../../shared/models/api-error.model';

@Component({
  selector: 'app-user-detail',
  imports: [RouterLink, ReactiveFormsModule, DatePipe],
  template: `
    <div class="mx-auto max-w-3xl space-y-6">
      <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <a
            routerLink="/users"
            class="inline-flex items-center gap-1 text-xs font-semibold text-amber-700 hover:text-amber-800 mb-1"
          >
            &larr; Volver a cuentas de usuario
          </a>
          <h2 class="text-xl sm:text-2xl font-bold text-slate-900">
            Gestionar Cuenta de Usuario
          </h2>
          <p class="text-sm text-slate-500">
            Actualiza correo de acceso, roles RBAC y estado de la cuenta (<code class="font-mono text-xs">PUT /api/v1/users/{{ userId }}</code>).
          </p>
        </div>
      </div>

      @if (loading()) {
        <div class="rounded-xl border border-slate-200 bg-white p-12 text-center text-sm text-slate-500">
          Cargando cuenta de usuario...
        </div>
      } @else if (loadError()) {
        <div role="alert" class="rounded-xl border border-rose-200 bg-rose-50 p-6 text-sm text-rose-800">
          {{ loadError() }}
        </div>
      } @else if (user(); as current) {
        @if (saveSuccessMessage()) {
          <div
            role="status"
            class="rounded-xl border border-emerald-200 bg-emerald-50 p-4 text-sm font-medium text-emerald-900"
          >
            {{ saveSuccessMessage() }}
          </div>
        }

        @if (serverError()) {
          <div
            role="alert"
            aria-live="assertive"
            class="rounded-xl border border-rose-300 bg-rose-50 p-4 text-sm text-rose-900"
          >
            {{ serverError() }}
          </div>
        }

        <!-- Account Summary & Linked Employee -->
        <section class="rounded-xl border border-slate-200 bg-white p-6 shadow-2xs">
          <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <div class="flex flex-wrap items-center gap-2.5">
                <h3 class="text-lg font-extrabold text-slate-900">{{ current.email }}</h3>
                <span
                  class="inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold"
                  [class.bg-emerald-100]="current.status === 'ACTIVE'"
                  [class.text-emerald-800]="current.status === 'ACTIVE'"
                  [class.bg-slate-100]="current.status === 'DISABLED'"
                  [class.text-slate-700]="current.status === 'DISABLED'"
                  [class.bg-rose-100]="current.status === 'LOCKED'"
                  [class.text-rose-800]="current.status === 'LOCKED'"
                >
                  {{ statusLabels[current.status] }}
                </span>
              </div>
              <p class="mt-1 font-mono text-xs text-slate-400">User ID: {{ current.id }}</p>
              <p class="mt-1 text-xs text-slate-500">
                Creada el {{ current.createdAt | date: 'medium' }}
              </p>
            </div>

            <div class="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3 text-sm">
              <p class="text-[11px] font-bold uppercase tracking-wider text-slate-500">
                Empleado vinculado (1:1 inmutable)
              </p>
              @if (linkedEmployee(); as emp) {
                <a
                  [routerLink]="['/employees', emp.employeeId]"
                  class="mt-0.5 block font-bold text-amber-700 hover:text-amber-800 hover:underline"
                >
                  {{ emp.firstName }} {{ emp.lastName }}
                </a>
                <p class="font-mono text-xs text-slate-500">DUI: {{ emp.dui }}</p>
              } @else {
                <a
                  [routerLink]="['/employees', current.employeeId]"
                  class="mt-0.5 block font-mono text-xs text-amber-700 hover:underline"
                >
                  {{ current.employeeId }}
                </a>
              }
            </div>
          </div>
        </section>

        <!-- Edit Account Form -->
        <form
          [formGroup]="editForm"
          (ngSubmit)="onSubmit()"
          method="POST"
          novalidate
          class="rounded-xl border border-slate-200 bg-white p-6 shadow-2xs space-y-6"
        >
          <fieldset class="space-y-5">
            <legend class="text-base font-bold text-slate-900">
              Configuración de Acceso y Permisos
            </legend>

            <div>
              <label for="edit-user-email" class="block text-sm font-semibold text-slate-800 mb-1.5">
                Correo de acceso <span class="text-rose-600">*</span>
              </label>
              <input
                id="edit-user-email"
                name="email"
                type="email"
                formControlName="email"
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
                  Ingresa un correo electrónico válido (máximo 254 caracteres).
                </p>
              }
            </div>

            <div>
              <span class="block text-sm font-semibold text-slate-800 mb-2">
                Estado de la cuenta <span class="text-rose-600">*</span>
              </span>
              <div class="grid grid-cols-1 gap-3 sm:grid-cols-3">
                @for (st of statusOptions; track st.value) {
                  <label
                    class="flex items-center gap-2.5 rounded-lg border px-3.5 py-3 text-sm font-semibold text-slate-800 cursor-pointer transition-colors"
                    [class.border-amber-500]="editForm.controls.status.value === st.value"
                    [class.bg-amber-50/70]="editForm.controls.status.value === st.value"
                    [class.border-slate-300]="editForm.controls.status.value !== st.value"
                  >
                    <input
                      type="radio"
                      name="status"
                      [value]="st.value"
                      formControlName="status"
                      class="h-4 w-4 accent-amber-600 shrink-0"
                    />
                    <span>{{ st.label }}</span>
                  </label>
                }
              </div>
            </div>

            <div>
              <span class="block text-sm font-semibold text-slate-800 mb-2">
                Roles asignados (RBAC) <span class="text-rose-600">*</span>
              </span>
              <div class="grid grid-cols-1 gap-3 sm:grid-cols-2">
                @for (role of availableRoles; track role.value) {
                  <label
                    class="flex items-start gap-3 rounded-lg border px-4 py-3 text-sm cursor-pointer transition-colors"
                    [class.border-amber-500]="isRoleSelected(role.value)"
                    [class.bg-amber-50/70]="isRoleSelected(role.value)"
                    [class.border-slate-300]="!isRoleSelected(role.value)"
                  >
                    <input
                      type="checkbox"
                      [checked]="isRoleSelected(role.value)"
                      (change)="toggleRole(role.value)"
                      class="mt-0.5 h-4 w-4 accent-amber-600 shrink-0"
                    />
                    <span>
                      <span class="block font-semibold text-slate-900">{{ role.label }}</span>
                      <span class="block text-xs text-slate-500">{{ role.description }}</span>
                    </span>
                  </label>
                }
              </div>
              @if (rolesError()) {
                <p role="alert" class="mt-1.5 text-xs font-medium text-rose-600">
                  La cuenta debe tener al menos un rol asignado.
                </p>
              }
            </div>
          </fieldset>

          <div class="flex flex-wrap items-center justify-between gap-3 border-t border-slate-100 pt-4">
            <p class="text-xs text-slate-500">
              Por seguridad, el sistema impide desactivar o degradar al último Administrador activo.
            </p>
            <div class="flex items-center gap-3">
              <a
                routerLink="/users"
                class="rounded-xl border border-slate-300 bg-white px-4 py-2.5 text-xs font-semibold text-slate-700 hover:bg-slate-50"
              >
                Cancelar
              </a>
              <button
                type="submit"
                [disabled]="saving()"
                class="rounded-xl bg-amber-500 px-5 py-2.5 text-xs font-bold text-slate-950 shadow-xs hover:bg-amber-400 disabled:opacity-60 cursor-pointer"
              >
                {{ saving() ? 'Guardando cambios...' : 'Guardar Cambios' }}
              </button>
            </div>
          </div>
        </form>
      }
    </div>
  `,
})
export class UserDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly userService = inject(UserService);
  private readonly employeeService = inject(EmployeeService);
  private readonly fb = inject(FormBuilder);

  userId = '';
  readonly user = signal<UserSummary | null>(null);
  readonly linkedEmployee = signal<EmployeeDetails | null>(null);
  readonly selectedRoles = signal<Role[]>([]);
  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly submitAttempted = signal(false);
  readonly loadError = signal<string | null>(null);
  readonly serverError = signal<string | null>(null);
  readonly saveSuccessMessage = signal<string | null>(null);

  readonly statusLabels = USER_STATUS_LABELS;

  readonly statusOptions: { value: UserStatus; label: string }[] = [
    { value: 'ACTIVE', label: USER_STATUS_LABELS.ACTIVE },
    { value: 'DISABLED', label: USER_STATUS_LABELS.DISABLED },
    { value: 'LOCKED', label: USER_STATUS_LABELS.LOCKED },
  ];

  readonly availableRoles: { value: Role; label: string; description: string }[] = [
    {
      value: 'ADMINISTRATOR',
      label: ROLE_LABELS.ADMINISTRATOR,
      description: 'Acceso completo al sistema, usuarios y configuración.',
    },
    {
      value: 'MANAGER',
      label: ROLE_LABELS.MANAGER,
      description: 'Gestión operativa de personal y procesos administrativos.',
    },
  ];

  readonly editForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email, Validators.maxLength(254)]],
    status: ['ACTIVE' as UserStatus, [Validators.required]],
  });

  ngOnInit(): void {
    this.userId = this.route.snapshot.paramMap.get('id') ?? '';
    this.loadUser();
  }

  loadUser(): void {
    this.loading.set(true);
    this.loadError.set(null);

    this.userService
      .getUserById(this.userId)
      .pipe(
        switchMap((user) =>
          this.employeeService.getEmployeeById(user.employeeId).pipe(
            catchError(() => of(null)),
            switchMap((emp) => of({ user, emp }))
          )
        )
      )
      .subscribe({
        next: ({ user, emp }) => {
          this.user.set(user);
          this.linkedEmployee.set(emp);
          this.selectedRoles.set([...user.roles]);
          this.editForm.patchValue({
            email: user.email,
            status: user.status,
          });
          this.loading.set(false);
        },
        error: (err) => {
          this.loadError.set(
            extractApiErrorMessage(err, 'No se pudo cargar la cuenta de usuario solicitada.')
          );
          this.loading.set(false);
        },
      });
  }

  isRoleSelected(role: Role): boolean {
    return this.selectedRoles().includes(role);
  }

  toggleRole(role: Role): void {
    this.selectedRoles.update((roles) =>
      roles.includes(role) ? roles.filter((r) => r !== role) : [...roles, role]
    );
  }

  rolesError(): boolean {
    return this.submitAttempted() && this.selectedRoles().length === 0;
  }

  showError(controlName: keyof typeof this.editForm.controls): boolean {
    const control = this.editForm.controls[controlName];
    return control.invalid && (control.touched || this.submitAttempted());
  }

  onSubmit(): void {
    this.submitAttempted.set(true);
    this.serverError.set(null);
    this.saveSuccessMessage.set(null);

    if (this.editForm.invalid || this.selectedRoles().length === 0) {
      this.editForm.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    const raw = this.editForm.getRawValue();
    const payload: UpdateUserRequest = {
      email: raw.email.trim(),
      roles: this.selectedRoles(),
      status: raw.status,
    };

    this.userService.updateUser(this.userId, payload).subscribe({
      next: () => {
        this.saving.set(false);
        this.saveSuccessMessage.set('Cuenta de usuario actualizada correctamente.');
        this.loadUser();
      },
      error: (err) => {
        this.saving.set(false);
        this.serverError.set(
          extractApiErrorMessage(err, 'No se pudo actualizar la cuenta de usuario.')
        );
      },
    });
  }
}
