import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Turno } from '../models/turno';

@Injectable({
  providedIn: 'root',
})
export class AgendaService {

  private apiUrl = 'http://localhost:8080/agenda';

  constructor(private http: HttpClient) {}

  obtenerAgenda(inicio: string, fin: string): Observable<Turno[]> {

    const params = new HttpParams().set('inicio', inicio).set('fin', fin);

    return this.http.get<Turno[]>(`${this.apiUrl}/turnos`, { params });
  }

  obtenerDisponibilidad(idEmpleado: number, serviciosIds: number[], fecha: string): Observable<string[]> {

    let params = new HttpParams().set('fecha', fecha);

    serviciosIds.forEach(id => {params = params.append('serviciosIds', id);});

    return this.http.get<string[]>(`${this.apiUrl}/disponibilidad/${idEmpleado}`, { params });
  }

  obtenerDisponibilidadRango(idEmpleado: number, serviciosIds: number[], inicio: string, fin: string): Observable<string[]> {

    let params = new HttpParams().set('inicio', inicio).set('fin', fin);

    serviciosIds.forEach(id => {params = params.append('serviciosIds', id);});

    return this.http.get<string[]>(`${this.apiUrl}/disponibilidad-rango/${idEmpleado}`, { params });
  }

  obtenerDisponibilidadPorEmpleado(serviciosIds: number[], fecha: string): Observable<string[]> {

    let params = new HttpParams().set('fecha', fecha);

    serviciosIds.forEach(id => {params = params.append('serviciosIds', id);});

    return this.http.get<string[]>(`${this.apiUrl}/disponibilidad-empleado`, { params });
  }

  obtenerDisponibilidadGeneral(fecha: string): Observable<string[]> {

    const params = new HttpParams().set('fecha', fecha);

    return this.http.get<string[]>(`${this.apiUrl}/disponibilidad-general`, { params });
  }

  validarDisponibilidad(idEmpleado: number, inicio: string, fin: string): Observable<boolean> {

    const params = new HttpParams().set('inicio', inicio).set('fin', fin);

    return this.http.get<boolean>(`${this.apiUrl}/validar/${idEmpleado}`, { params });
  }

  calcularDuracionTotal(serviciosIds: number[]): Observable<number> {

    let params = new HttpParams();

    serviciosIds.forEach(id => {params = params.append('serviciosIds', id);});

    return this.http.get<number>(`${this.apiUrl}/duracion-total`, { params });
  }
}
