import { EspecialidadEmpleado } from "../enums/especialidadEmpleado";
import { EstadoEmpleado } from "../enums/estadoEmpleado";

export interface ActualizarEmpleado {
    especialidad?: EspecialidadEmpleado;
    estadoEmpleado?: EstadoEmpleado;
}