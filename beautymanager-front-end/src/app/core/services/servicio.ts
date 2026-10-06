import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ObtenerServicio } from '../models/obtener-servicio';
import { CrearServicio } from '../models/crear-servicio';
import { ActualizarServicio } from '../models/actualizar-servicio';


@Injectable({
  providedIn: 'root',
})
export class ServicioService {

  private apiUrl = 'http://localhost:8080/servicio';

  constructor(private http: HttpClient) {}

  crearServicio(servicio: CrearServicio): Observable<ObtenerServicio> {
    return this.http.post<ObtenerServicio>(`${this.apiUrl}/crear`, servicio);
  }

  actualizarServicio(id: number, servicio: ActualizarServicio): Observable<ObtenerServicio> {
    return this.http.put<ObtenerServicio>(`${this.apiUrl}/actualizar/${id}`, servicio);
  }

  eliminarServicio(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/eliminar/${id}`, { responseType: 'text' });
  }

  obtenerPorId(id: number): Observable<ObtenerServicio> {
    return this.http.get<ObtenerServicio>(`${this.apiUrl}/id/${id}`);
  }

  listarActivos(): Observable<ObtenerServicio[]> {
    return this.http.get<ObtenerServicio[]>(`${this.apiUrl}/listar-activos`);
  }

  listarInactivos(): Observable<ObtenerServicio[]> {
    return this.http.get<ObtenerServicio[]>(`${this.apiUrl}/listar-inactivos`);
  }

  buscarPorNombre(nombre: string): Observable<ObtenerServicio[]> {
    return this.http.get<ObtenerServicio[]>(`${this.apiUrl}/nombre/${nombre}`);
  }

  buscarPorDescripcion(descripcion: string): Observable<ObtenerServicio[]> {
    return this.http.get<ObtenerServicio[]>(`${this.apiUrl}/descripcion/${descripcion}`);
  }

  buscarPorCategoria(categoria: string): Observable<ObtenerServicio[]> {
    return this.http.get<ObtenerServicio[]>(`${this.apiUrl}/categoria/${categoria}`);
  }

  buscarActivosPorCategoria(categoria: string): Observable<ObtenerServicio[]> {
    return this.http.get<ObtenerServicio[]>(`${this.apiUrl}/categoria-activa/${categoria}`);
  }

  buscarPorRangoPrecio(min: number, max: number): Observable<ObtenerServicio[]> {
    return this.http.get<ObtenerServicio[]>(`${this.apiUrl}/precio-rango/${min}/${max}`);
  }

  buscarPrecioMaximo(precio: number): Observable<ObtenerServicio[]> {
    return this.http.get<ObtenerServicio[]>(`${this.apiUrl}/precio-max/${precio}`);
  }

  buscarPrecioMinimo(precio: number): Observable<ObtenerServicio[]> {
    return this.http.get<ObtenerServicio[]>(`${this.apiUrl}/precio-min/${precio}`);
  }

  buscarPorDuracion(minutos: number): Observable<ObtenerServicio[]> {
    return this.http.get<ObtenerServicio[]>(`${this.apiUrl}/duracion/${minutos}`);
  }

  buscarDuracionMaxima(minutos: number): Observable<ObtenerServicio[]> {
    return this.http.get<ObtenerServicio[]>(`${this.apiUrl}/duracion-max/${minutos}`);
  }

  buscarDuracionMinima(minutos: number): Observable<ObtenerServicio[]> {
    return this.http.get<ObtenerServicio[]>(`${this.apiUrl}/duracion-min/${minutos}`);
  }

  buscarPorRangoDuracion(min: number, max: number): Observable<ObtenerServicio[]> {
    return this.http.get<ObtenerServicio[]>(`${this.apiUrl}/duracion-rango/${min}/${max}`);
  }
}
