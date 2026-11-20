import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PedidoResponse, PedidoRequest } from '../models/pedido';
import { Producto } from '../models/producto';

@Injectable({ providedIn: 'root' })
export class PedidoService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8081/api/pedidos';
  carrito = signal<{ producto: Producto; cantidad: number }[]>([]);
  localIdCarrito = signal<number | null>(null);
  agregarAlCarrito(producto: Producto, localId: number) {
    if (this.localIdCarrito() !== null && this.localIdCarrito() !== localId) {
      this.carrito.set([]);
    }
    this.localIdCarrito.set(localId);
    
    const item = this.carrito().find(i => i.producto.id === producto.id);
    if (item) {
      item.cantidad++;
      this.carrito.set([...this.carrito()]);
    } else {
      this.carrito.set([...this.carrito(), { producto, cantidad: 1 }]);
    }
  }

  vaciarCarrito() {
    this.carrito.set([]);
    this.localIdCarrito.set(null);
  }

  crearPedido(datos: PedidoRequest): Observable<PedidoResponse> {
    return this.http.post<PedidoResponse>(this.url, datos);
  }

  misPedidos(): Observable<PedidoResponse[]> {
    return this.http.get<PedidoResponse[]>(`${this.url}/mios`);
  }
}