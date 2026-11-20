export interface Reserva {
  id: number;
  localId: number;
  localNombre: string;
  marcaNombre: string;
  fechaHora: string;
  personas: number;
  estado: 'PENDIENTE_PAGO' | 'CONFIRMADA' | 'CANCELADA' | 'COMPLETADA' | 'NO_SHOW' | 'EXPIRADA';
  requiereGarantia: boolean;
  montoGarantia: number;
  expiraEn: string | null;
  creadoEn: string;
  canceladaEn: string | null;
  garantiaResultado: 'REEMBOLSO_TOTAL' | 'RETENIDA' | null;
    clienteNombre: string;
}