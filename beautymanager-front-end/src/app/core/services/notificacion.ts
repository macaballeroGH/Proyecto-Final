import { Injectable } from '@angular/core';
import { ObtenerNotificacion } from '../models/obtener-notificacion';
import { ActualizarNotificacion } from '../models/actualizar-notificacion';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class Notificacion {

   private apiUrl = 'http://localhost:8080/notificacion';

  constructor(private http: HttpClient) {}

  obtenerMisNotificaciones(): Observable<ObtenerNotificacion[]> {
    return this.http.get<ObtenerNotificacion[]>(`${this.apiUrl}/mis-notificaciones`);
  }

  obtenerMisNotificacionesNoLeidas(): Observable<ObtenerNotificacion[]> {
    return this.http.get<ObtenerNotificacion[]>(`${this.apiUrl}/mis-no-leidas`);
  }

  actualizarNotificacion(idNotificacion: number, notificacion: ActualizarNotificacion): Observable<ObtenerNotificacion> {
    return this.http.put<ObtenerNotificacion>(`${this.apiUrl}/actualizar/${idNotificacion}`, notificacion);
  }
}
