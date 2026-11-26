import { Routes } from '@angular/router';
import { MainLayoutComponent } from './core/layout/main-layout/main-layout.component';
import { authGuard, roleGuard } from './core/auth/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/pages/login/login.component').then((m) => m.LoginComponent),
    title: 'Iniciar Sesión — Arco & Metal Mecánica',
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard',
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/pages/dashboard-page/dashboard-page.component').then(
            (m) => m.DashboardPageComponent
          ),
        title: 'Panel General — Arco & Metal Mecánica',
      },
      {
        path: 'employees',
        loadComponent: () =>
          import('./features/employee/pages/employee-list/employee-list.component').then(
            (m) => m.EmployeeListComponent
          ),
        title: 'Empleados — Arco & Metal Mecánica',
      },
      {
        path: 'employees/new',
        loadComponent: () =>
          import('./features/employee/pages/employee-form/employee-form.component').then(
            (m) => m.EmployeeFormComponent
          ),
        title: 'Registrar Empleado — Arco & Metal Mecánica',
      },
      {
        path: 'employees/:id',
        loadComponent: () =>
          import('./features/employee/pages/employee-detail/employee-detail.component').then(
            (m) => m.EmployeeDetailComponent
          ),
        title: 'Expediente de Empleado — Arco & Metal Mecánica',
      },
      {
        path: 'users',
        canActivate: [roleGuard(['ADMINISTRATOR'])],
        loadComponent: () =>
          import('./features/identity-access/pages/user-list/user-list.component').then(
            (m) => m.UserListComponent
          ),
        title: 'Usuarios y Accesos — Arco & Metal Mecánica',
      },
      {
        path: 'users/new',
        canActivate: [roleGuard(['ADMINISTRATOR'])],
        loadComponent: () =>
          import('./features/identity-access/pages/user-form/user-form.component').then(
            (m) => m.UserFormComponent
          ),
        title: 'Nueva Cuenta de Usuario — Arco & Metal Mecánica',
      },
      {
        path: 'users/:id',
        canActivate: [roleGuard(['ADMINISTRATOR'])],
        loadComponent: () =>
          import('./features/identity-access/pages/user-detail/user-detail.component').then(
            (m) => m.UserDetailComponent
          ),
        title: 'Gestionar Cuenta de Usuario — Arco & Metal Mecánica',
      },
      {
        path: 'inventory',
        loadComponent: () =>
          import('./shared/components/coming-soon/coming-soon.component').then(
            (m) => m.ComingSoonComponent
          ),
        data: { module: 'inventory' },
        title: 'Inventario — Arco & Metal Mecánica',
      },
      {
        path: 'payroll',
        loadComponent: () =>
          import('./shared/components/coming-soon/coming-soon.component').then(
            (m) => m.ComingSoonComponent
          ),
        data: { module: 'payroll' },
        title: 'Planillas — Arco & Metal Mecánica',
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'dashboard',
  },
];
