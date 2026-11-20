import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MiLocal } from '../../models/mi-local';
import { Reserva } from '../../models/reserva';
import { PedidoResponse } from '../../models/pedido';
import { StaffService } from '../../services/staff.service';

@Component({
  selector: 'app-staff-panel',
  imports: [CommonModule],
  templateUrl: './staff-panel.html',
  styleUrl: './staff-panel.scss'
})
export class StaffPanel implements OnInit {
  private staffService = inject(StaffService);

  datos = signal<MiLocal | null>(null);
  reservas = signal<Reserva[]>([]);
  pedidos = signal<PedidoResponse[]>([]);
  error = signal('');
  mensaje = signal('');

  ngOnInit() {
    this.cargarDatos();
    this.cargarReservas();
    this.cargarPedidos();
  }

  cargarDatos() {
    this.staffService.miLocal().subscribe({
      next: datos => this.datos.set(datos),
      error: err => {
        if (err.status === 404) this.error.set('Todavía no tienes un local asignado');
        else if (err.status === 403) this.error.set('Esta pantalla es solo para el personal');
        else this.error.set('No se pudieron cargar los datos de tu local');
      }
    });
  }

  cargarReservas() {
    this.staffService.listarReservas().subscribe({
      next: datos => this.reservas.set(datos),
      error: () => console.error('No se pudieron cargar las reservas')
    });
  }

  cargarPedidos() {
    this.staffService.listarPedidos().subscribe({
      next: datos => this.pedidos.set(datos),
      error: () => console.error('No se pudieron cargar los pedidos')
    });
  }

  completar(id: number) {
    this.mensaje.set('');
    this.staffService.completarReserva(id).subscribe({
      next: () => {
        this.mensaje.set('Reserva marcada como COMPLETADA');
        this.cargarReservas();
      },
      error: () => this.error.set('No se pudo actualizar la reserva')
    });
  }

  noShow(id: number) {
    this.mensaje.set('');
    this.staffService.marcarNoShow(id).subscribe({
      next: () => {
        this.mensaje.set('Reserva marcada como NO SHOW');
        this.cargarReservas();
      },
      error: () => this.error.set('No se pudo actualizar la reserva')
    });
  }

  actualizarEstadoPedido(id: number, estado: string) {
    this.mensaje.set('');
    this.staffService.cambiarEstadoPedido(id, estado).subscribe({
      next: () => {
        this.mensaje.set('Pedido actualizado a ' + estado);
        this.cargarPedidos();
      },
      error: () => this.error.set('No se pudo actualizar el pedido')
    });
  }
}