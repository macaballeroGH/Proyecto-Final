import { TipoGastoEnum } from "../enums/tipoGasto";

export interface CrearTipoGasto {
    nombreGasto: TipoGastoEnum;
    descripcion: string;
}