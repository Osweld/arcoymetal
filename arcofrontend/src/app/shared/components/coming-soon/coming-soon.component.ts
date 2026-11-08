import { Component, computed, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  selector: 'app-coming-soon',
  imports: [RouterLink],
  template: `
    <div class="mx-auto max-w-3xl rounded-2xl border border-slate-200 bg-white p-8 shadow-2xs space-y-5">
      <div class="inline-flex items-center gap-2 rounded-full bg-amber-100 px-3 py-1 text-xs font-bold text-amber-900">
        <span>Módulo en Hoja de Ruta &mdash; Fase 2</span>
      </div>

      <h2 class="text-2xl font-extrabold text-slate-900">{{ title() }}</h2>
      <p class="text-sm text-slate-600 leading-relaxed">{{ description() }}</p>

      <div class="rounded-xl bg-slate-50 border border-slate-200 p-5">
        <h3 class="text-xs font-bold uppercase tracking-wider text-slate-500 mb-3">
          Capacidades Planificadas en el Monolito Modular
        </h3>
        <ul class="space-y-2 text-sm text-slate-700 list-disc pl-5">
          @for (feature of features(); track feature) {
            <li>{{ feature }}</li>
          }
        </ul>
      </div>

      <div class="pt-2">
        <a
          routerLink="/dashboard"
          class="inline-flex items-center gap-1.5 rounded-xl bg-slate-900 px-4 py-2.5 text-xs font-bold text-white hover:bg-slate-800"
        >
          &larr; Volver al Panel General
        </a>
      </div>
    </div>
  `,
})
export class ComingSoonComponent {
  private readonly route = inject(ActivatedRoute);

  readonly moduleKey = computed(() => this.route.snapshot.data['module'] as string);

  readonly title = computed(() =>
    this.moduleKey() === 'inventory'
      ? 'Módulo de Inventario y Materiales (Inventory)'
      : 'Módulo de Planillas y Legislación Laboral (Payroll)'
  );

  readonly description = computed(() =>
    this.moduleKey() === 'inventory'
      ? 'Este módulo gestionará el catálogo de materiales metálicos, perfiles, consumibles de soldadura y movimientos de bodega una vez completado el cierre de seguridad JWT en IdentityAccess.'
      : 'Este módulo automatizará el cálculo de planillas quincenales y mensuales, retenciones legales salvadoreñas (ISSS, AFP, Renta) y emisión de boletas de pago en PDF.'
  );

  readonly features = computed(() =>
    this.moduleKey() === 'inventory'
      ? [
          'CRUD de productos, estructuras y materia prima metálica.',
          'Historial inmutable de movimientos (Kardex) y ajustes de inventario.',
          'Entradas y salidas masivas por proyecto u orden de trabajo.',
          'Escaneo de códigos de barras mediante cámara móvil.',
        ]
      : [
          'Gestión de planillas mensuales y quincenales con preservación histórica.',
          'Cálculo automático de retenciones (AFP Confía / Crecer / ISP, ISSS y Renta).',
          'Registro de vacaciones, incapacidades y aguinaldo según el Código de Trabajo de El Salvador.',
          'Generación de borradores de planilla y boletas de pago en PDF.',
        ]
  );
}
