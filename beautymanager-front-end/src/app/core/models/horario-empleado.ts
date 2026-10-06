import { EspecialidadEmpleado } from "../enums/especialidadEmpleado";

export interface HorarioEmpleado {
    idHorario: number;
    diaSemana: number;
    nombreDia: string;
    horaInicio: string;
    horaFin: string;
    idEmpleado: number;
    nombreEmpleado: string;
    apellidoEmpleado: string;
    especialidadEmpleado: EspecialidadEmpleado;
}