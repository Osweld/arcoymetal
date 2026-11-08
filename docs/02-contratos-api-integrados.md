# 02. Contratos de API Integrados (`arcobackend` ↔ `arcofrontend`)

Este documento registra cómo cada endpoint expuesto actualmente en `arcobackend` está conectado con los servicios y modelos de `arcofrontend`.

---

## 1. Módulo `Location` (`/api/v1/locations`)

| Método y Ruta | Controlador Backend | Servicio Frontend | Respuesta / Modelo |
| :--- | :--- | :--- | :--- |
| `GET /api/v1/locations/hierarchy` | `GetAllDepartmentHierarchyController` | `LocationService.getHierarchy()` | `DepartmentDetails[]` (con `MunicipalityDetails[]` y `DistrictDetails[]` anidados) |

- **Uso en el Frontend**:
  - En `EmployeeFormComponent` y `EmployeeDetailComponent` alimenta los 3 `<select>` dependientes (`Departamento -> Municipio -> Distrito`).
  - `LocationService.resolveDistrict(hierarchy, districtId)` permite reconstruir el nombre del Departamento, Municipio y Distrito a partir del `districtId` almacenado en el empleado.

---

## 2. Módulo `Employee` (`/api/v1/employees`)

| Método y Ruta | Controlador Backend | Servicio Frontend | Request DTO | Response DTO |
| :--- | :--- | :--- | :--- | :--- |
| `GET /api/v1/employees` | `GetAllEmployeeController` | `EmployeeService.getAllEmployees()` | — | `EmployeeDetails[]` |
| `GET /api/v1/employees/{id}` | `GetEmployeeByIdController` | `EmployeeService.getEmployeeById(id)` | — | `EmployeeDetails` |
| `POST /api/v1/employees` | `CreateEmployeeController` | `EmployeeService.createEmployee(payload)` | `CreateEmployeeRequest` | `CreateEmployeeResponse` (`{ employeeId: UUID }`) |
| `PUT /api/v1/employees/{id}/personal-information` | `UpdatePersonalInformationController` | `EmployeeService.updatePersonalInformation(id, payload)` | `UpdatePersonalInformationRequest` | `UpdatePersonalInformationResponse` (`{ employeeId: UUID }`) |

### Reglas de Validación Sincronizadas en `CreateEmployeeRequest`

| Campo | Regla en Backend (`CreateEmployeeRequest.java`) | Manejo en `EmployeeFormComponent` |
| :--- | :--- | :--- |
| `firstName` | `@NotBlank @Size(min = 1, max = 100)` | `Validators.required`, `Validators.maxLength(100)` |
| `lastName` | `@NotBlank @Size(min = 1, max = 100)` | `Validators.required`, `Validators.maxLength(100)` |
| `email` | `@NotBlank @Email @Size(min = 1, max = 254)` | `Validators.required`, `Validators.email`, `Validators.maxLength(254)` |
| `phoneNumber` | `@NotBlank @Size(min = 8, max = 8)` | `Validators.required`, `Validators.pattern(/^\d{8}$/)` |
| `gender` | `@NotNull Gender` (`MALE`, `FEMALE`) | Radio buttons obligatorios |
| `birthDate` | `@NotNull @Past LocalDate` | `<input type="date" required>` |
| `addressLine` | `@NotBlank @Size(min = 1, max = 150)` | `Validators.required`, `Validators.maxLength(150)` |
| `districtId` | `@NotBlank String` | Selector en cascada obligatorio |
| `jobTitle` | `@NotBlank @Size(min = 1, max = 100)` | `Validators.required`, `Validators.maxLength(100)` |
| `baseSalary` | `@NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)` | `Validators.required`, `Validators.min(0)` |
| `contractType` | `@NotNull ContractType` (`FULL_TIME`, `PART_TIME`, `CONTRACTOR`, `INTERN`, `TEMPORARY`) | Radio buttons obligatorios |
| `dui` | `@NotBlank @Pattern(regexp = "^(?:\\d{9}\|\\d{8}-\\d)$")` | `Validators.required`, mismo regex |
| `nit` | `@Pattern(regexp = "^(?:\\d{14}\|\\d{4}-\\d{6}-\\d{3}-\\d)$")` (opcional) | Si el usuario lo deja vacío, se envía `null` (no `""`) para cumplir `@Pattern` |
| `isss` | `@Pattern(regexp = "^(?:\\d{9}\|\\d{8}-\\d)$")` (opcional) | Si está vacío, se envía `null` |
| `nup` | `@Pattern(regexp = "^(?:\\d{9}\|\\d{8}-\\d)$")` (opcional) | Si está vacío, se envía `null` |
| `pensionFundInstitution` | `@NotNull PensionFundInstitution` (`AFP_CONFIA`, `AFP_CRECER`, `ISP`) | Radio buttons obligatorios |
| `hireDate` | `@NotNull @PastOrPresent LocalDate` | `<input type="date" required>` |

---

## 3. Módulo `IdentityAccess` (`/api/v1/users`)

| Método y Ruta | Controlador Backend | Servicio Frontend | Request DTO | Response DTO |
| :--- | :--- | :--- | :--- | :--- |
| `GET /api/v1/users?page=0&size=10` | `GetAllUserWithPaginationController` | `UserService.getUsers(page, size)` | Query params `page`, `size` | `PageResult<UserSummary>` |
| `GET /api/v1/users/{id}` | `GetUserByIdController` | `UserService.getUserById(id)` | — | `UserSummary` |
| `POST /api/v1/users` | `CreateUserController` | `UserService.createUser(payload)` | `CreateUserRequest` | `CreateUserResponse` (`{ userId: UUID }`) |

### Reglas de Validación Sincronizadas en `CreateUserRequest`

| Campo | Regla en Backend (`CreateUserRequest.java`) | Manejo en `UserFormComponent` |
| :--- | :--- | :--- |
| `employeeId` | `@NotNull UUID` | Selector poblado desde `GET /api/v1/employees` |
| `email` | `@NotBlank @Email @Size(min = 5, max = 254)` | Autocompletado al elegir empleado, editable, validado |
| `password` | `@NotBlank @Size(min = 8, max = 20)` | `minLength(8)`, `maxLength(20)` |
| `roles` | `@NotNull Set<Role>` (`ADMINISTRATOR`, `MANAGER`) | Checkboxes con validación de al menos 1 rol seleccionado |
