import { MetodoPago } from "../enums/metodoPago";
import { TipoPago } from "../enums/tipoPago";
import { EstadoPago } from "../enums/estadoPago";

export interface ObtenerPago {
    idPago: number;
    monto: number;
    fecha: string;
    metodoPago: MetodoPago;
    estadoPago: EstadoPago;
    tipoPago: TipoPago;
    idTurno: number | null;
    fechaTurno: string | null;
    precioTurno: number | null;
    idCompra: number | null;
    totalCompra: number | null;
    fechaCompra: string | null;
    idCliente: number;
    nombreCliente: string;
    apellidoCliente: string;
}