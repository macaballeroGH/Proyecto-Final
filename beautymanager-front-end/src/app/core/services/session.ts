import { Injectable } from '@angular/core';
import { UserSession } from '../models/user-session';

@Injectable({
  providedIn: 'root',
})
export class Session {

  private key = 'usuario';

  guardarSesion(usuario: UserSession): void {
    localStorage.setItem(this.key, JSON.stringify(usuario));
  }

  obtenerSesion(): UserSession | null {
    const usuario = localStorage.getItem(this.key);

    if(!usuario) {
      return null;
    }

    return JSON.parse(usuario);
  }

  obtenerToken(): string | null {
    const usuario = this.obtenerSesion();

    return usuario ? usuario.token : null;
  }

  cerrarSesion(): void {
    localStorage.removeItem(this.key);
  }
}
