import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Session } from '../../../core/services/session';
import { UserSession } from '../../../core/models/user-session';
import { Rol } from '../../../core/enums/rol';

interface MenuItem {
  nombre: string;
  ruta: string;
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

  constructor(private session: Session) {}

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
            nombre: 'Perfil',
            ruta: '/admin/perfil'
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
          }
        ];

        break;
    }
  }
}
