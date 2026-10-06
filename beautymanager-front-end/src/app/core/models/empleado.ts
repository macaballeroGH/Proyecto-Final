import { EspecialidadEmpleado } from "../enums/especialidadEmpleado";
import { EstadoEmpleado } from "../enums/estadoEmpleado";
import { EstadoUsuario } from "../enums/estadoUsuario";

export interface Empleado {
    idEmpleado: number;
    idUsuario: number;

    nombre: string;
    apellido: string;
    email: string;
    telefono: string;
    estadoUsuario: EstadoUsuario;

    especialidad:EspecialidadEmpleado;
    fechaAlta: string;
    fechaBaja: string | null;
    estadoEmpleado: EstadoEmpleado;
}