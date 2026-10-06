import { ComparacionEstadistica } from "./comparacion-estadistica";
import { ComparacionRanking } from "./comparacion-ranking";
import { DatoEstadistica } from "./dato-estadistica";
import { RankingEstadistica } from "./ranking-estadistica";

export interface ObtenerEstadisticas {
    ingresos: DatoEstadistica[];
    turnos: DatoEstadistica[];
    serviciosMasSolicitados: RankingEstadistica[];
    productosMasVendidos: RankingEstadistica[];
    comparacionIngresos: ComparacionEstadistica | null;
    comparacionTurnos: ComparacionEstadistica | null;
    comparacionServicios: ComparacionRanking[] | null;
    comparacionProductos: ComparacionRanking[] | null;
}