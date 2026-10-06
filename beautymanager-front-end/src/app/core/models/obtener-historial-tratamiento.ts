import { EspecialidadEmpleado } from "../enums/especialidadEmpleado";
import { EstadoTurno } from "../enums/estadoTurno";

export interface ObtenerHistorialTratamiento {
    idHistorial: number;
    fecha: string;
    observacion: string | null;
    duracionReal: number | null;
    productosUtilizados: string | null;
    idTurno: number;
    fechaHoraTurno: string;
    estadoTurno: EstadoTurno;
    precioTotalTurno: number;
    idCliente: number;
    nombreCliente: string;
    apellidoCliente: string;
    idEmpleado: number;
    nombreEmpleado: string;
    apellidoEmpleado: string;
    especialidadEmpleado: EspecialidadEmpleado;
    servicios: string[];
}