import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Producto } from '../../models/producto';
@Component({
  selector: 'app-tarjeta-producto',
  templateUrl: './tarjeta-producto.html',
  styleUrl: './tarjeta-producto.scss'
})
export class TarjetaProducto {
  @Input({ required: true }) producto!: Producto;
  @Input() puedeAgregar: boolean = false;
  @Output() agregar = new EventEmitter<void>();
}