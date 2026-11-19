
export interface ObtenerHistorialTratamientoServicio {

    //========================================
    //IDENTIFICADOR DEL REGISTRO
    //========================================

    idHistorialServicio: number;

    //========================================
    //SERVICIO REALIZADO
    //========================================

    idServicioRealizado: number;

    nombreServicioRealizado: string;

    //========================================
    //SERVICIO ORIGINALMENTE RESERVADO
    //========================================

    idTurnoServicioReservado: number | null;

    idServicioReservado: number | null;

    nombreServicioReservado: string | null;

    //========================================
    //CAMBIOS DEL TRATAMIENTO
    //========================================

    motivoCambio: string | null;

    fechaCambio: string | null;
}
