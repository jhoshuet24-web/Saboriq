import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Local } from '../models/local';

export interface LocalRequest {
  marcaId: number;
  nombre: string;
  direccion: string;
  distrito: string;
  ciudad: string;
  telefono: string | null;
  aforo: number;
  pedidoMinimo: number;
  costoEnvio: number;
  activo: boolean;
  distritosCobertura: string | null;
}

@Injectable({ providedIn: 'root' })
export class LocalService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8081/api/locales';

  listar(): Observable<Local[]> {
    return this.http.get<Local[]>(this.url);
  }

  listarPorMarca(marcaId: number): Observable<Local[]> {
    return this.http.get<Local[]>(`${this.url}/marca/${marcaId}`);
  }

  crear(datos: LocalRequest): Observable<Local> {
    return this.http.post<Local>(this.url, datos);
  }

  actualizar(id: number, datos: LocalRequest): Observable<Local> {
    return this.http.put<Local>(`${this.url}/${id}`, datos);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}