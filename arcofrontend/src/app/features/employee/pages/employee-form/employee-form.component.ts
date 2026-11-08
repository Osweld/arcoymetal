import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { EmployeeService } from '../../services/employee.service';
import { LocationService } from '../../services/location.service';
import {
  CONTRACT_TYPE_LABELS,
  ContractType,
  CreateEmployeeRequest,
  GENDER_LABELS,
  Gender,
  PENSION_FUND_LABELS,
  PensionFundInstitution,
} from '../../models/employee.model';
import {
  DepartmentDetails,
  DistrictDetails,
  MunicipalityDetails,
} from '../../models/location.model';

@Component({
  selector: 'app-employee-form',
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="mx-auto max-w-4xl space-y-6">
      <!-- Header -->
      <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <a
            routerLink="/employees"
            class="inline-flex items-center gap-1 text-xs font-semibold text-amber-700 hover:text-amber-800 mb-1"
          >
            &larr; Volver al directorio de empleados
          </a>
          <h2 class="text-xl sm:text-2xl font-bold text-slate-900">
            Registrar Nuevo Empleado
          </h2>
          <p class="text-sm text-slate-500">
            Completa las secciones de información personal, ubicación geográfica, contratación e identificación legal.
          </p>
        </div>
      </div>

      @if (serverError()) {
        <div
          role="alert"
          aria-live="assertive"
          class="rounded-xl border border-rose-300 bg-rose-50 p-4 text-sm text-rose-900"
        >
          <p class="font-bold">Error al registrar el empleado</p>
          <p class="mt-1 text-xs text-rose-800">{{ serverError() }}</p>
        </div>
      }

      <form
        [formGroup]="employeeForm"
        (ngSubmit)="onSubmit()"
        method="POST"
        action="/api/v1/employees"
        novalidate
        class="space-y-6"
      >
        <!-- 1. Información Personal -->
        <fieldset class="rounded-xl border border-slate-200 bg-white p-6 shadow-2xs space-y-5">
          <legend class="px-2 text-base font-bold text-slate-900">
            1. Información Personal
          </legend>

          <div class="grid grid-cols-1 gap-5 sm:grid-cols-2">
            <div>
              <label for="emp-firstName" class="block text-sm font-semibold text-slate-800 mb-1.5">
                Nombres <span class="text-rose-600">*</span>
              </label>
              <input
                id="emp-firstName"
                name="firstName"
                type="text"
                formControlName="firstName"
                autocomplete="given-name"
                maxlength="100"
                required
                [attr.aria-describedby]="showError('firstName') ? 'err-firstName' : null"
                class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
              />
              @if (showError('firstName')) {
                <p id="err-firstName" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                  El nombre es obligatorio (máximo 100 caracteres).
                </p>
              }
            </div>

            <div>
              <label for="emp-lastName" class="block text-sm font-semibold text-slate-800 mb-1.5">
                Apellidos <span class="text-rose-600">*</span>
              </label>
              <input
                id="emp-lastName"
                name="lastName"
                type="text"
                formControlName="lastName"
                autocomplete="family-name"
                maxlength="100"
                required
                [attr.aria-describedby]="showError('lastName') ? 'err-lastName' : null"
                class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
              />
              @if (showError('lastName')) {
                <p id="err-lastName" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                  El apellido es obligatorio (máximo 100 caracteres).
                </p>
              }
            </div>

            <div>
              <label for="emp-email" class="block text-sm font-semibold text-slate-800 mb-1.5">
                Correo electrónico <span class="text-rose-600">*</span>
              </label>
              <input
                id="emp-email"
                name="email"
                type="email"
                formControlName="email"
                autocomplete="email"
                autocorrect="off"
                autocapitalize="off"
                spellcheck="false"
                maxlength="254"
                required
                [attr.aria-describedby]="showError('email') ? 'err-email' : null"
                class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
              />
              @if (showError('email')) {
                <p id="err-email" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                  Ingresa un correo electrónico válido.
                </p>
              }
            </div>

            <div>
              <label for="emp-phone" class="block text-sm font-semibold text-slate-800 mb-1.5">
                Teléfono celular (8 dígitos sin guion) <span class="text-rose-600">*</span>
              </label>
              <input
                id="emp-phone"
                name="phoneNumber"
                type="text"
                inputmode="numeric"
                formControlName="phoneNumber"
                autocomplete="tel-national"
                autocorrect="off"
                autocapitalize="off"
                spellcheck="false"
                maxlength="8"
                placeholder="70001234"
                required
                [attr.aria-describedby]="showError('phoneNumber') ? 'err-phone' : 'help-phone'"
                class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
              />
              <span id="help-phone" class="mt-1 block text-[11px] text-slate-500">
                Exactamente 8 dígitos numéricos (ej. 71234567).
              </span>
              @if (showError('phoneNumber')) {
                <p id="err-phone" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                  El teléfono debe contener exactamente 8 dígitos numéricos.
                </p>
              }
            </div>

            <div>
              <label for="emp-birthDate" class="block text-sm font-semibold text-slate-800 mb-1.5">
                Fecha de nacimiento <span class="text-rose-600">*</span>
              </label>
              <input
                id="emp-birthDate"
                name="birthDate"
                type="date"
                formControlName="birthDate"
                autocomplete="bday"
                required
                [attr.aria-describedby]="showError('birthDate') ? 'err-birthDate' : null"
                class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
              />
              @if (showError('birthDate')) {
                <p id="err-birthDate" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                  Indica una fecha de nacimiento válida en el pasado.
                </p>
              }
            </div>

            <div>
              <span class="block text-sm font-semibold text-slate-800 mb-1.5">
                Género <span class="text-rose-600">*</span>
              </span>
              <div class="grid grid-cols-2 gap-3 pt-0.5">
                @for (g of genderOptions; track g.value) {
                  <label
                    class="flex items-center gap-2.5 rounded-lg border px-3.5 py-2.5 text-sm font-medium cursor-pointer transition-colors"
                    [class.border-amber-500]="employeeForm.controls.gender.value === g.value"
                    [class.bg-amber-50/60]="employeeForm.controls.gender.value === g.value"
                    [class.border-slate-300]="employeeForm.controls.gender.value !== g.value"
                  >
                    <input
                      type="radio"
                      name="gender"
                      [value]="g.value"
                      formControlName="gender"
                      class="h-4 w-4 accent-amber-600"
                    />
                    <span>{{ g.label }}</span>
                  </label>
                }
              </div>
            </div>
          </div>
        </fieldset>

        <!-- 2. Ubicación Geográfica -->
        <fieldset class="rounded-xl border border-slate-200 bg-white p-6 shadow-2xs space-y-5">
          <legend class="px-2 text-base font-bold text-slate-900">
            2. Ubicación Geográfica y Domicilio
          </legend>

          <div class="grid grid-cols-1 gap-5 sm:grid-cols-3">
            <div>
              <label for="emp-department" class="block text-sm font-semibold text-slate-800 mb-1.5">
                Departamento <span class="text-rose-600">*</span>
              </label>
              <select
                id="emp-department"
                name="departmentId"
                [value]="selectedDepartmentId()"
                (change)="onDepartmentChange($any($event.target).value)"
                class="block w-full rounded-lg border border-slate-300 bg-white px-3.5 py-2.5 text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
              >
                <option value="">Selecciona departamento...</option>
                @for (dept of departments(); track dept.id) {
                  <option [value]="dept.id">{{ dept.name }}</option>
                }
              </select>
            </div>

            <div>
              <label for="emp-municipality" class="block text-sm font-semibold text-slate-800 mb-1.5">
                Municipio <span class="text-rose-600">*</span>
              </label>
              <select
                id="emp-municipality"
                name="municipalityId"
                [value]="selectedMunicipalityId()"
                [disabled]="availableMunicipalities().length === 0"
                (change)="onMunicipalityChange($any($event.target).value)"
                class="block w-full rounded-lg border border-slate-300 bg-white px-3.5 py-2.5 text-base sm:text-sm text-slate-900 disabled:bg-slate-100 disabled:text-slate-400 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
              >
                <option value="">Selecciona municipio...</option>
                @for (mun of availableMunicipalities(); track mun.id) {
                  <option [value]="mun.id">{{ mun.name }}</option>
                }
              </select>
            </div>

            <div>
              <label for="emp-district" class="block text-sm font-semibold text-slate-800 mb-1.5">
                Distrito <span class="text-rose-600">*</span>
              </label>
              <select
                id="emp-district"
                name="districtId"
                formControlName="districtId"
                [disabled]="availableDistricts().length === 0"
                required
                [attr.aria-describedby]="showError('districtId') ? 'err-district' : null"
                class="block w-full rounded-lg border border-slate-300 bg-white px-3.5 py-2.5 text-base sm:text-sm text-slate-900 disabled:bg-slate-100 disabled:text-slate-400 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
              >
                <option value="">Selecciona distrito...</option>
                @for (dist of availableDistricts(); track dist.id) {
                  <option [value]="dist.id">{{ dist.name }}</option>
                }
              </select>
              @if (showError('districtId')) {
                <p id="err-district" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                  Debes seleccionar un distrito geográfico.
                </p>
              }
            </div>

            <div class="sm:col-span-3">
              <label for="emp-addressLine" class="block text-sm font-semibold text-slate-800 mb-1.5">
                Dirección exacta de residencia <span class="text-rose-600">*</span>
              </label>
              <input
                id="emp-addressLine"
                name="addressLine"
                type="text"
                formControlName="addressLine"
                autocomplete="street-address"
                maxlength="150"
                placeholder="Colonia, calle, pasaje y número de casa..."
                required
                [attr.aria-describedby]="showError('addressLine') ? 'err-addressLine' : null"
                class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
              />
              @if (showError('addressLine')) {
                <p id="err-addressLine" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                  La dirección es obligatoria (máximo 150 caracteres).
                </p>
              }
            </div>
          </div>
        </fieldset>

        <!-- 3. Información Laboral y Contractual -->
        <fieldset class="rounded-xl border border-slate-200 bg-white p-6 shadow-2xs space-y-5">
          <legend class="px-2 text-base font-bold text-slate-900">
            3. Información Laboral y Salarial
          </legend>

          <div class="grid grid-cols-1 gap-5 sm:grid-cols-3">
            <div>
              <label for="emp-jobTitle" class="block text-sm font-semibold text-slate-800 mb-1.5">
                Cargo / Puesto <span class="text-rose-600">*</span>
              </label>
              <input
                id="emp-jobTitle"
                name="jobTitle"
                type="text"
                formControlName="jobTitle"
                maxlength="100"
                placeholder="Ej. Maestro de Obra, Soldador MIG..."
                required
                [attr.aria-describedby]="showError('jobTitle') ? 'err-jobTitle' : null"
                class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
              />
              @if (showError('jobTitle')) {
                <p id="err-jobTitle" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                  El cargo es obligatorio.
                </p>
              }
            </div>

            <div>
              <label for="emp-baseSalary" class="block text-sm font-semibold text-slate-800 mb-1.5">
                Salario Base Mensual (USD $) <span class="text-rose-600">*</span>
              </label>
              <input
                id="emp-baseSalary"
                name="baseSalary"
                type="number"
                step="0.01"
                min="0"
                formControlName="baseSalary"
                required
                [attr.aria-describedby]="showError('baseSalary') ? 'err-baseSalary' : null"
                class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
              />
              @if (showError('baseSalary')) {
                <p id="err-baseSalary" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                  Ingresa un salario base mayor o igual a $0.00.
                </p>
              }
            </div>

            <div>
              <label for="emp-hireDate" class="block text-sm font-semibold text-slate-800 mb-1.5">
                Fecha de Contratación <span class="text-rose-600">*</span>
              </label>
              <input
                id="emp-hireDate"
                name="hireDate"
                type="date"
                formControlName="hireDate"
                required
                [attr.aria-describedby]="showError('hireDate') ? 'err-hireDate' : null"
                class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
              />
              @if (showError('hireDate')) {
                <p id="err-hireDate" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                  La fecha de contratación es obligatoria.
                </p>
              }
            </div>

            <div class="sm:col-span-3">
              <span class="block text-sm font-semibold text-slate-800 mb-2">
                Modalidad de Contrato <span class="text-rose-600">*</span>
              </span>
              <div class="grid grid-cols-1 gap-2.5 sm:grid-cols-3 lg:grid-cols-5">
                @for (ct of contractTypeOptions; track ct.value) {
                  <label
                    class="flex items-center gap-2 rounded-lg border px-3 py-2.5 text-xs font-semibold cursor-pointer transition-colors"
                    [class.border-amber-500]="employeeForm.controls.contractType.value === ct.value"
                    [class.bg-amber-50/70]="employeeForm.controls.contractType.value === ct.value"
                    [class.border-slate-300]="employeeForm.controls.contractType.value !== ct.value"
                  >
                    <input
                      type="radio"
                      name="contractType"
                      [value]="ct.value"
                      formControlName="contractType"
                      class="h-4 w-4 accent-amber-600 shrink-0"
                    />
                    <span>{{ ct.label }}</span>
                  </label>
                }
              </div>
            </div>
          </div>
        </fieldset>

        <!-- 4. Identificación Legal y Previsional -->
        <fieldset class="rounded-xl border border-slate-200 bg-white p-6 shadow-2xs space-y-5">
          <legend class="px-2 text-base font-bold text-slate-900">
            4. Identificación Legal y Previsional (El Salvador)
          </legend>

          <div class="grid grid-cols-1 gap-5 sm:grid-cols-2">
            <div>
              <label for="emp-dui" class="block text-sm font-semibold text-slate-800 mb-1.5">
                DUI <span class="text-rose-600">*</span>
              </label>
              <input
                id="emp-dui"
                name="dui"
                type="text"
                inputmode="numeric"
                formControlName="dui"
                autocorrect="off"
                autocapitalize="off"
                spellcheck="false"
                placeholder="01234567-8"
                required
                [attr.aria-describedby]="showError('dui') ? 'err-dui' : 'help-dui'"
                class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 font-mono text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
              />
              <span id="help-dui" class="mt-1 block text-[11px] text-slate-500">
                Formato 00000000-0 o 9 dígitos continuos.
              </span>
              @if (showError('dui')) {
                <p id="err-dui" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                  Formato de DUI inválido (usa 01234567-8 o 012345678).
                </p>
              }
            </div>

            <div>
              <label for="emp-nit" class="block text-sm font-semibold text-slate-800 mb-1.5">
                NIT <span class="text-xs font-normal text-slate-500">(Opcional)</span>
              </label>
              <input
                id="emp-nit"
                name="nit"
                type="text"
                inputmode="numeric"
                formControlName="nit"
                autocorrect="off"
                autocapitalize="off"
                spellcheck="false"
                placeholder="0614-010190-101-2"
                [attr.aria-describedby]="showError('nit') ? 'err-nit' : 'help-nit'"
                class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 font-mono text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
              />
              <span id="help-nit" class="mt-1 block text-[11px] text-slate-500">
                Formato 0000-000000-000-0 o 14 dígitos.
              </span>
              @if (showError('nit')) {
                <p id="err-nit" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                  Formato de NIT inválido.
                </p>
              }
            </div>

            <div>
              <label for="emp-isss" class="block text-sm font-semibold text-slate-800 mb-1.5">
                Número de afiliación ISSS <span class="text-xs font-normal text-slate-500">(Opcional)</span>
              </label>
              <input
                id="emp-isss"
                name="isss"
                type="text"
                inputmode="numeric"
                formControlName="isss"
                autocorrect="off"
                autocapitalize="off"
                spellcheck="false"
                placeholder="123456789"
                [attr.aria-describedby]="showError('isss') ? 'err-isss' : null"
                class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 font-mono text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
              />
              @if (showError('isss')) {
                <p id="err-isss" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                  Debe contener 9 dígitos (o formato 00000000-0).
                </p>
              }
            </div>

            <div>
              <label for="emp-nup" class="block text-sm font-semibold text-slate-800 mb-1.5">
                NUP (Número Único Previsional) <span class="text-xs font-normal text-slate-500">(Opcional)</span>
              </label>
              <input
                id="emp-nup"
                name="nup"
                type="text"
                inputmode="numeric"
                formControlName="nup"
                autocorrect="off"
                autocapitalize="off"
                spellcheck="false"
                placeholder="123456789"
                [attr.aria-describedby]="showError('nup') ? 'err-nup' : null"
                class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 font-mono text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
              />
              @if (showError('nup')) {
                <p id="err-nup" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                  Debe contener 9 dígitos (o formato 00000000-0).
                </p>
              }
            </div>

            <div class="sm:col-span-2">
              <span class="block text-sm font-semibold text-slate-800 mb-2">
                Institución Previsional (AFP / ISP) <span class="text-rose-600">*</span>
              </span>
              <div class="grid grid-cols-1 gap-3 sm:grid-cols-3">
                @for (pf of pensionFundOptions; track pf.value) {
                  <label
                    class="flex items-center gap-2.5 rounded-lg border px-3.5 py-3 text-sm font-medium cursor-pointer transition-colors"
                    [class.border-amber-500]="employeeForm.controls.pensionFundInstitution.value === pf.value"
                    [class.bg-amber-50/70]="employeeForm.controls.pensionFundInstitution.value === pf.value"
                    [class.border-slate-300]="employeeForm.controls.pensionFundInstitution.value !== pf.value"
                  >
                    <input
                      type="radio"
                      name="pensionFundInstitution"
                      [value]="pf.value"
                      formControlName="pensionFundInstitution"
                      class="h-4 w-4 accent-amber-600 shrink-0"
                    />
                    <span>{{ pf.label }}</span>
                  </label>
                }
              </div>
            </div>
          </div>
        </fieldset>

        <!-- Actions -->
        <div class="flex items-center justify-end gap-3 pt-2">
          <a
            routerLink="/employees"
            class="rounded-xl border border-slate-300 bg-white px-5 py-2.5 text-sm font-semibold text-slate-700 hover:bg-slate-50"
          >
            Cancelar
          </a>
          <button
            type="submit"
            [disabled]="isSubmitting()"
            class="rounded-xl bg-amber-500 px-6 py-2.5 text-sm font-bold text-slate-950 shadow-xs hover:bg-amber-400 disabled:opacity-60 cursor-pointer"
          >
            {{ isSubmitting() ? 'Guardando expediente...' : 'Guardar Empleado' }}
          </button>
        </div>
      </form>
    </div>
  `,
})
export class EmployeeFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly employeeService = inject(EmployeeService);
  private readonly locationService = inject(LocationService);
  private readonly router = inject(Router);

  readonly departments = signal<DepartmentDetails[]>([]);
  readonly selectedDepartmentId = signal<string>('');
  readonly selectedMunicipalityId = signal<string>('');

  readonly isSubmitting = signal(false);
  readonly submitAttempted = signal(false);
  readonly serverError = signal<string | null>(null);

  readonly availableMunicipalities = computed<MunicipalityDetails[]>(() => {
    const deptId = this.selectedDepartmentId();
    const dept = this.departments().find((d) => d.id === deptId);
    return dept ? dept.municipalities : [];
  });

  readonly availableDistricts = computed<DistrictDetails[]>(() => {
    const munId = this.selectedMunicipalityId();
    const mun = this.availableMunicipalities().find((m) => m.id === munId);
    return mun ? mun.districts : [];
  });

  readonly genderOptions: { value: Gender; label: string }[] = [
    { value: 'MALE', label: GENDER_LABELS.MALE },
    { value: 'FEMALE', label: GENDER_LABELS.FEMALE },
  ];

  readonly contractTypeOptions: { value: ContractType; label: string }[] = [
    { value: 'FULL_TIME', label: CONTRACT_TYPE_LABELS.FULL_TIME },
    { value: 'PART_TIME', label: CONTRACT_TYPE_LABELS.PART_TIME },
    { value: 'CONTRACTOR', label: CONTRACT_TYPE_LABELS.CONTRACTOR },
    { value: 'INTERN', label: CONTRACT_TYPE_LABELS.INTERN },
    { value: 'TEMPORARY', label: CONTRACT_TYPE_LABELS.TEMPORARY },
  ];

  readonly pensionFundOptions: { value: PensionFundInstitution; label: string }[] = [
    { value: 'AFP_CONFIA', label: PENSION_FUND_LABELS.AFP_CONFIA },
    { value: 'AFP_CRECER', label: PENSION_FUND_LABELS.AFP_CRECER },
    { value: 'ISP', label: PENSION_FUND_LABELS.ISP },
  ];

  readonly employeeForm = this.fb.nonNullable.group({
    firstName: ['', [Validators.required, Validators.maxLength(100)]],
    lastName: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(254)]],
    phoneNumber: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
    gender: ['MALE' as Gender, [Validators.required]],
    birthDate: ['', [Validators.required]],
    addressLine: ['', [Validators.required, Validators.maxLength(150)]],
    districtId: ['', [Validators.required]],
    jobTitle: ['', [Validators.required, Validators.maxLength(100)]],
    baseSalary: [450.0, [Validators.required, Validators.min(0)]],
    contractType: ['FULL_TIME' as ContractType, [Validators.required]],
    dui: ['', [Validators.required, Validators.pattern(/^(?:\d{9}|\d{8}-\d)$/)]],
    nit: ['', [Validators.pattern(/^(?:\d{14}|\d{4}-\d{6}-\d{3}-\d)$/)]],
    isss: ['', [Validators.pattern(/^(?:\d{9}|\d{8}-\d)$/)]],
    nup: ['', [Validators.pattern(/^(?:\d{9}|\d{8}-\d)$/)]],
    pensionFundInstitution: ['AFP_CONFIA' as PensionFundInstitution, [Validators.required]],
    hireDate: [new Date().toISOString().slice(0, 10), [Validators.required]],
  });

  ngOnInit(): void {
    this.locationService.getHierarchy().subscribe({
      next: (hierarchy) => {
        this.departments.set(hierarchy);
      },
      error: () => {
        this.serverError.set(
          'No se pudo cargar el catálogo geográfico desde GET /api/v1/locations/hierarchy.'
        );
      },
    });
  }

  onDepartmentChange(departmentId: string): void {
    this.selectedDepartmentId.set(departmentId);
    this.selectedMunicipalityId.set('');
    this.employeeForm.controls.districtId.setValue('');
  }

  onMunicipalityChange(municipalityId: string): void {
    this.selectedMunicipalityId.set(municipalityId);
    this.employeeForm.controls.districtId.setValue('');
  }

  showError(controlName: keyof typeof this.employeeForm.controls): boolean {
    const control = this.employeeForm.controls[controlName];
    return control.invalid && (control.touched || this.submitAttempted());
  }

  onSubmit(): void {
    this.submitAttempted.set(true);
    this.serverError.set(null);

    if (this.employeeForm.invalid) {
      this.employeeForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    const raw = this.employeeForm.getRawValue();

    const payload: CreateEmployeeRequest = {
      firstName: raw.firstName.trim(),
      lastName: raw.lastName.trim(),
      email: raw.email.trim(),
      phoneNumber: raw.phoneNumber.trim(),
      gender: raw.gender,
      birthDate: raw.birthDate,
      addressLine: raw.addressLine.trim(),
      districtId: raw.districtId,
      jobTitle: raw.jobTitle.trim(),
      baseSalary: Number(raw.baseSalary),
      contractType: raw.contractType,
      dui: raw.dui.trim(),
      nit: raw.nit.trim() ? raw.nit.trim() : null,
      isss: raw.isss.trim() ? raw.isss.trim() : null,
      nup: raw.nup.trim() ? raw.nup.trim() : null,
      pensionFundInstitution: raw.pensionFundInstitution,
      hireDate: raw.hireDate,
    };

    this.employeeService.createEmployee(payload).subscribe({
      next: (res) => {
        this.isSubmitting.set(false);
        this.router.navigate(['/employees', res.employeeId]);
      },
      error: (err) => {
        this.isSubmitting.set(false);
        this.serverError.set(
          err?.error?.message ||
            'No se pudo guardar el empleado. Revisa que el DUI/correo no estén duplicados y que los datos cumplan las reglas del dominio.'
        );
      },
    });
  }
}
