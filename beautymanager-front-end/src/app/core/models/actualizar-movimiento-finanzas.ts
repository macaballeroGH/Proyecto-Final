import { EstadoMovimiento } from "../enums/estadoMovimiento";
import { MetodoPago } from "../enums/metodoPago";
import { TipoGastoEnum } from "../enums/tipoGasto";
import { TipoMovimiento } from "../enums/tipoMovimiento";

export interface ActualizarMovimientoFinanzas {
    fechaGasto: string;
    monto: number;
    tipoMovimiento: TipoMovimiento;
    descripcion: string;
    estado: EstadoMovimiento;
    nombreGasto: TipoGastoEnum | null;
    idCompra: number | null;
    metodoPago: MetodoPago;
}