import { Local } from './local';
import { Mesa } from './mesa';
import { Repartidor } from './repartidor';
export interface MiLocal {
  local: Local;
  mesas: Mesa[];
  repartidores: Repartidor[];
}