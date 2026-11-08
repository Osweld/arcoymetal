# 01. Arquitectura del Frontend (`arcofrontend`)

## 1. Stack Tecnológico

- **Framework**: Angular `^22.2.0` (Componentes Standalone por defecto, Signals API, nuevo flujo de control `@if` / `@for`, `provideHttpClient(withFetch())`).
- **Estilos**: Tailwind CSS `^4.1.12` (`@import 'tailwindcss';` en `src/styles.css`) con línea visual industrial (tonos acero `slate-900`/`slate-800` y acentos metálicos `amber-500`/`amber-600`).
- **Comunicación Dev Server → Backend**: Proxy configurado en `arcofrontend/proxy.conf.json` que redirige `/api/*` hacia `http://localhost:8080`.

---

## 2. Estructura de Directorios Implementada

```text
arcofrontend/
├── proxy.conf.json
└── src/
    ├── index.html
    ├── styles.css
    └── app/
        ├── app.config.ts
        ├── app.routes.ts
        ├── app.ts
        ├── core/
        │   ├── auth/
        │   │   ├── guards/
        │   │   │   └── auth.guard.ts            # authGuard y roleGuard(['ADMINISTRATOR', ...])
        │   │   ├── interceptors/
        │   │   │   └── auth.interceptor.ts      # Adjunta Bearer Token y withCredentials: true
        │   │   └── services/
        │   │       └── auth.service.ts          # Estado reactivo de sesión con Signals
        │   └── layout/
        │       ├── header/
        │       │   └── header.component.ts      # Barra superior con rol activo y acceso a sesión
        │       ├── sidebar/
        │       │   └── sidebar.component.ts     # Navegación lateral (Módulos Activos + Próximas Fases)
        │       └── main-layout/
        │           └── main-layout.component.ts # Shell adaptable (Desktop + Drawer móvil)
        │
        ├── features/
        │   ├── auth/
        │   │   └── pages/
        │   │       └── login/
        │   │           └── login.component.ts   # Pantalla de inicio de sesión preparada para JWT
        │   ├── dashboard/
        │   │   └── pages/
        │   │       └── dashboard-page/
        │   │           └── dashboard-page.component.ts # KPIs en tiempo real y accesos rápidos
        │   ├── employee/
        │   │   ├── models/
        │   │   │   ├── employee.model.ts        # Interfaces y diccionarios de etiquetas en español
        │   │   │   └── location.model.ts        # DepartmentDetails, MunicipalityDetails, DistrictDetails
        │   │   ├── services/
        │   │   │   ├── employee.service.ts      # Cliente HTTP para /api/v1/employees
        │   │   │   └── location.service.ts      # Cliente HTTP con caché en memoria para /api/v1/locations/hierarchy
        │   │   └── pages/
        │   │       ├── employee-list/
        │   │       │   └── employee-list.component.ts   # Directorio con filtros y tabla responsiva
        │   │       ├── employee-form/
        │   │       │   └── employee-form.component.ts   # Creación con selectores geográficos en cascada
        │   │       └── employee-detail/
        │   │           └── employee-detail.component.ts # Vista de expediente + edición personal en línea
        │   └── identity-access/
        │       ├── models/
        │       │   └── user.model.ts            # UserSummary, CreateUserRequest, PageResult<T>, Role, UserStatus
        │       ├── services/
        │       │   └── user.service.ts          # Cliente HTTP para /api/v1/users
        │       └── pages/
        │           ├── user-list/
        │           │   └── user-list.component.ts       # Tabla paginada cruzada con datos de empleado
        │           └── user-form/
        │               └── user-form.component.ts       # Vinculación de empleado existente + roles RBAC
        └── shared/
            └── components/
                └── coming-soon/
                    └── coming-soon.component.ts # Vista informativa para Inventory y Payroll (Fase 2)
```

---

## 3. Mapa de Rutas (`app.routes.ts`)

| Ruta | Componente (Lazy Loaded) | Propósito |
| :--- | :--- | :--- |
| `/login` | `LoginComponent` | Formulario de acceso institucional preparado para `POST /api/v1/auth/login`. |
| `/dashboard` | `DashboardPageComponent` | Resumen ejecutivo, indicadores de planilla activa y últimos registros. |
| `/employees` | `EmployeeListComponent` | Listado general, búsqueda por nombre/cargo/DUI y filtro por estado laboral. |
| `/employees/new` | `EmployeeFormComponent` | Registro de nuevo empleado con jerarquía geográfica y validaciones salvadoreñas. |
| `/employees/:id` | `EmployeeDetailComponent` | Detalle completo del empleado y edición de información personal (`PUT /personal-information`). |
| `/users` | `UserListComponent` | Consulta paginada de cuentas de usuario y sus roles asignados. |
| `/users/new` | `UserFormComponent` | Creación de cuenta de usuario a partir de un empleado existente (`?employeeId=UUID` opcional). |
| `/inventory` | `ComingSoonComponent` | Hoja de ruta del módulo `Inventory`. |
| `/payroll` | `ComingSoonComponent` | Hoja de ruta del módulo `Payroll`. |

---

## 4. Convenciones de UI, Formularios y Accesibilidad

1. **Tablas de Datos Responsivas (`.table-wrapper`)**:
   - Se utiliza `container-type: inline-size` en `src/styles.css` con `<thead>` y `<th scope="row">` fijos (`sticky`), transformándose automáticamente en tarjetas apiladas con etiquetas inyectadas por CSS (`--label-2`, `--label-3`, etc.) cuando el contenedor mide menos de `640px`.
2. **Controles de Selección en Formularios**:
   - Para catálogos cerrados de menos de 6 opciones (`Gender`, `ContractType`, `PensionFundInstitution`), se utilizan botones de radio visibles (`<input type="radio">`) para facilitar el escaneo visual sin clics adicionales.
   - Para selección múltiple (`Role`), se utilizan casillas de verificación (`<input type="checkbox">`).
   - Para catálogos extensos (`Departamento`, `Municipio`, `Distrito`, `Empleado`), se utilizan `<select>` dependientes.
3. **Campos Numéricos de Identificación**:
   - Campos como `DUI`, `NIT`, `ISSS`, `NUP` y `phoneNumber` usan `<input type="text" inputmode="numeric" autocorrect="off" autocapitalize="off" spellcheck="false">` para conservar ceros a la izquierda y habilitar el teclado numérico en dispositivos móviles.
