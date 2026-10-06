import { EspecialidadEmpleado } from "../enums/especialidadEmpleado";

export interface CrearEmpleado {
    nombre: string;
    apellido: string;
    email: string;
    password: string;
    telefono: string;
    especialidad: EspecialidadEmpleado;
}