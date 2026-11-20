import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Categoria } from '../../models/categoria';
import { CategoriaService } from '../../services/categoria.service';

@Component({
  selector: 'app-admin-categorias',
  imports: [ReactiveFormsModule],
  templateUrl: './admin-categorias.html',
  styleUrl: './admin-categorias.scss'
})
export class AdminCategorias implements OnInit {
  private fb = inject(FormBuilder);
  private service = inject(CategoriaService);

  categorias = signal<Categoria[]>([]);
  editandoId = signal<number | null>(null);
  error = signal('');
  mensaje = signal('');

  form = this.fb.group({
    nombre: ['', [Validators.required, Validators.maxLength(20)]],
    descripcion: ['', [Validators.maxLength(50)]]
  });

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.service.listar().subscribe({
      next: datos => this.categorias.set(datos),
      error: () => this.error.set('No se pudieron cargar las categorías')
    });
  }

  guardar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.error.set('');
    this.mensaje.set('');

    const datos = {
      nombre: (this.form.value.nombre ?? '').trim(),
      descripcion: this.form.value.descripcion ?? ''
    };
    const id = this.editandoId();
    const peticion = id
      ? this.service.actualizar(id, datos)
      : this.service.crear(datos);

    peticion.subscribe({
      next: () => {
        this.mensaje.set(id ? 'Categoría actualizada' : 'Categoría creada');
        this.cancelar();
        this.cargar();
      },
      error: err => {
        if (err.status === 403) this.error.set('No tienes permisos de administrador');
        else if (err.status === 409) this.error.set('Ya existe una categoría con ese nombre');
        else if (err.status === 400) this.error.set('Revisa los datos ingresados');
        else this.error.set('No se pudo guardar la categoría');
      }
    });
  }

  editar(c: Categoria) {
    this.editandoId.set(c.id);
    this.form.patchValue({ nombre: c.nombre, descripcion: c.descripcion });
  }

  cancelar() {
    this.editandoId.set(null);
    this.form.reset({ nombre: '', descripcion: '' });
  }

  eliminar(id: number) {
    this.error.set('');
    this.mensaje.set('');
    this.service.eliminar(id).subscribe({
      next: () => {
        this.mensaje.set('Categoría eliminada');
        this.cargar();
      },
      error: err => {
        if (err.status === 409) this.error.set('No se puede eliminar: la categoría tiene productos asociados');
        else this.error.set('No se pudo eliminar la categoría');
      }
    });
  }
}