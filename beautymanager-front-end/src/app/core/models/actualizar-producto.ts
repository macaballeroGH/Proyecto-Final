import { EstadoProducto } from "../enums/estadoProducto";

export interface ActualizarProducto {
    nombre?: string;
    descripcion?: string;
    precio?: number;
    stock?: number;
    estado?: EstadoProducto;
}