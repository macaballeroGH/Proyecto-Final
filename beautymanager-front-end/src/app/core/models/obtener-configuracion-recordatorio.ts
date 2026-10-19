import { EstadoRecordatorio } from "../enums/estadoRecordatorio";

export interface ObtenerConfiguracionRecordatorio {
    idConfiguracion: number;
    estadoEmail: EstadoRecordatorio;
    estadoWhatsapp: EstadoRecordatorio;
    estadoNotificacionInterna: EstadoRecordatorio;
    primerRecordatorioHoras: number;
    segundoRecordatorioHoras: number;
}