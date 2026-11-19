import { ObtenerAuditoria } from "./obtener-auditoria";

export interface PaginaAuditoria {

  content: ObtenerAuditoria[];

  totalElements: number;
  totalPages: number;

  number: number;
  size: number;

  first: boolean;
  last: boolean;
}