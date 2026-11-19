export interface ObtenerAuditoria {

  idAuditoria: number;

  accion: string;
  tablaAfectada: string;
  idRegistro: number | null;

  detalle: string | null;
  fechaHora: string;

  tipoEvento: string;
  origen: string;
  resultado: string;

  idUsuario: number | null;

  nombreUsuario: string | null;
  apellidoUsuario: string | null;
  emailUsuario: string | null;
  rolUsuario: string | null;

  identificadorExterno: string | null;

  valoresAnteriores: string | null;
  valoresNuevos: string | null;
}