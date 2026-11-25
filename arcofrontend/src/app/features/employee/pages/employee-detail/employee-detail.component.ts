import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { EmployeeService } from '../../services/employee.service';
import { EmploymentPeriodService } from '../../services/employment-period.service';
import { LocationService } from '../../services/location.service';
import {
  CONTRACT_TYPE_LABELS,
  ContractType,
  CreateEmploymentPeriodRequest,
  EmployeeDetails,
  GENDER_LABELS,
  Gender,
  PENSION_FUND_LABELS,
  PensionFundInstitution,
  UpdateEmployeeRequest,
} from '../../models/employee.model';
import {
  DepartmentDetails,
  DistrictDetails,
  MunicipalityDetails,
  ResolvedLocation,
} from '../../models/location.model';
import { extractApiErrorMessage } from '../../../../shared/models/api-error.model';

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
              {{ editingEmployee() ? 'Cancelar edición' : 'Editar Expediente' }}
            </button>

            <button
              type="button"
              (click)="toggleEmploymentPeriodForm()"
              class="inline-flex items-center gap-1.5 rounded-xl border border-slate-900 bg-slate-900 px-4 py-2 text-xs font-semibold text-white hover:bg-slate-800 cursor-pointer"
            >
              {{ creatingPeriod() ? 'Ocultar Periodo Laboral' : '+ Registrar Periodo Laboral' }}
            </button>

            <a
              [routerLink]="['/users/new']"
              [queryParams]="{ employeeId: emp.employeeId }"
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

        @if (actionError()) {
          <div
            role="alert"
            aria-live="assertive"
            class="rounded-xl border border-rose-300 bg-rose-50 p-4 text-sm text-rose-900"
          >
            {{ actionError() }}
          </div>
        }

        <!-- Summary Banner -->
        <section class="rounded-xl border border-slate-200 bg-white p-6 shadow-2xs">
          <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h3 class="text-xl font-extrabold text-slate-900">
                {{ emp.firstName }} {{ emp.lastName }}
              </h3>
              <p class="mt-1 text-sm text-slate-600">
                {{ emp.email }} &bull; Tel: {{ emp.phoneNumber }}
              </p>
              <p class="mt-1 font-mono text-xs text-slate-400">ID: {{ emp.employeeId }}</p>
            </div>

            <div class="text-left sm:text-right">
              <p class="text-xs font-semibold uppercase tracking-wider text-slate-500">Institución Previsional</p>
              <p class="text-sm font-bold text-slate-900 mt-0.5">
                {{ pensionLabels[emp.pensionFundInstitution] }}
              </p>
              <p class="text-xs text-slate-500 mt-1">
                Registrado: {{ emp.createdAt | date: 'mediumDate' }}
              </p>
            </div>
          </div>
        </section>

        <!-- Last Created Employment Period Confirmation -->
        @if (lastCreatedPeriod(); as period) {
          <section
            role="status"
            class="rounded-xl border border-emerald-200 bg-emerald-50/70 p-5 space-y-2"
          >
            <div class="flex flex-wrap items-center justify-between gap-2">
              <h4 class="text-sm font-bold text-emerald-900">
                Periodo laboral activo registrado correctamente
              </h4>
              <span class="font-mono text-xs text-emerald-800">
                ID: {{ period.employmentPeriodId }}
              </span>
            </div>
            <p class="text-xs text-emerald-900">
              <strong>{{ period.jobTitle }}</strong> &bull; {{ contractLabels[period.contractType] }} &bull;
              Salario base: <strong>{{ period.baseSalary | currency: 'USD' : 'symbol' : '1.2-2' }}</strong> &bull;
              Inicio: {{ period.startDate | date: 'mediumDate' }}
            </p>
          </section>
        }

        <!-- Create Employment Period Form (POST /api/v1/employment-periods) -->
        @if (creatingPeriod()) {
          <form
            [formGroup]="employmentPeriodForm"
            (ngSubmit)="onCreateEmploymentPeriod()"
            method="POST"
            action="/api/v1/employment-periods"
            novalidate
            class="rounded-xl border-2 border-slate-900 bg-white p-6 shadow-sm space-y-5"
          >
            <fieldset class="space-y-4">
              <legend class="text-base font-bold text-slate-900">
                Registrar Periodo Laboral (<code class="font-mono text-xs">POST /api/v1/employment-periods</code>)
              </legend>

              <div class="grid grid-cols-1 gap-4 sm:grid-cols-3">
                <div>
                  <label for="ep-startDate" class="block text-sm font-semibold text-slate-800 mb-1">
                    Fecha de inicio <span class="text-rose-600">*</span>
                  </label>
                  <input
                    id="ep-startDate"
                    name="startDate"
                    type="date"
                    formControlName="startDate"
                    min="1950-01-01"
                    required
                    [attr.aria-describedby]="showPeriodError('startDate') ? 'err-ep-startDate' : null"
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm text-slate-900 focus:border-amber-500 focus:outline-none"
                  />
                  @if (showPeriodError('startDate')) {
                    <p id="err-ep-startDate" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                      La fecha de inicio es obligatoria (desde 1950-01-01).
                    </p>
                  }
                </div>

                <div>
                  <label for="ep-jobTitle" class="block text-sm font-semibold text-slate-800 mb-1">
                    Cargo / Puesto <span class="text-rose-600">*</span>
                  </label>
                  <input
                    id="ep-jobTitle"
                    name="jobTitle"
                    type="text"
                    formControlName="jobTitle"
                    maxlength="100"
                    placeholder="Ej. Soldador MIG, Maestro de Obra..."
                    required
                    [attr.aria-describedby]="showPeriodError('jobTitle') ? 'err-ep-jobTitle' : null"
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm text-slate-900 focus:border-amber-500 focus:outline-none"
                  />
                  @if (showPeriodError('jobTitle')) {
                    <p id="err-ep-jobTitle" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                      El cargo es obligatorio (máximo 100 caracteres).
                    </p>
                  }
                </div>

                <div>
                  <label for="ep-baseSalary" class="block text-sm font-semibold text-slate-800 mb-1">
                    Salario Base (USD $) <span class="text-rose-600">*</span>
                  </label>
                  <input
                    id="ep-baseSalary"
                    name="baseSalary"
                    type="text"
                    inputmode="decimal"
                    formControlName="baseSalary"
                    autocorrect="off"
                    autocapitalize="off"
                    spellcheck="false"
                    placeholder="650.00"
                    required
                    [attr.aria-describedby]="showPeriodError('baseSalary') ? 'err-ep-baseSalary' : 'help-ep-baseSalary'"
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 font-mono text-sm text-slate-900 focus:border-amber-500 focus:outline-none"
                  />
                  <span id="help-ep-baseSalary" class="mt-1 block text-[11px] text-slate-500">
                    Monto no negativo con máximo 2 decimales (ej. 650.00).
                  </span>
                  @if (showPeriodError('baseSalary')) {
                    <p id="err-ep-baseSalary" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                      Ingresa un monto válido >= 0.00 con máximo 2 decimales.
                    </p>
                  }
                </div>
              </div>

              <div>
                <span class="block text-sm font-semibold text-slate-800 mb-2">
                  Modalidad de Contrato <span class="text-rose-600">*</span>
                </span>
                <div class="grid grid-cols-1 gap-2.5 sm:grid-cols-3 lg:grid-cols-5">
                  @for (ct of contractTypeOptions; track ct.value) {
                    <label
                      class="flex items-center gap-2 rounded-lg border px-3 py-2.5 text-xs font-semibold cursor-pointer transition-colors"
                      [class.border-amber-500]="employmentPeriodForm.controls.contractType.value === ct.value"
                      [class.bg-amber-50/70]="employmentPeriodForm.controls.contractType.value === ct.value"
                      [class.border-slate-300]="employmentPeriodForm.controls.contractType.value !== ct.value"
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
            </fieldset>

            <div class="flex justify-end gap-3 pt-2">
              <button
                type="button"
                (click)="toggleEmploymentPeriodForm()"
                class="rounded-lg border border-slate-300 bg-white px-4 py-2 text-xs font-semibold text-slate-700 hover:bg-slate-50 cursor-pointer"
              >
                Cancelar
              </button>
              <button
                type="submit"
                [disabled]="savingPeriod()"
                class="rounded-lg bg-amber-500 px-5 py-2 text-xs font-bold text-slate-950 hover:bg-amber-400 disabled:opacity-60 cursor-pointer"
              >
                {{ savingPeriod() ? 'Registrando periodo...' : 'Crear Periodo Laboral' }}
              </button>
            </div>
          </form>
        }

        <!-- Inline Edit Form for Full Employee Record (PUT /api/v1/employees/{id}) -->
        @if (editingEmployee()) {
          <form
            [formGroup]="editForm"
            (ngSubmit)="onSaveEmployee()"
            method="POST"
            novalidate
            class="rounded-xl border-2 border-amber-400 bg-white p-6 shadow-sm space-y-5"
          >
            <fieldset class="space-y-4">
              <legend class="text-base font-bold text-slate-900">
                Actualizar Expediente del Empleado (<code class="font-mono text-xs">PUT /api/v1/employees/{{ emp.employeeId }}</code>)
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
                    maxlength="100"
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
                    maxlength="100"
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
                    maxlength="254"
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
                    autocorrect="off"
                    autocapitalize="off"
                    spellcheck="false"
                    maxlength="8"
                    formControlName="phoneNumber"
                    required
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm text-slate-900 focus:border-amber-500 focus:outline-none"
                  />
                </div>

                <div>
                  <label for="edit-birthDate" class="block text-sm font-semibold text-slate-800 mb-1">
                    Fecha de nacimiento <span class="text-rose-600">*</span>
                  </label>
                  <input
                    id="edit-birthDate"
                    name="birthDate"
                    type="date"
                    formControlName="birthDate"
                    required
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm text-slate-900 focus:border-amber-500 focus:outline-none"
                  />
                </div>

                <div>
                  <span class="block text-sm font-semibold text-slate-800 mb-1">
                    Género <span class="text-rose-600">*</span>
                  </span>
                  <div class="grid grid-cols-2 gap-2.5">
                    @for (g of genderOptions; track g.value) {
                      <label class="flex items-center gap-2 rounded-lg border border-slate-300 px-3 py-2 text-sm cursor-pointer">
                        <input type="radio" name="gender" [value]="g.value" formControlName="gender" class="accent-amber-600" />
                        <span>{{ g.label }}</span>
                      </label>
                    }
                  </div>
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
                  maxlength="150"
                  formControlName="addressLine"
                  required
                  class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm text-slate-900 focus:border-amber-500 focus:outline-none"
                />
              </div>

              <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div>
                  <label for="edit-dui" class="block text-sm font-semibold text-slate-800 mb-1">
                    DUI <span class="text-rose-600">*</span>
                  </label>
                  <input
                    id="edit-dui"
                    name="dui"
                    type="text"
                    inputmode="numeric"
                    autocorrect="off"
                    autocapitalize="off"
                    spellcheck="false"
                    formControlName="dui"
                    required
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 font-mono text-sm text-slate-900"
                  />
                </div>

                <div>
                  <label for="edit-nit" class="block text-sm font-semibold text-slate-800 mb-1">
                    NIT (Opcional)
                  </label>
                  <input
                    id="edit-nit"
                    name="nit"
                    type="text"
                    inputmode="numeric"
                    autocorrect="off"
                    autocapitalize="off"
                    spellcheck="false"
                    formControlName="nit"
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 font-mono text-sm text-slate-900"
                  />
                </div>

                <div>
                  <label for="edit-isss" class="block text-sm font-semibold text-slate-800 mb-1">
                    ISSS (Opcional)
                  </label>
                  <input
                    id="edit-isss"
                    name="isss"
                    type="text"
                    inputmode="numeric"
                    autocorrect="off"
                    autocapitalize="off"
                    spellcheck="false"
                    formControlName="isss"
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 font-mono text-sm text-slate-900"
                  />
                </div>

                <div>
                  <label for="edit-nup" class="block text-sm font-semibold text-slate-800 mb-1">
                    NUP (Opcional)
                  </label>
                  <input
                    id="edit-nup"
                    name="nup"
                    type="text"
                    inputmode="numeric"
                    autocorrect="off"
                    autocapitalize="off"
                    spellcheck="false"
                    formControlName="nup"
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 font-mono text-sm text-slate-900"
                  />
                </div>
              </div>

              <div>
                <span class="block text-sm font-semibold text-slate-800 mb-2">
                  Institución Previsional (AFP / ISP) <span class="text-rose-600">*</span>
                </span>
                <div class="grid grid-cols-1 gap-2.5 sm:grid-cols-3">
                  @for (pf of pensionFundOptions; track pf.value) {
                    <label class="flex items-center gap-2 rounded-lg border border-slate-300 px-3 py-2 text-sm cursor-pointer">
                      <input type="radio" name="pensionFundInstitution" [value]="pf.value" formControlName="pensionFundInstitution" class="accent-amber-600" />
                      <span>{{ pf.label }}</span>
                    </label>
                  }
                </div>
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
                [disabled]="savingEmployee()"
                class="rounded-lg bg-slate-900 px-5 py-2 text-xs font-bold text-white hover:bg-slate-800 disabled:opacity-60 cursor-pointer"
              >
                {{ savingEmployee() ? 'Actualizando...' : 'Guardar Cambios' }}
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
  private readonly employmentPeriodService = inject(EmploymentPeriodService);
  private readonly locationService = inject(LocationService);
  private readonly fb = inject(FormBuilder);

  employeeId = '';
  readonly employee = signal<EmployeeDetails | null>(null);
  readonly departments = signal<DepartmentDetails[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly actionError = signal<string | null>(null);

  readonly editingEmployee = signal(false);
  readonly savingEmployee = signal(false);
  readonly saveSuccessMessage = signal<string | null>(null);

  readonly creatingPeriod = signal(false);
  readonly savingPeriod = signal(false);
  readonly periodSubmitAttempted = signal(false);
  readonly lastCreatedPeriod = signal<
    (CreateEmploymentPeriodRequest & { employmentPeriodId: string }) | null
  >(null);

  readonly selectedDepartmentId = signal<string>('');
  readonly selectedMunicipalityId = signal<string>('');

  readonly genderLabels = GENDER_LABELS;
  readonly contractLabels = CONTRACT_TYPE_LABELS;
  readonly pensionLabels = PENSION_FUND_LABELS;

  readonly genderOptions: { value: Gender; label: string }[] = [
    { value: 'MALE', label: GENDER_LABELS.MALE },
    { value: 'FEMALE', label: GENDER_LABELS.FEMALE },
  ];

  readonly pensionFundOptions: { value: PensionFundInstitution; label: string }[] = [
    { value: 'AFP_CONFIA', label: PENSION_FUND_LABELS.AFP_CONFIA },
    { value: 'AFP_CRECER', label: PENSION_FUND_LABELS.AFP_CRECER },
    { value: 'ISP', label: PENSION_FUND_LABELS.ISP },
  ];

  readonly contractTypeOptions: { value: ContractType; label: string }[] = [
    { value: 'FULL_TIME', label: CONTRACT_TYPE_LABELS.FULL_TIME },
    { value: 'PART_TIME', label: CONTRACT_TYPE_LABELS.PART_TIME },
    { value: 'CONTRACTOR', label: CONTRACT_TYPE_LABELS.CONTRACTOR },
    { value: 'INTERN', label: CONTRACT_TYPE_LABELS.INTERN },
    { value: 'TEMPORARY', label: CONTRACT_TYPE_LABELS.TEMPORARY },
  ];

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

  readonly editForm = this.fb.nonNullable.group({
    firstName: ['', [Validators.required, Validators.maxLength(100)]],
    lastName: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(254)]],
    phoneNumber: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
    gender: ['MALE' as Gender, [Validators.required]],
    birthDate: ['', [Validators.required]],
    addressLine: ['', [Validators.required, Validators.maxLength(150)]],
    districtId: ['', [Validators.required, Validators.maxLength(10)]],
    dui: ['', [Validators.required, Validators.pattern(/^(?:\d{9}|\d{8}-\d)$/)]],
    nit: ['', [Validators.pattern(/^(?:\d{14}|\d{4}-\d{6}-\d{3}-\d)$/)]],
    isss: ['', [Validators.pattern(/^(?:\d{9}|\d{8}-\d)$/)]],
    nup: ['', [Validators.pattern(/^(?:\d{9}|\d{8}-\d)$/)]],
    pensionFundInstitution: ['AFP_CONFIA' as PensionFundInstitution, [Validators.required]],
  });

  readonly employmentPeriodForm = this.fb.nonNullable.group({
    startDate: [new Date().toISOString().slice(0, 10), [Validators.required]],
    jobTitle: ['', [Validators.required, Validators.maxLength(100)]],
    contractType: ['FULL_TIME' as ContractType, [Validators.required]],
    baseSalary: ['', [Validators.required, Validators.pattern(/^\d+(?:\.\d{1,2})?$/)]],
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
      error: (err) => {
        this.errorMessage.set(
          extractApiErrorMessage(
            err,
            'No se pudo recuperar el expediente del empleado. Verifica que el ID exista y que el backend esté activo.'
          )
        );
        this.loading.set(false);
      },
    });
  }

  toggleEditMode(): void {
    const nextState = !this.editingEmployee();
    this.editingEmployee.set(nextState);
    this.creatingPeriod.set(false);
    this.saveSuccessMessage.set(null);
    this.actionError.set(null);
    const emp = this.employee();
    if (nextState && emp) {
      this.syncFormWithEmployee(emp, this.departments());
    }
  }

  toggleEmploymentPeriodForm(): void {
    const nextState = !this.creatingPeriod();
    this.creatingPeriod.set(nextState);
    this.editingEmployee.set(false);
    this.saveSuccessMessage.set(null);
    this.actionError.set(null);
    this.periodSubmitAttempted.set(false);
  }

  onDepartmentChange(deptId: string): void {
    this.selectedDepartmentId.set(deptId);
    this.selectedMunicipalityId.set('');
    this.editForm.controls.districtId.setValue('');
  }

  onMunicipalityChange(munId: string): void {
    this.selectedMunicipalityId.set(munId);
    this.editForm.controls.districtId.setValue('');
  }

  showPeriodError(controlName: keyof typeof this.employmentPeriodForm.controls): boolean {
    const control = this.employmentPeriodForm.controls[controlName];
    return control.invalid && (control.touched || this.periodSubmitAttempted());
  }

  onSaveEmployee(): void {
    this.actionError.set(null);
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }

    this.savingEmployee.set(true);
    const raw = this.editForm.getRawValue();
    const payload: UpdateEmployeeRequest = {
      firstName: raw.firstName.trim(),
      lastName: raw.lastName.trim(),
      email: raw.email.trim(),
      phoneNumber: raw.phoneNumber.trim(),
      gender: raw.gender,
      birthDate: raw.birthDate,
      addressLine: raw.addressLine.trim(),
      districtId: raw.districtId,
      dui: raw.dui.trim(),
      nit: raw.nit.trim() ? raw.nit.trim() : null,
      isss: raw.isss.trim() ? raw.isss.trim() : null,
      nup: raw.nup.trim() ? raw.nup.trim() : null,
      pensionFundInstitution: raw.pensionFundInstitution,
    };

    this.employeeService.updateEmployee(this.employeeId, payload).subscribe({
      next: () => {
        this.savingEmployee.set(false);
        this.editingEmployee.set(false);
        this.saveSuccessMessage.set('Expediente del empleado actualizado correctamente.');
        this.loadData();
      },
      error: (err) => {
        this.savingEmployee.set(false);
        this.actionError.set(
          extractApiErrorMessage(err, 'Error al actualizar el expediente del empleado.')
        );
      },
    });
  }

  onCreateEmploymentPeriod(): void {
    this.periodSubmitAttempted.set(true);
    this.actionError.set(null);

    if (this.employmentPeriodForm.invalid) {
      this.employmentPeriodForm.markAllAsTouched();
      return;
    }

    this.savingPeriod.set(true);
    const raw = this.employmentPeriodForm.getRawValue();
    const normalizedSalary = Number(Number(raw.baseSalary.trim()).toFixed(2));
    const payload: CreateEmploymentPeriodRequest = {
      employeeId: this.employeeId,
      startDate: raw.startDate,
      jobTitle: raw.jobTitle.trim(),
      contractType: raw.contractType,
      baseSalary: normalizedSalary,
    };

    this.employmentPeriodService.createEmploymentPeriod(payload).subscribe({
      next: (res) => {
        this.savingPeriod.set(false);
        this.creatingPeriod.set(false);
        this.lastCreatedPeriod.set({
          ...payload,
          employmentPeriodId: res.employmentPeriodId,
        });
        this.employmentPeriodForm.reset({
          startDate: new Date().toISOString().slice(0, 10),
          jobTitle: '',
          contractType: 'FULL_TIME',
          baseSalary: '',
        });
      },
      error: (err) => {
        this.savingPeriod.set(false);
        this.actionError.set(
          extractApiErrorMessage(
            err,
            'No se pudo registrar el periodo laboral. Verifica si el empleado ya cuenta con un periodo activo.'
          )
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

    this.editForm.patchValue({
      firstName: emp.firstName,
      lastName: emp.lastName,
      email: emp.email,
      phoneNumber: emp.phoneNumber,
      gender: emp.gender,
      birthDate: emp.birthDate,
      addressLine: emp.addressLine,
      districtId: emp.districtId,
      dui: emp.dui,
      nit: emp.nit ?? '',
      isss: emp.isss ?? '',
      nup: emp.nup ?? '',
      pensionFundInstitution: emp.pensionFundInstitution,
    });
  }
}
