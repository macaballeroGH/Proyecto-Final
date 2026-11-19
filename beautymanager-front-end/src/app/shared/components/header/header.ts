import { Component, OnInit } from '@angular/core';
import { Session } from '../../../core/services/session';
import { UserSession } from '../../../core/models/user-session';
import { RouterLink } from "@angular/router";
import { Rol } from '../../../core/enums/rol';

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header implements OnInit {

  usuario: UserSession | null = null;

  rutaPerfil: string | null = null;

  constructor(private session: Session) {}

  ngOnInit(): void {

    this.usuario = this.session.obtenerSesion();

    this.configurarRutaPerfil();
  }

  configurarRutaPerfil(): void {

    if(!this.usuario){

      this.rutaPerfil = null;

      return;
    }

    switch(this.usuario.rol){

      case Rol.ADMIN:

        this.rutaPerfil = '/admin/perfil';

        break;

      case Rol.CLIENTE:

        this.rutaPerfil = '/cliente/perfil';

        break;

      case Rol.EMPLEADO:

        this.rutaPerfil = '/empleado/perfil';

        break;

      default:

        this.rutaPerfil = null;

        break;
    }
  }
}
