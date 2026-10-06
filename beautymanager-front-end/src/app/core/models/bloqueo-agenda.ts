import { EspecialidadEmpleado } from "../enums/especialidadEmpleado";

export interface BloqueoAgenda{
    idBloqueo: number;
    inicio: string;
    fin: string;
    motivo: string;
    idEmpleado: number;
    nombreEmpleado: string;
    apellidoEmpleado: string;
    especialidadEmpleado: EspecialidadEmpleado;
}