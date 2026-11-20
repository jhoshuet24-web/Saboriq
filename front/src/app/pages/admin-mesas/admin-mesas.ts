import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Mesa } from '../../models/mesa';
import { Local } from '../../models/local';
import { MesaService } from '../../services/mesa.service';
import { LocalService } from '../../services/local.service';

@Component({
  selector: 'app-admin-mesas',
  imports: [ReactiveFormsModule],
  templateUrl: './admin-mesas.html',
  styleUrl: './admin-mesas.scss'
})
export class AdminMesas implements OnInit {
  private fb = inject(FormBuilder);
  private mesaService = inject(MesaService);
  private localService = inject(LocalService);

  mesas = signal<Mesa[]>([]);
  locales = signal<Local[]>([]);
  editandoId = signal<number | null>(null);
  error = signal('');
  mensaje = signal('');

  form = this.fb.group({
    localId: ['', [Validators.required]],
    numero: [null as number | null, [Validators.required, Validators.min(1)]],
    capacidad: [null as number | null, [Validators.required, Validators.min(1)]],
    activa: [true]
  });

  ngOnInit() {
    this.localService.listar().subscribe({
      next: datos => this.locales.set(datos),
      error: () => this.error.set('No se pudieron cargar los locales')
    });
    this.cargar('todos');
    this.form.controls.localId.valueChanges.subscribe(valor => {
      this.cargar(valor ? valor : 'todos');
    });
  }
  cargar(filtro: string) {
    const peticion = filtro === 'todos'
      ? this.mesaService.listar()
      : this.mesaService.listarPorLocal(Number(filtro));
    peticion.subscribe({
      next: datos => {
        this.mesas.set(datos);
        this.sugerirNumero(filtro, datos);
      },
      error: () => this.error.set('No se pudieron cargar las mesas')
    });
  }
  private sugerirNumero(filtro: string, datos: Mesa[]) {
    if (filtro === 'todos' || this.editandoId() !== null) return;
    const mayor = datos.reduce((max, m) => Math.max(max, m.numero), 0);
    this.form.controls.numero.setValue(mayor + 1);
  }
  private localSeleccionado(): string {
    return this.form.value.localId || 'todos';
  }
  guardar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.error.set('');
    this.mensaje.set('');
    const v = this.form.value;
    const datos = {
      localId: Number(v.localId),
      numero: Number(v.numero),
      capacidad: Number(v.capacidad),
      activa: !!v.activa
    };
    const id = this.editandoId();
    const peticion = id
      ? this.mesaService.actualizar(id, datos)
      : this.mesaService.crear(datos);
    peticion.subscribe({
      next: () => {
        this.mensaje.set(id ? 'Mesa actualizada' : 'Mesa creada');
        this.cancelar();
      },
      error: err => {
        if (err.status === 403) this.error.set('No tienes permisos de administrador');
        else if (err.status === 404) this.error.set('El local o la mesa no existe');
        else if (err.status === 409) this.error.set('Ya existe una mesa con ese número en ese local');
        else if (err.status === 400) this.error.set('Revisa los datos ingresados');
        else this.error.set('No se pudo guardar la mesa');
      }
    });
  }
  editar(m: Mesa) {
    this.editandoId.set(m.id);
    this.form.patchValue({
      localId: String(m.localId),
      numero: m.numero,
      capacidad: m.capacidad,
      activa: m.activa
    });
  }
  cancelar() {
    const localId = this.form.value.localId ?? '';
    this.editandoId.set(null);
    this.form.reset({ localId, numero: null, capacidad: null, activa: true });
  }
  eliminar(id: number) {
    this.error.set('');
    this.mensaje.set('');
    this.mesaService.eliminar(id).subscribe({
      next: () => {
        this.mensaje.set('Mesa eliminada');
        this.cargar(this.localSeleccionado());
      },
      error: () => this.error.set('No se pudo eliminar la mesa')
    });
  }
}
