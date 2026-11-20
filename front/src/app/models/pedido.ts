export interface DetallePedidoResponse {
  productoNombre: string;
  cantidad: number;
  precioUnitario: number;
}
export interface PedidoResponse {
  id: number;
  localNombre: string;
  direccionEntrega: string;
  distritoEntrega: string;
  subtotal: number;
  costoEnvio: number;
  total: number;
  estado: string;
  creadoEn: string;
  detalles: DetallePedidoResponse[];
}
export interface PedidoRequest {
  localId: number;
  direccionEntrega: string;
  distritoEntrega: string;
  items: { productoId: number; cantidad: number }[];
}
export interface PedidoResponse {
  id: number;
  localNombre: string;
  direccionEntrega: string;
  distritoEntrega: string;
  subtotal: number;
  costoEnvio: number;
  total: number;
  estado: string;
  creadoEn: string;
  detalles: DetallePedidoResponse[];
  repartidorNombre: string | null;
}