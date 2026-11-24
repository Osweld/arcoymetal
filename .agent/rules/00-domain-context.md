# 00 — Contexto del Dominio y Alcance (Fase 1)

## 1. Contexto del Negocio

Estamos construyendo un **ERP desde cero** para **Arco & Metal Mecánica**, una pequeña empresa dedicada a la **fabricación de estructuras metálicas** (operando bajo normativa laboral y geográfica de El Salvador).

El personal se divide en dos perfiles operativos principales:
- **Operarios de taller / planta**: ejecutan fabricación y montaje; en su mayoría **no** utilizan el software ERP.
- **Personal administrativo / gerencial**: gestiona expedientes, accesos y planilla a través del sistema.

---

## 2. Alcance de la Fase 1

En esta fase se desarrollan y consolidan **tres módulos de negocio principales** (apoyados por el catálogo geográfico `location` y la infraestructura común `shared`):

1. **`employee` (Gestión de Personal)**:
   - Expediente personal e identificación legal salvadoreña (`DUI`, `NIT`, `ISSS`, `NUP`, `PensionFundInstitution`, dirección vinculada a `DistrictId` de `location`).
   - Ciclo de vida laboral mediante `EmploymentPeriod` (periodos de contratación, estado `ACTIVE`/baja, motivo de terminación) y `EmploymentChange` (historial de puesto `JobTitle`, tipo de contrato `ContractType`, salario base `BaseSalary` y fecha de vigencia `EffectiveFrom`).
2. **`identity-access` (`identityaccess` en backend / `features/identity-access` y `features/auth` en frontend)**:
   - Autenticación, autorización basada en roles (RBAC) y administración de cuentas de usuario (`User`, `Role`, `UserStatus`, hashing seguro de contraseñas).
3. **`payroll` (Nómina y Boletas de Pago)**:
   - Cálculo de salarios, ingresos/deducciones aplicables y generación de **boletas de pago en PDF**.

> Todo módulo fuera de estos tres (por ejemplo, `inventory`, compras, producción o contabilidad) pertenece a fases posteriores y **no** debe implementarse en Fase 1 salvo instrucción explícita.

---

## 3. Invariantes Críticas del Dominio

Estas reglas de negocio son obligatorias y nunca deben violarse ni relajarse:

### 3.1 Relación Opcional `Employee` → `User` (`0..1`)
- **No todos los empleados tienen usuario del sistema**: crear o dar de alta un `Employee` **nunca** debe exigir ni crear automáticamente un `User`.
- **Todo usuario que represente a un trabajador debe estar vinculado a un empleado existente**: al crear un `User` en `identityaccess`, el `employeeId` es obligatorio, único (`1:1` entre cuenta y empleado) y debe validarse su existencia contra el contrato público `employee.api.EmployeeExistenceVerifier`.
- Un `User` sin `Employee` válido o un segundo `User` para el mismo `employeeId` debe rechazarse.

### 3.2 Frontera Estricta de `payroll`
- `payroll` **calcula** montos de nómina y **emite/genera el documento de boleta de pago en PDF**.
- `payroll` **NO ejecuta pagos**, no realiza transferencias bancarias, no se integra con pasarelas financieras ni registra movimientos bancarios.
- Ningún caso de uso ni puerto de `payroll` debe modelar dispersión bancaria ni cobros/pagos electrónicos.

### 3.3 Integridad e Inmutabilidad Histórica
- Los cambios salariales y de puesto (`EmploymentChange`) son registros históricos con fecha de vigencia (`effective_from`), no sobrescrituras destructivas.
- Las boletas de pago emitidas en `payroll` deben conservar una instantánea (snapshot) auditable de los datos y montos calculados en el momento de emisión; una boleta emitida/cerrada no se altera silenciosamente al cambiar posteriormente el salario base en `employee`.

### 3.4 Invariantes por Módulo
- **`identityaccess`**: todo `User` debe tener **al menos un `Role`** asignado (`UserMustHaveAtLeastOneRoleException`) y no puede duplicar un rol ya asignado.
- **`employee`**: los identificadores legales (`DUI`, `NIT`, `ISSS`, `NUP`), correo electrónico y teléfono siguen los formatos validados en los Value Objects del dominio; el salario base (`BaseSalary`) nunca puede ser negativo.
