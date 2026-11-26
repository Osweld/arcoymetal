import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import { EmployeeService } from '../../services/employee.service';
import { EmploymentPeriodService } from '../../services/employment-period.service';
import { LocationService } from '../../services/location.service';
import { UserService } from '../../../identity-access/services/user.service';
import { UserSummary } from '../../../identity-access/models/user.model';
import { AuthService } from '../../../../core/auth/services/auth.service';
import {
  CONTRACT_TYPE_LABELS,
  ContractType,
  CreateEmploymentPeriodRequest,
  EMPLOYMENT_STATUS_LABELS,
  EmployeeDetails,
  EmploymentChangeDetails,
  EmploymentPeriodDetails,
  GENDER_LABELS,
  Gender,
  PENSION_FUND_LABELS,
  PensionFundInstitution,
  RegisterEmploymentChangeRequest,
  TERMINATION_REASON_LABELS,
  TerminateEmploymentPeriodRequest,
  TerminationReason,
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
  imports: [RouterLink, ReactiveFormsModule, CurrencyPipe, DatePipe, DecimalPipe],
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
          <div class="flex flex-wrap items-center gap-2">
            <button
              type="button"
              (click)="toggleEditMode()"
              class="inline-flex items-center gap-1.5 rounded-xl border border-slate-300 bg-white px-3.5 py-2 text-xs font-semibold text-slate-800 hover:bg-slate-50 cursor-pointer"
            >
              {{ activePanel() === 'edit' ? 'Cancelar edición' : 'Editar Expediente' }}
            </button>

            @if (activePeriod()) {
              <button
                type="button"
                (click)="togglePanel('change')"
                class="inline-flex items-center gap-1.5 rounded-xl border border-slate-900 bg-slate-900 px-3.5 py-2 text-xs font-semibold text-white hover:bg-slate-800 cursor-pointer"
              >
                {{ activePanel() === 'change' ? 'Ocultar Cambio' : '+ Registrar Cambio Laboral' }}
              </button>

              <button
                type="button"
                (click)="togglePanel('terminate')"
                class="inline-flex items-center gap-1.5 rounded-xl border border-rose-300 bg-rose-50 px-3.5 py-2 text-xs font-semibold text-rose-800 hover:bg-rose-100 cursor-pointer"
              >
                {{ activePanel() === 'terminate' ? 'Cancelar Baja' : 'Dar de Baja' }}
              </button>
            } @else {
              <button
                type="button"
                (click)="togglePanel('period')"
                class="inline-flex items-center gap-1.5 rounded-xl border border-slate-900 bg-slate-900 px-3.5 py-2 text-xs font-semibold text-white hover:bg-slate-800 cursor-pointer"
              >
                {{ activePanel() === 'period' ? 'Ocultar Periodo' : '+ Registrar Periodo Laboral' }}
              </button>
            }

            @if (authService.isAdmin()) {
              @if (linkedUser(); as user) {
                <span
                  class="inline-flex items-center gap-1.5 rounded-xl border border-emerald-200 bg-emerald-50 px-3.5 py-2 text-xs font-semibold text-emerald-800"
                  title="Este empleado ya cuenta con una cuenta de acceso al sistema"
                >
                  Cuenta: {{ user.email }}
                </span>
              } @else {
                <a
                  [routerLink]="['/users/new']"
                  [queryParams]="{ employeeId: emp.employeeId }"
                  class="inline-flex items-center gap-1.5 rounded-xl bg-amber-500 px-3.5 py-2 text-xs font-bold text-slate-950 hover:bg-amber-400"
                >
                  + Usuario
                </a>
              }
            }
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

        <!-- Summary & Current Employment Banner -->
        <section class="rounded-xl border border-slate-200 bg-white p-6 shadow-2xs">
          <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <div class="flex flex-wrap items-center gap-2.5">
                <h3 class="text-xl font-extrabold text-slate-900">
                  {{ emp.firstName }} {{ emp.lastName }}
                </h3>
                @if (activePeriod()) {
                  <span class="inline-flex items-center rounded-full bg-emerald-100 px-2.5 py-0.5 text-xs font-semibold text-emerald-800">
                    {{ statusLabels.ACTIVE }}
                  </span>
                } @else {
                  <span class="inline-flex items-center rounded-full bg-slate-100 px-2.5 py-0.5 text-xs font-semibold text-slate-700">
                    Sin periodo activo
                  </span>
                }
              </div>

              @if (currentEmployment(); as current) {
                <p class="mt-1 text-sm font-medium text-slate-700">
                  {{ current.jobTitle }} &bull; {{ contractLabels[current.contractType] }}
                </p>
              } @else {
                <p class="mt-1 text-sm text-slate-500">
                  {{ emp.email }} &bull; Tel: {{ emp.phoneNumber }}
                </p>
              }
              <p class="mt-1 font-mono text-xs text-slate-400">ID: {{ emp.employeeId }}</p>
            </div>

            <div class="text-left sm:text-right">
              @if (currentEmployment(); as current) {
                <p class="text-xs font-semibold uppercase tracking-wider text-slate-500">Salario Base Vigente</p>
                <p class="text-2xl font-extrabold text-slate-900">
                  {{ current.baseSalary | currency: 'USD' : 'symbol' : '1.2-2' }}
                </p>
                <p class="text-xs text-slate-500">
                  Vigente desde {{ current.effectiveFrom | date: 'mediumDate' }}
                </p>
              } @else {
                <p class="text-xs font-semibold uppercase tracking-wider text-slate-500">Institución Previsional</p>
                <p class="text-sm font-bold text-slate-900 mt-0.5">
                  {{ pensionLabels[emp.pensionFundInstitution] }}
                </p>
                <p class="text-xs text-slate-500 mt-1">
                  Registrado: {{ emp.createdAt | date: 'mediumDate' }}
                </p>
              }
            </div>
          </div>
        </section>

        <!-- Form: Create Employment Period (POST /api/v1/employment-periods) -->
        @if (activePanel() === 'period') {
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
                      La fecha de inicio es obligatoria.
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
                    Monto >= 0.00 con máximo 2 decimales.
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
                      class="flex items-center gap-2 rounded-lg border px-3 py-2.5 text-xs font-semibold text-slate-800 cursor-pointer transition-colors"
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
                (click)="togglePanel('period')"
                class="rounded-lg border border-slate-300 bg-white px-4 py-2 text-xs font-semibold text-slate-700 hover:bg-slate-50 cursor-pointer"
              >
                Cancelar
              </button>
              <button
                type="submit"
                [disabled]="savingAction()"
                class="rounded-lg bg-amber-500 px-5 py-2 text-xs font-bold text-slate-950 hover:bg-amber-400 disabled:opacity-60 cursor-pointer"
              >
                {{ savingAction() ? 'Registrando periodo...' : 'Crear Periodo Laboral' }}
              </button>
            </div>
          </form>
        }

        <!-- Form: Register Employment Change (POST /api/v1/employment-periods/{id}/changes) -->
        @if (activePanel() === 'change' && activePeriod()) {
          <form
            [formGroup]="registerChangeForm"
            (ngSubmit)="onRegisterChange()"
            method="POST"
            novalidate
            class="rounded-xl border-2 border-slate-900 bg-white p-6 shadow-sm space-y-5"
          >
            <fieldset class="space-y-4">
              <legend class="text-base font-bold text-slate-900">
                Registrar Cambio Laboral (Salario / Puesto / Contrato)
              </legend>

              <div class="grid grid-cols-1 gap-4 sm:grid-cols-3">
                <div>
                  <label for="chg-effectiveFrom" class="block text-sm font-semibold text-slate-800 mb-1">
                    Fecha de vigencia <span class="text-rose-600">*</span>
                  </label>
                  <input
                    id="chg-effectiveFrom"
                    name="effectiveFrom"
                    type="date"
                    formControlName="effectiveFrom"
                    [min]="activePeriod()?.startDate ?? '1950-01-01'"
                    required
                    [attr.aria-describedby]="showChangeError('effectiveFrom') || isEffectiveFromBeforePeriodStart() ? 'err-chg-effectiveFrom' : null"
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm text-slate-900 focus:border-amber-500 focus:outline-none"
                  />
                  @if (showChangeError('effectiveFrom')) {
                    <p id="err-chg-effectiveFrom" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                      La fecha de vigencia es obligatoria.
                    </p>
                  } @else if (isEffectiveFromBeforePeriodStart()) {
                    <p id="err-chg-effectiveFrom" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                      La fecha de vigencia no puede ser anterior al inicio del periodo ({{ activePeriod()?.startDate }}).
                    </p>
                  }
                </div>

                <div>
                  <label for="chg-jobTitle" class="block text-sm font-semibold text-slate-800 mb-1">
                    Nuevo Cargo / Puesto <span class="text-rose-600">*</span>
                  </label>
                  <input
                    id="chg-jobTitle"
                    name="jobTitle"
                    type="text"
                    formControlName="jobTitle"
                    maxlength="100"
                    required
                    [attr.aria-describedby]="showChangeError('jobTitle') ? 'err-chg-jobTitle' : null"
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm text-slate-900 focus:border-amber-500 focus:outline-none"
                  />
                  @if (showChangeError('jobTitle')) {
                    <p id="err-chg-jobTitle" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                      El cargo es obligatorio (máximo 100 caracteres).
                    </p>
                  }
                </div>

                <div>
                  <label for="chg-baseSalary" class="block text-sm font-semibold text-slate-800 mb-1">
                    Nuevo Salario Base (USD $) <span class="text-rose-600">*</span>
                  </label>
                  <input
                    id="chg-baseSalary"
                    name="baseSalary"
                    type="text"
                    inputmode="decimal"
                    formControlName="baseSalary"
                    autocorrect="off"
                    autocapitalize="off"
                    spellcheck="false"
                    placeholder="750.00"
                    required
                    [attr.aria-describedby]="showChangeError('baseSalary') ? 'err-chg-baseSalary' : null"
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 font-mono text-sm text-slate-900 focus:border-amber-500 focus:outline-none"
                  />
                  @if (showChangeError('baseSalary')) {
                    <p id="err-chg-baseSalary" role="alert" class="mt-1 text-xs font-medium text-rose-600">
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
                      class="flex items-center gap-2 rounded-lg border px-3 py-2.5 text-xs font-semibold text-slate-800 cursor-pointer transition-colors"
                      [class.border-amber-500]="registerChangeForm.controls.contractType.value === ct.value"
                      [class.bg-amber-50/70]="registerChangeForm.controls.contractType.value === ct.value"
                      [class.border-slate-300]="registerChangeForm.controls.contractType.value !== ct.value"
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
                (click)="togglePanel('change')"
                class="rounded-lg border border-slate-300 bg-white px-4 py-2 text-xs font-semibold text-slate-700 hover:bg-slate-50 cursor-pointer"
              >
                Cancelar
              </button>
              <button
                type="submit"
                [disabled]="savingAction()"
                class="rounded-lg bg-amber-500 px-5 py-2 text-xs font-bold text-slate-950 hover:bg-amber-400 disabled:opacity-60 cursor-pointer"
              >
                {{ savingAction() ? 'Guardando cambio...' : 'Guardar Cambio Histórico' }}
              </button>
            </div>
          </form>
        }

        <!-- Form: Terminate Employment Period (POST /api/v1/employment-periods/{id}/terminate) -->
        @if (activePanel() === 'terminate' && activePeriod()) {
          <form
            [formGroup]="terminateForm"
            (ngSubmit)="onTerminatePeriod()"
            method="POST"
            novalidate
            class="rounded-xl border-2 border-rose-400 bg-white p-6 shadow-sm space-y-5"
          >
            <fieldset class="space-y-4">
              <legend class="text-base font-bold text-rose-900">
                Finalizar Periodo Laboral Activo (Baja)
              </legend>

              <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div>
                  <label for="term-endDate" class="block text-sm font-semibold text-slate-800 mb-1">
                    Fecha de baja <span class="text-rose-600">*</span>
                  </label>
                  <input
                    id="term-endDate"
                    name="endDate"
                    type="date"
                    formControlName="endDate"
                    [min]="activePeriod()?.startDate ?? '1950-01-01'"
                    required
                    [attr.aria-describedby]="showTerminateError('endDate') || isEndDateBeforePeriodStart() ? 'err-term-endDate' : null"
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm text-slate-900 focus:border-rose-500 focus:outline-none"
                  />
                  @if (showTerminateError('endDate')) {
                    <p id="err-term-endDate" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                      La fecha de baja es obligatoria.
                    </p>
                  } @else if (isEndDateBeforePeriodStart()) {
                    <p id="err-term-endDate" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                      La fecha de baja no puede ser anterior al inicio del periodo ({{ activePeriod()?.startDate }}).
                    </p>
                  }
                </div>

                <div>
                  <label for="term-reason" class="block text-sm font-semibold text-slate-800 mb-1">
                    Motivo de terminación <span class="text-rose-600">*</span>
                  </label>
                  <select
                    id="term-reason"
                    name="terminationReason"
                    formControlName="terminationReason"
                    required
                    class="block w-full rounded-lg border border-slate-300 bg-white px-3.5 py-2.5 text-sm text-slate-900 focus:border-rose-500 focus:outline-none"
                  >
                    @for (reason of terminationReasonOptions; track reason.value) {
                      <option [value]="reason.value">{{ reason.label }}</option>
                    }
                  </select>
                </div>
              </div>
            </fieldset>

            <div class="flex justify-end gap-3 pt-2">
              <button
                type="button"
                (click)="togglePanel('terminate')"
                class="rounded-lg border border-slate-300 bg-white px-4 py-2 text-xs font-semibold text-slate-700 hover:bg-slate-50 cursor-pointer"
              >
                Cancelar
              </button>
              <button
                type="submit"
                [disabled]="savingAction()"
                class="rounded-lg bg-rose-600 px-5 py-2 text-xs font-bold text-white hover:bg-rose-700 disabled:opacity-60 cursor-pointer"
              >
                {{ savingAction() ? 'Finalizando periodo...' : 'Confirmar Baja Laboral' }}
              </button>
            </div>
          </form>
        }

        <!-- Form: Edit Full Employee Record (PUT /api/v1/employees/{id}) -->
        @if (activePanel() === 'edit') {
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
                    [attr.aria-describedby]="showEditError('birthDate') ? 'err-edit-birthDate' : null"
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm text-slate-900 focus:border-amber-500 focus:outline-none"
                  />
                  @if (showEditError('birthDate')) {
                    <p id="err-edit-birthDate" role="alert" class="mt-1 text-xs font-medium text-rose-600">
                      @if (editForm.controls.birthDate.hasError('futureDate')) {
                        La fecha de nacimiento no puede ser futura.
                      } @else if (editForm.controls.birthDate.hasError('underage')) {
                        El empleado debe tener al menos 18 años cumplidos.
                      } @else {
                        Indica una fecha de nacimiento válida.
                      }
                    </p>
                  }
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
                    [formControl]="departmentControl"
                    (change)="onDepartmentChange(departmentControl.value)"
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
                    [formControl]="municipalityControl"
                    (change)="onMunicipalityChange(municipalityControl.value)"
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

              <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 border-t border-slate-100 pt-4">
                <div>
                  <label for="edit-emergencyContactName" class="block text-sm font-semibold text-slate-800 mb-1">
                    Contacto de emergencia — Nombre (Opcional)
                  </label>
                  <input
                    id="edit-emergencyContactName"
                    name="emergencyContactName"
                    type="text"
                    maxlength="150"
                    formControlName="emergencyContactName"
                    placeholder="Ej. María López (Esposa)"
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm text-slate-900 focus:border-amber-500 focus:outline-none"
                  />
                </div>

                <div>
                  <label for="edit-emergencyContactPhone" class="block text-sm font-semibold text-slate-800 mb-1">
                    Contacto de emergencia — Teléfono (8 dígitos)
                  </label>
                  <input
                    id="edit-emergencyContactPhone"
                    name="emergencyContactPhone"
                    type="text"
                    inputmode="numeric"
                    autocorrect="off"
                    autocapitalize="off"
                    spellcheck="false"
                    maxlength="8"
                    formControlName="emergencyContactPhone"
                    placeholder="70009999"
                    class="block w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm text-slate-900 focus:border-amber-500 focus:outline-none"
                  />
                  @if (showEditError('emergencyContactPhone')) {
                    <p role="alert" class="mt-1 text-xs font-medium text-rose-600">
                      El teléfono de emergencia debe contener exactamente 8 dígitos numéricos.
                    </p>
                  }
                </div>
              </div>

              @if (editForm.hasError('incompleteEmergencyContact') && (editSubmitAttempted() || editForm.controls.emergencyContactName.touched || editForm.controls.emergencyContactPhone.touched)) {
                <p role="alert" class="text-xs font-medium text-rose-600">
                  Si registras contacto de emergencia, debes completar tanto el nombre como el teléfono de 8 dígitos.
                </p>
              }
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
                [disabled]="savingAction()"
                class="rounded-lg bg-slate-900 px-5 py-2 text-xs font-bold text-white hover:bg-slate-800 disabled:opacity-60 cursor-pointer"
              >
                {{ savingAction() ? 'Actualizando...' : 'Guardar Cambios' }}
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
              <div class="flex justify-between gap-4 border-t border-slate-100 pt-3">
                <dt class="text-slate-500">Contacto de emergencia</dt>
                <dd class="font-medium text-slate-900 text-right">
                  @if (emp.emergencyContactName && emp.emergencyContactPhone) {
                    {{ emp.emergencyContactName }}
                    <span class="block font-mono text-xs text-slate-500">Tel: {{ emp.emergencyContactPhone }}</span>
                  } @else {
                    <span class="text-slate-400">No registrado</span>
                  }
                </dd>
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

        <!-- Employment History Section -->
        <section class="rounded-xl border border-slate-200 bg-white shadow-2xs overflow-hidden">
          <div class="border-b border-slate-200 px-6 py-4 flex items-center justify-between">
            <div>
              <h4 class="text-sm font-bold uppercase tracking-wider text-slate-700">
                Historial Laboral y Cambios Salariales
              </h4>
              <p class="text-xs text-slate-500">
                Registros históricos inmutables de periodos de contratación y vigencias salariales.
              </p>
            </div>
            <span class="text-xs font-semibold text-slate-500">
              {{ employmentHistory().length }} periodo(s)
            </span>
          </div>

          @if (employmentHistory().length === 0) {
            <div class="p-8 text-center">
              <p class="text-sm font-semibold text-slate-800">Sin periodos laborales registrados</p>
              <p class="mt-1 text-xs text-slate-500">
                Registra un periodo laboral para asignar cargo, modalidad de contrato y salario base.
              </p>
            </div>
          } @else {
            <div class="divide-y divide-slate-200">
              @for (period of employmentHistory(); track period.employmentPeriodId) {
                <div class="p-6 space-y-4">
                  <div class="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
                    <div class="flex flex-wrap items-center gap-2">
                      <span
                        class="inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold"
                        [class.bg-emerald-100]="period.status === 'ACTIVE'"
                        [class.text-emerald-800]="period.status === 'ACTIVE'"
                        [class.bg-rose-100]="period.status === 'TERMINATED'"
                        [class.text-rose-800]="period.status === 'TERMINATED'"
                      >
                        {{ statusLabels[period.status] }}
                      </span>
                      <span class="text-sm font-bold text-slate-900">
                        Inicio: {{ period.startDate | date: 'mediumDate' }}
                      </span>
                      @if (period.endDate) {
                        <span class="text-sm text-slate-600">
                          &mdash; Baja: {{ period.endDate | date: 'mediumDate' }}
                        </span>
                      }
                    </div>

                    @if (period.terminationReason) {
                      <span class="text-xs font-medium text-rose-700">
                        Motivo: {{ terminationReasonLabels[period.terminationReason] }}
                      </span>
                    }
                  </div>

                  <div
                    tabindex="0"
                    role="region"
                    [attr.aria-label]="'Cambios del periodo iniciado el ' + period.startDate"
                    class="table-wrapper rounded-lg border border-slate-200"
                  >
                    <table
                      style="
                        --label-2: 'Cargo / Puesto';
                        --label-3: 'Contrato';
                        --label-4: 'Salario Base';
                        --label-5: 'Acciones';
                      "
                    >
                      <thead class="bg-slate-50 text-left text-[11px] font-bold uppercase tracking-wider text-slate-600">
                        <tr>
                          <th scope="col" class="px-4 py-2.5 border-b border-slate-200">Vigente Desde</th>
                          <th scope="col" class="px-4 py-2.5 border-b border-slate-200">Cargo / Puesto</th>
                          <th scope="col" class="px-4 py-2.5 border-b border-slate-200">Contrato</th>
                          <th scope="col" class="px-4 py-2.5 border-b border-slate-200 text-right">Salario Base</th>
                          <th scope="col" class="px-4 py-2.5 border-b border-slate-200 text-right">Acciones</th>
                        </tr>
                      </thead>
                      <tbody class="divide-y divide-slate-100 text-sm">
                        @for (change of period.changes; track change.employmentChangeId) {
                          <tr>
                            <th scope="row" class="px-4 py-3 text-left font-medium text-slate-700">
                              {{ change.effectiveFrom | date: 'mediumDate' }}
                            </th>
                            <td class="px-4 py-3 font-semibold text-slate-900">
                              {{ change.jobTitle }}
                            </td>
                            <td class="px-4 py-3 text-xs text-slate-600">
                              {{ contractLabels[change.contractType] }}
                            </td>
                            <td class="px-4 py-3 text-right font-mono font-bold text-slate-900">
                              <div>{{ change.baseSalary | currency: 'USD' : 'symbol' : '1.2-2' }}</div>
                              @if (getSalaryDelta(period, change); as delta) {
                                <div
                                  class="text-[11px] font-semibold"
                                  [class.text-emerald-700]="delta.diff > 0"
                                  [class.text-rose-700]="delta.diff < 0"
                                >
                                  {{ delta.diff > 0 ? '+' : '' }}{{ delta.diff | currency: 'USD' : 'symbol' : '1.2-2' }}
                                  @if (delta.percent !== null) {
                                    ({{ delta.percent > 0 ? '+' : '' }}{{ delta.percent | number: '1.1-1' }}%)
                                  }
                                </div>
                              }
                            </td>
                            <td class="px-4 py-3 text-right">
                              @if (canRemoveChange(period, change)) {
                                @if (confirmDeleteChangeId() === change.employmentChangeId) {
                                  <div class="inline-flex items-center gap-1.5">
                                    <button
                                      type="button"
                                      [disabled]="savingAction()"
                                      (click)="onRemoveChange(period.employmentPeriodId, change.employmentChangeId)"
                                      class="rounded-md bg-rose-600 px-2 py-1 text-[11px] font-bold text-white hover:bg-rose-700 disabled:opacity-60 cursor-pointer"
                                    >
                                      Confirmar
                                    </button>
                                    <button
                                      type="button"
                                      (click)="confirmDeleteChangeId.set(null)"
                                      class="rounded-md border border-slate-300 bg-white px-2 py-1 text-[11px] font-semibold text-slate-700 hover:bg-slate-50 cursor-pointer"
                                    >
                                      Cancelar
                                    </button>
                                  </div>
                                } @else {
                                  <button
                                    type="button"
                                    (click)="confirmDeleteChangeId.set(change.employmentChangeId)"
                                    class="rounded-md border border-rose-200 bg-rose-50 px-2.5 py-1 text-[11px] font-semibold text-rose-700 hover:bg-rose-100 cursor-pointer"
                                    title="Eliminar este último cambio si fue registrado por error"
                                  >
                                    Eliminar
                                  </button>
                                }
                              } @else {
                                <span class="text-[11px] text-slate-400">&mdash;</span>
                              }
                            </td>
                          </tr>
                        }
                      </tbody>
                    </table>
                  </div>
                </div>
              }
            </div>
          }
        </section>
      }
    </div>
  `,
})
export class EmployeeDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly employeeService = inject(EmployeeService);
  private readonly employmentPeriodService = inject(EmploymentPeriodService);
  private readonly locationService = inject(LocationService);
  private readonly userService = inject(UserService);
  readonly authService = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  employeeId = '';
  readonly employee = signal<EmployeeDetails | null>(null);
  readonly linkedUser = signal<UserSummary | null>(null);
  readonly employmentHistory = signal<EmploymentPeriodDetails[]>([]);
  readonly departments = signal<DepartmentDetails[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly actionError = signal<string | null>(null);
  readonly saveSuccessMessage = signal<string | null>(null);

  readonly activePanel = signal<'none' | 'edit' | 'period' | 'change' | 'terminate'>('none');
  readonly savingAction = signal(false);
  readonly periodSubmitAttempted = signal(false);
  readonly changeSubmitAttempted = signal(false);
  readonly terminateSubmitAttempted = signal(false);
  readonly editSubmitAttempted = signal(false);
  readonly confirmDeleteChangeId = signal<string | null>(null);

  readonly selectedDepartmentId = signal<string>('');
  readonly selectedMunicipalityId = signal<string>('');
  readonly departmentControl = new FormControl('', { nonNullable: true });
  readonly municipalityControl = new FormControl('', { nonNullable: true });

  readonly genderLabels = GENDER_LABELS;
  readonly contractLabels = CONTRACT_TYPE_LABELS;
  readonly pensionLabels = PENSION_FUND_LABELS;
  readonly statusLabels = EMPLOYMENT_STATUS_LABELS;
  readonly terminationReasonLabels = TERMINATION_REASON_LABELS;

  readonly activePeriod = computed<EmploymentPeriodDetails | null>(
    () => this.employmentHistory().find((p) => p.status === 'ACTIVE') ?? null
  );

  readonly currentEmployment = computed<EmploymentChangeDetails | null>(() => {
    const changes = this.activePeriod()?.changes ?? [];
    if (changes.length === 0) return null;
    return changes.reduce((latest, candidate) =>
      candidate.effectiveFrom > latest.effectiveFrom ? candidate : latest
    );
  });

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

  readonly terminationReasonOptions: { value: TerminationReason; label: string }[] = [
    { value: 'RESIGNATION', label: TERMINATION_REASON_LABELS.RESIGNATION },
    { value: 'DISMISSAL', label: TERMINATION_REASON_LABELS.DISMISSAL },
    { value: 'MUTUAL_AGREEMENT', label: TERMINATION_REASON_LABELS.MUTUAL_AGREEMENT },
    { value: 'CONTRACT_EXPIRATION', label: TERMINATION_REASON_LABELS.CONTRACT_EXPIRATION },
    { value: 'RETIREMENT', label: TERMINATION_REASON_LABELS.RETIREMENT },
    { value: 'DEATH', label: TERMINATION_REASON_LABELS.DEATH },
    { value: 'OTHER', label: TERMINATION_REASON_LABELS.OTHER },
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

  readonly editForm = this.fb.nonNullable.group(
    {
      firstName: ['', [Validators.required, Validators.maxLength(100)]],
      lastName: ['', [Validators.required, Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(254)]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
      gender: ['MALE' as Gender, [Validators.required]],
      birthDate: ['', [Validators.required, minimumAgeValidator(18)]],
      addressLine: ['', [Validators.required, Validators.maxLength(150)]],
      districtId: ['', [Validators.required, Validators.maxLength(10)]],
      dui: ['', [Validators.required, Validators.pattern(/^(?:\d{9}|\d{8}-\d)$/)]],
      nit: ['', [Validators.pattern(/^(?:\d{14}|\d{4}-\d{6}-\d{3}-\d)$/)]],
      isss: ['', [Validators.pattern(/^(?:\d{9}|\d{8}-\d)$/)]],
      nup: ['', [Validators.pattern(/^(?:\d{9}|\d{8}-\d)$/)]],
      pensionFundInstitution: ['AFP_CONFIA' as PensionFundInstitution, [Validators.required]],
      emergencyContactName: ['', [Validators.maxLength(150)]],
      emergencyContactPhone: ['', [Validators.pattern(/^\d{8}$/)]],
    },
    { validators: [emergencyContactPairValidator] }
  );

  readonly employmentPeriodForm = this.fb.nonNullable.group({
    startDate: [new Date().toISOString().slice(0, 10), [Validators.required]],
    jobTitle: ['', [Validators.required, Validators.maxLength(100)]],
    contractType: ['FULL_TIME' as ContractType, [Validators.required]],
    baseSalary: ['', [Validators.required, Validators.pattern(/^\d+(?:\.\d{1,2})?$/)]],
  });

  readonly registerChangeForm = this.fb.nonNullable.group({
    effectiveFrom: [new Date().toISOString().slice(0, 10), [Validators.required]],
    jobTitle: ['', [Validators.required, Validators.maxLength(100)]],
    contractType: ['FULL_TIME' as ContractType, [Validators.required]],
    baseSalary: ['', [Validators.required, Validators.pattern(/^\d+(?:\.\d{1,2})?$/)]],
  });

  readonly terminateForm = this.fb.nonNullable.group({
    endDate: [new Date().toISOString().slice(0, 10), [Validators.required]],
    terminationReason: ['RESIGNATION' as TerminationReason, [Validators.required]],
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
      history: this.employmentPeriodService.getEmployeeHistory(this.employeeId),
      linkedUser: this.authService.isAdmin()
        ? this.userService.getUserByEmployeeId(this.employeeId).pipe(catchError(() => of(null)))
        : of(null),
    }).subscribe({
      next: ({ employee, hierarchy, history, linkedUser }) => {
        this.employee.set(employee);
        this.departments.set(hierarchy);
        this.employmentHistory.set(history);
        this.linkedUser.set(linkedUser);
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
    this.togglePanel('edit');
    const emp = this.employee();
    if (this.activePanel() === 'edit' && emp) {
      this.syncFormWithEmployee(emp, this.departments());
    }
  }

  togglePanel(panel: 'edit' | 'period' | 'change' | 'terminate'): void {
    const nextPanel = this.activePanel() === panel ? 'none' : panel;
    this.activePanel.set(nextPanel);
    this.saveSuccessMessage.set(null);
    this.actionError.set(null);
    this.periodSubmitAttempted.set(false);
    this.changeSubmitAttempted.set(false);
    this.terminateSubmitAttempted.set(false);
    this.editSubmitAttempted.set(false);
    this.confirmDeleteChangeId.set(null);

    if (nextPanel === 'change') {
      const current = this.currentEmployment();
      this.registerChangeForm.reset({
        effectiveFrom: new Date().toISOString().slice(0, 10),
        jobTitle: current?.jobTitle ?? '',
        contractType: current?.contractType ?? 'FULL_TIME',
        baseSalary: current ? current.baseSalary.toFixed(2) : '',
      });
    }
  }

  onDepartmentChange(deptId: string): void {
    this.selectedDepartmentId.set(deptId);
    this.selectedMunicipalityId.set('');
    this.municipalityControl.setValue('', { emitEvent: false });
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

  showChangeError(controlName: keyof typeof this.registerChangeForm.controls): boolean {
    const control = this.registerChangeForm.controls[controlName];
    return control.invalid && (control.touched || this.changeSubmitAttempted());
  }

  showTerminateError(controlName: keyof typeof this.terminateForm.controls): boolean {
    const control = this.terminateForm.controls[controlName];
    return control.invalid && (control.touched || this.terminateSubmitAttempted());
  }

  showEditError(controlName: keyof typeof this.editForm.controls): boolean {
    const control = this.editForm.controls[controlName];
    return control.invalid && (control.touched || this.editSubmitAttempted());
  }

  isEffectiveFromBeforePeriodStart(): boolean {
    const startDate = this.activePeriod()?.startDate;
    const effectiveFrom = this.registerChangeForm.controls.effectiveFrom.value;
    if (!startDate || !effectiveFrom) return false;
    return effectiveFrom < startDate;
  }

  isEndDateBeforePeriodStart(): boolean {
    const startDate = this.activePeriod()?.startDate;
    const endDate = this.terminateForm.controls.endDate.value;
    if (!startDate || !endDate) return false;
    return endDate < startDate;
  }

  canRemoveChange(period: EmploymentPeriodDetails, change: EmploymentChangeDetails): boolean {
    if (period.status !== 'ACTIVE' || period.changes.length <= 1) {
      return false;
    }
    if (change.effectiveFrom === period.startDate) {
      return false;
    }
    const latest = period.changes.reduce((acc, candidate) =>
      candidate.effectiveFrom > acc.effectiveFrom ? candidate : acc
    );
    return latest.employmentChangeId === change.employmentChangeId;
  }

  getSalaryDelta(
    period: EmploymentPeriodDetails,
    change: EmploymentChangeDetails
  ): { diff: number; percent: number | null } | null {
    if (period.changes.length <= 1) return null;
    const chronological = [...period.changes].sort((a, b) =>
      a.effectiveFrom.localeCompare(b.effectiveFrom)
    );
    const idx = chronological.findIndex(
      (c) => c.employmentChangeId === change.employmentChangeId
    );
    if (idx <= 0) return null;
    const previous = chronological[idx - 1];
    const diff = Number((change.baseSalary - previous.baseSalary).toFixed(2));
    if (diff === 0) return null;
    const percent = previous.baseSalary > 0 ? (diff / previous.baseSalary) * 100 : null;
    return { diff, percent };
  }

  onRemoveChange(periodId: string, changeId: string): void {
    this.actionError.set(null);
    this.savingAction.set(true);

    this.employmentPeriodService.removeChange(periodId, changeId).subscribe({
      next: () => {
        this.savingAction.set(false);
        this.confirmDeleteChangeId.set(null);
        this.saveSuccessMessage.set('Último cambio laboral eliminado correctamente.');
        this.loadData();
      },
      error: (err) => {
        this.savingAction.set(false);
        this.confirmDeleteChangeId.set(null);
        this.actionError.set(
          extractApiErrorMessage(err, 'No se pudo eliminar el cambio laboral.')
        );
      },
    });
  }

  onSaveEmployee(): void {
    this.editSubmitAttempted.set(true);
    this.actionError.set(null);
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }

    this.savingAction.set(true);
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
      emergencyContactName: raw.emergencyContactName.trim() ? raw.emergencyContactName.trim() : null,
      emergencyContactPhone: raw.emergencyContactPhone.trim()
        ? raw.emergencyContactPhone.trim()
        : null,
    };

    this.employeeService.updateEmployee(this.employeeId, payload).subscribe({
      next: () => {
        this.savingAction.set(false);
        this.activePanel.set('none');
        this.saveSuccessMessage.set('Expediente del empleado actualizado correctamente.');
        this.loadData();
      },
      error: (err) => {
        this.savingAction.set(false);
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

    this.savingAction.set(true);
    const raw = this.employmentPeriodForm.getRawValue();
    const payload: CreateEmploymentPeriodRequest = {
      employeeId: this.employeeId,
      startDate: raw.startDate,
      jobTitle: raw.jobTitle.trim(),
      contractType: raw.contractType,
      baseSalary: Number(Number(raw.baseSalary.trim()).toFixed(2)),
    };

    this.employmentPeriodService.createEmploymentPeriod(payload).subscribe({
      next: () => {
        this.savingAction.set(false);
        this.activePanel.set('none');
        this.saveSuccessMessage.set('Periodo laboral activo registrado correctamente.');
        this.employmentPeriodForm.reset({
          startDate: new Date().toISOString().slice(0, 10),
          jobTitle: '',
          contractType: 'FULL_TIME',
          baseSalary: '',
        });
        this.loadData();
      },
      error: (err) => {
        this.savingAction.set(false);
        this.actionError.set(
          extractApiErrorMessage(
            err,
            'No se pudo registrar el periodo laboral. Verifica que no exista otro periodo activo o solapado.'
          )
        );
      },
    });
  }

  onRegisterChange(): void {
    const period = this.activePeriod();
    if (!period) return;
    this.changeSubmitAttempted.set(true);
    this.actionError.set(null);

    if (this.registerChangeForm.invalid || this.isEffectiveFromBeforePeriodStart()) {
      this.registerChangeForm.markAllAsTouched();
      return;
    }

    this.savingAction.set(true);
    const raw = this.registerChangeForm.getRawValue();
    const payload: RegisterEmploymentChangeRequest = {
      effectiveFrom: raw.effectiveFrom,
      jobTitle: raw.jobTitle.trim(),
      contractType: raw.contractType,
      baseSalary: Number(Number(raw.baseSalary.trim()).toFixed(2)),
    };

    this.employmentPeriodService.registerChange(period.employmentPeriodId, payload).subscribe({
      next: () => {
        this.savingAction.set(false);
        this.activePanel.set('none');
        this.saveSuccessMessage.set('Cambio laboral registrado en el historial correctamente.');
        this.loadData();
      },
      error: (err) => {
        this.savingAction.set(false);
        this.actionError.set(
          extractApiErrorMessage(
            err,
            'No se pudo registrar el cambio laboral. Verifica la fecha de vigencia.'
          )
        );
      },
    });
  }

  onTerminatePeriod(): void {
    const period = this.activePeriod();
    if (!period) return;
    this.terminateSubmitAttempted.set(true);
    this.actionError.set(null);

    if (this.terminateForm.invalid || this.isEndDateBeforePeriodStart()) {
      this.terminateForm.markAllAsTouched();
      return;
    }

    this.savingAction.set(true);
    const raw = this.terminateForm.getRawValue();
    const payload: TerminateEmploymentPeriodRequest = {
      endDate: raw.endDate,
      terminationReason: raw.terminationReason,
    };

    this.employmentPeriodService.terminatePeriod(period.employmentPeriodId, payload).subscribe({
      next: () => {
        this.savingAction.set(false);
        this.activePanel.set('none');
        this.saveSuccessMessage.set('Periodo laboral finalizado (baja registrada) correctamente.');
        this.loadData();
      },
      error: (err) => {
        this.savingAction.set(false);
        this.actionError.set(
          extractApiErrorMessage(err, 'No se pudo finalizar el periodo laboral.')
        );
      },
    });
  }

  private syncFormWithEmployee(emp: EmployeeDetails, hierarchy: DepartmentDetails[]): void {
    const resolved = this.locationService.resolveDistrict(hierarchy, emp.districtId);
    if (resolved) {
      this.selectedDepartmentId.set(resolved.department.id);
      this.selectedMunicipalityId.set(resolved.municipality.id);
      this.departmentControl.setValue(resolved.department.id, { emitEvent: false });
      this.municipalityControl.setValue(resolved.municipality.id, { emitEvent: false });
    } else {
      this.selectedDepartmentId.set('');
      this.selectedMunicipalityId.set('');
      this.departmentControl.setValue('', { emitEvent: false });
      this.municipalityControl.setValue('', { emitEvent: false });
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
      emergencyContactName: emp.emergencyContactName ?? '',
      emergencyContactPhone: emp.emergencyContactPhone ?? '',
    });
  }
}

function minimumAgeValidator(minYears: number) {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value as string;
    if (!value) return null;
    const parts = value.split('-').map(Number);
    if (parts.length !== 3 || parts.some(Number.isNaN)) return { invalidDate: true };

    const [year, month, day] = parts;
    const today = new Date();
    const todayYear = today.getFullYear();
    const todayMonth = today.getMonth() + 1;
    const todayDay = today.getDate();

    if (
      year > todayYear ||
      (year === todayYear && month > todayMonth) ||
      (year === todayYear && month === todayMonth && day > todayDay)
    ) {
      return { futureDate: true };
    }

    let age = todayYear - year;
    if (todayMonth < month || (todayMonth === month && todayDay < day)) {
      age--;
    }

    return age < minYears ? { underage: true } : null;
  };
}

function emergencyContactPairValidator(group: AbstractControl): ValidationErrors | null {
  const name = (group.get('emergencyContactName')?.value ?? '').trim();
  const phone = (group.get('emergencyContactPhone')?.value ?? '').trim();
  if ((name && !phone) || (!name && phone)) {
    return { incompleteEmergencyContact: true };
  }
  return null;
}
