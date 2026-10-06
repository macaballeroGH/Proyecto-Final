import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ObtenerHistorialTratamiento } from '../models/obtener-historial-tratamiento';
import { RegistrarTratamiento } from '../models/registrar-tratamiento';

@Injectable({
  providedIn: 'root',
})
export class HistorialTratamientoService {

  private apiUrl = 'http://localhost:8080/historial-tratamiento';

  constructor(private http: HttpClient) {}

  registrarTratamiento(tratamiento: RegistrarTratamiento): Observable<ObtenerHistorialTratamiento> {
    return this.http.post<ObtenerHistorialTratamiento>(`${this.apiUrl}/registrar`, tratamiento);
  }

  buscarPorId(idHistorial: number): Observable<ObtenerHistorialTratamiento> {

    return this.http.get<ObtenerHistorialTratamiento>(`${this.apiUrl}/id/${idHistorial}`);
  }

  listarTodos(): Observable<ObtenerHistorialTratamiento[]> {
    return this.http.get<ObtenerHistorialTratamiento[]>(`${this.apiUrl}/listar`);
  }

  misTratamientos(): Observable<ObtenerHistorialTratamiento[]> {
    return this.http.get<ObtenerHistorialTratamiento[]>(`${this.apiUrl}/mis-tratamientos`);
  }

  listarPorEmpleado(idEmpleado: number): Observable<ObtenerHistorialTratamiento[]> {
    return this.http.get<ObtenerHistorialTratamiento[]>(`${this.apiUrl}/empleado/${idEmpleado}`);
  }

  listarPorFecha(fecha: string): Observable<ObtenerHistorialTratamiento[]> {

    const params = new HttpParams().set('fecha', fecha);

    return this.http.get<ObtenerHistorialTratamiento[]>(`${this.apiUrl}/fecha`, { params });
  }

  listarPorRangoFechas(fechaInicio: string, fechaFin: string): Observable<ObtenerHistorialTratamiento[]> {

    const params = new HttpParams().set('fechaInicio', fechaInicio).set('fechaFin', fechaFin);

    return this.http.get<ObtenerHistorialTratamiento[]>(`${this.apiUrl}/rango-fechas`, { params });
  }

  listarPorTurno(idTurno: number): Observable<ObtenerHistorialTratamiento[]> {
    return this.http.get<ObtenerHistorialTratamiento[]>(`${this.apiUrl}/turno/${idTurno}`);
  }

  buscarPorTexto(texto: string): Observable<ObtenerHistorialTratamiento[]> {
    return this.http.get<ObtenerHistorialTratamiento[]>(`${this.apiUrl}/texto/${texto}`);
  }

  verHistorialCliente(idCliente: number): Observable<ObtenerHistorialTratamiento[]> {
    return this.http.get<ObtenerHistorialTratamiento[]>(`${this.apiUrl}/cliente/${idCliente}`);
  }

  elimienar(idHistorial: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/eliminar/${idHistorial}`, { responseType: 'text' });
  }
}