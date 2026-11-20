import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { PagoService } from '../../services/pago.service';
import { ReservaService } from '../../services/reserva.service';
import { Reserva } from '../../models/reserva';

@Component({
  selector: 'app-pagar-reserva',
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './pagar-reserva.html',
  styleUrl: './pagar-reserva.scss'
})
export class PagarReserva implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private pagoService = inject(PagoService);
  private reservaService = inject(ReservaService);

  reservaId = signal<number | null>(null);
  reserva = signal<Reserva | null>(null);
  error = signal('');
  mensaje = signal('');
  cargando = signal(false);

  form = this.fb.group({
    metodo: ['YAPE', [Validators.required]]
  });

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.reservaId.set(Number(idParam));
      this.reservaService.misReservas().subscribe({
        next: lista => this.reserva.set(lista.find(r => r.id === Number(idParam)) ?? null),
        error: () => this.error.set('No se pudo cargar la reserva')
      });
    } else {
      this.error.set('Reserva no válida');
    }
  }

  pagar() {
    if (this.form.invalid || !this.reservaId()) return;
    this.cargando.set(true);
    this.error.set('');
    this.mensaje.set('');

    const metodo = this.form.value.metodo!;

    this.pagoService.pagarReserva(this.reservaId()!, metodo).subscribe({
      next: (pago) => {
        this.cargando.set(false);
        this.mensaje.set(`Pago exitoso. Referencia: ${pago.referencia}. Redirigiendo...`);
        setTimeout(() => this.router.navigate(['/mis-reservas']), 2000);
      },
      error: (err) => {
        this.cargando.set(false);
        if (err.status === 400 || err.status === 409) {
          this.error.set(err.error?.mensaje || 'No se pudo procesar el pago');
        } else {
          this.error.set('Error al procesar el pago');
        }
      }
    });
  }
}