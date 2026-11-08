import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { EmployeeService } from '../../services/employee.service';
import { LocationService } from '../../services/location.service';
import {
  CONTRACT_TYPE_LABELS,
  EMPLOYEE_STATUS_LABELS,
  EmployeeDetails,
  GENDER_LABELS,
  PENSION_FUND_LABELS,
  UpdatePersonalInformationRequest,
} from '../../models/employee.model';
import {
  DepartmentDetails,
  DistrictDetails,
  MunicipalityDetails,
  ResolvedLocation,
} from '../../models/location.model';

@Component({
  selector: 'app-employee-detail',
  imports: [RouterLink, ReactiveFormsModule, CurrencyPipe, DatePipe],
  template: `
    <div class="mx-auto max-w-4xl space-y-6">
      <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <a
            routerLink="/employees"
            class="inline-flex items-center gap-1 text-xs font-semibold text-amber-700 hover:text-amber-800 mb-1"
          >
            &larr; Volver al directorio de empleados
          </a>
          <h2 class="text-xl sm:text-2xl font-bold text-slate-900">
            Expediente del Empleado
          </h2>
        </div>

        @if (employee(); as emp) {
          <div class="flex flex-wrap items-center gap-2.5">
            <button
              type="button"
              (click)="toggleEditMode()"
              class="inline-flex items-center gap-1.5 rounded-xl border border-slate-300 bg-white px-4 py-2 text-xs font-semibold text-slate-800 hover:bg-slate-50 cursor-pointer"
            >
              {{ editingPersonal() ? 'Cancelar edición' : 'Editar Información Personal' }}
            </button>

            <a
              [routerLink]="['/users/new']"
              [queryParams]="{ employeeId: emp.id }"
              class="inline-flex items-center gap-1.5 rounded-xl bg-amber-500 px-4 py-2 text-xs font-bold text-slate-950 hover:bg-amber-400"
            >
              + Asignar Cuenta de Usuario
            </a>
          </div>
        }
      </div>

      @if (loading()) {
        <div class="rounded-xl border border-slate-200 bg-white p-12 text-center text-sm text-slate-500">
          Cargando expediente desde <code class="font-mono text-xs">GET /api/v1/employees/{{ employeeId }}</code>...
        </div>
      } @else if (errorMessage()) {
        <div role="alert" class="rounded-xl border border-rose-200 bg-rose-50 p-6 text-sm text-rose-800">
          {{ errorMessage() }}
        </div>
      } @else if (employee(); as emp) {
        @if (saveSuccessMessage()) {
          <div
            role="status"
            class="rounded-xl border border-emerald-200 bg-emerald-50 p-4 text-sm font-medium text-emerald-900"
          >
            {{ saveSuccessMessage() }}
          </div>
        }

        <!-- Summary Banner -->
        <section class="rounded-xl border border-slate-200 bg-white p-6 shadow-2xs">
          <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <div class="flex items-center gap-3">
                <h3 class="text-xl font-extrabold text-slate-900">
                  {{ emp.firstName }} {{ emp.lastName }}
                </h3>
                <span
                  class="inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold"
                  [class.bg-emerald-100]="emp.status === 'ACTIVE'"
                  [class.text-emerald-800]="emp.status === 'ACTIVE'"
                  [class.bg-amber-100]="emp.status !== 'ACTIVE'"
                  [class.text-amber-800]="emp.status !== 'ACTIVE'"
                >
                  {{ statusLabels[emp.status] }}
                </span>
              </div>
              <p class="mt-1 text-sm text-slate-600">
                {{ emp.jobTitle }} &bull; {{ contractLabels[emp.contractType] }}
              </p>
              <p class="mt-1 font-mono text-xs text-slate-400">ID: {{ emp.id }}</p>
            </div>

            <div class="text-left sm:text-right">
              <p class="text-xs font-semibold uppercase tracking-wider text-slate-500">Salario Base</p>
              <p class="text-2xl font-extrabold text-slate-900">
                {{ emp.baseSalary | currency: 'USD' : 'symbol' : '1.2-2' }}
              </p>
              <p class="text-xs text-slate-500">
                Contratado: {{ emp.hireDate | date: 'mediumDate' }}
              </p>
            </div>
          </div>
        </section>

        <!-- Inline Edit Form for Personal Information (PUT /api/v1/employees/{id}/personal-information) -->
        @if (editingPersonal()) {
          <form
            [formGroup]="personalForm"
            (ngSubmit)="onSavePersonal()"
            method="POST"
            novalidate
            class="rounded-xl border-2 border-amber-400 bg-white p-6 shadow-sm space-y-5"
          >
            <fieldset class="space-y-4">
              <legend class="text-base font-bold text-slate-900">
                Actualizar Información Personal y Domicilio (<code class="font-mono text-xs">PUT /personal-information</code>)
              </legend>

              <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div>
                  <label for="edit-firstName" class="block text-sm font-semibold text-slate-800 mb-1">
                    Nombres <span class="text-rose-600">*</span>
                  </label>
                  <input
                    id="edit-firstName"
                    name="firstName"
                    type="text"
                    formControlName="firstName"
                    required
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm text-slate-900 focus:border-amber-500 focus:outline-none"
                  />
                </div>

                <div>
                  <label for="edit-lastName" class="block text-sm font-semibold text-slate-800 mb-1">
                    Apellidos <span class="text-rose-600">*</span>
                  </label>
                  <input
                    id="edit-lastName"
                    name="lastName"
                    type="text"
                    formControlName="lastName"
                    required
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm text-slate-900 focus:border-amber-500 focus:outline-none"
                  />
                </div>

                <div>
                  <label for="edit-email" class="block text-sm font-semibold text-slate-800 mb-1">
                    Correo electrónico <span class="text-rose-600">*</span>
                  </label>
                  <input
                    id="edit-email"
                    name="email"
                    type="email"
                    formControlName="email"
                    autocorrect="off"
                    autocapitalize="off"
                    spellcheck="false"
                    required
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm text-slate-900 focus:border-amber-500 focus:outline-none"
                  />
                </div>

                <div>
                  <label for="edit-phone" class="block text-sm font-semibold text-slate-800 mb-1">
                    Teléfono (8 dígitos) <span class="text-rose-600">*</span>
                  </label>
                  <input
                    id="edit-phone"
                    name="phoneNumber"
                    type="text"
                    inputmode="numeric"
                    maxlength="8"
                    formControlName="phoneNumber"
                    required
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm text-slate-900 focus:border-amber-500 focus:outline-none"
                  />
                </div>
              </div>

              <div class="grid grid-cols-1 gap-4 sm:grid-cols-3">
                <div>
                  <label for="edit-dept" class="block text-sm font-semibold text-slate-800 mb-1">
                    Departamento <span class="text-rose-600">*</span>
                  </label>
                  <select
                    id="edit-dept"
                    name="departmentId"
                    [value]="selectedDepartmentId()"
                    (change)="onDepartmentChange($any($event.target).value)"
                    class="block w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900"
                  >
                    <option value="">Selecciona...</option>
                    @for (dept of departments(); track dept.id) {
                      <option [value]="dept.id">{{ dept.name }}</option>
                    }
                  </select>
                </div>

                <div>
                  <label for="edit-mun" class="block text-sm font-semibold text-slate-800 mb-1">
                    Municipio <span class="text-rose-600">*</span>
                  </label>
                  <select
                    id="edit-mun"
                    name="municipalityId"
                    [value]="selectedMunicipalityId()"
                    (change)="onMunicipalityChange($any($event.target).value)"
                    class="block w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900"
                  >
                    <option value="">Selecciona...</option>
                    @for (mun of availableMunicipalities(); track mun.id) {
                      <option [value]="mun.id">{{ mun.name }}</option>
                    }
                  </select>
                </div>

                <div>
                  <label for="edit-dist" class="block text-sm font-semibold text-slate-800 mb-1">
                    Distrito <span class="text-rose-600">*</span>
                  </label>
                  <select
                    id="edit-dist"
                    name="districtId"
                    formControlName="districtId"
                    required
                    class="block w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900"
                  >
                    <option value="">Selecciona...</option>
                    @for (dist of availableDistricts(); track dist.id) {
                      <option [value]="dist.id">{{ dist.name }}</option>
                    }
                  </select>
                </div>
              </div>

              <div>
                <label for="edit-address" class="block text-sm font-semibold text-slate-800 mb-1">
                  Dirección exacta <span class="text-rose-600">*</span>
                </label>
                <input
                  id="edit-address"
                  name="addressLine"
                  type="text"
                  formControlName="addressLine"
                  required
                  class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm text-slate-900 focus:border-amber-500 focus:outline-none"
                />
              </div>
            </fieldset>

            <div class="flex justify-end gap-3 pt-2">
              <button
                type="button"
                (click)="toggleEditMode()"
                class="rounded-lg border border-slate-300 bg-white px-4 py-2 text-xs font-semibold text-slate-700 hover:bg-slate-50 cursor-pointer"
              >
                Cancelar
              </button>
              <button
                type="submit"
                [disabled]="savingPersonal()"
                class="rounded-lg bg-slate-900 px-5 py-2 text-xs font-bold text-white hover:bg-slate-800 disabled:opacity-60 cursor-pointer"
              >
                {{ savingPersonal() ? 'Actualizando...' : 'Guardar Cambios Personales' }}
              </button>
            </div>
          </form>
        }

        <!-- Details Grid -->
        <div class="grid grid-cols-1 gap-6 md:grid-cols-2">
          <!-- Personal & Location Card -->
          <section class="rounded-xl border border-slate-200 bg-white p-6 shadow-2xs space-y-4">
            <h4 class="text-sm font-bold uppercase tracking-wider text-slate-500 border-b border-slate-100 pb-2">
              Datos Personales y Ubicación
            </h4>
            <dl class="space-y-3 text-sm">
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500">Correo electrónico</dt>
                <dd class="font-medium text-slate-900 text-right">{{ emp.email }}</dd>
              </div>
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500">Teléfono</dt>
                <dd class="font-medium text-slate-900 text-right">{{ emp.phoneNumber }}</dd>
              </div>
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500">Género</dt>
                <dd class="font-medium text-slate-900 text-right">{{ genderLabels[emp.gender] }}</dd>
              </div>
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500">Fecha de nacimiento</dt>
                <dd class="font-medium text-slate-900 text-right">{{ emp.birthDate | date: 'longDate' }}</dd>
              </div>
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500">Ubicación geográfica</dt>
                <dd class="font-medium text-slate-900 text-right">
                  @if (resolvedLocation(); as loc) {
                    {{ loc.department.name }} / {{ loc.municipality.name }} / {{ loc.district.name }}
                  } @else {
                    Distrito ID: {{ emp.districtId }}
                  }
                </dd>
              </div>
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500">Dirección</dt>
                <dd class="font-medium text-slate-900 text-right">{{ emp.addressLine }}</dd>
              </div>
            </dl>
          </section>

          <!-- Identification & Previsional Card -->
          <section class="rounded-xl border border-slate-200 bg-white p-6 shadow-2xs space-y-4">
            <h4 class="text-sm font-bold uppercase tracking-wider text-slate-500 border-b border-slate-100 pb-2">
              Identificación Legal y Previsional
            </h4>
            <dl class="space-y-3 text-sm">
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500">DUI</dt>
                <dd class="font-mono font-semibold text-slate-900">{{ emp.dui }}</dd>
              </div>
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500">NIT</dt>
                <dd class="font-mono font-medium text-slate-900">{{ emp.nit || 'No registrado' }}</dd>
              </div>
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500">Afiliación ISSS</dt>
                <dd class="font-mono font-medium text-slate-900">{{ emp.isss || 'No registrado' }}</dd>
              </div>
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500">NUP</dt>
                <dd class="font-mono font-medium text-slate-900">{{ emp.nup || 'No registrado' }}</dd>
              </div>
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500">Institución de Pensión</dt>
                <dd class="font-semibold text-slate-900 text-right">
                  {{ pensionLabels[emp.pensionFundInstitution] }}
                </dd>
              </div>
              @if (emp.terminationDate) {
                <div class="flex justify-between gap-4">
                  <dt class="text-rose-600">Fecha de baja</dt>
                  <dd class="font-semibold text-rose-700">{{ emp.terminationDate | date: 'mediumDate' }}</dd>
                </div>
              }
            </dl>
          </section>
        </div>
      }
    </div>
  `,
})
export class EmployeeDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly employeeService = inject(EmployeeService);
  private readonly locationService = inject(LocationService);
  private readonly fb = inject(FormBuilder);

  employeeId = '';
  readonly employee = signal<EmployeeDetails | null>(null);
  readonly departments = signal<DepartmentDetails[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);

  readonly editingPersonal = signal(false);
  readonly savingPersonal = signal(false);
  readonly saveSuccessMessage = signal<string | null>(null);

  readonly selectedDepartmentId = signal<string>('');
  readonly selectedMunicipalityId = signal<string>('');

  readonly genderLabels = GENDER_LABELS;
  readonly contractLabels = CONTRACT_TYPE_LABELS;
  readonly pensionLabels = PENSION_FUND_LABELS;
  readonly statusLabels = EMPLOYEE_STATUS_LABELS;

  readonly resolvedLocation = computed<ResolvedLocation | null>(() => {
    const emp = this.employee();
    const depts = this.departments();
    if (!emp || depts.length === 0) return null;
    return this.locationService.resolveDistrict(depts, emp.districtId);
  });

  readonly availableMunicipalities = computed<MunicipalityDetails[]>(() => {
    const dept = this.departments().find((d) => d.id === this.selectedDepartmentId());
    return dept ? dept.municipalities : [];
  });

  readonly availableDistricts = computed<DistrictDetails[]>(() => {
    const mun = this.availableMunicipalities().find((m) => m.id === this.selectedMunicipalityId());
    return mun ? mun.districts : [];
  });

  readonly personalForm = this.fb.nonNullable.group({
    firstName: ['', [Validators.required, Validators.maxLength(100)]],
    lastName: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(254)]],
    phoneNumber: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
    addressLine: ['', [Validators.required, Validators.maxLength(150)]],
    districtId: ['', [Validators.required]],
  });

  ngOnInit(): void {
    this.employeeId = this.route.snapshot.paramMap.get('id') ?? '';
    this.loadData();
  }

  loadData(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    forkJoin({
      employee: this.employeeService.getEmployeeById(this.employeeId),
      hierarchy: this.locationService.getHierarchy(),
    }).subscribe({
      next: ({ employee, hierarchy }) => {
        this.employee.set(employee);
        this.departments.set(hierarchy);
        this.syncFormWithEmployee(employee, hierarchy);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set(
          'No se pudo recuperar el expediente del empleado. Verifica que el ID exista y que el backend esté activo.'
        );
        this.loading.set(false);
      },
    });
  }

  toggleEditMode(): void {
    const nextState = !this.editingPersonal();
    this.editingPersonal.set(nextState);
    this.saveSuccessMessage.set(null);
    const emp = this.employee();
    if (nextState && emp) {
      this.syncFormWithEmployee(emp, this.departments());
    }
  }

  onDepartmentChange(deptId: string): void {
    this.selectedDepartmentId.set(deptId);
    this.selectedMunicipalityId.set('');
    this.personalForm.controls.districtId.setValue('');
  }

  onMunicipalityChange(munId: string): void {
    this.selectedMunicipalityId.set(munId);
    this.personalForm.controls.districtId.setValue('');
  }

  onSavePersonal(): void {
    if (this.personalForm.invalid) {
      this.personalForm.markAllAsTouched();
      return;
    }

    this.savingPersonal.set(true);
    const raw = this.personalForm.getRawValue();
    const payload: UpdatePersonalInformationRequest = {
      firstName: raw.firstName.trim(),
      lastName: raw.lastName.trim(),
      email: raw.email.trim(),
      phoneNumber: raw.phoneNumber.trim(),
      addressLine: raw.addressLine.trim(),
      districtId: raw.districtId,
    };

    this.employeeService.updatePersonalInformation(this.employeeId, payload).subscribe({
      next: () => {
        this.savingPersonal.set(false);
        this.editingPersonal.set(false);
        this.saveSuccessMessage.set(
          'Información personal y de domicilio actualizada correctamente.'
        );
        this.loadData();
      },
      error: (err) => {
        this.savingPersonal.set(false);
        this.errorMessage.set(
          err?.error?.message || 'Error al actualizar la información personal.'
        );
      },
    });
  }

  private syncFormWithEmployee(emp: EmployeeDetails, hierarchy: DepartmentDetails[]): void {
    const resolved = this.locationService.resolveDistrict(hierarchy, emp.districtId);
    if (resolved) {
      this.selectedDepartmentId.set(resolved.department.id);
      this.selectedMunicipalityId.set(resolved.municipality.id);
    }

    this.personalForm.patchValue({
      firstName: emp.firstName,
      lastName: emp.lastName,
      email: emp.email,
      phoneNumber: emp.phoneNumber,
      addressLine: emp.addressLine,
      districtId: emp.districtId,
    });
  }
}
