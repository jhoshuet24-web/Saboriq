import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LocalService } from '../../services/local.service';
import { ReservaService } from '../../services/reserva.service';
import { fechaNoPasada, anticipacionReserva } from '../../validators/validadores';
import { Alerta } from '../../components/alerta/alerta';
import { Local } from '../../models/local';


@Component({
  selector: 'app-reservar',
    imports: [ReactiveFormsModule, Alerta],
  templateUrl: './reservar.html',
  styleUrl: './reservar.scss'
})
export class Reservar implements OnInit {
  private fb = inject(FormBuilder);
  private localService = inject(LocalService);
  private reservaService = inject(ReservaService);
  private router = inject(Router);

  locales = signal<Local[]>([]);
  error = signal('');
  mensaje = signal('');
  cargando = signal(false);

    form = this.fb.group({
    localId: ['', [Validators.required]],
    fecha: ['', [Validators.required, fechaNoPasada]],
    hora: ['', [Validators.required]],
    personas: [2, [Validators.required, Validators.min(1), Validators.max(50)]]
  }, { validators: [anticipacionReserva] });

  ngOnInit() {
    this.localService.listar().subscribe({
      next: datos => this.locales.set(datos.filter(l => l.activo)),
      error: () => this.error.set('No se pudieron cargar los locales')
    });
  }


    reservar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.error.set('');
    this.mensaje.set('');
    this.cargando.set(true);

    const v = this.form.value;
    const fechaHora = `${v.fecha}T${v.hora}:00`;

    const datos = {
      localId: Number(v.localId),
      fechaHora: fechaHora,
      personas: Number(v.personas)
    };

    this.reservaService.crear(datos).subscribe({
      next: (reserva) => {
        this.cargando.set(false);
        if (reserva.requiereGarantia) {
          this.mensaje.set(`Reserva creada. Se requiere un pago de garantía de S/ ${reserva.montoGarantia.toFixed(2)}. Tienes 10 minutos para pagar.`);
        } else {
          this.mensaje.set('Reserva confirmada exitosamente.');
        }
        setTimeout(() => this.router.navigate(['/mis-reservas']), 2000);
      },
      error: err => {
        this.cargando.set(false);
        if (err.status === 400) this.error.set(err.error?.mensaje || 'Revisa los datos ingresados');
        else if (err.status === 403) this.error.set('No tienes permisos para reservar');
        else if (err.status === 409) this.error.set(err.error?.mensaje || 'No hay cupo o la reserva expiró');
        else this.error.set('No se pudo crear la reserva');
      }
    });
  }
}