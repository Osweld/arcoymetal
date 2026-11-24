---
name: build-feature
description: Flujo estándar de principio a fin para diseñar, implementar y verificar una funcionalidad nueva en Arco & Metal ERP respetando las Workspace Rules y los skills del proyecto.
---

# Workflow: Construir Funcionalidad Nueva (`build-feature`)

Usa este flujo para toda nueva funcionalidad en Fase 1 (pendientes de `employee`/`identityaccess` y desarrollo de `payroll`).

---

## Etapa 1 — Spec Corta

Antes de proponer código, redacta una especificación breve que responda:
- **Objetivo**: qué problema de negocio resuelve.
- **Módulo(s) afectados**: `employee`, `identityaccess`, `payroll` (y si requiere contrato público en `<modulo>.api`).
- **Invariantes aplicables** según [00-domain-context.md](../../rules/00-domain-context.md) (ej. relación opcional `Employee`–`User`, frontera de `payroll` sin pagos bancarios, inmutabilidad histórica).
- **Contrato HTTP**: endpoints `/api/v1/...`, payload `Request`/`Response` y códigos de estado (`201`, `200`, `400`, `404`, `409`).
- **Criterios de aceptación** verificables.

---

## Etapa 2 — Implementation Plan (Puerta de Aprobación Obligatoria)

Elabora el `implementation_plan.md` detallando los archivos exactos a crear o modificar en:
1. Dominio puro (`internal/domain/`) y tests unitarios.
2. Aplicación (`command`/`query`, `port/in`, `port/out`, `service`).
3. Infraestructura (`entity`, `repository`, `mapper`, `adapter`, `web/controller`, `pdf` si aplica) y migración Liquibase (`changes/<modulo>/NNN-*.yaml` + `db.changelog-master.yml`).
4. Frontend (`arcofrontend/src/app/features/<feature>/` + `app.routes.ts`).
5. Plan de verificación (Puertas 1 a 4).

> **DETENERSE AQUÍ**: no escribir código de aplicación ni migraciones hasta recibir aprobación explícita del usuario sobre el plan.

---

## Etapa 3 — Dominio Puro + Tests Unitarios

Siguiendo [hexagonal-use-case](../hexagonal-use-case/SKILL.md) y [01-architecture.md](../../rules/01-architecture.md):
1. Implementar agregados, entidades, Value Objects (`BigDecimal` para dinero) y excepciones en `internal/domain/` sin dependencias de Spring/JPA/Lombok.
2. Escribir los tests unitarios JUnit 5 puros en `src/test/java/com/arcoymetal/arcobackend/<modulo>/internal/domain/`.
3. Ejecutar los tests de dominio y confirmar que pasan antes de avanzar a infraestructura.

---

## Etapa 4 — Aplicación + Infraestructura + Migración Liquibase

Siguiendo [hexagonal-use-case](../hexagonal-use-case/SKILL.md) y [02-stack-conventions.md](../../rules/02-stack-conventions.md):
1. Implementar `Command`/`Query`, `*UseCase`, `port/out`, `*Service` (`@Transactional`) y, si otro módulo necesita consumir datos, exponer el contrato en `<modulo>.api` + `internal/infrastructure/api/*Impl`.
2. Crear el changeset Liquibase numerado en `db/changelog/changes/<modulo>/` y registrarlo en `db.changelog-master.yml` (manteniendo `ddl-auto=validate`).
3. Implementar entidades JPA (`extends AuditableEntity`), `SpringData*Repository`, mappers, adaptadores y **1 `@RestController` por caso de uso** bajo `/api/v1/*`.

---

## Etapa 5 — UI Angular

Si la funcionalidad tiene interfaz de usuario, seguir [angular-feature-ui](../angular-feature-ui/SKILL.md):
1. Definir modelos TypeScript y diccionarios `*_LABELS` alineados a los `record` del backend.
2. Implementar/actualizar el servicio HTTP en `services/` y los componentes standalone con Signals y `@if`/`@for` en `pages/`.
3. Aplicar `.table-wrapper` en tablas, formularios accesibles con `inputmode="numeric"` para códigos salvadoreños, y registrar rutas lazy en `app.routes.ts`.

---

## Etapa 6 — Verificación Completa y Walkthrough

Ejecutar **las 4 puertas** de [03-verification-harness.md](../../rules/03-verification-harness.md):
1. **Puerta 1 (Dominio)**: `cd arcobackend && ./mvnw test -Dtest="*Test"`
2. **Puerta 2 (Fronteras Modulares)**: verificación Spring Modulith (`ApplicationModules.verify()`).
3. **Puerta 3 (PostgreSQL + Integración)**: `docker compose -f compose.yml up -d` + `cd arcobackend && ./mvnw test` (Liquibase + `ddl-auto=validate` + tests de integración).
4. **Puerta 4 (Frontend + UI)**: `cd arcofrontend && npm run build`, `npm test -- --watch=false`, y validación visual en navegador del flujo implementado (desktop y móvil).

Al finalizar todo en verde, entregar un **Walkthrough** conciso con los archivos creados/modificados, decisiones clave y evidencia de las verificaciones ejecutadas.
