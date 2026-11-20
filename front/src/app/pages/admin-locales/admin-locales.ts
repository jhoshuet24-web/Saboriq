import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Local } from '../../models/local';
import { Marca } from '../../models/marca';
import { LocalService } from '../../services/local.service';
import { MarcaService } from '../../services/marca.service';

@Component({
  selector: 'app-admin-locales',
  imports: [ReactiveFormsModule],
  templateUrl: './admin-locales.html',
  styleUrl: './admin-locales.scss'
})
export class AdminLocales implements OnInit {
  private fb = inject(FormBuilder);
  private localService = inject(LocalService);
  private marcaService = inject(MarcaService);

  locales = signal<Local[]>([]);
  marcas = signal<Marca[]>([]);
  editandoId = signal<number | null>(null);
  error = signal('');
  mensaje = signal('');

  form = this.fb.group({
    marcaId: ['', [Validators.required]],
    nombre: ['', [Validators.required, Validators.maxLength(20)]],
    direccion: ['', [Validators.required, Validators.maxLength(50)]],
    distrito: ['', [Validators.required, Validators.maxLength(20)]],
    ciudad: ['', [Validators.required, Validators.maxLength(20)]],
    telefono: ['', [Validators.maxLength(20)]],
    aforo: [null as number | null, [Validators.required, Validators.min(1)]],
    pedidoMinimo: [0 as number | null, [Validators.required, Validators.min(0)]],
    costoEnvio: [0 as number | null, [Validators.required, Validators.min(0)]],
    activo: [true],
    distritosCobertura: ['', [Validators.maxLength(500)]]
  });

  ngOnInit() {
    this.marcaService.listar().subscribe({
      next: datos => this.marcas.set(datos),
      error: () => this.error.set('No se pudieron cargar las marcas')
    });
    this.cargar();
  }

  cargar() {
    this.localService.listar().subscribe({
      next: datos => this.locales.set(datos),
      error: () => this.error.set('No se pudieron cargar los locales')
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
    const telefono = (v.telefono ?? '').trim();
    const datos = {
      marcaId: Number(v.marcaId),
      nombre: (v.nombre ?? '').trim(),
      direccion: (v.direccion ?? '').trim(),
      distrito: (v.distrito ?? '').trim(),
      ciudad: (v.ciudad ?? '').trim(),
      telefono: telefono === '' ? null : telefono,
      aforo: Number(v.aforo),
      pedidoMinimo: Number(v.pedidoMinimo),
      costoEnvio: Number(v.costoEnvio),
      activo: !!v.activo,
      distritosCobertura: (v.distritosCobertura ?? '').trim() || null
    };
    const id = this.editandoId();
    const peticion = id
      ? this.localService.actualizar(id, datos)
      : this.localService.crear(datos);

    peticion.subscribe({
      next: () => {
        this.mensaje.set(id ? 'Local actualizado' : 'Local creado');
        this.cancelar();
        this.cargar();
      },
      error: err => {
        if (err.status === 403) this.error.set('No tienes permisos de administrador');
        else if (err.status === 404) this.error.set('La marca o el local no existe');
        else if (err.status === 409) this.error.set('Ya existe un local con ese nombre en esa marca');
        else if (err.status === 400) this.error.set('Revisa los datos ingresados');
        else this.error.set('No se pudo guardar el local');
      }
    });
  }

  editar(l: Local) {
    this.editandoId.set(l.id);
    this.form.patchValue({
      marcaId: String(l.marcaId),
      nombre: l.nombre,
      direccion: l.direccion,
      distrito: l.distrito,
      ciudad: l.ciudad,
      telefono: l.telefono ?? '',
      aforo: l.aforo,
      pedidoMinimo: l.pedidoMinimo,
      costoEnvio: l.costoEnvio,
      activo: l.activo,
      distritosCobertura: l.distritosCobertura ?? ''
    });
  }

  cancelar() {
    this.editandoId.set(null);
    this.form.reset({
      marcaId: '', nombre: '', direccion: '', distrito: '', ciudad: '',
      telefono: '', aforo: null, pedidoMinimo: 0, costoEnvio: 0, activo: true,
      distritosCobertura: ''
    });
  }

  eliminar(id: number) {
    this.error.set('');
    this.mensaje.set('');
    this.localService.eliminar(id).subscribe({
      next: () => {
        this.mensaje.set('Local eliminado');
        this.cargar();
      },
      error: err => {
        if (err.status === 409) this.error.set('No se puede eliminar: el local tiene mesas, repartidores o personal asignado');
        else this.error.set('No se pudo eliminar el local');
      }
    });
  }
}