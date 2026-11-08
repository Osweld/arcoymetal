# 04. Observaciones y Recomendaciones para el Backend (`arcobackend`)

Durante la revisión de los controladores, DTOs y estructura de paquetes de `arcobackend` para conectar el frontend de Angular, se identificaron los siguientes detalles técnicos que te pueden resultar útiles en tus próximos ajustes del backend:

---

## 1. Manejo Global de Excepciones (`@RestControllerAdvice` + `ProblemDetail`)

- **Situación actual**: Existen excepciones de dominio y aplicación bien definidas (`EmployeeNotFoundException`, `UserMustHaveAtLeastOneRoleException`, `UserAlreadyHasRoleException`, `UserDoesNotHaveRoleException`), pero aún no hay un `@RestControllerAdvice` en `shared/infrastructure/web/` que las traduzca a respuestas HTTP estructuradas.
- **Recomendación**: Implementar un `GlobalExceptionHandler` utilizando `ProblemDetail` (estándar RFC 7807 incluido nativamente en Spring Boot 3):
  - `EmployeeNotFoundException` → `404 Not Found`
  - `UserMustHaveAtLeastOneRoleException` / `IllegalArgumentException` → `400 Bad Request`
  - `UserAlreadyHasRoleException` / violaciones de unicidad (`email`, `dui`, `employee_id`) → `409 Conflict`
  - `MethodArgumentNotValidException` (`@Valid`) → `400 Bad Request` devolviendo un mapa `errors: { campo: mensaje }` para que Angular pueda mostrar el error exacto debajo de cada `<input>`.

---

## 2. Longitud Máxima de Contraseña en `CreateUserRequest`

- **Situación actual**: En `CreateUserRequest.java` la restricción actual es:
  ```java
  @NotBlank @Size(min = 8, max = 20) String password
  ```
- **Recomendación**: En tu diseño conceptual planteabas `@Size(min = 8, max = 72)` (que coincide con el límite de 72 bytes de BCrypt). Ampliar `max = 20` a `max = 72` en `CreateUserRequest.java` permitirá a los usuarios utilizar gestores de contraseñas o frases de paso largas sin ser rechazados por validación HTTP. *(Nota: En Angular actualmente validamos `maxLength(20)` para coincidir con tu código actual; cuando lo cambies a `72` lo actualizamos en `UserFormComponent`).*

---

## 3. Nombre del Paquete de Dominio en `employee` (`domian` vs `domain`)

- **Situación actual**: En el módulo `employee`, la carpeta del dominio se llama `com.arcoymetal.arcobackend.employee.internal.domian` (con `ia` en lugar de `ai`), mientras que en `identityaccess` está escrita correctamente como `domain`.
- **Recomendación**: Renombrar el paquete `domian` a `domain` desde el IDE (Refactor → Rename Package) antes de agregar más clases al módulo `employee`, especialmente si utilizas reglas de verificación de arquitectura con Spring Modulith o ArchUnit que busquen `..domain..`.

---

## 4. Enriquecimiento o Filtro de Empleados al Crear Usuarios

- **Situación actual**: `UserSummary` devuelve `employeeId` (`UUID`), y para mostrar el nombre del empleado en la tabla de usuarios o saber qué empleados aún no tienen usuario al crear una cuenta en `/users/new`, el frontend cruza datos con `GET /api/v1/employees`.
- **Recomendación**:
  1. Ya que tienes diseñado el puerto público `EmployeeProfileProvider` en `employee/api/`, puedes enriquecer la consulta de usuarios (o exponer un campo `employeeFullName` en `UserSummary`) o mantener un endpoint ligero de empleados sin cuenta asignada.
  2. Cuando el número de empleados crezca, convendrá agregar paginación (`PageQuery` / `PageResult`) o búsqueda por texto en `GET /api/v1/employees`, igual que hiciste en `GetAllUserWithPaginationController`.

---

## 5. Códigos de Estado HTTP en `POST` (`201 Created`)

- **Situación actual**: Tanto `CreateEmployeeController` como `CreateUserController` devuelven `ResponseEntity.ok(response)` (`200 OK`).
- **Recomendación**: Devolver `ResponseEntity.status(HttpStatus.CREATED).body(response)` (`201 Created`) al crear recursos nuevos con `POST`, manteniendo `200 OK` para consultas (`GET`) y actualizaciones (`PUT`).
