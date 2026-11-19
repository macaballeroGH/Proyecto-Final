import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ObtenerNotificacion } from '../../../core/models/obtener-notificacion';
import { Notificacion } from '../../../core/services/notificacion';
import { ActualizarNotificacion } from '../../../core/models/actualizar-notificacion';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-notificaciones',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './notificaciones.html',
  styleUrl: './notificaciones.css',
})
export class Notificaciones implements OnInit {

  notificaciones: ObtenerNotificacion[] = [];

  notificacionesFiltradas: ObtenerNotificacion[] = [];

  notificacionesNoLeidas: ObtenerNotificacion[] = [];

  notificacionSeleccionada: ObtenerNotificacion | null = null;

  filtroSeleccionado: 'TODAS' | 'NO_LEIDAS' = 'TODAS';

  cargando = false;

  actualizando = false;

  idNotificacionActualizando: number | null = null;

  errorCarga = '';

  errorActualizacion = '';

  constructor(private notificacionService: Notificacion, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.cargarNotificaciones();
  }

  cargarNotificaciones(): void {

    this.cargando = true;

    this.errorCarga = '';

    this.errorActualizacion = '';

    this.notificacionService.obtenerMisNotificaciones().subscribe({

      next: (respuesta) => {

        this.notificaciones = [...respuesta].sort((a, b) => new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime());

        this.cargarNotificacionesNoLeidas();

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al cargar las notificaciones:', error);

        this.errorCarga = 'No se pudieron cargar tus notificaciones. Intentá nuevamente.';

        this.notificaciones = [];

        this.notificacionesFiltradas = [];

        this.notificacionesNoLeidas = [];

        this.cargando = false;

        this.cdr.detectChanges();
      },
    });
  }

  cargarNotificacionesNoLeidas(): void {

    this.notificacionService.obtenerMisNotificacionesNoLeidas().subscribe({

      next: (respuesta) => {

        this.notificacionesNoLeidas = [...respuesta].sort((a, b) => new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime());

        this.aplicarFiltro();

        this.cargando = false;

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al cargar las notificaciones no leídas:', error);

        this.errorCarga = 'No se pudieron cargar las notificaciones pendientes de lectura.';

        this.notificacionesNoLeidas = [];

        this.aplicarFiltro();

        this.cargando = false;

        this.cdr.detectChanges();
      },
    });
  }

  get cantidadNotificaciones(): number {
    return this.notificaciones.length;
  }

  get cantidadNoLeidas(): number {
    return this.notificacionesNoLeidas.length;
  }

  cambiarFiltro(filtro: 'TODAS' | 'NO_LEIDAS'): void {

    this.filtroSeleccionado = filtro;

    this.aplicarFiltro();
  }

  aplicarFiltro(): void {

    this.notificacionesFiltradas = this.filtroSeleccionado === 'TODAS' ? [...this.notificaciones] : [...this.notificacionesNoLeidas];

  }

  abrirDetalle(notificacion: ObtenerNotificacion): void {

    this.notificacionSeleccionada = notificacion;

    this.errorActualizacion = '';
  }

  cerrarDetalle(): void {

    this.notificacionSeleccionada = null;

    this.errorActualizacion = '';
  }

  marcarComoLeida(notificacion: ObtenerNotificacion): void {

    if (notificacion.leida || this.actualizando) {
      return;
    }

    const datosActualizacion: ActualizarNotificacion = {
      leida: true
    };

    this.actualizando = true;

    this.idNotificacionActualizando = notificacion.idNotificacion;

    this.errorActualizacion = '';

    this.notificacionService.actualizarNotificacion(notificacion.idNotificacion, datosActualizacion).subscribe({

      next: (respuesta) => {

        const indice = this.notificaciones.findIndex(n => n.idNotificacion === respuesta.idNotificacion);

        if (indice !== -1) {
          this.notificaciones[indice] = respuesta;
        }

        this.notificacionesNoLeidas = this.notificacionesNoLeidas.filter(n => n.idNotificacion !== respuesta.idNotificacion);

        if (this.notificacionSeleccionada?.idNotificacion === respuesta.idNotificacion) {
          this.notificacionSeleccionada = respuesta;
        }

        this.aplicarFiltro();

        this.actualizando = false;

        this.cdr.detectChanges();

        this.idNotificacionActualizando = null;
      },

      error: (error) => {

        console.error('Error al actualizar la notificación:', error);

        this.errorActualizacion = 'No se pudo marcar la notificación como leída. Intentá nuevamente.';

        this.actualizando = false;

        this.cdr.detectChanges();

        this.idNotificacionActualizando = null;
      },
    });
  }

  obtenerGrupoFecha(fechaCreacion: string): string {

    const fechaNotificacion = new Date(fechaCreacion);

    const hoy = new Date();

    const ayer = new Date();

    ayer.setDate(hoy.getDate() - 1);

    const inicioSemana = new Date();

    const diaSemana = inicioSemana.getDay();

    const diferenciaLunes = diaSemana === 0 ? 6 : diaSemana - 1;

    inicioSemana.setDate(inicioSemana.getDate() - diferenciaLunes);

    const fecha = new Date(fechaNotificacion.getFullYear(), fechaNotificacion.getMonth(), fechaNotificacion.getDate());

    const fechaHoy = new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate());

    const fechaAyer = new Date(ayer.getFullYear(), ayer.getMonth(), ayer.getDate());

    const fechaInicioSemana = new Date(inicioSemana.getFullYear(), inicioSemana.getMonth(), inicioSemana.getDate());

    if (fecha.getTime() === fechaHoy.getTime()) {
      return 'Hoy';
    }

    if (fecha.getTime() === fechaAyer.getTime()) {
      return 'Ayer';
    }

    if (fecha >= fechaInicioSemana) {
      return 'Esta semana';
    }

    return 'Anteriores';
  }

  get gruposNotificaciones(): { titulo: string; notificaciones: ObtenerNotificacion[] }[] {

    const grupos: { [key: string]: ObtenerNotificacion[] } = {
      'Hoy': [],
      'Ayer': [],
      'Esta semana': [],
      'Anteriores': []
    };

    this.notificacionesFiltradas.forEach(notificacion => {

      const grupo = this.obtenerGrupoFecha(notificacion.fechaCreacion);

      grupos[grupo].push(notificacion);
    });

    return Object.entries(grupos).filter(([_, notificaciones]) => notificaciones.length > 0).map(([titulo, notificaciones]) => ({titulo, notificaciones}));
  }
}
