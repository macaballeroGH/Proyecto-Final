
export interface FiltroAuditoria {

  idUsuario: number | null;

  accion: string;
  tablaAfectada: string;

  tipoEvento: string;
  resultado: string;

  fechaDesde: string;
  fechaHasta: string;
}