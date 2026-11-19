import { EstadoProducto } from "../enums/estadoProducto";

export interface ObtenerProducto {
    idProducto: number;
    nombre: string;
    descripcion: string | null;
    foto: string | null;
    precio: number;
    stock: number;
    estadoProducto: EstadoProducto;
}