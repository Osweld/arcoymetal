# Documentación del Proyecto — Arco & Metal Mecánica

Esta carpeta centraliza el contexto técnico, el estado de avance del **Frontend (`arcofrontend`)**, los contratos HTTP integrados con el **Backend (`arcobackend`)** y las observaciones técnicas para facilitar futuras iteraciones.

## Alcance y Regla de Trabajo

- **Responsabilidad de implementación**: **100% exclusiva en `arcofrontend/`** (Angular 22 + TypeScript + Tailwind CSS v4).
- **Relación con `arcobackend/`**: Lectura de controladores, DTOs (`record`), enums y migraciones para garantizar que los modelos y formularios de Angular coincidan exactamente con el backend, ofreciendo recomendaciones y mejoras para el backend cuando se detecten oportunidades.

## Índice de Documentos

1. [01-arquitectura-frontend.md](./01-arquitectura-frontend.md) — Estructura de carpetas, rutas, manejo de estado con Signals, interceptores, guards y convenciones de UI/UX en Angular 22.
2. [02-contratos-api-integrados.md](./02-contratos-api-integrados.md) — Catálogo exacto de endpoints REST (`/api/v1/*`), DTOs de entrada/salida, validaciones y enums conectados entre `arcobackend` y `arcofrontend`.
3. [03-bitacora-y-pendientes.md](./03-bitacora-y-pendientes.md) — Registro cronológico de funcionalidades completadas en el frontend y lista de pantallas/integraciones pendientes a medida que avance el backend.
4. [04-recomendaciones-backend.md](./04-recomendaciones-backend.md) — Sugerencias técnicas detectadas al inspeccionar `arcobackend` para facilitar el consumo desde Angular y reforzar buenas prácticas en Spring Boot.
