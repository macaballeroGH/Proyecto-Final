import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Empleado } from '../models/empleado';
import { CrearEmpleado } from '../models/crear-empleado';
import { ActualizarEmpleado } from '../models/actualizar-empleado';
import { EspecialidadEmpleado } from '../enums/especialidadEmpleado';
import { EstadoEmpleado } from '../enums/estadoEmpleado';

@Injectable({
  providedIn: 'root',
})
export class EmpleadoService {

  private apiUrl = 'http://localhost:8080/empleados';

  constructor(private http: HttpClient) {}

  crearEmpleado(empleado: CrearEmpleado): Observable<Empleado> {
    return this.http.post<Empleado>(`${this.apiUrl}/crear`, empleado);
  }

  actualizarEmpleado(id: number, empleado: ActualizarEmpleado): Observable<Empleado> {
    return this.http.put<Empleado>(`${this.apiUrl}/actualizar/${id}`, empleado);
  }

  eliminarEmpleado(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/eliminar/${id}`, { responseType: 'text' });
  }

  buscarPorId(id: number): Observable<Empleado> {
    return this.http.get<Empleado>(`${this.apiUrl}/id/${id}`);
  }

  listarTodos(): Observable<Empleado[]> {
    return this.http.get<Empleado[]>(`${this.apiUrl}/listar`);
  }

  buscarPorEspecialidad(especialidad: EspecialidadEmpleado): Observable<Empleado[]> {
    return this.http.get<Empleado[]>(`${this.apiUrl}/especialidad/${especialidad}`);
  }

  buscarPorEstado(estado: EstadoEmpleado): Observable<Empleado[]> {
    return this.http.get<Empleado[]>(`${this.apiUrl}/estado/${estado}`);
  }

  buscarPorEspecialidadYEstado(especialidad: EspecialidadEmpleado, estado: EstadoEmpleado): Observable<Empleado[]> {
    return this.http.get<Empleado[]>(`${this.apiUrl}/especialidad-estado/${especialidad}/${estado}`);
  }

  buscarPorEmail(email: string): Observable<Empleado> {
    return this.http.get<Empleado>(`${this.apiUrl}/email/${email}`);
  }

  buscarPorUsuarioId(idUsuario: number): Observable<Empleado> {
    return this.http.get<Empleado>(`${this.apiUrl}/usuario/${idUsuario}`);
  }

  existePorUsuario(idUsuario: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/existe-usuario/${idUsuario}`);
  }

  cambiarEstado(idEmpleado: number, estado: EstadoEmpleado): Observable<Empleado> {
    return this.http.put<Empleado>(`${this.apiUrl}/cambiar-estado/${idEmpleado}/${estado}`, {});
  }

  darDeBaja(idEmpleado: number): Observable<Empleado> {
    return this.http.put<Empleado>(`${this.apiUrl}/dar-baja/${idEmpleado}`, {});
  }

  reactivarEmpleado(idEmpleado: number): Observable<Empleado> {
    return this.http.put<Empleado>(`${this.apiUrl}/reactivar/${idEmpleado}`, {});
  }

  empleadosActivos(): Observable<Empleado[]> {
    return this.http.get<Empleado[]>(`${this.apiUrl}/activos`);
  }

  empleadosInactivos(): Observable<Empleado[]> {
    return this.http.get<Empleado[]>(`${this.apiUrl}/inactivos`);
  }

  altasEntreFechas(inicio: string, fin: string): Observable<Empleado[]> {

    const params = new HttpParams().set('inicio', inicio).set('fin', fin);

    return this.http.get<Empleado[]>(`${this.apiUrl}/altas`, { params });
  }

  bajasEntreFechas(inicio: string, fin: string): Observable<Empleado[]> {

    const params = new HttpParams().set('inicio', inicio).set('fin', fin);

    return this.http.get<Empleado[]>(`${this.apiUrl}/bajas`, { params });
  }
}
