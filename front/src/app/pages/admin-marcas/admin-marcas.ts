import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Marca } from '../../models/marca';
import { MarcaService } from '../../services/marca.service';

@Component({
  selector: 'app-admin-marcas',
  imports: [ReactiveFormsModule],
  templateUrl: './admin-marcas.html',
  styleUrl: './admin-marcas.scss'
})
export class AdminMarcas implements OnInit {
  private fb = inject(FormBuilder);
  private service = inject(MarcaService);
  marcas = signal<Marca[]>([]);
  editandoId = signal<number | null>(null);
  error = signal('');
  mensaje = signal('');
  form = this.fb.group({
    nombre: ['', [Validators.required, Validators.maxLength(20)]],
    ruc: ['', [Validators.pattern(/^[0-9]{11}$/)]],
    activa: [true]
  });

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.service.listar().subscribe({
      next: datos => this.marcas.set(datos),
      error: () => this.error.set('No se pudieron cargar las marcas')
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
    const ruc = (v.ruc ?? '').trim();
    const datos = {
      nombre: (v.nombre ?? '').trim(),
      ruc: ruc === '' ? null : ruc,
      activa: !!v.activa
    };
    const id = this.editandoId();
    const peticion = id
      ? this.service.actualizar(id, datos)
      : this.service.crear(datos);

    peticion.subscribe({
      next: () => {
        this.mensaje.set(id ? 'Marca actualizada' : 'Marca creada');
        this.cancelar();
        this.cargar();
      },
      error: err => {
        if (err.status === 403) this.error.set('No tienes permisos de administrador');
        else if (err.status === 409) this.error.set('Ya existe una marca con ese nombre o RUC');
        else if (err.status === 400) this.error.set('Revisa los datos ingresados');
        else this.error.set('No se pudo guardar la marca');
      }
    });
  }

  editar(m: Marca) {
    this.editandoId.set(m.id);
    this.form.patchValue({ nombre: m.nombre, ruc: m.ruc ?? '', activa: m.activa });
  }

  cancelar() {
    this.editandoId.set(null);
    this.form.reset({ nombre: '', ruc: '', activa: true });
  }

  eliminar(id: number) {
    this.error.set('');
    this.mensaje.set('');
    this.service.eliminar(id).subscribe({
      next: () => {
        this.mensaje.set('Marca eliminada');
        this.cargar();
      },
      error: err => {
        if (err.status === 409) this.error.set('No se puede eliminar: la marca tiene locales asociados');
        else this.error.set('No se pudo eliminar la marca');
      }
    });
  }
}