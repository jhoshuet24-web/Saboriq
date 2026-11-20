import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PagoResponse } from '../models/pago';

@Injectable({ providedIn: 'root' })
export class PagoService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8081/api/pagos';

  pagarReserva(reservaId: number, metodo: string): Observable<PagoResponse> {
    return this.http.post<PagoResponse>(`${this.url}/reserva/${reservaId}`, { metodo });
  }
  pagarPedido(pedidoId: number, metodo: string): Observable<PagoResponse> {
    return this.http.post<PagoResponse>(`${this.url}/pedido/${pedidoId}`, { metodo });
  }
}