import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import { EmployeeService } from '../../../employee/services/employee.service';
import { UserService } from '../../../identity-access/services/user.service';
import { AuthService } from '../../../../core/auth/services/auth.service';
import {
  EmployeeDetails,
  PENSION_FUND_LABELS,
} from '../../../employee/models/employee.model';
import { ROLE_LABELS, UserSummary } from '../../../identity-access/models/user.model';

@Component({
  selector: 'app-dashboard-page',
  imports: [RouterLink, DatePipe],
  template: `
    <div class="space-y-6">
      <!-- Welcome & Quick Actions Banner -->
      <section
        class="rounded-2xl bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 p-6 sm:p-8 text-white shadow-sm border border-slate-800"
      >
        <div class="flex flex-col gap-6 lg:flex-row lg:items-center lg:justify-between">
          <div class="space-y-2 max-w-2xl">
            <div class="inline-flex items-center gap-2 rounded-full bg-amber-500/15 px-3 py-1 text-xs font-semibold text-amber-400 border border-amber-500/30">
              <span>Arquitectura Hexagonal &bull; Monolito Modular</span>
            </div>
            <h2 class="text-2xl sm:text-3xl font-bold tracking-tight">
              Panel de Control Administrativo
            </h2>
            <p class="text-sm text-slate-300 leading-relaxed">
              Centralización de expedientes de empleados, jerarquía geográfica de El Salvador, control de acceso basado en roles (RBAC) y planilla operativa.
            </p>
          </div>

          <div class="flex flex-wrap items-center gap-3">
            <a
              routerLink="/employees/new"
              class="inline-flex items-center gap-2 rounded-xl bg-amber-500 px-4 py-2.5 text-sm font-bold text-slate-950 shadow-xs hover:bg-amber-400 transition-colors"
            >
              <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
              </svg>
              <span>Registrar Empleado</span>
            </a>

            @if (authService.isAdmin()) {
              <a
                routerLink="/users/new"
                class="inline-flex items-center gap-2 rounded-xl border border-slate-600 bg-slate-800/80 px-4 py-2.5 text-sm font-semibold text-white hover:bg-slate-700 transition-colors"
              >
                <svg class="h-4 w-4 text-amber-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"
                  />
                </svg>
                <span>Nueva Cuenta de Usuario</span>
              </a>
            }
          </div>
        </div>
      </section>

      @if (backendOffline()) {
        <div
          role="status"
          class="rounded-xl border border-amber-300 bg-amber-50 p-4 text-sm text-amber-900 flex items-start gap-3"
        >
          <svg class="h-5 w-5 shrink-0 text-amber-600 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
            />
          </svg>
          <div>
            <p class="font-semibold">Backend Spring Boot no detectado en <code class="font-mono text-xs bg-amber-100 px-1.5 py-0.5 rounded">localhost:8080</code></p>
            <p class="mt-0.5 text-xs text-amber-800">
              Inicia <code class="font-mono">arcobackend</code> para consultar y persistir datos reales en PostgreSQL. El proxy de Angular (<code class="font-mono">/api/v1/*</code>) ya está configurado.
            </p>
          </div>
        </div>
      }

      <!-- KPI Cards -->
      <section aria-label="Indicadores principales" class="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div class="rounded-xl border border-slate-200 bg-white p-5 shadow-2xs">
          <div class="flex items-center justify-between">
            <span class="text-xs font-semibold uppercase tracking-wider text-slate-500">
              Total Empleados
            </span>
            <span class="rounded-lg bg-slate-100 p-2 text-slate-700">
              <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
                <path stroke-linecap="round" stroke-linejoin="round" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
            </span>
          </div>
          <p class="mt-3 text-3xl font-extrabold text-slate-900">{{ totalEmployeesCount() }}</p>
          <p class="mt-1 text-xs text-slate-500">Expedientes registrados en módulo Employee</p>
        </div>

        @if (authService.isAdmin()) {
          <div class="rounded-xl border border-slate-200 bg-white p-5 shadow-2xs">
            <div class="flex items-center justify-between">
              <span class="text-xs font-semibold uppercase tracking-wider text-slate-500">
                Cuentas de Usuario
              </span>
              <span class="rounded-lg bg-amber-50 p-2 text-amber-700">
                <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z" />
                </svg>
              </span>
            </div>
            <p class="mt-3 text-3xl font-extrabold text-slate-900">{{ totalUsersCount() }}</p>
            <p class="mt-1 text-xs text-slate-500">Vinculadas 1:1 en IdentityAccess</p>
          </div>
        }
      </section>

      <!-- Two-Column Overview -->
      <div class="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <!-- Recent Employees -->
        <section class="rounded-xl border border-slate-200 bg-white shadow-2xs flex flex-col">
          <div class="flex items-center justify-between border-b border-slate-100 px-5 py-4">
            <div>
              <h3 class="text-sm font-bold text-slate-900">Empleados Recientes</h3>
              <p class="text-xs text-slate-500">Últimos expedientes en módulo Employee</p>
            </div>
            <a
              routerLink="/employees"
              class="text-xs font-semibold text-amber-700 hover:text-amber-800"
            >
              Ver todos &rarr;
            </a>
          </div>

          <div class="divide-y divide-slate-100 flex-1">
            @if (loading()) {
              <div class="p-6 text-center text-sm text-slate-500">Cargando empleados...</div>
            } @else if (recentEmployees().length === 0) {
              <div class="p-8 text-center">
                <p class="text-sm font-medium text-slate-700">Aún no hay empleados registrados</p>
                <p class="mt-1 text-xs text-slate-500">
                  Registra el primer empleado para habilitar también la creación de usuarios.
                </p>
                <a
                  routerLink="/employees/new"
                  class="mt-4 inline-flex items-center rounded-lg bg-slate-900 px-3.5 py-2 text-xs font-semibold text-white hover:bg-slate-800"
                >
                  + Registrar empleado
                </a>
              </div>
            } @else {
              @for (emp of recentEmployees(); track emp.employeeId) {
                <div class="flex items-center justify-between px-5 py-3.5 hover:bg-slate-50">
                  <div class="min-w-0">
                    <a
                      [routerLink]="['/employees', emp.employeeId]"
                      class="truncate text-sm font-semibold text-slate-900 hover:text-amber-700"
                    >
                      {{ emp.firstName }} {{ emp.lastName }}
                    </a>
                    <p class="truncate text-xs text-slate-500">
                      DUI: {{ emp.dui }} &bull; {{ pensionLabels[emp.pensionFundInstitution] }}
                    </p>
                  </div>
                  <span class="text-xs text-slate-500 shrink-0">
                    {{ emp.createdAt | date: 'mediumDate' }}
                  </span>
                </div>
              }
            }
          </div>
        </section>

        <!-- Recent Users -->
        @if (authService.isAdmin()) {
          <section class="rounded-xl border border-slate-200 bg-white shadow-2xs flex flex-col">
            <div class="flex items-center justify-between border-b border-slate-100 px-5 py-4">
              <div>
                <h3 class="text-sm font-bold text-slate-900">Cuentas de Acceso (IdentityAccess)</h3>
                <p class="text-xs text-slate-500">Usuarios con roles RBAC asignados</p>
              </div>
              <a
                routerLink="/users"
                class="text-xs font-semibold text-amber-700 hover:text-amber-800"
              >
                Administrar &rarr;
              </a>
            </div>

            <div class="divide-y divide-slate-100 flex-1">
              @if (loading()) {
                <div class="p-6 text-center text-sm text-slate-500">Cargando usuarios...</div>
              } @else if (recentUsers().length === 0) {
                <div class="p-8 text-center">
                  <p class="text-sm font-medium text-slate-700">Sin cuentas de usuario registradas</p>
                  <p class="mt-1 text-xs text-slate-500">
                    Asocia un empleado existente con sus credenciales y roles del sistema.
                  </p>
                  <a
                    routerLink="/users/new"
                    class="mt-4 inline-flex items-center rounded-lg bg-slate-900 px-3.5 py-2 text-xs font-semibold text-white hover:bg-slate-800"
                  >
                    + Crear usuario
                  </a>
                </div>
              } @else {
                @for (user of recentUsers(); track user.id) {
                  <div class="flex items-center justify-between px-5 py-3.5 hover:bg-slate-50">
                    <div class="min-w-0">
                      <p class="truncate text-sm font-semibold text-slate-900">{{ user.email }}</p>
                      <p class="truncate text-xs text-slate-500">
                        Creado: {{ user.createdAt | date: 'mediumDate' }}
                      </p>
                    </div>
                    <div class="flex flex-wrap gap-1 justify-end">
                      @for (role of user.roles; track role) {
                        <span
                          class="inline-flex items-center rounded bg-amber-100 px-2 py-0.5 text-[11px] font-bold text-amber-900"
                        >
                          {{ roleLabels[role] }}
                        </span>
                      }
                    </div>
                  </div>
                }
              }
            </div>
          </section>
        }
      </div>
    </div>
  `,
})
export class DashboardPageComponent implements OnInit {
  private readonly employeeService = inject(EmployeeService);
  private readonly userService = inject(UserService);
  readonly authService = inject(AuthService);

  readonly recentEmployees = signal<EmployeeDetails[]>([]);
  readonly totalEmployeesCount = signal(0);
  readonly recentUsers = signal<UserSummary[]>([]);
  readonly totalUsersCount = signal(0);
  readonly loading = signal(true);
  readonly backendOffline = signal(false);

  readonly pensionLabels = PENSION_FUND_LABELS;
  readonly roleLabels = ROLE_LABELS;

  ngOnInit(): void {
    forkJoin({
      employeesPage: this.employeeService.getEmployeesPage(0, 5).pipe(
        catchError(() => {
          this.backendOffline.set(true);
          return of({
            content: [] as EmployeeDetails[],
            page: 0,
            size: 5,
            totalElements: 0,
            totalPages: 0,
            first: true,
            last: true,
          });
        })
      ),
      usersPage: this.authService.isAdmin()
        ? this.userService.getUsers(0, 5).pipe(
            catchError(() => {
              this.backendOffline.set(true);
              return of({
                content: [] as UserSummary[],
                page: 0,
                size: 5,
                totalElements: 0,
                totalPages: 0,
                first: true,
                last: true,
              });
            })
          )
        : of({
            content: [] as UserSummary[],
            page: 0,
            size: 5,
            totalElements: 0,
            totalPages: 0,
            first: true,
            last: true,
          }),
    }).subscribe(({ employeesPage, usersPage }) => {
      this.recentEmployees.set(employeesPage.content);
      this.totalEmployeesCount.set(employeesPage.totalElements);
      this.recentUsers.set(usersPage.content);
      this.totalUsersCount.set(usersPage.totalElements);
      this.loading.set(false);
    });
  }
}
