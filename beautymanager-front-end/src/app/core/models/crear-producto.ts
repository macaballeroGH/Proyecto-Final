import { EstadoProducto } from "../enums/estadoProducto";

export interface CrearProducto {
    nombre: string;
    descripcion?: string | null;
    precio: number;
    stock: number;
    estado?: EstadoProducto;
}