import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ReservaService } from '../../services/reserva.service';
import { Reserva } from '../../models/reserva';
import { Alerta } from '../../components/alerta/alerta';
import { BadgeEstado } from '../../components/badge-estado/badge-estado';

@Component({
  selector: 'app-mis-reservas',
   imports: [CommonModule, RouterLink, Alerta, BadgeEstado],
  templateUrl: './mis-reservas.html',
  styleUrl: './mis-reservas.scss'
})
export class MisReservas implements OnInit {
  private reservaService = inject(ReservaService);
  reservas = signal<Reserva[]>([]);
  error = signal('');
  mensaje = signal('');
  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.reservaService.misReservas().subscribe({
      next: datos => this.reservas.set(datos),
      error: () => this.error.set('No se pudieron cargar tus reservas')
    });
  }
  cancelar(id: number) {
    if (!confirm('¿Estás seguro de cancelar esta reserva?')) return;

    this.error.set('');
    this.mensaje.set('');
    this.reservaService.cancelar(id).subscribe({
      next: (reserva) => {
        if (reserva.garantiaResultado === 'REEMBOLSO_TOTAL') {
          this.mensaje.set('Reserva cancelada. Se procesará el reembolso total de la garantía.');
        } else if (reserva.garantiaResultado === 'RETENIDA') {
          this.mensaje.set('Reserva cancelada. La garantía ha sido retenida (cancelación con menos de 4 horas).');
        } else {
          this.mensaje.set('Reserva cancelada exitosamente.');
        }
        this.cargar();
      },
      error: (err) => {
        if (err.status === 409) this.error.set(err.error?.mensaje || 'No se puede cancelar esta reserva');
        else this.error.set('No se pudo cancelar la reserva');
      }
    });
  }
  getEstadoClase(estado: string): string {
    switch (estado) {
      case 'CONFIRMADA': return 'confirmada';
      case 'PENDIENTE_PAGO': return 'pendiente';
      case 'CANCELADA': return 'cancelada';
      case 'EXPIRADA': return 'expirada';
      case 'COMPLETADA': return 'completada';
      case 'NO_SHOW': return 'no-show';
      default: return '';
    }
  }
}