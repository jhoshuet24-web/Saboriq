import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReporteService } from '../../services/reporte.service';
import { ReporteMarca } from '../../models/reporte';

@Component({
  selector: 'app-admin-reportes',
  imports: [CommonModule],
  templateUrl: './admin-reportes.html',
  styleUrl: './admin-reportes.scss'
})
export class AdminReportes implements OnInit {
  private reporteService = inject(ReporteService);
  reportes = signal<ReporteMarca[]>([]);
  error = signal('');
  cargando = signal(true);
  totalIngresos = computed(() => 
    this.reportes().reduce((sum, r) => sum + r.ingresosGarantias, 0)
  );
  
  totalReservas = computed(() => 
    this.reportes().reduce((sum, r) => sum + r.totalReservas, 0)
  );

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.cargando.set(true);
    this.reporteService.obtenerReporte().subscribe({
      next: (datos) => {
        this.reportes.set(datos);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los reportes');
        this.cargando.set(false);
      }
    });
  }
}