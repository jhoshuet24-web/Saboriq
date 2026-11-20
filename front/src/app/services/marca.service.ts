import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Marca } from '../models/marca';

export interface MarcaRequest {
  nombre: string;
  ruc: string | null;
  activa: boolean;
}

@Injectable({ providedIn: 'root' })
export class MarcaService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8081/api/marcas';

  listar(): Observable<Marca[]> {
    return this.http.get<Marca[]>(this.url);
  }

  crear(datos: MarcaRequest): Observable<Marca> {
    return this.http.post<Marca>(this.url, datos);
  }

  actualizar(id: number, datos: MarcaRequest): Observable<Marca> {
    return this.http.put<Marca>(`${this.url}/${id}`, datos);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}