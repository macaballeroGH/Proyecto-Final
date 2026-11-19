import { TipoGastoEnum } from "../enums/tipoGasto";

export interface ActualizarTipoGasto {
    nombreGasto: TipoGastoEnum;
    descripcion: string;
}