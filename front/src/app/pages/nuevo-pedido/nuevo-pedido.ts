import { Component, inject, signal, computed } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PedidoService } from '../../services/pedido.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-nuevo-pedido',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './nuevo-pedido.html',
  styleUrl: './nuevo-pedido.scss'
})
export class NuevoPedido {
  public pedidoService = inject(PedidoService);
  private fb = inject(FormBuilder);
  private router = inject(Router);

  error = signal('');

  form = this.fb.group({
    direccionEntrega: ['', Validators.required],
    distritoEntrega: ['', Validators.required]
  });

  subtotal = computed(() => this.pedidoService.carrito().reduce((acc, i) => acc + (i.producto.precio * i.cantidad), 0));

  confirmar() {
    if (this.form.invalid || this.pedidoService.carrito().length === 0) return;
    this.error.set('');

    const datos = {
      localId: this.pedidoService.localIdCarrito()!,
      direccionEntrega: this.form.value.direccionEntrega!,
      distritoEntrega: this.form.value.distritoEntrega!,
      items: this.pedidoService.carrito().map(i => ({ productoId: i.producto.id, cantidad: i.cantidad }))
    };

    this.pedidoService.crearPedido(datos).subscribe({
      next: () => {
        this.pedidoService.vaciarCarrito();
        this.router.navigate(['/pedidos/mis-pedidos']);
      },
      error: (err) => this.error.set(err.error?.mensaje || 'Error al crear el pedido')
    });
  }
}