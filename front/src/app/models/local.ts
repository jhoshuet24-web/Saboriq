export interface Local {
  id: number;
  marcaId: number;
  marcaNombre: string;
  nombre: string;
  direccion: string;
  distrito: string;
  ciudad: string;
  telefono: string | null;
  aforo: number;
  pedidoMinimo: number;
  costoEnvio: number;
  activo: boolean;
  distritosCobertura: string | null;
}