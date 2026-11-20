import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Producto } from '../../models/producto';
import { Categoria } from '../../models/categoria';
import { Marca } from '../../models/marca';
import { ProductoService } from '../../services/producto.service';
import { CategoriaService } from '../../services/categoria.service';
import { MarcaService } from '../../services/marca.service';

@Component({
  selector: 'app-admin-productos',
  imports: [ReactiveFormsModule],
  templateUrl: './admin-productos.html',
  styleUrl: './admin-productos.scss'
})
export class AdminProductos implements OnInit {
  private fb = inject(FormBuilder);
  private productoService = inject(ProductoService);
  private categoriaService = inject(CategoriaService);
  private marcaService = inject(MarcaService);

  productos = signal<Producto[]>([]);
  categorias = signal<Categoria[]>([]);
  marcas = signal<Marca[]>([]);
  editandoId = signal<number | null>(null);
  error = signal('');
  mensaje = signal('');

  form = this.fb.group({
    nombre: ['', [Validators.required, Validators.maxLength(20)]],
    descripcion: ['', [Validators.maxLength(255)]],
    precio: [null as number | null, [Validators.required, Validators.min(0.01)]],
    categoriaId: ['', [Validators.required]],
    marcaId: [''],
    disponible: [true]
  });

  ngOnInit() {
    this.categoriaService.listar().subscribe({
      next: datos => this.categorias.set(datos),
      error: () => this.error.set('No se pudieron cargar las categorías')
    });
    this.marcaService.listar().subscribe({
      next: datos => this.marcas.set(datos),
      error: () => this.error.set('No se pudieron cargar las marcas')
    });
    this.cargar();
  }

  cargar() {
    this.productoService.listar().subscribe({
      next: datos => this.productos.set(datos),
      error: () => this.error.set('No se pudieron cargar los productos')
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
      nombre: (v.nombre ?? '').trim(),
      descripcion: v.descripcion ?? '',
      precio: Number(v.precio),
      disponible: !!v.disponible,
      categoriaId: Number(v.categoriaId),
      marcaId: v.marcaId ? Number(v.marcaId) : null
    };
    const id = this.editandoId();
    const peticion = id
      ? this.productoService.actualizar(id, datos)
      : this.productoService.crear(datos);

    peticion.subscribe({
      next: () => {
        this.mensaje.set(id ? 'Producto actualizado' : 'Producto creado');
        this.cancelar();
        this.cargar();
      },
      error: err => {
        if (err.status === 403) this.error.set('No tienes permisos de administrador');
        else if (err.status === 404) this.error.set('La categoría o el producto no existe');
        else if (err.status === 409) this.error.set('Ya existe un producto con ese nombre en esa categoría y marca');
        else if (err.status === 400) this.error.set('Revisa los datos ingresados');
        else this.error.set('No se pudo guardar el producto');
      }
    });
  }

  editar(p: Producto) {
    this.editandoId.set(p.id);
    this.form.patchValue({
      nombre: p.nombre,
      descripcion: p.descripcion,
      precio: p.precio,
      categoriaId: String(p.categoriaId),
      marcaId: p.marcaId ? String(p.marcaId) : '',
      disponible: p.disponible
    });
  }

  cancelar() {
    this.editandoId.set(null);
    this.form.reset({ nombre: '', descripcion: '', precio: null, categoriaId: '', marcaId: '', disponible: true });
  }

  cambiarDisponibilidad(p: Producto) {
    this.error.set('');
    this.mensaje.set('');
    this.productoService.cambiarDisponibilidad(p.id, !p.disponible).subscribe({
      next: () => this.cargar(),
      error: () => this.error.set('No se pudo cambiar la disponibilidad')
    });
  }

  eliminar(id: number) {
    this.error.set('');
    this.mensaje.set('');
    this.productoService.eliminar(id).subscribe({
      next: () => {
        this.mensaje.set('Producto eliminado');
        this.cargar();
      },
      error: () => this.error.set('No se pudo eliminar el producto')
    });
  }
}