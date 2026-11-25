import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { EmployeeService } from '../../services/employee.service';
import { EmployeeDetails, PENSION_FUND_LABELS } from '../../models/employee.model';
import { PageResult } from '../../../../shared/models/pagination.model';
import { extractApiErrorMessage } from '../../../../shared/models/api-error.model';

@Component({
  selector: 'app-employee-list',
  imports: [RouterLink, FormsModule, DatePipe],
  template: `
    <div class="space-y-6">
      <!-- Page Header -->
      <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h2 class="text-xl sm:text-2xl font-bold text-slate-900">Directorio de Empleados</h2>
          <p class="text-sm text-slate-500">
            Consulta paginada de expedientes (<code class="font-mono text-xs">GET /api/v1/employees?page={{ currentPage() }}&amp;size={{ pageSize() }}</code>)
          </p>
        </div>

        <div class="flex items-center gap-2.5">
          <button
            type="button"
            (click)="loadPage(currentPage())"
            class="inline-flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3.5 py-2.5 text-xs font-semibold text-slate-700 hover:bg-slate-50 transition-colors cursor-pointer"
          >
            <svg class="h-4 w-4 text-slate-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            <span>Actualizar</span>
          </button>

          <a
            routerLink="/employees/new"
            class="inline-flex items-center gap-2 rounded-xl bg-amber-500 px-4 py-2.5 text-sm font-bold text-slate-950 shadow-2xs hover:bg-amber-400 transition-colors"
          >
            <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
            </svg>
            <span>Nuevo Empleado</span>
          </a>
        </div>
      </div>

      <!-- Filter Bar -->
      <form
        (submit)="$event.preventDefault()"
        method="GET"
        action="/employees"
        class="rounded-xl border border-slate-200 bg-white p-4 shadow-2xs"
      >
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-3">
          <div class="sm:col-span-2">
            <label for="employee-search" class="block text-xs font-semibold text-slate-700 mb-1">
              Filtrar página actual por nombre, correo o DUI
            </label>
            <input
              id="employee-search"
              name="q"
              type="search"
              [ngModel]="searchTerm()"
              (ngModelChange)="searchTerm.set($event)"
              autocorrect="off"
              autocapitalize="off"
              spellcheck="false"
              enterkeyhint="search"
              placeholder="Ej. Carlos Mendoza, 012345678..."
              class="block w-full rounded-lg border border-slate-300 bg-white px-3.5 py-2.5 text-base sm:text-sm text-slate-900 placeholder-slate-400 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
            />
          </div>

          <div>
            <label for="employee-page-size" class="block text-xs font-semibold text-slate-700 mb-1">
              Registros por página
            </label>
            <select
              id="employee-page-size"
              name="size"
              [ngModel]="pageSize()"
              (ngModelChange)="onPageSizeChange(+$event)"
              class="block w-full rounded-lg border border-slate-300 bg-white px-3.5 py-2.5 text-base sm:text-sm text-slate-900 focus:border-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500/20"
            >
              <option [value]="10">10 por página</option>
              <option [value]="20">20 por página</option>
              <option [value]="50">50 por página</option>
            </select>
          </div>
        </div>
      </form>

      @if (errorMessage()) {
        <div
          role="alert"
          class="rounded-xl border border-rose-200 bg-rose-50 p-4 text-sm text-rose-800 flex items-center justify-between gap-4"
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

      <!-- Responsive Table Card -->
      <div class="rounded-xl border border-slate-200 bg-white shadow-2xs overflow-hidden">
        @if (loading()) {
          <div class="p-12 text-center text-sm text-slate-500">
            Consultando empleados desde <code class="font-mono text-xs">GET /api/v1/employees</code>...
          </div>
        } @else if (filteredEmployees().length === 0) {
          <div class="p-12 text-center">
            <p class="text-base font-semibold text-slate-800">No se encontraron empleados</p>
            <p class="mt-1 text-xs text-slate-500">
              @if (pageResult().content.length === 0) {
                Aún no existen registros en la tabla <code class="font-mono">employee</code>.
              } @else {
                Prueba ajustando el texto de búsqueda en la página actual.
              }
            </p>
            <a
              routerLink="/employees/new"
              class="mt-4 inline-flex items-center gap-1.5 rounded-lg bg-slate-900 px-4 py-2 text-xs font-semibold text-white hover:bg-slate-800"
            >
              + Registrar Empleado
            </a>
          </div>
        } @else {
          <div
            tabindex="0"
            role="region"
            aria-labelledby="employee-table-caption"
            class="table-wrapper"
          >
            <table
              style="
                --label-2: 'DUI / Identificación';
                --label-3: 'AFP / Previsión';
                --label-4: 'Fecha de Registro';
                --label-5: 'Acciones';
              "
            >
              <caption id="employee-table-caption" class="visually-hidden">
                Directorio paginado de empleados de Arco &amp; Metal Mecánica
              </caption>
              <thead class="bg-slate-100 text-left text-xs font-bold uppercase tracking-wider text-slate-600">
                <tr>
                  <th scope="col" class="px-5 py-3.5 border-b border-slate-200">Empleado</th>
                  <th scope="col" class="px-5 py-3.5 border-b border-slate-200">DUI / Identificación</th>
                  <th scope="col" class="px-5 py-3.5 border-b border-slate-200">AFP / Previsión</th>
                  <th scope="col" class="px-5 py-3.5 border-b border-slate-200">Fecha de Registro</th>
                  <th scope="col" class="px-5 py-3.5 border-b border-slate-200 text-right">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-200 text-sm">
                @for (emp of filteredEmployees(); track emp.employeeId) {
                  <tr class="hover:bg-slate-50/80 transition-colors">
                    <th scope="row" class="px-5 py-4 text-left font-normal">
                      <div class="font-bold text-slate-900">
                        {{ emp.firstName }} {{ emp.lastName }}
                      </div>
                      <div class="text-xs text-slate-500">
                        {{ emp.email }} &bull; Tel: {{ emp.phoneNumber }}
                      </div>
                    </th>
                    <td class="px-5 py-4">
                      <div class="font-mono text-xs font-semibold text-slate-800">
                        DUI: {{ emp.dui }}
                      </div>
                      @if (emp.nit) {
                        <div class="font-mono text-[11px] text-slate-500">NIT: {{ emp.nit }}</div>
                      }
                    </td>
                    <td class="px-5 py-4">
                      <div class="text-xs font-medium text-slate-800">
                        {{ pensionLabels[emp.pensionFundInstitution] }}
                      </div>
                      @if (emp.isss) {
                        <div class="font-mono text-[11px] text-slate-500">ISSS: {{ emp.isss }}</div>
                      }
                    </td>
                    <td class="px-5 py-4 text-slate-600 text-xs">
                      {{ emp.createdAt | date: 'mediumDate' }}
                    </td>
                    <td class="px-5 py-4 text-right">
                      <div class="inline-flex items-center gap-2">
                        <a
                          [routerLink]="['/employees', emp.employeeId]"
                          class="rounded-lg border border-slate-200 bg-white px-2.5 py-1.5 text-xs font-semibold text-slate-700 hover:bg-slate-100 hover:text-slate-900"
                        >
                          Expediente
                        </a>
                        <a
                          [routerLink]="['/users/new']"
                          [queryParams]="{ employeeId: emp.employeeId }"
                          class="rounded-lg bg-slate-900 px-2.5 py-1.5 text-xs font-semibold text-white hover:bg-slate-800"
                          title="Crear cuenta de acceso opcional para este empleado"
                        >
                          + Usuario
                        </a>
                      </div>
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
              &bull; Total: <span class="font-bold text-slate-900">{{ pageResult().totalElements }}</span> empleados
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
export class EmployeeListComponent implements OnInit {
  private readonly employeeService = inject(EmployeeService);

  readonly currentPage = signal(0);
  readonly pageSize = signal(20);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly searchTerm = signal('');

  readonly pageResult = signal<PageResult<EmployeeDetails>>({
    content: [],
    page: 0,
    size: 20,
    totalElements: 0,
    totalPages: 0,
    first: true,
    last: true,
  });

  readonly pensionLabels = PENSION_FUND_LABELS;

  readonly filteredEmployees = computed(() => {
    const query = this.searchTerm().trim().toLowerCase();
    const list = this.pageResult().content;
    if (!query) return list;

    return list.filter((emp) => {
      const fullName = `${emp.firstName} ${emp.lastName}`.toLowerCase();
      return (
        fullName.includes(query) ||
        emp.email.toLowerCase().includes(query) ||
        emp.dui.toLowerCase().includes(query)
      );
    });
  });

  ngOnInit(): void {
    this.loadPage(0);
  }

  onPageSizeChange(newSize: number): void {
    this.pageSize.set(newSize);
    this.loadPage(0);
  }

  loadPage(page: number): void {
    if (page < 0) return;
    this.loading.set(true);
    this.errorMessage.set(null);

    this.employeeService.getEmployeesPage(page, this.pageSize()).subscribe({
      next: (result) => {
        this.currentPage.set(result.page);
        this.pageResult.set(result);
        this.loading.set(false);
      },
      error: (err) => {
        this.errorMessage.set(
          extractApiErrorMessage(
            err,
            'No se pudo conectar con GET /api/v1/employees. Verifica que el backend Spring Boot esté en ejecución.'
          )
        );
        this.loading.set(false);
      },
    });
  }
}
