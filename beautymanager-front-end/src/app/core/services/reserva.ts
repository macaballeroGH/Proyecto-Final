import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CrearReserva } from '../models/crear-reserva';
import { Observable } from 'rxjs';
import { CrearReservaCliente } from '../models/crear-reserva-cliente';

@Injectable({
  providedIn: 'root',
})
export class ReservaService {

  private apiUrl = 'http://localhost:8080/reservas';

  constructor(private http: HttpClient) {}

  crearReserva(reserva: CrearReserva): Observable<number> {
    return this.http.post<number>(`${this.apiUrl}/crear`, reserva);
  }

  confirmarYPagar(reserva: CrearReservaCliente): Observable<number> {
    return this.http.post<number>(`${this.apiUrl}/confirmar-y-pagar`, reserva);
  }
}
