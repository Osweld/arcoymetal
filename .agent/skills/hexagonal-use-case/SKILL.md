---
name: hexagonal-use-case
description: >-
  Use this skill when creating, extending, or refactoring backend use cases
  (commands, queries, aggregates, persistence adapters, Liquibase migrations, or
  REST controllers) in arcobackend modules (employee, identityaccess, payroll)
  following Arco & Metal's modular monolith and hexagonal architecture.
---

# Skill: Caso de Uso Hexagonal (`arcobackend`)

Procedimiento estándar para implementar o refactorizar casos de uso en `arcobackend` respetando [01-architecture.md](../../rules/01-architecture.md) y [02-stack-conventions.md](../../rules/02-stack-conventions.md).

---

## 1. Plantilla Real de Archivos por Caso de Uso

Raíz del módulo: `arcobackend/src/main/java/com/arcoymetal/arcobackend/<modulo>/`

### A. Caso de Uso de Escritura (`Command`)

| Capa | Ruta y convención de archivo | Responsabilidad |
| :--- | :--- | :--- |
| Dominio | `internal/domain/<subdominio>/<Agregado>.java` y VOs `record` | Reglas de negocio puras, fábricas `create(...)` y `reconstitute(...)`. |
| App — Command | `internal/application/command/<Accion><Entidad>Command.java` | Java `record` inmutable con datos de entrada del caso de uso. |
| App — Port In | `internal/application/port/in/<Accion><Entidad>UseCase.java` | Interfaz con un único método de entrada. |
| App — Port Out | `internal/application/port/out/<Entidad>Repository.java` | Puerto de persistencia o servicio externo (ej. `PasswordHasher`). |
| App — Service | `internal/application/service/<Accion><Entidad>Service.java` | `@Service`, `@RequiredArgsConstructor`, `@Transactional`. Orquesta unicidad, contratos `<otro-modulo>.api.*`, dominio y `port/out`. |
| Infra — Entity | `internal/infrastructure/persistence/entity/<Entidad>Entity.java` | `@Entity`, `@Table`, `extends AuditableEntity`, `@Getter`, `@NoArgsConstructor(access = AccessLevel.PROTECTED)`, `@AllArgsConstructor`, `@Enumerated(EnumType.STRING)`. |
| Infra — Repo JPA | `internal/infrastructure/persistence/repository/SpringData<Entidad>Repository.java` | Extiende `JpaRepository<<Entidad>Entity, UUID>`. |
| Infra — Mapper | `internal/infrastructure/persistence/mapper/<Entidad>PersistenceMapper.java` | `@Component` con `toEntity(Domain)` y `toDomain(Entity)` (usando `reconstitute`). |
| Infra — Adapter | `internal/infrastructure/persistence/adapter/<Entidad>PersistenceAdapter.java` | `@Repository`, `@RequiredArgsConstructor`, implementa `port/out/<Entidad>Repository`. |
| Infra — Web | `internal/infrastructure/web/{request,response,mapper,controller}/` | `record` Request (`@Valid`), `record` Response, `*RequestMapper` (`@Component`) y **1 `@RestController` por caso de uso** bajo `/api/v1/<recurso>`. |
| Migración BD | `src/main/resources/db/changelog/changes/<modulo>/NNN-<desc>.yaml` | Changeset Liquibase inmutable + `include` en `db.changelog-master.yml`. |

### B. Caso de Uso de Lectura (`Query`)

| Capa | Ruta y convención de archivo | Responsabilidad |
| :--- | :--- | :--- |
| App — Query / DTO | `internal/application/query/<Get...>Query.java` y `dto/<Entidad>Details.java` (o `PageQuery` / `PageResult<T>`) | Java `record`s de consulta y proyección de salida. |
| App — Port In/Out | `port/in/<Get...>UseCase.java` y `port/out/<Entidad>QueryRepository.java` | Contratos de lectura desacoplados del agregado de escritura. |
| App — Service | `internal/application/service/<Get...>Service.java` | `@Service`, `@Transactional(readOnly = true)` si aplica. |
| Infra — Read | `persistence/projection/*Projection.java`, `persistence/adapter/*QueryRepositoryAdapter.java`, `web/controller/<Get...>Controller.java` | Consulta optimizada + **1 `@RestController` por consulta**. |

---

## 2. Reglas de Implementación por Capa

1. **Dominio puro (`internal/domain/`)**:
   - **Cero imports** de `org.springframework.*`, `jakarta.persistence.*`, `jakarta.validation.*`, `lombok.*` o Jackson.
   - Validar invariantes con `Objects.requireNonNull(...)` y excepciones de dominio o `IllegalArgumentException`.
   - Inyectar `java.time.Clock` como parámetro de método cuando una regla dependa de la fecha actual (ej. `Employee.create(..., clock)`), nunca llamar `LocalDate.now()` oculto en el dominio.
   - Dinero siempre con `BigDecimal` (escala 2; `RoundingMode.UNNECESSARY` en entrada como `BaseSalary`, `RoundingMode.HALF_UP` en cálculos), jamás `float`/`double`.
2. **Aplicación (`internal/application/`)**:
   - Nunca importar `internal.infrastructure.*` ni `<otro-modulo>.internal.*`.
   - Para validar existencia o datos de otro módulo, inyectar exclusivamente su interfaz pública `<otro-modulo>.api.*` (ej. `EmployeeExistenceVerifier`).
   - En `findById`, lanzar una excepción semántica de negocio/módulo (ej. `EmployeeNotFoundException`) manejada por `GlobalExceptionHandler`, nunca `RuntimeException` genérica.
3. **Infraestructura (`internal/infrastructure/`)**:
   - Mantener **un controlador por caso de uso** bajo `/api/v1/<recurso>`.
   - Devolver `HttpStatus.CREATED` (`201`) en `POST` que creen recursos nuevos, y `200 OK` en `GET`/`PUT`.
   - En Liquibase, usar `UUID` como PK, `DECIMAL(12,2)` para dinero y columnas de auditoría (`created_at`, `updated_at`, `created_by`, `updated_by`). Nunca editar changesets ya aplicados; mantener `spring.jpa.hibernate.ddl-auto=validate`.

---

## 3. Checklist Obligatorio antes de Cerrar

- [ ] `internal/domain/` contiene únicamente imports de `java.*` y del propio `domain`.
- [ ] `internal/application/` no importa nada de `internal.infrastructure.*`.
- [ ] No existen imports hacia `<otro-modulo>.internal.*` ni relaciones JPA cruzadas entre módulos.
- [ ] Todos los valores monetarios usan `BigDecimal` y `DECIMAL(12,2)`.
- [ ] Si hubo cambios de esquema, existe changeset YAML nuevo registrado en `db.changelog-master.yml`.
- [ ] Existen tests unitarios puros en `src/test/java/com/arcoymetal/arcobackend/<modulo>/internal/domain/` (sin Spring).
- [ ] Verificadas las puertas aplicables de [03-verification-harness.md](../../rules/03-verification-harness.md) (`./mvnw test` + Spring Modulith + PostgreSQL).
