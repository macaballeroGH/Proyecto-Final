import { Empleado } from "./empleado";

export interface ObtenerDisponibilidadReserva {
    idEmpleado: number;
    nombre: string;
    apellido: string;
    especialidad: Empleado['especialidad'];
    horariosDisponibles: string[];
}