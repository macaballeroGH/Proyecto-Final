import { EstadoTurno } from '../enums/estadoTurno';
import { EstadoReprogramacion } from '../enums/estadoReprogramacion';

export interface ObtenerReprogramacionTurno {

  idReprogramacion: number;

  idTurno: number;

  inicioAnterior: string;

  finAnterior: string;

  inicioNuevo: string;

  finNuevo: string;

  motivo: string;

  estadoPropuesta: EstadoReprogramacion;

  estadoTurno: EstadoTurno;

  fechaCambio: string;
}
