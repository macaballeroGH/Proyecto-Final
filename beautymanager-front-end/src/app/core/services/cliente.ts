import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ActualizarCliente } from '../models/actualizar-cliente';
import { ObtenerCliente } from '../models/obtener-cliente';

@Injectable({
  providedIn: 'root',
})
export class ClienteService {

  private apiUrl = 'http://localhost:8080/cliente';

  constructor(private http: HttpClient) {}

  actualizarCliente(cliente: ActualizarCliente): Observable<ObtenerCliente> {
    return this.http.put<ObtenerCliente>(`${this.apiUrl}/mi-perfil`, cliente);
  }

  elimienarCliente(idCliente: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/eliminar/${idCliente}`, { responseType: 'text' });
  }

  buscarPorId(idCliente: number): Observable<ObtenerCliente> {
    return this.http.get<ObtenerCliente>(`${this.apiUrl}/${idCliente}`);
  }

  miPerfil(): Observable<ObtenerCliente> {
    return this.http.get<ObtenerCliente>(`${this.apiUrl}/mi-perfil`);
  }

  buscarPorEmail(email: string): Observable<ObtenerCliente> {
    return this.http.get<ObtenerCliente>(`${this.apiUrl}/email/${email}`);
  }

  listarTodos(): Observable<ObtenerCliente[]> {
    return this.http.get<ObtenerCliente[]>(`${this.apiUrl}/listar`);
  }
}
