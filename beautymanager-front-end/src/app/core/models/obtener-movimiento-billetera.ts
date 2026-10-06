import { MovimientoBilletera } from "../enums/movimientoBilletera";

export interface ObtenerMovimientoBilletera {
    idMovimiento: number;
    fecha: string;
    monto: number;
    descripcion: string;
    tipoMovimiento: MovimientoBilletera;
    idBilletera: number;
    idUsuario: number;
    nombreUsuario: string;
    apellidoUsuario: string;
    emailUsuario: string;
    idCompra: number | null;
}