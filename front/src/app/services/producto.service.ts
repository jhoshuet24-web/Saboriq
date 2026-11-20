import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Producto } from '../models/producto';

export interface ProductoRequest {
  nombre: string;
  descripcion: string;
  precio: number;
  disponible: boolean;
  categoriaId: number;
  marcaId: number | null;
}

@Injectable({ providedIn: 'root' })
export class ProductoService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8081/api/productos';

  listar(): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.url);
  }

  listarPorCategoria(categoriaId: number): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.url}/categoria/${categoriaId}`);
  }

  listarPorMarca(marcaId: number): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.url}/marca/${marcaId}`);
  }

  crear(datos: ProductoRequest): Observable<Producto> {
    return this.http.post<Producto>(this.url, datos);
  }

  actualizar(id: number, datos: ProductoRequest): Observable<Producto> {
    return this.http.put<Producto>(`${this.url}/${id}`, datos);
  }

  cambiarDisponibilidad(id: number, disponible: boolean): Observable<Producto> {
    return this.http.patch<Producto>(`${this.url}/${id}/disponibilidad`, { disponible });
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}