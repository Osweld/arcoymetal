---
name: angular-feature-ui
description: >-
  Use this skill when building, extending, or refactoring Angular 22 screens,
  forms, responsive tables, models, or HTTP services in arcofrontend features
  (employee, identity-access, auth, payroll) and shared components.
---

# Skill: Feature UI en Angular 22 (`arcofrontend`)

Procedimiento estándar para crear o refactorizar interfaces en `arcofrontend` respetando [01-architecture.md](../../rules/01-architecture.md) y [02-stack-conventions.md](../../rules/02-stack-conventions.md).

---

## 1. Estructura Real de un Feature

Raíz: `arcofrontend/src/app/features/<feature>/`

```text
features/<feature>/
├── models/
│   └── <entidad>.model.ts          # Interfaces alineadas a records Java + *_LABELS en español
├── services/
│   └── <entidad>.service.ts        # @Injectable({ providedIn: 'root' }), inject(HttpClient), /api/v1/*
└── pages/
    ├── <entidad>-list/
    │   └── <entidad>-list.component.ts
    ├── <entidad>-form/
    │   └── <entidad>-form.component.ts
    └── <entidad>-detail/
        └── <entidad>-detail.component.ts
```

- Registrar rutas en `arcofrontend/src/app/app.routes.ts` con carga diferida `loadComponent` y protegerlas con `authGuard` / `roleGuard([...])`.
- Componentes reutilizables transversales (sin acoplamiento a un dominio) van en `arcofrontend/src/app/shared/components/`.

---

## 2. Patrones Obligatorios de Angular 22

1. **Standalone y DI moderna**:
   - Todos los componentes son **standalone** (sin `NgModule`, declarar `imports: [...]` directamente en `@Component`).
   - Inyectar dependencias con `private readonly ... = inject(...)` en lugar de constructores con parámetros.
2. **Signals API**:
   - Usar `signal()` para estado local (`loading`, `errorMessage`, `items`, filtros, paginación) y `computed()` para estado derivado.
   - En servicios con estado compartido (como `AuthService`), exponer señales de solo lectura con `.asReadonly()`.
   - En componentes hijos, usar `input()` / `input.required()` y `output()` en lugar de `@Input()` / `@Output()`.
   - Reservar RxJS para llamadas `HttpClient` (`/api/v1/*`, proxeado por `proxy.conf.json` a `http://localhost:8080`) y composición (`forkJoin`, `catchError`, `tap`).
3. **Flujo de control nativo**:
   - Usar `@if (...) { ... } @else if (...) { ... } @else { ... }` y `@for (item of list(); track item.id) { ... }`. Nunca usar `*ngIf` ni `*ngFor`.
4. **Modelos sincronizados con backend**:
   - Los tipos TypeScript deben coincidir campo por campo con los `record` de `arcobackend`.
   - Definir diccionarios de etiquetas en español (`CONTRACT_TYPE_LABELS`, `EMPLOYEE_STATUS_LABELS`, `ROLE_LABELS`, etc.) junto a los tipos unión de enums.

---

## 3. Convenciones de UI, Tablas y Formularios Salvadoreños

1. **Estética industrial (Tailwind CSS v4)**:
   - Paleta base: `slate-900`/`slate-800` para estructura, tarjetas `rounded-xl border border-slate-200 bg-white shadow-2xs`, acentos primarios `amber-500`/`amber-400` con texto `slate-950`, alertas de error en `rose-*`.
2. **Tablas responsivas (`.table-wrapper` en `src/styles.css`)**:
   - Envolver cada tabla en:
     ```html
     <div tabindex="0" role="region" aria-labelledby="mi-tabla-caption" class="table-wrapper">
       <table style="--label-2: 'Columna 2'; --label-3: 'Columna 3';">
         <caption id="mi-tabla-caption" class="visually-hidden">Descripción accesible</caption>
         <thead>...</thead>
         <tbody>
           @for (row of items(); track row.id) {
             <tr>
               <th scope="row">...</th>
               <td>...</td>
             </tr>
           }
         </tbody>
       </table>
     </div>
     ```
   - Definir `--label-2` hasta `--label-6` para que en contenedores `<640px` (`@container`) cada fila se convierta automáticamente en tarjeta apilada legible.
3. **Formularios accesibles y códigos salvadoreños**:
   - Usar `ReactiveFormsModule` + `FormBuilder` con `novalidate`, `<fieldset>`/`<legend>`, `aria-describedby` y mensajes de error con `role="alert"`.
   - Catálogos cerrados de <6 opciones (`Gender`, `ContractType`, `PensionFundInstitution`): `<input type="radio">`. Selección múltiple (`Role`): `<input type="checkbox">`. Jerarquía geográfica (`location`): `<select>` en cascada.
   - Campos de identificación (`DUI`, `NIT`, `ISSS`, `NUP`, `phoneNumber`): usar **siempre** `type="text" inputmode="numeric" autocorrect="off" autocapitalize="off" spellcheck="false"` para preservar ceros a la izquierda y activar el teclado numérico móvil.

---

## 4. Checklist Obligatorio antes de Cerrar

- [ ] Componentes standalone, `inject()`, Signals y `@if`/`@for` con `track` aplicados consistentemente.
- [ ] Modelos y endpoints alineados exactamente con `arcobackend/src/main/java/.../web/`.
- [ ] Tablas usan `.table-wrapper` con `--label-N` y accesibilidad (`caption`, `th scope="row"`).
- [ ] Campos `DUI`/`NIT`/`ISSS`/`NUP`/`phoneNumber` usan `type="text" inputmode="numeric"`.
- [ ] `cd arcofrontend && npm run build` y `npm test -- --watch=false` pasan sin errores.
- [ ] Flujo visual verificado en navegador (desktop y móvil/estrecho) sin errores en consola.
