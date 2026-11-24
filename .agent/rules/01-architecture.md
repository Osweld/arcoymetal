# 01 — Arquitectura: Monolito Modular y Hexagonal

## 1. Monolito Modular (Spring Modulith)

El backend (`arcobackend`) es un **monolito modular** bajo el paquete raíz `com.arcoymetal.arcobackend`.

### 1.1 Módulos y Mapeo Backend ↔ Frontend

| Módulo | Paquete Backend (`com.arcoymetal.arcobackend.*`) | Feature Frontend (`arcofrontend/src/app/features/*`) | Tipo |
| :--- | :--- | :--- | :--- |
| Employee | `employee` | `employee/` | Negocio (Fase 1) |
| Identity & Access | `identityaccess` | `identity-access/` y `auth/` | Negocio (Fase 1) |
| Payroll | `payroll` | `payroll/` | Negocio (Fase 1) |
| Location | `location` | Consumido en `employee/` | Soporte |
| Shared | `shared` | `core/` y `shared/` | Transversal |

### 1.2 Fronteras Estrictas entre Módulos
1. **Encapsulación por `internal/`**: toda la implementación de un módulo vive dentro de `<modulo>.internal.*`.
2. **Contratos públicos en `<modulo>.api`**: la comunicación síncrona o por eventos entre módulos ocurre **exclusivamente** a través de interfaces, DTOs o eventos expuestos en `<modulo>.api` (ejemplo existente: `employee.api.EmployeeExistenceVerifier`).
3. **PROHIBIDO importar `<otro-modulo>.internal.*`**: ningún módulo (`identityaccess`, `payroll`, `employee`, `location`) puede importar clases de `internal` de otro módulo.
4. **Referencias entre agregados solo por ID**: las relaciones entre módulos se modelan únicamente por identificador (`UUID` o Value Object local, como `identityaccess.internal.domain.vo.EmployeeId`). **PROHIBIDO** crear relaciones JPA (`@ManyToOne`, `@OneToOne`, `@OneToMany`) que crucen fronteras de módulo.
5. **Declaración explícita de módulo**: cada módulo debe declarar sus dependencias permitidas en su `package-info.java` mediante `@org.springframework.modulith.ApplicationModule`.

---

## 2. Arquitectura Hexagonal por Módulo

Dentro de cada módulo de negocio (`<modulo>/internal/`), el código se organiza en tres capas con dirección de dependencias estricta hacia el centro (`infrastructure → application → domain`):

```text
<modulo>/
├── package-info.java
├── api/                         # Contratos públicos expuestos a otros módulos
└── internal/
    ├── domain/                  # Núcleo puro de negocio
    ├── application/
    │   ├── port/in/             # Casos de uso (*UseCase)
    │   ├── port/out/            # Puertos de salida (*Repository, *QueryRepository, generadores)
    │   ├── command/             # Comandos de escritura (*Command)
    │   ├── query/               # Consultas (*Query)
    │   ├── dto/                 # DTOs de aplicación (PageQuery, PageResult, Details, Summary)
    │   └── service/             # Implementaciones de *UseCase (*Service)
    └── infrastructure/
        ├── api/                 # Implementaciones de <modulo>.api.* (*Impl)
        ├── persistence/         # entity/, repository/, mapper/, adapter/, projection/
        ├── web/                 # controller/, request/, response/, mapper/
        ├── security/            # Adaptadores de seguridad (si aplica)
        ├── pdf/                 # Adaptadores de generación PDF (en payroll)
        └── exception/           # Excepciones de infraestructura si aplica
```

### 2.1 Prohibiciones Estrictas de Importación por Capa

#### Capa `internal.domain` (Dominio Puro)
- **PERMITIDO**: únicamente JDK estándar (`java.*`) y clases del propio paquete `domain` del mismo módulo.
- **PROHIBIDO**:
  - `org.springframework.*` (cero `@Service`, `@Component`, `@Transactional`, etc.)
  - `jakarta.persistence.*` (cero `@Entity`, `@Table`, `@Column`, etc.)
  - `jakarta.validation.*` (las invariantes se validan explícitamente en constructores/fábricas de dominio)
  - `com.fasterxml.jackson.*` o `tools.jackson.*`
  - `lombok.*` (las entidades y Value Objects del dominio son Java puro / `record`)
  - Cualquier import desde `internal.application.*`, `internal.infrastructure.*` u otro módulo.

#### Capa `internal.application` (Casos de Uso y Puertos)
- **PERMITIDO**: `internal.domain.*`, contratos públicos `<otro-modulo>.api.*`, anotaciones mínimas de orquestación (`@Service`, `@Transactional`) y Lombok `@RequiredArgsConstructor` en `service/`.
- **PROHIBIDO**:
  - Cualquier import desde `internal.infrastructure.*` (entidades JPA `*Entity`, repositorios `SpringData*`, controladores REST, clases `Request`/`Response` web, librerías de PDF o BCrypt concretas).
  - Construir respuestas HTTP (`ResponseEntity`) o depender de `jakarta.servlet.*`.

#### Capa `internal.infrastructure` (Adaptadores)
- **PERMITIDO**: implementar `port/out/*` y `<modulo>.api.*`; invocar `port/in/*UseCase` desde controladores REST (mantener la convención de **un controlador por caso de uso**).
- **PROHIBIDO**:
  - Colocar reglas de negocio, validaciones de invariantes o cálculos de nómina dentro de controladores, mappers o adaptadores de persistencia.
  - Saltarse `port/in/*UseCase` inyectando repositorios `SpringData*` directamente en controladores.

---

## 3. Arquitectura Frontend (`arcofrontend`)

- **`src/app/core/`**: servicios singleton transversales (`auth/services`, `auth/guards`, `auth/interceptors`) y layout principal (`header`, `sidebar`, `main-layout`).
- **`src/app/features/<modulo>/`**: módulos funcionales autocontenidos con `models/`, `services/` y `pages/`.
- **`src/app/shared/`**: componentes UI reutilizables sin lógica específica de un dominio.
- Las rutas en `app.routes.ts` deben cargarse de forma diferida (`loadComponent`). Evitar acoplamiento directo entre páginas de distintos features.
