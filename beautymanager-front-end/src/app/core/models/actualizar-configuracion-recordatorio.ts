import { EstadoRecordatorio } from "../enums/estadoRecordatorio";

export interface ActualizarConfiguracionRecordatorio {
    estadoEmail?: EstadoRecordatorio;
    estadoWhatsapp?: EstadoRecordatorio;
    estadoNotificacionInterna?: EstadoRecordatorio;
    primerRecordatorioHoras?: number;
    segundoRecordatorioHoras?: number;
}