# 02 — Convenciones del Stack Tecnológico

## 1. Versiones Exactas del Proyecto

Respetar las APIs y capacidades de las versiones verificadas en el repositorio:

| Capa | Tecnología | Versión exacta |
| :--- | :--- | :--- |
| Backend Runtime | Java | `25` |
| Backend Framework | Spring Boot | `4.1.1` |
| Modularidad | Spring Modulith | `2.1.1` (`core`, `jdbc`, `test`) |
| Base de Datos | PostgreSQL | `17` (en [compose.yml](file:///home/silent/projects/arcoymetal/compose.yml)) |
| Migraciones | Liquibase | Gestionado por Spring Boot `4.1.1` |
| Build Backend | Maven Wrapper | `./mvnw` en `arcobackend/` |
| Frontend Framework | Angular | `^22.2.0` (`@angular/cli` / `@angular/build` `^22.2.6`) |
| Lenguaje Frontend | TypeScript | `~6.0.2`, RxJS `~7.8.0`, `npm@12.2.0` |
| Estilos | Tailwind CSS | `^4.1.12` (`@tailwindcss/postcss`) |
| Testing Frontend | Vitest | `^5.0.0` + `jsdom ^30.0.0` (`@angular/build:unit-test`) |

---

## 2. Manejo de Dinero y Precisión Numérica

- **PROHIBIDO** usar `float`, `double`, `Float` o `Double` para salarios, deducciones, horas extras, bonificaciones, porcentajes de retención o totales monetarios.
- Usar **siempre `java.math.BigDecimal`**:
  - En Value Objects de entrada monetaria (como `BaseSalary`), validar no nulidad, no negatividad y escala exacta (`value.setScale(2, RoundingMode.UNNECESSARY)`).
  - En operaciones aritméticas y cálculos de nómina (`payroll`), construir constantes desde `String` (ej. `new BigDecimal("0.03")`, nunca `new BigDecimal(0.03)`), especificar explícitamente la escala y `RoundingMode.HALF_UP` al redondear divisiones o porcentajes.
  - En PostgreSQL / Liquibase, definir columnas monetarias como `DECIMAL(12,2)` (o precisión explícita mayor si un cálculo intermedio lo requiere).
  - En TypeScript/Angular, formatear montos de visualización con 2 decimales y nunca realizar cálculos fiscales/salariales definitivos en el cliente; el backend es la única fuente de verdad del cálculo.

---

## 3. Base de Datos y Migraciones (Liquibase + JPA)

- **Validación estricta de esquema**: `spring.jpa.hibernate.ddl-auto=validate` es obligatorio. Nunca cambiarlo a `update`, `create` ni `create-drop`.
- **Estructura de changelogs**:
  - Maestro: `arcobackend/src/main/resources/db/changelog/db.changelog-master.yml`.
  - Cambios por módulo: `arcobackend/src/main/resources/db/changelog/changes/<modulo>/NNN-descripcion.yaml` (incluido con `relativeToChangelogFile: true` en el maestro).
  - **Inmutabilidad de migraciones**: nunca editar un changeset ya existente y aplicado; crear siempre un nuevo archivo numerado secuencialmente.
- **Convenciones de entidades JPA**:
  - Llaves primarias `UUID` generadas en el dominio (`*Id.generate()`).
  - Extender `AuditableEntity` (`shared.infrastructure.persistence.entity.AuditableEntity`) para tablas de negocio (`created_at`, `updated_at`, `created_by`, `updated_by` con `TIMESTAMP WITH TIME ZONE` y `UUID`).
  - Persistir enums como `@Enumerated(EnumType.STRING)` (nunca `ORDINAL`).
  - Constructores JPA con `@NoArgsConstructor(access = AccessLevel.PROTECTED)` y `@Getter` (sin `@Data` ni `@Setter` abiertos).

---

## 4. Convenciones Backend (Spring Boot 4 + REST)

- **Endpoints REST**: prefijo `/api/v1/<recurso>` en plural (ej. `/api/v1/employees`, `/api/v1/users`, `/api/v1/payrolls`).
- **DTOs y Value Objects**: usar Java `record` para Value Objects inmutables, `Command`, `Query`, `Request`, `Response` y proyecciones de lectura.
- **Validación HTTP**: validar payloads de entrada con Jakarta Validation (`@Valid`, `@NotBlank`, `@NotNull`, `@Size`, etc.) en los `Request` de `infrastructure/web/request/`.
- **Manejo de errores**: centralizar respuestas de error en `shared.infrastructure.web.exception.GlobalExceptionHandler` devolviendo estructura consistente (`ApiErrorResponse` / códigos HTTP semánticos: `201 Created` en creación de recursos nuevos, `200 OK` en consultas/actualizaciones, `400`, `404`, `409`).
- **Docker Compose local**: el archivo de infraestructura local es `compose.yml` en la raíz del repositorio (PostgreSQL 17 en `127.0.0.1:5432`).

---

## 5. Convenciones Frontend (Angular 22 + Tailwind CSS v4)

- **Standalone Components**: todos los componentes, directivas y pipes son `standalone` por defecto; **PROHIBIDO** crear `NgModule`.
- **Signals API**: usar `signal()`, `computed()`, `effect()`, `input()` y `output()` para el estado reactivo de componentes y servicios (como `AuthService`). Reservar RxJS para llamadas HTTP (`HttpClient`) y composición asíncrona.
- **Sintaxis moderna de plantillas**: usar exclusivamente el flujo de control nativo (`@if`, `@for` con `track`, `@switch`); evitar `*ngIf` y `*ngFor`.
- **HTTP y Routing**: usar `provideHttpClient(withFetch(), withInterceptors([authInterceptor]))` y `provideRouter(routes, withComponentInputBinding())`. El dev server proxea `/api/*` a `http://localhost:8080` vía `arcofrontend/proxy.conf.json`.
- **UI/UX y Estilos**:
  - Tailwind CSS v4 (`@import 'tailwindcss';` en `src/styles.css`) manteniendo la estética industrial (`slate-900`/`slate-800` y acentos `amber-500`/`amber-600`).
  - Tablas responsivas usando `.table-wrapper` (`container-type: inline-size`) con etiquetas móviles vía variables CSS (`--label-2`, etc.).
  - Campos de códigos salvadoreños (`DUI`, `NIT`, `ISSS`, `NUP`, teléfono) como `<input type="text" inputmode="numeric" autocorrect="off" autocapitalize="off" spellcheck="false">` para preservar ceros a la izquierda.
