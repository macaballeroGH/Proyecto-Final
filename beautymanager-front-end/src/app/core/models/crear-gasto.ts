import { MetodoPago } from "../enums/metodoPago";
import { TipoGastoEnum } from "../enums/tipoGasto";


export interface CrearGasto {
    fechaGasto: string;
    monto: number;
    descripcion: string;
    metodoPago: MetodoPago;
    nombreGasto: TipoGastoEnum;
}