import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { Session } from '../../../core/services/session';
import { Auth } from '../../../core/services/auth';
import { UserSession } from '../../../core/models/user-session';
import { Rol } from '../../../core/enums/rol';

interface MenuItem {
  nombre: string;
  ruta?: string;
  accion?: 'logout';
}

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule, RouterLink],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar implements OnInit {

  usuario: UserSession | null = null;
  
  menuItems: MenuItem[] = [];

  constructor(private session: Session, private router: Router, private auth: Auth) {}

  ngOnInit(): void {
    
    this.usuario = this.session.obtenerSesion();

    this.cargarMenu();
  }

  cargarMenu(): void {

    if(!this.usuario) {
      return;
    }

    switch(this.usuario.rol) {

      case Rol.ADMIN:

        this.menuItems = [
          {
            nombre: 'Inicio',
            ruta: '/admin/inicio'
          },
          {
            nombre: 'Agenda',
            ruta: '/admin/agenda'
          },
          {
            nombre: 'Tienda',
            ruta: '/admin/tienda'
          },
          {
            nombre: 'Historial de clientes',
            ruta: '/admin/historial-clientes'
          },
          {
            nombre: 'Empleados',
            ruta: '/admin/empleados'
          },
          {
            nombre: 'Gastos',
            ruta: '/admin/gastos'
          },
          {
            nombre: 'Estadísticas',
            ruta: '/admin/estadisticas'
          },
          {
            nombre: 'Recordatorios',
            ruta: '/admin/recordatorios'
          },
          {
            nombre: 'Cerrar sesión',
            accion: 'logout'
          }
        ];

        break;

      case Rol.CLIENTE:

        this.menuItems = [
          {
            nombre: 'Inicio',
            ruta: '/cliente/inicio'
          },
          {
            nombre: 'Agenda',
            ruta: '/cliente/agenda'
          },
          {
            nombre: 'Historial de Tratamientos',
            ruta: '/cliente/historial-tratamientos'
          },
          {
            nombre: 'Notificaciones',
            ruta: '/cliente/notificaciones'
          },
          {
            nombre: 'Tienda',
            ruta: '/cliente/tienda'
          },
          {
            nombre: 'Cerrar sesión',
            accion: 'logout'
          }
        ];

        break;

      case Rol.EMPLEADO:

        this.menuItems = [
          {
            nombre: 'Inicio',
            ruta: '/empleado/inicio'
          },
          {
            nombre: 'Agenda',
            ruta: '/empleado/agenda'
          },
          {
            nombre: 'Tratamientos',
            ruta: '/empleado/tratamientos'
          },
          {
            nombre: 'Cerrar sesión',
            accion: 'logout'
          }
        ];

        break;
    }
  }

  cerrarSesion(): void {
    this.session.cerrarSesionCompleta(this.auth, this.router);
  }
}
