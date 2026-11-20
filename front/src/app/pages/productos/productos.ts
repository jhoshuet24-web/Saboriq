import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Producto } from '../../models/producto';
import { Categoria } from '../../models/categoria';
import { Marca } from '../../models/marca';
import { Local } from '../../models/local';
import { ProductoService } from '../../services/producto.service';
import { CategoriaService } from '../../services/categoria.service';
import { MarcaService } from '../../services/marca.service';
import { LocalService } from '../../services/local.service';
import { PedidoService } from '../../services/pedido.service';
import { AuthService } from '../../services/auth.service';
import { TarjetaProducto } from '../../components/tarjeta-producto/tarjeta-producto';

@Component({
  selector: 'app-productos',
  imports: [TarjetaProducto],
  templateUrl: './productos.html',
  styleUrl: './productos.scss'
})
export class Productos implements OnInit {
  private productoService = inject(ProductoService);
  private categoriaService = inject(CategoriaService);
  private marcaService = inject(MarcaService);
  private localService = inject(LocalService);
  public pedidoService = inject(PedidoService);
  public auth = inject(AuthService);
  private router = inject(Router);

  productos = signal<Producto[]>([]);
  categorias = signal<Categoria[]>([]);
  marcas = signal<Marca[]>([]);
  locales = signal<Local[]>([]);
  
  categoriaSel = signal('todas');
  marcaSel = signal('todas');
  localSel = signal<string>('');
  error = signal('');
  mensaje = signal('');

  marcaDelLocal = computed(() => {
    const local = this.locales().find(l => l.id === Number(this.localSel()));
    return local ? local.marcaId : null;
  });

  visibles = computed(() => {
    const cat = this.categoriaSel();
    const marcaLocal = this.marcaDelLocal();

    return this.productos().filter(p => {
      const okCategoria = cat === 'todas' || p.categoriaId === Number(cat);
      const okMarca = marcaLocal === null || p.marcaId === marcaLocal;
      return okCategoria && okMarca;
    });
  });

    ngOnInit() {
    const falla = () => this.error.set('No se pudo conectar con el servidor');
    this.categoriaService.listar().subscribe({ next: d => this.categorias.set(d), error: falla });
    this.marcaService.listar().subscribe({ next: d => this.marcas.set(d), error: falla });
    this.localService.listar().subscribe({ next: d => this.locales.set(d.filter(l => l.activo)), error: falla });
    this.productoService.listar().subscribe({ next: d => this.productos.set(d), error: falla });
  }

  agregarAlCarrito(p: Producto) {
    const localId = Number(this.localSel());
    if (!localId) return;

    this.pedidoService.agregarAlCarrito(p, localId);
    this.mensaje.set(`${p.nombre} agregado al carrito`);
    setTimeout(() => this.mensaje.set(''), 2000);
  }

  irAlCarrito() {
    this.router.navigate(['/pedidos/nuevo']);
  }
}