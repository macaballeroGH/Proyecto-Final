import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BloqueoAgenda } from '../models/bloqueo-agenda';
import { CrearBloqueoAgenda } from '../models/crear-bloqueo-agenda';
import { ActualizarBloqueoAgenda } from '../models/actualizar-bloqueo-agenda';

@Injectable({
  providedIn: 'root',
})
export class BloqueoAgendaService {

  private apiUrl = 'http://localhost:8080/bloqueo-agenda';

  constructor(private http: HttpClient) {}

  crearBloqueo(bloqueo: CrearBloqueoAgenda): Observable<BloqueoAgenda> {
    return this.http.post<BloqueoAgenda>(`${this.apiUrl}/crear`, bloqueo);
  }

  actualizarBloqueo(idBloqueo: number, bloqueo: ActualizarBloqueoAgenda): Observable<BloqueoAgenda>{
    return this.http.put<BloqueoAgenda>(`${this.apiUrl}/actualizar/${idBloqueo}`, bloqueo);
  }

  obtenerPorId(idBloqueo: number): Observable<BloqueoAgenda> {
    return this.http.get<BloqueoAgenda>(`${this.apiUrl}/id/${idBloqueo}`);
  }

  obtenerTodos(): Observable<BloqueoAgenda[]> {
    return this.http.get<BloqueoAgenda[]>(`${this.apiUrl}/listar`);
  }

  obtenerPorEmpleado(idEmpleado: number): Observable<BloqueoAgenda[]> {
    return this.http.get<BloqueoAgenda[]>(`${this.apiUrl}/empleado/${idEmpleado}`);
  }

  obtenerBloqueosSuperpuestos(idEmpleado: number, inicio: string, fin: string): Observable<BloqueoAgenda[]> {

    const params = new HttpParams().set('inicio', inicio).set('fin', fin);

    return this.http.get<BloqueoAgenda[]>(`${this.apiUrl}/superpuestos/${idEmpleado}`, { params });
  }

  eliminar(idBloqueo: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/eliminar/${idBloqueo}`, {responseType: 'text'});
  }
}
