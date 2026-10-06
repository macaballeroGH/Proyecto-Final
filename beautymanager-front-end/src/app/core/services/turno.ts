import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Turno } from '../models/turno';
import { CrearTurno } from '../models/crear-turno';

@Injectable({
  providedIn: 'root',
})
export class TurnoService {

  private apiUrl= 'http://localhost:8080/turno';

  constructor(private http: HttpClient) {}

  crearTurno(turno: CrearTurno): Observable<Turno> {
    return this.http.post<Turno>(`${this.apiUrl}/reservar`, turno);
  }

  crearTurnoAdmin(turno: CrearTurno): Observable<Turno> {
    return this.http.post<Turno>(`${this.apiUrl}/admin/reservar`, turno);
  }

  obtenerTodosLosTurnos(): Observable<Turno[]> {
    return this.http.get<Turno[]>(`${this.apiUrl}/admin/todos`);
  }

  obtenerMisTurnosCliente(): Observable<Turno[]> {
    return this.http.get<Turno[]>(`${this.apiUrl}/cliente/mis-turnos`);
  }

  obtenerMisTurnosEmpleado(): Observable<Turno[]> {
    return this.http.get<Turno[]>(`${this.apiUrl}/empleado/mis-turnos`);
  }

  cancelarTurno(idTurno: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/cancelar/${idTurno}`);
  }

  aceptarTurno(idTurno: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/aceptar/${idTurno}`, {});
  }

  rechazarTurno(idTurno: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/rechazar/${idTurno}`, {});
  }

  marcarAusente(idTurno: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/ausente/${idTurno}`, {});
  }

  iniciarTurno(idTurno: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/iniciar/${idTurno}`, {});
  }

  finalizarTurno(idTurno: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/finalizar/${idTurno}`, {});
  }
}
