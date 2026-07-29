import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Turno } from '../models/turno';

@Injectable({
  providedIn: 'root',
})
export class TurnoService {

  private apiUrl= 'http://localhost:8080/turno';

  constructor(private http: HttpClient) {}

  obtenerTodosLosTurnos(): Observable<Turno[]> {
    return this.http.get<Turno[]>(`${this.apiUrl}/admin/todos`);
  }
}
