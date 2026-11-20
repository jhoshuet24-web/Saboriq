import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Repartidor } from '../models/repartidor';

export interface RepartidorRequest {
  localId: number;
  nombre: string;
  telefono: string;
  vehiculo: string;
  activo: boolean;
}

@Injectable({ providedIn: 'root' })
export class RepartidorService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8081/api/repartidores';

  listar(): Observable<Repartidor[]> {
    return this.http.get<Repartidor[]>(this.url);
  }

  listarPorLocal(localId: number): Observable<Repartidor[]> {
    return this.http.get<Repartidor[]>(`${this.url}/local/${localId}`);
  }

  crear(datos: RepartidorRequest): Observable<Repartidor> {
    return this.http.post<Repartidor>(this.url, datos);
  }

  actualizar(id: number, datos: RepartidorRequest): Observable<Repartidor> {
    return this.http.put<Repartidor>(`${this.url}/${id}`, datos);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
