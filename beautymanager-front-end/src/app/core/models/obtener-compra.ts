import { EstadoCompra } from "../enums/estadoCompra";
import { DetalleCompra } from "./detalle-compra";

export interface ObtenerCompra {

    idCompra: number;
    fecha: string;
    total: number;
    idCliente: number;
    nombreCliente: string;
    apellidoCliente: string;
    estadoCompra: EstadoCompra;
    detalles: DetalleCompra[];
}