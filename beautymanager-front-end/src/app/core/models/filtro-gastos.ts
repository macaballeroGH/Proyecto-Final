import { TipoGastoEnum } from "../enums/tipoGasto";

export interface FiltroGastos {
  fechaDesde?: string;
  fechaHasta?: string;
  nombreGasto?: TipoGastoEnum;
  montoMinimo?: number;
  montoMaximo?: number;
  ordenarPor?: string;
  sentido?: string;
}