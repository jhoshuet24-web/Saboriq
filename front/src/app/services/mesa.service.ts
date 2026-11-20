import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Mesa } from '../models/mesa';

export interface MesaRequest {
  localId: number;
  numero: number;
  capacidad: number;
  activa: boolean;
}

@Injectable({ providedIn: 'root' })
export class MesaService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8081/api/mesas';

  listar(): Observable<Mesa[]> {
    return this.http.get<Mesa[]>(this.url);
  }

  listarPorLocal(localId: number): Observable<Mesa[]> {
    return this.http.get<Mesa[]>(`${this.url}/local/${localId}`);
  }

  crear(datos: MesaRequest): Observable<Mesa> {
    return this.http.post<Mesa>(this.url, datos);
  }

  actualizar(id: number, datos: MesaRequest): Observable<Mesa> {
    return this.http.put<Mesa>(`${this.url}/${id}`, datos);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}