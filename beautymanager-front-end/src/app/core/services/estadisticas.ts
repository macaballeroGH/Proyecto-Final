import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FiltoEstadisticas } from '../models/filtro-estadisticas';
import { ObtenerEstadisticas } from '../models/obtener-estadisticas';

@Injectable({
  providedIn: 'root',
})
export class EstadisticasService {

  private apiUrl = 'http://localhost:8080/estadisticas';

  constructor(private http: HttpClient) {}

  obtenerEstadisticas(filtro: FiltoEstadisticas): Observable<ObtenerEstadisticas> {
    return this.http.post<ObtenerEstadisticas>(`${this.apiUrl}/obtener`, filtro);
  }
}
