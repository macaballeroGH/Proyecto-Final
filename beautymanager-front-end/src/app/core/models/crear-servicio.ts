import { EstadoServicio } from "../enums/estadoServicio";

export interface CrearServicio {
    nombre: string;
    descripcion: string;
    precio: number;
    duracionMinutos: number;
    idCategoria: number;
    estado: EstadoServicio;
}