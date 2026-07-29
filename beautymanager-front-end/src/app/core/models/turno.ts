import { EstadoTurno } from "../enums/estadoTurno";
import { EspecialidadEmpleado } from "../enums/especialidadEmpleado";
import { ObtenerServicio } from "./obtener-servicio";

export interface Turno {

    id: number;
    fechaHoraInicio: string;
    fechaHoraFin: string;
    estadoTurno: EstadoTurno;
    precioTotal: number;

    //cliente
    idCliente: number;
    nombreCliente: string;
    apellidoCliente: string;
    fotoCliente: string;

    //Empleado
    idEmpleado: number;
    nombreEmpleado: string;
    apellidoEmpleado: string;
    fotoEmpleado: string;
    especialidadEmpleado: EspecialidadEmpleado;

    //Servicio
    servicios: ObtenerServicio[];
}
