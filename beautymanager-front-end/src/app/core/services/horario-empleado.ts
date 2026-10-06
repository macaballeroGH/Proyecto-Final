import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { CrearHorarioEmpleado } from '../models/crear-horario-empleado';
import { ActualizarHorarioEmpleado } from '../models/actualizar-horario-empleado';
import { HorarioEmpleado } from '../models/horario-empleado';

@Injectable({
  providedIn: 'root',
})
export class HorarioEmpleadoService {

  private apiUrl = 'http://localhost:8080/horario-empleado';

  constructor(private http: HttpClient) {}

  crearHorario(horario: CrearHorarioEmpleado): Observable<HorarioEmpleado> {
    return this.http.post<HorarioEmpleado>(`${this.apiUrl}/asignar`, horario);
  }

  actualizarHorario(idHorario: number, horario: ActualizarHorarioEmpleado): Observable<HorarioEmpleado> {
    return this.http.put<HorarioEmpleado>(`${this.apiUrl}/actualizar/${idHorario}`, horario);
  }

  obtenerPorId(idHorario: number): Observable<HorarioEmpleado> {
    return this.http.get<HorarioEmpleado>(`${this.apiUrl}/id/${idHorario}`);
  }

  obtenerTodos(): Observable<HorarioEmpleado[]> {
    return this.http.get<HorarioEmpleado[]>(`${this.apiUrl}/listar`);
  }

  obtenerPorEmpleado(idEmpleado: number): Observable<HorarioEmpleado[]> {
    return this.http.get<HorarioEmpleado[]>(`${this.apiUrl}/empleado/${idEmpleado}`);
  }

  obtenerPorEmpleadoYDia(idEmpelado: number, diaSemana: number): Observable<HorarioEmpleado[]> {
    return this.http.get<HorarioEmpleado[]>(`${this.apiUrl}/empleado-dia/${idEmpelado}/${diaSemana}`);
  }

  eliminarHorario(idHorario: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/eliminar/${idHorario}`);
  }
}
