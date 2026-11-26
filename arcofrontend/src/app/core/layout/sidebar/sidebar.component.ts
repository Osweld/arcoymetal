import { Component, input, output } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

export interface NavItem {
  label: string;
  route: string;
  badge?: string;
  section: 'core' | 'upcoming';
  iconPath: string;
}

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterLinkActive],
  template: `
    <aside
      class="flex h-full w-72 flex-col bg-slate-900 text-slate-200 border-r border-slate-800 select-none"
      aria-label="Navegación principal"
    >
      <!-- Brand Header -->
      <a
        routerLink="/dashboard"
        (click)="navigate.emit()"
        class="flex items-center gap-3 px-5 py-4 border-b border-slate-800/80 hover:bg-slate-800/40 transition-colors"
      >
        <div
          class="flex h-12 w-12 shrink-0 items-center justify-center rounded-full bg-white p-0.5 shadow-sm ring-2 ring-cyan-400/40 overflow-hidden"
        >
          <img
            src="images/logo-full.jpg"
            alt="Logo de Arco &amp; Metal Mecánica"
            class="h-full w-full rounded-full object-contain"
          />
        </div>
        <div class="min-w-0">
          <p class="truncate text-sm font-bold tracking-wide text-white uppercase">
            Arco &amp; Metal
          </p>
          <p class="truncate text-xs text-slate-400">Mecánica &bull; Metapán, SV</p>
        </div>
      </a>

      <!-- Main Navigation -->
      <nav class="flex-1 overflow-y-auto px-3 py-5 space-y-6">
        <div>
          <p class="px-3 mb-2 text-[11px] font-semibold uppercase tracking-wider text-slate-400">
            Módulos Activos
          </p>
          <ul class="space-y-1">
            @for (item of coreItems; track item.route) {
              <li>
                <a
                  [routerLink]="item.route"
                  routerLinkActive="bg-amber-500/15 text-amber-400 border-amber-500/40 font-semibold"
                  [routerLinkActiveOptions]="{ exact: item.route === '/dashboard' }"
                  (click)="navigate.emit()"
                  class="group flex items-center justify-between gap-3 rounded-lg border border-transparent px-3 py-2.5 text-sm text-slate-300 transition-colors hover:bg-slate-800/70 hover:text-white"
                >
                  <span class="flex items-center gap-3">
                    <svg
                      class="h-5 w-5 shrink-0 text-slate-400 group-hover:text-amber-400 transition-colors"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                      stroke-width="1.8"
                      aria-hidden="true"
                    >
                      <path stroke-linecap="round" stroke-linejoin="round" [attr.d]="item.iconPath" />
                    </svg>
                    <span>{{ item.label }}</span>
                  </span>
                  @if (item.badge) {
                    <span
                      class="rounded-full bg-emerald-500/15 px-2 py-0.5 text-[10px] font-semibold text-emerald-400 border border-emerald-500/30"
                    >
                      {{ item.badge }}
                    </span>
                  }
                </a>
              </li>
            }
          </ul>
        </div>

        <div>
          <p class="px-3 mb-2 text-[11px] font-semibold uppercase tracking-wider text-slate-400">
            Próximas Fases
          </p>
          <ul class="space-y-1">
            @for (item of upcomingItems; track item.route) {
              <li>
                <a
                  [routerLink]="item.route"
                  routerLinkActive="bg-slate-800 text-white font-medium"
                  (click)="navigate.emit()"
                  class="group flex items-center justify-between gap-3 rounded-lg px-3 py-2.5 text-sm text-slate-400 transition-colors hover:bg-slate-800/60 hover:text-slate-200"
                >
                  <span class="flex items-center gap-3">
                    <svg
                      class="h-5 w-5 shrink-0 text-slate-500 group-hover:text-slate-300"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                      stroke-width="1.8"
                      aria-hidden="true"
                    >
                      <path stroke-linecap="round" stroke-linejoin="round" [attr.d]="item.iconPath" />
                    </svg>
                    <span>{{ item.label }}</span>
                  </span>
                  @if (item.badge) {
                    <span
                      class="rounded-full bg-slate-800 px-2 py-0.5 text-[10px] font-medium text-slate-400 border border-slate-700"
                    >
                      {{ item.badge }}
                    </span>
                  }
                </a>
              </li>
            }
          </ul>
        </div>
      </nav>

      <!-- Architecture Footer -->
      <div class="border-t border-slate-800/80 p-4">
        <div class="rounded-lg bg-slate-800/60 p-3 border border-slate-700/60">
          <div class="flex items-center gap-2 text-xs font-medium text-slate-200">
            <span class="h-2 w-2 rounded-full bg-amber-400 animate-pulse" aria-hidden="true"></span>
            Monolito Modular API v1
          </div>
          <p class="mt-1 text-[11px] text-slate-400 leading-relaxed">
            Employee &bull; Location &bull; IdentityAccess conectados
          </p>
        </div>
      </div>
    </aside>
  `,
})
export class SidebarComponent {
  readonly mobileOpen = input<boolean>(false);
  readonly navigate = output<void>();

  readonly coreItems: NavItem[] = [
    {
      label: 'Panel General',
      route: '/dashboard',
      section: 'core',
      iconPath:
        'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6',
    },
    {
      label: 'Empleados',
      route: '/employees',
      badge: 'API v1',
      section: 'core',
      iconPath:
        'M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z',
    },
    {
      label: 'Usuarios y Accesos',
      route: '/users',
      badge: 'RBAC',
      section: 'core',
      iconPath:
        'M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z',
    },
  ];

  readonly upcomingItems: NavItem[] = [
    {
      label: 'Inventario y Materiales',
      route: '/inventory',
      badge: 'Fase 2',
      section: 'upcoming',
      iconPath:
        'M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4',
    },
    {
      label: 'Planillas y Boletas',
      route: '/payroll',
      badge: 'Fase 2',
      section: 'upcoming',
      iconPath:
        'M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z',
    },
  ];
}
