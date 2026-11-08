# 03. Bitácora de Avance del Frontend y Pendientes

## Historial de Implementación

### [2026-11-08] — Iteración 1: Base Arquitectónica + Módulos Employee, Location e IdentityAccess
- **Configuración general**:
  - Creación de `arcofrontend/proxy.conf.json` apuntando a `http://localhost:8080`.
  - Configuración de `provideHttpClient(withFetch(), withInterceptors([authInterceptor]))` en `app.config.ts`.
  - Definición de rutas con `loadComponent` en `app.routes.ts`.
- **Core y Layout**:
  - Implementación de `AuthService`, `authInterceptor`, `authGuard` y `roleGuard`.
  - Implementación de `MainLayoutComponent`, `SidebarComponent` y `HeaderComponent` con diseño industrial en Tailwind CSS v4.
- **Pantallas funcionales conectadas al Backend**:
  - `LoginComponent` (`/login`): Lista para conectarse al futuro endpoint `POST /api/v1/auth/login`.
  - `DashboardPageComponent` (`/dashboard`): Tarjetas de indicadores y resúmenes de empleados y usuarios.
  - `EmployeeListComponent` (`/employees`): Tabla responsiva, buscador y filtro por estado.
  - `EmployeeFormComponent` (`/employees/new`): Formulario completo de alta de empleado con selectores geográficos en cascada (`GET /api/v1/locations/hierarchy`).
  - `EmployeeDetailComponent` (`/employees/:id`): Vista de detalle del expediente y formulario en línea para editar información personal (`PUT /api/v1/employees/{id}/personal-information`).
  - `UserListComponent` (`/users`): Listado paginado (`PageResult<UserSummary>`) cruzado con nombres de empleados.
  - `UserFormComponent` (`/users/new`): Creación de cuentas de usuario vinculadas a un empleado y asignación de roles RBAC.
  - `ComingSoonComponent` (`/inventory`, `/payroll`): Vistas de hoja de ruta para los módulos de la Fase 2.

---

## Próximas Tareas en el Frontend (Cuando se agreguen en el Backend)

### 1. Módulo `Employee` (Comportamientos del Agregado pendientes de exponer en Web Controllers)
Cuando expongas los controladores REST en `arcobackend` para los demás métodos del agregado `Employee`, agregaremos en `EmployeeDetailComponent`:
- [ ] **Actualizar información laboral** (`updateEmploymentInformation()`): Modal o sección en línea para cambiar puesto (`jobTitle`) y tipo de contrato (`contractType`).
- [ ] **Cambiar salario base** (`changeBaseSalary()`): Formulario específico para ajuste salarial.
- [ ] **Corregir documentos de identificación** (`correctIdentificationInformation()`): Edición de `DUI`, `NIT`, `ISSS`, `NUP` e institución de pensión.
- [ ] **Corregir fechas** (`correctBirthDate()` y `correctHireDate()`).
- [ ] **Dar de baja / Reactivar empleado** (`terminate()` y `reactivate()`): Botones de acción con confirmación y fecha de baja (`terminationDate`).

### 2. Módulo `IdentityAccess` (Gestión de Usuarios y Autenticación JWT)
Cuando implementes los endpoints correspondientes en `arcobackend`:
- [ ] **Conectar `AuthService` a los endpoints reales de seguridad**:
  - `POST /api/v1/auth/login` (guardar Access Token en memoria/estado y recibir cookie `HttpOnly` de Refresh Token).
  - `POST /api/v1/auth/refresh` (renovación silenciosa en `authInterceptor` ante respuestas `401 Unauthorized`).
  - `POST /api/v1/auth/logout` (limpieza de estado y revocación de sesión).
  - `GET /api/v1/auth/me` (recuperar el perfil del usuario autenticado + datos de `EmployeeProfileProvider`).
- [ ] **Acciones de administración de cuentas en `UserListComponent`**:
  - Modificar roles de un usuario existente (agregar/quitar `ADMINISTRATOR` o `MANAGER`).
  - Desactivar (`DISABLED`) y reactivar (`ACTIVE`) cuentas de usuario.
  - Cambiar o restablecer contraseña.
