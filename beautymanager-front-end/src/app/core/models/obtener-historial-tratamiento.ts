import { EspecialidadEmpleado } from "../enums/especialidadEmpleado";
import { EstadoTurno } from "../enums/estadoTurno";
import { ResultadoTratamiento } from "../enums/resultadoTratamiento";
import { ObtenerHistorialTratamientoServicio } from "./obtener-tratamiento-servicio";

export interface ObtenerHistorialTratamiento {
    idHistorial: number;
    fecha: string;
    observacion: string | null;
    duracionReal: number | null;
    productosUtilizados: string | null;
    resultadoTratamiento: ResultadoTratamiento | null;
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
    categorias: string[];
    serviciosRealizados: ObtenerHistorialTratamientoServicio[];
}