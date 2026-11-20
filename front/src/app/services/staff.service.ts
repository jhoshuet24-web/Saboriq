import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Staff } from '../models/staff';
import { MiLocal } from '../models/mi-local';
import { Reserva } from '../models/reserva';
import { PedidoResponse } from '../models/pedido';

export interface StaffRequest {
  nombre: string;
  correo: string;
  password: string;
  localId: number;
}

export interface StaffUpdateRequest {
  nombre: string;
  localId: number;
}

@Injectable({ providedIn: 'root' })
export class StaffService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8081/api/usuarios/staff';

  listar(): Observable<Staff[]> {
    return this.http.get<Staff[]>(this.url);
  }
  crear(datos: StaffRequest): Observable<Staff> {
    return this.http.post<Staff>(this.url, datos);
  }
  actualizar(id: number, datos: StaffUpdateRequest): Observable<Staff> {
    return this.http.put<Staff>(`${this.url}/${id}`, datos);
  }
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  miLocal(): Observable<MiLocal> {
    return this.http.get<MiLocal>('http://localhost:8081/api/staff/mi-local');
  }
  misReservas(): Observable<Reserva[]> {
    return this.http.get<Reserva[]>('http://localhost:8081/api/staff/mis-reservas');
  }
  listarReservas(): Observable<Reserva[]> {
    return this.http.get<Reserva[]>('http://localhost:8081/api/staff/reservas');
  }
  completarReserva(id: number): Observable<Reserva> {
    return this.http.put<Reserva>(`http://localhost:8081/api/staff/reservas/${id}/completar`, {});
  }
  marcarNoShow(id: number): Observable<Reserva> {
    return this.http.put<Reserva>(`http://localhost:8081/api/staff/reservas/${id}/no-show`, {});
  }
  listarPedidos(): Observable<PedidoResponse[]> {
    return this.http.get<PedidoResponse[]>('http://localhost:8081/api/staff/pedidos');
  }

  cambiarEstadoPedido(id: number, estado: string): Observable<PedidoResponse> {
    return this.http.put<PedidoResponse>(`http://localhost:8081/api/staff/pedidos/${id}/estado?estado=${estado}`, {});
  }
}