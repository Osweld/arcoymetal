---
name: audit-and-test
description: Auditar un módulo existente (employee o identityaccess) contra las Workspace Rules, proponer refactorizaciones mínimas y completar su cobertura de pruebas unitarias e integración hasta dejar en verde el harness de verificación.
---

# Workflow: Auditar y Testear Módulo Existente (`audit-and-test`)

Usa este flujo para consolidar módulos ya implementados (`employee` o `identityaccess`) antes de agregar nuevas funcionalidades.

---

## Paso 1 — Auditoría Estática contra Workspace Rules

Selecciona el módulo objetivo (`employee` o `identityaccess`, o ambos si el usuario lo indica) e inspecciona **todo su código actual** (backend `arcobackend`, migraciones Liquibase, tests existentes y frontend `arcofrontend/src/app/features/<modulo>`) contra:
- [00-domain-context.md](../../rules/00-domain-context.md)
- [01-architecture.md](../../rules/01-architecture.md)
- [02-stack-conventions.md](../../rules/02-stack-conventions.md)

Genera un reporte estructurado de hallazgos clasificados por severidad (**Crítico**, **Medio**, **Menor**), revisando explícitamente:
1. **Fronteras modulares (Spring Modulith)**:
   - Existencia y corrección de `package-info.java` con `@ApplicationModule`.
   - Cero imports hacia `<otro-modulo>.internal.*` y cero relaciones JPA entre módulos.
   - Existencia de test `ApplicationModules.of(ArcobackendApplication.class).verify()`.
2. **Arquitectura hexagonal**:
   - `internal/domain/` libre de Spring, JPA, Validation, Jackson y Lombok.
   - `internal/application/` libre de `internal.infrastructure.*`.
   - Excepciones semánticas en lugar de `RuntimeException` genéricas (ej. `findById`).
   - Códigos HTTP correctos (`201 Created` en `POST` de creación, `200 OK` en `GET`/`PUT`).
3. **Invariantes de dominio y convenciones**:
   - Relación `0..1` `Employee` → `User` validada vía `EmployeeExistenceVerifier` y unicidad.
   - `BigDecimal` y `DECIMAL(12,2)` para salarios/montos.
   - Consistencia entre `EmployeeEntity`/`UserEntity` y los changesets de Liquibase (`ddl-auto=validate`).
   - Alineación de contratos DTO entre backend y `arcofrontend`.
4. **Higiene y brechas de pruebas**:
   - Typos en nombres de paquetes/clases (ej. `domian`, `Updade*`, `Employmen*`), código muerto o métodos sin cubrir.
   - Inventario exacto de clases de `internal/domain/` y endpoints/repositorios que carecen de tests.

---

## Paso 2 — Plan de Refactorización Mínima y Cobertura de Pruebas (Puerta de Aprobación)

Con base en el reporte del Paso 1, crea un **Implementation Plan** (`implementation_plan.md`) y **detente hasta que el usuario lo apruebe**:

1. **Refactorizaciones quirúrgicas** (solo lo necesario para corregir violaciones a las reglas, sin agregar funcionalidades fuera de alcance):
   - Corrección de typos en paquetes/clases (ej. mover tests de `domian` a `domain`).
   - Agregar `package-info.java` faltante y alinear excepciones/códigos HTTP.
2. **Plan de pruebas unitarias de dominio (`internal/domain/`)**:
   - Listar cada clase de dominio (`Agregado`, `Entidad`, `VO`, `Excepción`) y los casos a cubrir (camino feliz, valores límite y violaciones de invariantes) en tests JUnit 5 puros sin Spring.
3. **Plan de pruebas de fronteras e integración**:
   - Test de verificación modular con Spring Modulith (`ApplicationModules.verify()`).
   - Tests de integración con PostgreSQL (`compose.yml` / `TestcontainersConfiguration`) validando Liquibase, `hibernate.ddl-auto=validate`, adaptadores de persistencia y controladores REST.
   - Tests unitarios con Vitest en `arcofrontend` si se ajustaron contratos o servicios/componentes.

---

## Paso 3 — Ejecución y Verificación en Verde

Una vez aprobado el plan del Paso 2:
1. Aplicar las refactorizaciones mínimas siguiendo los skills [hexagonal-use-case](../hexagonal-use-case/SKILL.md) y [angular-feature-ui](../angular-feature-ui/SKILL.md).
2. Implementar todas las pruebas planificadas (dominio, Spring Modulith, integración PostgreSQL y frontend si aplica).
3. Ejecutar **todas las puertas aplicables** de [03-verification-harness.md](../../rules/03-verification-harness.md):
   - Puerta 1: `cd arcobackend && ./mvnw test -Dtest="*Test"`
   - Puerta 2: test `ApplicationModules.verify()` en verde.
   - Puerta 3: `docker compose -f compose.yml up -d` + `cd arcobackend && ./mvnw test` (Liquibase + `ddl-auto=validate` + integración PostgreSQL).
   - Puerta 4 (si se tocó frontend): `cd arcofrontend && npm run build` y `npm test -- --watch=false`.
4. Corregir cualquier fallo hasta lograr 100% en verde y entregar un resumen con la evidencia de comandos y resultados.
