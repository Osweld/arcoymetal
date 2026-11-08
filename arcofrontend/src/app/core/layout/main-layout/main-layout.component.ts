import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { HeaderComponent } from '../header/header.component';

@Component({
  selector: 'app-main-layout',
  imports: [RouterOutlet, SidebarComponent, HeaderComponent],
  template: `
    <div class="flex h-screen overflow-hidden bg-slate-50">
      <!-- Desktop Sidebar -->
      <div class="hidden lg:flex lg:shrink-0">
        <app-sidebar />
      </div>

      <!-- Mobile Sidebar Overlay -->
      @if (mobileMenuOpen()) {
        <div class="fixed inset-0 z-40 flex lg:hidden" role="dialog" aria-modal="true">
          <div
            class="fixed inset-0 bg-slate-900/60 backdrop-blur-xs transition-opacity"
            (click)="closeMobileMenu()"
            aria-hidden="true"
          ></div>
          <div class="relative z-50 flex w-72 max-w-xs flex-1 flex-col">
            <app-sidebar (navigate)="closeMobileMenu()" />
          </div>
        </div>
      }

      <!-- Content Area -->
      <div class="flex flex-1 flex-col overflow-hidden">
        <app-header (toggleMobileMenu)="toggleMobileMenu()" />

        <main class="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8">
          <div class="mx-auto max-w-7xl">
            <router-outlet />
          </div>
        </main>
      </div>
    </div>
  `,
})
export class MainLayoutComponent {
  readonly mobileMenuOpen = signal(false);

  toggleMobileMenu(): void {
    this.mobileMenuOpen.update((v) => !v);
  }

  closeMobileMenu(): void {
    this.mobileMenuOpen.set(false);
  }
}
