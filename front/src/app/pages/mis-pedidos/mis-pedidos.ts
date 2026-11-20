import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PedidoService } from '../../services/pedido.service';
import { PagoService } from '../../services/pago.service';
import { PedidoResponse } from '../../models/pedido';

@Component({
  selector: 'app-mis-pedidos',
  imports: [CommonModule],
  templateUrl: './mis-pedidos.html',
  styleUrl: './mis-pedidos.scss'
})
export class MisPedidos implements OnInit {
  private pedidoService = inject(PedidoService);
  private pagoService = inject(PagoService);
  pedidos = signal<PedidoResponse[]>([]);
  error = signal('');
  mensaje = signal('');

  pasos = ['PENDIENTE', 'PAGADO', 'EN_PREPARACION', 'EN_CAMINO', 'ENTREGADO'];

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.pedidoService.misPedidos().subscribe({
      next: d => this.pedidos.set(d),
      error: () => this.error.set('No se pudieron cargar los pedidos')
    });
  }

  pagar(id: number, metodo: string) {
    this.error.set('');
    this.mensaje.set('');
    this.pagoService.pagarPedido(id, metodo).subscribe({
      next: () => {
        this.mensaje.set('Pago realizado correctamente');
        this.cargar();
      },
      error: err => this.error.set(err.error?.mensaje || 'No se pudo realizar el pago')
    });
  }

  etiqueta(e: string): string {
    const m: Record<string, string> = {
      PENDIENTE: 'Pendiente de pago',
      PAGADO: 'Pagado',
      EN_PREPARACION: 'Preparando',
      EN_CAMINO: 'En camino',
      ENTREGADO: 'Entregado'
    };
    return m[e] ?? e;
  }

  indicePaso(e: string): number {
    return this.pasos.indexOf(e);
  }
}