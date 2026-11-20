import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReporteMarca } from '../models/reporte';

@Injectable({ providedIn: 'root' })
export class ReporteService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8081/api/reportes';

  obtenerReporte(): Observable<ReporteMarca[]> {
    return this.http.get<ReporteMarca[]>(`${this.url}/reservas`);
  }
}