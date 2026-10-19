import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { HttpErrorResponse } from '@angular/common/http';
import { ConfiguracionRecordatorio } from '../../../core/services/configuracion-recordatorio';
import { CrearConfiguracionRecordatorio } from '../../../core/models/crear-configuracion-recordatorio';
import { ActualizarConfiguracionRecordatorio } from '../../../core/models/actualizar-configuracion-recordatorio';
import { ObtenerConfiguracionRecordatorio } from '../../../core/models/obtener-configuracion-recordatorio';
import { EstadoRecordatorio } from '../../../core/enums/estadoRecordatorio';

@Component({
  selector: 'app-recordatorios',
  standalone: true,
  imports: [FormsModule, MatIconModule],
  templateUrl: './recordatorios.html',
  styleUrl: './recordatorios.css',
})
export class Recordatorios {

  existeConfiguracion = false;

  cargando = false;

  guardando = false;

  correoActivo = true;

  notificacionInternaActiva = true;

  whatsappActivo = false;

  primerRecordatorioHoras: number | null = null;

  segundoRecordatorioHoras: number | null = null;

  mensajeExito = '';

  mensajeError = '';

  mensajeWhatsapp = '';

  constructor(private configuracionRecordatorio: ConfiguracionRecordatorio) {}

  ngOnInit(): void {
    this.cargarConfiguracion();
  }

  cargarConfiguracion(): void {

    this.cargando = true;

    this.limpiarMensajes();

    this.configuracionRecordatorio.obtenerConfiguracion().subscribe({

      next: (configuracion: ObtenerConfiguracionRecordatorio) => {

        this.existeConfiguracion = true;

        this.correoActivo = configuracion.estadoEmail === EstadoRecordatorio.ACTIVO;

        this.notificacionInternaActiva = configuracion.estadoNotificacionInterna === EstadoRecordatorio.ACTIVO;

        this.whatsappActivo = false;

        this.primerRecordatorioHoras = configuracion.primerRecordatorioHoras;

        this.segundoRecordatorioHoras = configuracion.segundoRecordatorioHoras;

        this.cargando = false;
      },

      error: (error: HttpErrorResponse) => {

        this.cargando = false;

        if(this.esConfiguracionInexistente(error)){

          this.existeConfiguracion = false;

          this.correoActivo = true;

          this.notificacionInternaActiva = true;

          this.whatsappActivo = false;

          this.primerRecordatorioHoras = null;

          this.segundoRecordatorioHoras = null;

          return;
        }

        this.mensajeError = 'No se pudo cargar la configuración de recordatorios.';
      }
    });
  }

  cambiarEstadoWhatsapp(activado: boolean): void {

    this.whatsappActivo = activado;

    if (activado) {

      this.mensajeWhatsapp = 'La integración con WhatsApp todavía no está disponible. ' + 'En una próxima versión de BeautyManager, esta funcionalidad ' + 'estará disponible al 100 %.';
    } else {

      this.mensajeWhatsapp = '';
    }
  }

  guardarConfiguracion(): void {

    this.limpiarMensajes();

    if (!this.validarHorarios()) {
      return;
    }

    this.guardando = true;

    const estadoEmail = this.correoActivo ? EstadoRecordatorio.ACTIVO : EstadoRecordatorio.INACTIVO;

    const estadoNotificacionInterna = this.notificacionInternaActiva ? EstadoRecordatorio.ACTIVO : EstadoRecordatorio.INACTIVO;

    const datosConfiguracion = {estadoEmail, estadoWhatsapp: EstadoRecordatorio.INACTIVO, estadoNotificacionInterna, primerRecordatorioHoras: this.primerRecordatorioHoras!, segundoRecordatorioHoras: this.segundoRecordatorioHoras!};

    if (this.existeConfiguracion) {

      const datosActualizar: ActualizarConfiguracionRecordatorio = {...datosConfiguracion};

      this.configuracionRecordatorio.actualizarConfiguracion(datosActualizar).subscribe({

        next: (configuracion: ObtenerConfiguracionRecordatorio) => {

          this.finalizarGuardado(configuracion);
        },

        error: () => {

          this.guardando = false;

          this.mensajeError = 'No se pudo actualizar la configuración de recordatorios.';
        }
      });

    } else {

      const datosCrear: CrearConfiguracionRecordatorio = {...datosConfiguracion};

      this.configuracionRecordatorio.crearConfiguracion(datosCrear).subscribe({

        next: (configuracion: ObtenerConfiguracionRecordatorio) => {

          this.finalizarGuardado(configuracion);
        },

        error: () => {

          this.guardando = false;

          this.mensajeError = 'No se pudo crear la configuración de recordatorios.';
        }
      });
    }
  }

  private validarHorarios(): boolean {

    if (this.primerRecordatorioHoras === null || this.segundoRecordatorioHoras === null) {

      this.mensajeError = 'Debés completar los dos horarios.';

      return false;
    }

    if (!Number.isFinite(this.primerRecordatorioHoras) || !Number.isFinite(this.segundoRecordatorioHoras)) {

      this.mensajeError = 'Los horarios deben ser números válidos.';

      return false;
    }

    if (this.primerRecordatorioHoras <= 0 || this.segundoRecordatorioHoras <= 0) {

      this.mensajeError = 'Los horarios deben ser mayores que cero.';

      return false;
    }

    if (this.primerRecordatorioHoras <= this.segundoRecordatorioHoras) {

      this.mensajeError = 'El primer recordatorio debe enviarse antes que el segundo.';

      return false;
    }

    return true;
  }

  private finalizarGuardado(configuracion: ObtenerConfiguracionRecordatorio): void {

    this.existeConfiguracion = true;

    this.primerRecordatorioHoras = configuracion.primerRecordatorioHoras;

    this.segundoRecordatorioHoras = configuracion.segundoRecordatorioHoras;

    this.correoActivo = configuracion.estadoEmail === EstadoRecordatorio.ACTIVO;

    this.notificacionInternaActiva = configuracion.estadoNotificacionInterna === EstadoRecordatorio.ACTIVO;

    this.whatsappActivo = false;

    this.mensajeWhatsapp = '';

    this.guardando = false;

    this.mensajeError = '';

    this.mensajeExito = 'La configuración se guardó correctamente.';
  }

  private esConfiguracionInexistente(error: HttpErrorResponse): boolean {

    const mensaje = String(error.error?.mensaje ?? error.error?.message ?? error.message ?? '');

    return mensaje.toLowerCase().includes('no existe configuracion de recordatorios');
  }

  private limpiarMensajes(): void {

    this.mensajeExito = '';

    this.mensajeError = '';
  }
}
