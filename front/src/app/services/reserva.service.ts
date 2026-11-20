import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reserva } from '../models/reserva';

export interface ReservaRequest {
  localId: number;
  fechaHora: string;
  personas: number;
}

@Injectable({ providedIn: 'root' })
export class ReservaService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8081/api/reservas';

  crear(datos: ReservaRequest): Observable<Reserva> {
    return this.http.post<Reserva>(this.url, datos);
  }

  misReservas(): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(`${this.url}/mias`);
  }

  cancelar(id: number): Observable<Reserva> {
    return this.http.put<Reserva>(`${this.url}/${id}/cancelar`, {});
  }
}