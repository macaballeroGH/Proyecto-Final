import { TipoGastoEnum } from "../enums/tipoGasto";

export interface ObtenerTipoGasto {
    idTipoGasto: number;
    nombreGasto: TipoGastoEnum;
    descripcion: string;
    cantidadMovimientos: number;
}