import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LoginResponse } from '../models/login-response';
import { RegistroRequest } from '../models/registro-request';
import { SolicitarRecuperacionRequest } from '../models/solicitar-recuperacion-request';
import { RecuperacionPasswordResponse } from '../models/recuperacion-password-response';
import { RestablecerPasswordRequest } from '../models/restablecer-password-request';
import { ResponseDTO } from '../models/response-dto';

@Injectable({
  providedIn: 'root',
})
export class Auth {

  private apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient) {}

  login(email: string, password: string){
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, {
      email,
      password
    });
  }

  registrar(usuario: RegistroRequest) {
    return this.http.post('http://localhost:8080/registro/nuevo', usuario);
  }

  solicitarRecuperacionPassword(solicitud: SolicitarRecuperacionRequest){
    return this.http.post<RecuperacionPasswordResponse>(`${this.apiUrl}/forgot-password`, solicitud);
  }

  restablecerPassword(solicitud: RestablecerPasswordRequest){
    return this.http.post<ResponseDTO>(`${this.apiUrl}/reset-password`, solicitud);
  }
}
