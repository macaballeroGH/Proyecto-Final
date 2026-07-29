import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UsuarioPerfil } from '../models/usuario-perfil';

@Injectable({
  providedIn: 'root',
})
export class Usuario {

  private apiUrl = 'http://localhost:8080/usuario';

  constructor(private http: HttpClient) {}

  obtenerMiPerfil(): Observable<UsuarioPerfil>{
    return this.http.get<UsuarioPerfil>(`${this.apiUrl}/mi-perfil`);
  }

  actualizarFotoPerfil(foto: File): Observable<string> {

    const formData = new FormData();

    formData.append('foto', foto);

    return this.http.post(
          `${this.apiUrl}/foto-perfil`,
          formData,
          {
            responseType: 'text'
          }
    );
  }

  eliminarFotoPerfil(){
    return this.http.delete(`${this.apiUrl}/foto-perfil`, {responseType: 'text'});
  }
}
