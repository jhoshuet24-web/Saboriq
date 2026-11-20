import { AbstractControl, ValidationErrors } from '@angular/forms';

export function telefonoValido(control: AbstractControl): ValidationErrors | null {
  const telefono = control.value;

  if (!telefono) {
    return null;
  }

  if (/^9[0-9]{8}$/.test(telefono)) {
    return null;
  }

  return { telefonoInvalido: true };
}

export function fechaNoPasada(control: AbstractControl): ValidationErrors | null {
  if (!control.value) return null;
  const hoy = new Date();
  hoy.setHours(0, 0, 0, 0);
  const elegida = new Date(control.value + 'T00:00:00');
  return elegida < hoy ? { fechaPasada: true } : null;
}

export function anticipacionReserva(grupo: AbstractControl): ValidationErrors | null {
  const fecha = grupo.get('fecha')?.value;
  const hora = grupo.get('hora')?.value;
  if (!fecha || !hora) return null;

  const reserva = new Date(`${fecha}T${hora}:00`);
  const ahora = new Date();
  const minimo = new Date(ahora.getTime() + 2 * 60 * 60 * 1000);
  const maximo = new Date(ahora.getTime() + 30 * 24 * 60 * 60 * 1000);

  if (reserva < minimo) return { anticipacionMinima: true };
  if (reserva > maximo) return { anticipacionMaxima: true };
  return null;
}