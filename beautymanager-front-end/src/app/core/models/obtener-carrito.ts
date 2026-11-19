import { EstadoCarrito } from "../enums/estadoCarrito";
import { CarritoItem } from "./carrito-item";

export interface ObtenerCarrito {
    idCarrito: number;
    fechaCreacion: string;
    estadoCarrito: EstadoCarrito;
    idUsuario: number;
    nombreUsuario: string;
    apellidoUsuario: string;
    items: CarritoItem[];
    total: number;
}