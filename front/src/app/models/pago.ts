export interface PagoResponse {
  id: number;
  reservaId: number;
  monto: number;
  metodo: string;
  estado: string;
  referencia: string;
  creadoEn: string;
}