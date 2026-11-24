# 03 — Arnés de Verificación Obligatorio

Ninguna tarea se considera terminada hasta ejecutar y aprobar todas las puertas de verificación aplicables a los archivos modificados. No asumir que un cambio compila o pasa pruebas sin ejecutarlo.

---

## 1. Puerta 1 — Tests Unitarios de Dominio (Backend)

Todo código nuevo o modificado en `internal/domain/` (Agregados, Entidades, Value Objects, reglas de cálculo de `payroll`) **debe** acompañarse de tests unitarios JUnit 5 puros:
- **Sin levantar contexto de Spring** (ejecución instantánea, sin `@SpringBootTest`).
- **Ubicación**: `arcobackend/src/test/java/com/arcoymetal/arcobackend/<modulo>/internal/domain/` *(usar siempre `domain` correctamente escrito; no replicar el typo histórico `domian` existente en tests antiguos de `employee`)*.
- Cubrir casos felices, valores frontera (ej. montos en `0.00`, redondeos `BigDecimal`, fechas límite) y excepciones de invariantes (ej. salario negativo, usuario sin roles, formatos inválidos).

```bash
cd arcobackend && ./mvnw test -Dtest="*Test"
```

---

## 2. Puerta 2 — Verificación de Fronteras Modulares (Spring Modulith)

Cuando se agreguen módulos, contratos `api`, dependencias entre módulos o cambios estructurales de paquetes:
- Verificar la integridad del monolito modular con `spring-modulith-starter-test` (`ApplicationModules.of(ArcobackendApplication.class).verify()`).
- Confirmar que no existan ciclos entre módulos ni imports hacia `<otro-modulo>.internal.*`.

---

## 3. Puerta 3 — Integración con PostgreSQL (Docker Compose / Testcontainers)

Siempre que se modifiquen migraciones Liquibase, entidades JPA, repositorios, consultas SQL o controladores REST:

1. **PostgreSQL vía Docker Compose (`compose.yml`)**:
   - Asegurar que el contenedor `postgres:17` esté levantado y saludable (`pg_isready`):
     ```bash
     docker compose -f compose.yml up -d
     docker compose -f compose.yml ps
     ```
2. **Validación de Liquibase + Hibernate (`ddl-auto=validate`) e Integración**:
   - Ejecutar la suite completa de tests de backend (incluyendo `ArcobackendApplicationTests` y tests de integración con PostgreSQL vía `TestcontainersConfiguration` o contra el Postgres de Compose):
     ```bash
     cd arcobackend && ./mvnw test
     ```
   - Confirmar que todos los changesets de `db.changelog-master.yml` aplican sin errores y que Hibernate valida el esquema contra las entidades JPA sin discrepancias de tipos ni columnas.
   - Si se agrega un endpoint REST o generación de PDF (`payroll`), validar el código HTTP esperado, la estructura JSON o el `Content-Type: application/pdf` y bytes válidos del PDF.

---

## 4. Puerta 4 — Compilación, Tests y Validación de UI (Frontend)

Siempre que se modifique código en `arcofrontend/`:

1. **Compilación de producción (verificación estricta de TypeScript y plantillas Angular)**:
   ```bash
   cd arcofrontend && npm run build
   ```
2. **Tests unitarios con Vitest**:
   ```bash
   cd arcofrontend && npm test -- --watch=false
   ```
3. **Validación visual/funcional de UI**:
   - Cuando el cambio afecte pantallas, formularios, tablas o flujos de usuario, verificar el flujo afectado en el navegador contra el dev server (`npm start` con `proxy.conf.json` hacia `:8080`).
   - Comprobar ausencia de errores en consola del navegador y en respuestas HTTP (`/api/v1/*`), así como correcta visualización en escritorio y vista móvil/estrecha (`.table-wrapper`).

---

## 5. Criterio de Cierre (Definition of Done)

Antes de reportar una tarea como completada:
- [ ] Tests unitarios de dominio en verde para toda regla de negocio nueva o modificada.
- [ ] Fronteras modulares y prohibiciones de importación hexagonal respetadas (sin imports prohibidos en `domain` ni `application`).
- [ ] Migraciones Liquibase + `hibernate.ddl-auto=validate` + tests de integración con PostgreSQL en verde.
- [ ] `npm run build` y `npm test -- --watch=false` en verde (y revisión en navegador si hubo cambios de UI).
- [ ] Ningún secreto, archivo temporal ni configuración rota introducida en el repositorio.
