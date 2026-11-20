import { Component, OnInit, inject, signal } from '@angular/core';
import { Local } from '../../models/local';
import { Marca } from '../../models/marca';
import { LocalService } from '../../services/local.service';
import { MarcaService } from '../../services/marca.service';

@Component({
  selector: 'app-locales',
  templateUrl: './locales.html',
  styleUrl: './locales.scss'
})
export class Locales implements OnInit {
  private localService = inject(LocalService);
  private marcaService = inject(MarcaService);

  locales = signal<Local[]>([]);
  marcas = signal<Marca[]>([]);
  error = signal('');

  ngOnInit() {
    this.marcaService.listar().subscribe({
      next: datos => this.marcas.set(datos),
      error: () => this.error.set('No se pudieron cargar las marcas')
    });
    this.cargar('todas');
  }

  cargar(marca: string) {
    this.error.set('');
    const peticion = marca === 'todas'
      ? this.localService.listar()
      : this.localService.listarPorMarca(Number(marca));

    peticion.subscribe({
      next: datos => this.locales.set(datos),
      error: () => this.error.set('No se pudieron cargar los locales')
    });
  }
}