import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ActualizarTipoGasto } from '../models/actualizar-tipo-gasto';
import { CrearTipoGasto } from '../models/crear-tipo-gasto';
import { ObtenerTipoGasto } from '../models/obtener-tipo-gasto';

@Injectable({
  providedIn: 'root',
})
export class TipoGasto {

  private apiUrl = 'http://localhost:8080/tipo-gasto';

  constructor(private http: HttpClient) {}

  crearTipoGasto(tipoGasto: CrearTipoGasto): Observable<ObtenerTipoGasto> {
    return this.http.post<ObtenerTipoGasto>(`${this.apiUrl}/crear`, tipoGasto);
  }

  actualizarTipoGasto(id: number, tipoGasto: ActualizarTipoGasto): Observable<ObtenerTipoGasto> {
    return this.http.put<ObtenerTipoGasto>(`${this.apiUrl}/actualizar/${id}`, tipoGasto);
  }

  eliminarTipoGasto(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/eliminar/${id}`, { responseType: 'text' });
  }

  buscarPorId(id: number): Observable<ObtenerTipoGasto> {
    return this.http.get<ObtenerTipoGasto>(`${this.apiUrl}/id/${id}`);
  }

  listarTodos(): Observable<ObtenerTipoGasto[]> {
    return this.http.get<ObtenerTipoGasto[]>(`${this.apiUrl}/listar`);
  }

  listarOrdenados(): Observable<ObtenerTipoGasto[]> {
    return this.http.get<ObtenerTipoGasto[]>(`${this.apiUrl}/listar-ordenados`);
  }

  buscarPorNombre(nombre: string): Observable<ObtenerTipoGasto[]> {
    return this.http.get<ObtenerTipoGasto[]>(`${this.apiUrl}/nombre/${encodeURIComponent(nombre)}`);
  }

  buscarPorDescripcion(descripcion: string): Observable<ObtenerTipoGasto[]> {

    const params = new HttpParams().set('descripcion', descripcion);

    return this.http.get<ObtenerTipoGasto[]>(`${this.apiUrl}/descripcion`, { params });
  }

  existePorNombre(nombre: string): Observable<boolean> {

    const params = new HttpParams().set('nombre', nombre);

    return this.http.get<boolean>(`${this.apiUrl}/existe`, { params });
  }
}