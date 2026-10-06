import { TipoPeriodoEstadistica } from '../enums/tipoPeriodoEstadistica';

export interface FiltoEstadisticas {
    fechaInicio: string;
    fechaFin: string;
    tipoPeriodo: TipoPeriodoEstadistica;
    comparar: boolean;
}