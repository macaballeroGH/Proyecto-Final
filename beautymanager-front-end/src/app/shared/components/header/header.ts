import { Component } from '@angular/core';
import { Session } from '../../../core/services/session';
import { UserSession } from '../../../core/models/user-session';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {

  usuario: UserSession | null = null;

  constructor(private session: Session) {}

  ngOnInit(): void {
    this.usuario = this.session.obtenerSesion();
  }
}
