import { Component, inject, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../auth/services/auth.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  template: `
    <header
      class="sticky top-0 z-20 flex h-16 items-center justify-between border-b border-slate-200 bg-white/95 px-4 sm:px-6 lg:px-8 backdrop-blur"
    >
      <div class="flex items-center gap-3">
        <button
          type="button"
          (click)="toggleMobileMenu.emit()"
          class="inline-flex items-center justify-center rounded-lg p-2 text-slate-600 hover:bg-slate-100 hover:text-slate-900 lg:hidden"
          aria-label="Abrir menú de navegación"
        >
          <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>

        <a
          routerLink="/dashboard"
          class="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-white p-0.5 ring-1 ring-slate-200 shadow-2xs lg:hidden overflow-hidden"
          aria-label="Ir al Panel General"
        >
          <img
            src="images/logo-full.jpg"
            alt="Logo de Arco &amp; Metal Mecánica"
            class="h-full w-full rounded-full object-contain"
          />
        </a>

        <div>
          <h1 class="text-sm sm:text-base font-bold text-slate-900">
            Sistema de Gestión Administrativa
          </h1>
          <p class="hidden sm:block text-xs text-slate-500">
            Arco &amp; Metal Mecánica &mdash; Metapán, Santa Ana, El Salvador
          </p>
        </div>
      </div>

      <div class="flex items-center gap-3 sm:gap-4">
        @if (authService.currentUser(); as user) {
          <div class="flex items-center gap-3">
            <div class="hidden md:block text-right">
              <p class="text-xs font-semibold text-slate-800">{{ user.email }}</p>
              <div class="flex items-center justify-end gap-1.5 mt-0.5">
                @for (role of user.roles; track role) {
                  <span
                    class="inline-flex items-center rounded bg-amber-100 px-1.5 py-0.5 text-[10px] font-bold uppercase tracking-wide text-amber-800"
                  >
                    {{ role }}
                  </span>
                }
              </div>
            </div>

            <button
              type="button"
              (click)="authService.logout()"
              class="inline-flex items-center gap-1.5 rounded-lg border border-slate-200 bg-slate-50 px-3 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-100 hover:text-slate-900 transition-colors cursor-pointer"
              title="Cerrar sesión en el sistema"
            >
              <svg class="h-4 w-4 text-slate-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M15.75 9V5.25A2.25 2.25 0 0013.5 3h-6a2.25 2.25 0 00-2.25 2.25v13.5A2.25 2.25 0 007.5 21h6a2.25 2.25 0 002.25-2.25V15m3 0l3-3m0 0l-3-3m3 3H9"
                />
              </svg>
              <span>Cerrar sesión</span>
            </button>
          </div>
        }
      </div>
    </header>
  `,
})
export class HeaderComponent {
  readonly authService = inject(AuthService);
  readonly toggleMobileMenu = output<void>();
}
