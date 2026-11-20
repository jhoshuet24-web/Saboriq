import { Component, computed, input } from '@angular/core';

@Component({
  selector: 'app-badge-estado',
  template: `<span class="badge" [class]="clase()">{{ etiqueta() }}</span>`,
  styles: [`
    .badge { padding: 4px 12px; border-radius: 999px; font-size: 0.8rem; font-weight: 600; display: inline-block; }
    .pendiente { background: #fef3c7; color: #ae5b28; }
    .ok { background: #dcfce7; color: #149c48; }
    .proceso { background: #dbeafe; color: #123abc; }
    .malo { background: #fee2e2; color: #7e0101; }
  `]
})
export class BadgeEstado {
  estado = input.required<string>();

  clase = computed(() => {
    switch (this.estado()) {
      case 'PENDIENTE': case 'PENDIENTE_PAGO': return 'pendiente';
      case 'CONFIRMADA': case 'PAGADO': case 'ENTREGADO': case 'COMPLETADA': return 'ok';
      case 'EN_PREPARACION': case 'EN_CAMINO': return 'proceso';
      default: return 'malo';
    }
  });

  etiqueta = computed(() => this.estado().replace(/_/g, ' '));
}