import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import { UserService } from '../../services/user.service';
import { EmployeeService } from '../../../employee/services/employee.service';
import {
  PageResult,
  ROLE_LABELS,
  USER_STATUS_LABELS,
  UserSummary,
} from '../../models/user.model';
import { EmployeeDetails } from '../../../employee/models/employee.model';

@Component({
  selector: 'app-user-list',
  imports: [RouterLink, DatePipe],
  template: `
    <div class="space-y-6">
      <!-- Header -->
      <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h2 class="text-xl sm:text-2xl font-bold text-slate-900">
            Cuentas de Usuario y Control de Acceso (RBAC)
          </h2>
          <p class="text-sm text-slate-500">
            Consulta paginada conectada a <code class="font-mono text-xs">GET /api/v1/users?page={{ currentPage() }}&amp;size={{ pageSize() }}</code>
          </p>
        </div>

        <div class="flex items-center gap-2.5">
          <button
            type="button"
            (click)="loadPage(currentPage())"
            class="inline-flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3.5 py-2.5 text-xs font-semibold text-slate-700 hover:bg-slate-50 cursor-pointer"
          >
            Actualizar
          </button>

          <a
            routerLink="/users/new"
            class="inline-flex items-center gap-2 rounded-xl bg-amber-500 px-4 py-2.5 text-sm font-bold text-slate-950 shadow-2xs hover:bg-amber-400 transition-colors"
          >
            <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
            </svg>
            <span>Crear Cuenta de Usuario</span>
          </a>
        </div>
      </div>

      @if (errorMessage()) {
        <div
          role="alert"
          class="rounded-xl border border-rose-200 bg-rose-50 p-4 text-sm text-rose-800 flex items-center justify-between"
        >
          <span>{{ errorMessage() }}</span>
          <button
            type="button"
            (click)="loadPage(currentPage())"
            class="rounded-lg bg-rose-100 px-3 py-1.5 text-xs font-semibold text-rose-900 hover:bg-rose-200 cursor-pointer"
          >
            Reintentar
          </button>
        </div>
      }

      <!-- Table Card -->
      <div class="rounded-xl border border-slate-200 bg-white shadow-2xs overflow-hidden">
        @if (loading()) {
          <div class="p-12 text-center text-sm text-slate-500">
            Cargando página de usuarios...
          </div>
        } @else if (pageResult().content.length === 0) {
          <div class="p-12 text-center">
            <p class="text-base font-semibold text-slate-800">No hay cuentas de usuario registradas</p>
            <p class="mt-1 text-xs text-slate-500">
              Crea una cuenta vinculándola a un empleado existente del módulo <code class="font-mono">Employee</code>.
            </p>
            <a
              routerLink="/users/new"
              class="mt-4 inline-flex items-center gap-1.5 rounded-lg bg-slate-900 px-4 py-2 text-xs font-semibold text-white hover:bg-slate-800"
            >
              + Crear Primera Cuenta
            </a>
          </div>
        } @else {
          <div
            tabindex="0"
            role="region"
            aria-labelledby="users-table-caption"
            class="table-wrapper"
          >
            <table
              style="
                --label-2: 'Empleado Vinculado';
                --label-3: 'Roles Asignados';
                --label-4: 'Estado';
                --label-5: 'Fecha de Creación';
              "
            >
              <caption id="users-table-caption" class="visually-hidden">
                Listado paginado de cuentas de usuario y sus roles RBAC
              </caption>
              <thead class="bg-slate-100 text-left text-xs font-bold uppercase tracking-wider text-slate-600">
                <tr>
                  <th scope="col" class="px-5 py-3.5 border-b border-slate-200">Correo de Acceso</th>
                  <th scope="col" class="px-5 py-3.5 border-b border-slate-200">Empleado Vinculado</th>
                  <th scope="col" class="px-5 py-3.5 border-b border-slate-200">Roles Asignados</th>
                  <th scope="col" class="px-5 py-3.5 border-b border-slate-200">Estado</th>
                  <th scope="col" class="px-5 py-3.5 border-b border-slate-200">Fecha de Creación</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-200 text-sm">
                @for (user of pageResult().content; track user.id) {
                  <tr class="hover:bg-slate-50/80 transition-colors">
                    <th scope="row" class="px-5 py-4 text-left font-normal">
                      <div class="font-bold text-slate-900">{{ user.email }}</div>
                      <div class="font-mono text-[11px] text-slate-400">User ID: {{ user.id }}</div>
                    </th>
                    <td class="px-5 py-4">
                      @if (employeeMap().get(user.employeeId); as emp) {
                        <a
                          [routerLink]="['/employees', emp.id]"
                          class="font-semibold text-amber-700 hover:text-amber-800 hover:underline"
                        >
                          {{ emp.firstName }} {{ emp.lastName }}
                        </a>
                        <div class="text-xs text-slate-500">{{ emp.jobTitle }}</div>
                      } @else {
                        <a
                          [routerLink]="['/employees', user.employeeId]"
                          class="font-mono text-xs text-slate-600 hover:text-amber-700"
                        >
                          {{ user.employeeId }}
                        </a>
                      }
                    </td>
                    <td class="px-5 py-4">
                      <div class="flex flex-wrap gap-1.5">
                        @for (role of user.roles; track role) {
                          <span
                            class="inline-flex items-center rounded-md bg-amber-100 px-2.5 py-0.5 text-xs font-bold text-amber-900 border border-amber-300/60"
                          >
                            {{ roleLabels[role] }}
                          </span>
                        }
                      </div>
                    </td>
                    <td class="px-5 py-4">
                      <span
                        class="inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold"
                        [class.bg-emerald-100]="user.status === 'ACTIVE'"
                        [class.text-emerald-800]="user.status === 'ACTIVE'"
                        [class.bg-slate-100]="user.status === 'DISABLED'"
                        [class.text-slate-700]="user.status === 'DISABLED'"
                        [class.bg-rose-100]="user.status === 'LOCKED'"
                        [class.text-rose-800]="user.status === 'LOCKED'"
                      >
                        {{ statusLabels[user.status] }}
                      </span>
                    </td>
                    <td class="px-5 py-4 text-slate-600">
                      {{ user.createdAt | date: 'medium' }}
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>

          <!-- Pagination Bar -->
          <div class="flex flex-col gap-3 border-t border-slate-200 bg-slate-50 px-5 py-3.5 sm:flex-row sm:items-center sm:justify-between">
            <div class="text-xs text-slate-600">
              Página <span class="font-bold text-slate-900">{{ pageResult().page + 1 }}</span> de
              <span class="font-bold text-slate-900">{{ pageResult().totalPages || 1 }}</span>
              &bull; Total: <span class="font-bold text-slate-900">{{ pageResult().totalElements }}</span> cuentas
            </div>

            <div class="flex items-center gap-2">
              <button
                type="button"
                [disabled]="pageResult().first"
                (click)="loadPage(currentPage() - 1)"
                class="rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-xs font-semibold text-slate-700 hover:bg-slate-100 disabled:opacity-40 cursor-pointer"
              >
                &larr; Anterior
              </button>
              <button
                type="button"
                [disabled]="pageResult().last"
                (click)="loadPage(currentPage() + 1)"
                class="rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-xs font-semibold text-slate-700 hover:bg-slate-100 disabled:opacity-40 cursor-pointer"
              >
                Siguiente &rarr;
              </button>
            </div>
          </div>
        }
      </div>
    </div>
  `,
})
export class UserListComponent implements OnInit {
  private readonly userService = inject(UserService);
  private readonly employeeService = inject(EmployeeService);

  readonly currentPage = signal(0);
  readonly pageSize = signal(10);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);

  readonly employees = signal<EmployeeDetails[]>([]);
  readonly pageResult = signal<PageResult<UserSummary>>({
    content: [],
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
    first: true,
    last: true,
  });

  readonly roleLabels = ROLE_LABELS;
  readonly statusLabels = USER_STATUS_LABELS;

  readonly employeeMap = computed(() => {
    const map = new Map<string, EmployeeDetails>();
    for (const emp of this.employees()) {
      map.set(emp.id, emp);
    }
    return map;
  });

  ngOnInit(): void {
    this.loadPage(0);
  }

  loadPage(page: number): void {
    if (page < 0) return;
    this.loading.set(true);
    this.errorMessage.set(null);

    forkJoin({
      users: this.userService.getUsers(page, this.pageSize()),
      employees: this.employeeService.getAllEmployees().pipe(catchError(() => of([] as EmployeeDetails[]))),
    }).subscribe({
      next: ({ users, employees }) => {
        this.currentPage.set(users.page);
        this.pageResult.set(users);
        this.employees.set(employees);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set(
          'No se pudo consultar GET /api/v1/users. Verifica que el backend esté en ejecución.'
        );
        this.loading.set(false);
      },
    });
  }
}
