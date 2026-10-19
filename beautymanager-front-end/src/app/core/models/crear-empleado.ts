import { EspecialidadEmpleado } from "../enums/especialidadEmpleado";
import { CrearHorarioEmpleado } from "./crear-horario-empleado";

export interface CrearEmpleado {
    nombre: string;
    apellido: string;
    email: string;
    password: string;
    telefono: string;
    especialidad: EspecialidadEmpleado;
    horarios: CrearHorarioEmpleado[];
}