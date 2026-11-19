import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ObtenerCategoriaServicio } from '../models/obtener-categoria-servicio';
import { Observable } from 'rxjs';
import { CrearCategoriaServicio } from '../models/crear-categoria-servicio';
import { ActualizarCategoriaServicio } from '../models/actualizar-categoria-servicio';

@Injectable({
  providedIn: 'root',
})
export class CategoriaServicioService {
  
  private apiUrl = 'http://localhost:8080/categoria-servicio';

  constructor(private http: HttpClient) {}

  crearCategoria(categoria: CrearCategoriaServicio): Observable<ObtenerCategoriaServicio> {
    return this.http.post<ObtenerCategoriaServicio>(`${this.apiUrl}/crear`, categoria);
  }

  actualizarCategoria(id: number, categoria: ActualizarCategoriaServicio): Observable<ObtenerCategoriaServicio> {
    return this.http.put<ObtenerCategoriaServicio>(`${this.apiUrl}/actualizar/${id}`, categoria);
  }

  listarTodas(): Observable<ObtenerCategoriaServicio[]> {
    return this.http.get<ObtenerCategoriaServicio[]>(`${this.apiUrl}/listar`);
  }

  listarActivas(): Observable<ObtenerCategoriaServicio[]> {
    return this.http.get<ObtenerCategoriaServicio[]>(`${this.apiUrl}/listar-activas`);
  }

  listarInactivas(): Observable<ObtenerCategoriaServicio[]> {
    return this.http.get<ObtenerCategoriaServicio[]>(`${this.apiUrl}/listar-inactivas`);
  }

  obtenerPorId(id: number): Observable<ObtenerCategoriaServicio> {
    return this.http.get<ObtenerCategoriaServicio>(`${this.apiUrl}/id/${id}`);
  }
}