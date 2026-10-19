import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CrearConfiguracionRecordatorio } from '../models/crear-configuracion-recordatorio';
import { ActualizarConfiguracionRecordatorio } from '../models/actualizar-configuracion-recordatorio';
import { ObtenerConfiguracionRecordatorio } from '../models/obtener-configuracion-recordatorio';

@Injectable({
  providedIn: 'root',
})
export class ConfiguracionRecordatorio {

  private apiUrl = 'http://localhost:8080/configuracion-recordatorios';

  constructor(private http: HttpClient) {}

  crearConfiguracion(configuracion: CrearConfiguracionRecordatorio): Observable<ObtenerConfiguracionRecordatorio> {
    return this.http.post<ObtenerConfiguracionRecordatorio>(this.apiUrl, configuracion);
  }

  obtenerConfiguracion(): Observable<ObtenerConfiguracionRecordatorio> {
    return this.http.get<ObtenerConfiguracionRecordatorio>(this.apiUrl);
  }

  actualizarConfiguracion(configuracion: ActualizarConfiguracionRecordatorio): Observable<ObtenerConfiguracionRecordatorio> {
    return this.http.put<ObtenerConfiguracionRecordatorio>(this.apiUrl, configuracion);
  }

  probarEmail(email: string): Observable<string> {
    return this.http.post(`${this.apiUrl}/probar-email0`, { email }, { responseType: 'text' });
  }
}
