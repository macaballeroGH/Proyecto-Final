import { ResultadoTratamiento } from "../enums/resultadoTratamiento";

export interface RegistrarTratamiento {
    idTurno: number;

    observacion: string | null;

    duracionReal: number;

    productosUtilizados: string | null;

    resultadoTratamiento: ResultadoTratamiento;

    serviciosRealizados: {

        idServicioRealizado: number;

        idTurnoServicioReservado: number | null;

        motivoCambio: string | null;

    }[];
}