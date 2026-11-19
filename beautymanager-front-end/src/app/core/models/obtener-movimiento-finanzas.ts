import { EstadoMovimiento } from "../enums/estadoMovimiento";
import { MetodoPago } from "../enums/metodoPago";
import { TipoGastoEnum } from "../enums/tipoGasto";
import { TipoMovimiento } from "../enums/tipoMovimiento";

export interface ObtenerMovimientoFinanzas {
    idMovimiento: number;
    fechaGasto: string;
    fechaRegistro: string;
    monto: number;
    tipoMovimiento: TipoMovimiento;
    descripcion: string;
    estadoMovimiento: EstadoMovimiento;
    metodoPago: MetodoPago;

    idUsuario: number;
    nombreUsuario: string;
    apellidoUsuario: string;

    nombreGasto: TipoGastoEnum | null;

    idCompra: number | null;
}