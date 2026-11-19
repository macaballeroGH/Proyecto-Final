import { EstadoServicio } from "../enums/estadoServicio";

export interface ObtenerServicio {
    
    id: number;
    nombre: string;
    descripcion: string;
    precio: number;
    duracionMinutos: number;
    idCategoria: number;
    idTurnoServicio?: number;
    nombreCategoria: string;
    estado: EstadoServicio;
}