import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Repartidor } from '../../models/repartidor';
import { Local } from '../../models/local';
import { RepartidorService } from '../../services/repartidor.service';
import { telefonoValido } from '../../validators/validadores';
import { LocalService } from '../../services/local.service';


@Component({
  selector: 'app-admin-repartidores',
  imports: [ReactiveFormsModule],
  templateUrl: './admin-repartidores.html',
  styleUrl: './admin-repartidores.scss'
})
export class AdminRepartidores implements OnInit {
  private fb = inject(FormBuilder);
  private repartidorService = inject(RepartidorService);
  private localService = inject(LocalService);

  repartidores = signal<Repartidor[]>([]);
  locales = signal<Local[]>([]);
  editandoId = signal<number | null>(null);
  error = signal('');
  mensaje = signal('');

  vehiculos = ['MOTO', 'BICICLETA', 'AUTO'];

  form = this.fb.group({
    localId: ['', [Validators.required]],
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    telefono: ['', [Validators.required, telefonoValido]],
    vehiculo: ['', [Validators.required]],
    activo: [true]
  });

  ngOnInit() {
    this.localService.listar().subscribe({
      next: datos => this.locales.set(datos),
      error: () => this.error.set('No se pudieron cargar los locales')
    });
    this.cargar('todos');

    // Al elegir un local, se muestran sus repartidores
    this.form.controls.localId.valueChanges.subscribe(valor => {
      this.cargar(valor ? valor : 'todos');
    });
  }

  cargar(filtro: string) {
    const peticion = filtro === 'todos'
      ? this.repartidorService.listar()
      : this.repartidorService.listarPorLocal(Number(filtro));

    peticion.subscribe({
      next: datos => this.repartidores.set(datos),
      error: () => this.error.set('No se pudieron cargar los repartidores')
    });
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
      nombre: (v.nombre ?? '').trim(),
      telefono: (v.telefono ?? '').trim(),
      vehiculo: v.vehiculo ?? '',
      activo: !!v.activo
    };
    const id = this.editandoId();
    const peticion = id
      ? this.repartidorService.actualizar(id, datos)
      : this.repartidorService.crear(datos);

    peticion.subscribe({
      next: () => {
        this.mensaje.set(id ? 'Repartidor actualizado' : 'Repartidor creado');
        this.cancelar();
      },
      error: err => {
        if (err.status === 403) this.error.set('No tienes permisos de administrador');
        else if (err.status === 404) this.error.set('El local o el repartidor no existe');
        else if (err.status === 409) this.error.set('Ya existe un repartidor con ese teléfono');
        else if (err.status === 400) this.error.set('Revisa los datos ingresados');
        else this.error.set('No se pudo guardar el repartidor');
      }
    });
  }

  editar(r: Repartidor) {
    this.editandoId.set(r.id);
    this.form.patchValue({
      localId: String(r.localId),
      nombre: r.nombre,
      telefono: r.telefono,
      vehiculo: r.vehiculo,
      activo: r.activo
    });
  }
  cancelar() {
    const localId = this.form.value.localId ?? '';
    this.editandoId.set(null);
    this.form.reset({ localId, nombre: '', telefono: '', vehiculo: '', activo: true });
  }

  eliminar(id: number) {
    this.error.set('');
    this.mensaje.set('');
    this.repartidorService.eliminar(id).subscribe({
      next: () => {
        this.mensaje.set('Repartidor eliminado');
        this.cargar(this.form.value.localId || 'todos');
      },
      error: () => this.error.set('No se pudo eliminar el repartidor')
    });
  }
}
