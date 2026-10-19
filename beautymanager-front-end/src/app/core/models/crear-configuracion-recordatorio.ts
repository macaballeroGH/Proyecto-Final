import { EstadoRecordatorio } from "../enums/estadoRecordatorio";

export interface CrearConfiguracionRecordatorio {
    estadoEmail: EstadoRecordatorio;
    estadoWhatsapp: EstadoRecordatorio;
    estadoNotificacionInterna: EstadoRecordatorio;
    primerRecordatorioHoras: number;
    segundoRecordatorioHoras: number;
}