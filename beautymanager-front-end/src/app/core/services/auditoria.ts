import { Injectable } from '@angular/core';
import { PaginaAuditoria } from '../models/pagina-auditoria';
import { ObtenerAuditoria } from '../models/obtener-auditoria';
import { Observable } from 'rxjs';
import { FiltroAuditoria } from '../models/filtro-auditoria';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AuditoriaService {

  private readonly apiUrl =
    'http://localhost:8080/auditoria';

  constructor(private http: HttpClient) {}

  buscarAuditorias(
    filtro: FiltroAuditoria,
    pagina: number = 0,
    tamanio: number = 20
  ): Observable<PaginaAuditoria> {

    let params = new HttpParams()
      .set('pagina', pagina.toString())
      .set('tamanio', tamanio.toString());

    if (filtro.idUsuario !== null) {
      params = params.set(
        'idUsuario',
        filtro.idUsuario.toString()
      );
    }

    if (filtro.accion.trim()) {
      params = params.set(
        'accion',
        filtro.accion.trim()
      );
    }

    if (filtro.tablaAfectada.trim()) {
      params = params.set(
        'tablaAfectada',
        filtro.tablaAfectada.trim()
      );
    }

    if (filtro.tipoEvento) {
      params = params.set(
        'tipoEvento',
        filtro.tipoEvento
      );
    }

    if (filtro.resultado) {
      params = params.set(
        'resultado',
        filtro.resultado
      );
    }

    if (filtro.fechaDesde) {
      params = params.set(
        'fechaDesde',
        filtro.fechaDesde
      );
    }

    if (filtro.fechaHasta) {
      params = params.set(
        'fechaHasta',
        filtro.fechaHasta
      );
    }

    return this.http.get<PaginaAuditoria>(
      `${this.apiUrl}/buscar`,
      { params }
    );
  }

  obtenerPorId(
    idAuditoria: number
  ): Observable<ObtenerAuditoria> {

    return this.http.get<ObtenerAuditoria>(
      `${this.apiUrl}/id/${idAuditoria}`
    );
  }
}
