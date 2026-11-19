import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Turno } from '../models/turno';
import { CrearTurno } from '../models/crear-turno';
import { ObtenerReprogramacionTurno } from '../models/obtener-reprogramacion-turno';
import { ReprogramarTurno } from '../models/reprogramar-turno';

@Injectable({
  providedIn: 'root',
})
export class TurnoService {

  private apiUrl = 'http://localhost:8080/turno';

  private adminAgendaUrl = 'http://localhost:8080/admin/agenda';

  constructor(private http: HttpClient) {}

  //========================================
  //Crear turno (CLIENTE)
  //========================================
  crearTurno(turno: CrearTurno): Observable<Turno> {
    return this.http.post<Turno>(
      `${this.apiUrl}/reservar`,
      turno
    );
  }

  //========================================
  //Crear turno (ADMIN)
  //========================================
  crearTurnoAdmin(turno: CrearTurno): Observable<Turno> {
    return this.http.post<Turno>(
      `${this.apiUrl}/admin/reservar`,
      turno
    );
  }

  //========================================
  //Obtener todos los turnos (ADMIN)
  //========================================
  obtenerTodosLosTurnos(): Observable<Turno[]> {
    return this.http.get<Turno[]>(
      `${this.apiUrl}/admin/todos`
    );
  }

  //========================================
  //Obtener mis turnos (CLIENTE)
  //========================================
  obtenerMisTurnosCliente(): Observable<Turno[]> {
    return this.http.get<Turno[]>(
      `${this.apiUrl}/cliente/mis-turnos`
    );
  }

  //========================================
  //Obtener mis turnos (EMPLEADO)
  //========================================
  obtenerMisTurnosEmpleado(): Observable<Turno[]> {
    return this.http.get<Turno[]>(
      `${this.apiUrl}/empleado/mis-turnos`
    );
  }

  //========================================
  //Cancelar turno (CLIENTE)
  //========================================
  cancelarTurno(idTurno: number): Observable<any> {
    return this.http.delete(
      `${this.apiUrl}/cancelar/${idTurno}`
    );
  }

  //========================================
  //Aceptar turno (EMPLEADO)
  //========================================
  aceptarTurno(idTurno: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/aceptar/${idTurno}`,
      {}
    );
  }

  //========================================
  //Rechazar turno (EMPLEADO)
  //========================================
  rechazarTurno(idTurno: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/rechazar/${idTurno}`,
      {}
    );
  }

  //========================================
  //Marcar ausencia (EMPLEADO)
  //========================================
  marcarAusente(idTurno: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/ausente/${idTurno}`,
      {}
    );
  }

  //========================================
  //Iniciar turno (EMPLEADO)
  //========================================
  iniciarTurno(idTurno: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/iniciar/${idTurno}`,
      {}
    );
  }

  //========================================
  //Finalizar turno (EMPLEADO)
  //========================================
  finalizarTurno(idTurno: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/finalizar/${idTurno}`,
      {}
    );
  }

  //========================================
  //Consultar reprogramaciones pendientes
  //CLIENTE
  //========================================
  obtenerReprogramacionesPendientesCliente():
    Observable<ObtenerReprogramacionTurno[]> {

    return this.http.get<ObtenerReprogramacionTurno[]>(
      `${this.apiUrl}/cliente/reprogramaciones-pendientes`
    );
  }

  //========================================
  //Aceptar propuesta de reprogramación
  //CLIENTE
  //========================================
  aceptarReprogramacion(
    idReprogramacion: number
  ): Observable<Turno> {

    return this.http.put<Turno>(
      `${this.apiUrl}/reprogramacion/${idReprogramacion}/aceptar`,
      {}
    );
  }

  //========================================
  //Rechazar propuesta de reprogramación
  //CLIENTE
  //========================================
  rechazarReprogramacion(
    idReprogramacion: number
  ): Observable<unknown> {

    return this.http.put<unknown>(
      `${this.apiUrl}/reprogramacion/${idReprogramacion}/rechazar`,
      {}
    );
  }


  //========================================
  //Proponer reprogramación (ADMIN)
  //========================================
  proponerReprogramacionAdmin(
    idTurno: number,
    datos: ReprogramarTurno
  ): Observable<Turno> {

    return this.http.put<Turno>(
      `${this.adminAgendaUrl}/reprogramar/${idTurno}`,
      datos
    );
  }


  //========================================
  //Confirmar turno (ADMIN)
  //========================================
  confirmarTurnoAdmin(idTurno: number): Observable<Turno> {
    return this.http.put<Turno>(
      `${this.adminAgendaUrl}/aceptar/${idTurno}`,
      {}
    );
  }

  //========================================
  //Rechazar turno (ADMIN)
  //========================================
  rechazarTurnoAdmin(idTurno: number): Observable<Turno> {
    return this.http.put<Turno>(
      `${this.adminAgendaUrl}/rechazar/${idTurno}`,
      {}
    );
  }

  //========================================
  //Cancelar turno (ADMIN)
  //========================================
  cancelarTurnoAdmin(idTurno: number): Observable<Turno> {
    return this.http.put<Turno>(
      `${this.adminAgendaUrl}/cancelar/${idTurno}`,
      {}
    );
  }


  //========================================
  //Proponer reprogramación (EMPLEADO)
  //========================================
  proponerReprogramacionEmpleado(
    idTurno: number,
    datos: ReprogramarTurno
  ): Observable<Turno> {

    return this.http.put<Turno>(
      `${this.apiUrl}/reprogramar/${idTurno}`,
      datos
    );
  }

}
