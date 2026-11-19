import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, computed, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { UsuarioPerfil } from '../../../core/models/usuario-perfil';
import { Turno } from '../../../core/models/turno';
import { EstadoTurno } from '../../../core/enums/estadoTurno';
import { Usuario } from '../../../core/services/usuario';
import { TurnoService } from '../../../core/services/turno';
import { BilleteraService } from '../../../core/services/billetera';
import { HistorialTratamientoService } from '../../../core/services/historial-tratamiento';
import { ConfirmacionDialog } from '../../../shared/components/dialogs/confirmacion-dialog/confirmacion-dialog';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../../../core/services/cliente';
import { ActualizarCliente } from '../../../core/models/actualizar-cliente';
import { ObtenerCliente } from '../../../core/models/obtener-cliente';

@Component({
  selector: 'app-perfil-cliente',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatDialogModule, RouterLink, FormsModule],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class PerfilCliente implements OnInit {

  usuario = signal<UsuarioPerfil | null>(null);

  cargandoPerfil = signal<boolean>(false);

  errorPerfil = signal<string>('');

  actualizandoFoto = signal<boolean>(false);

  eliminandoFoto = signal<boolean>(false);

  errorFoto = signal<string>('');

  datosCliente = signal<ObtenerCliente | null>(null);

  cargandoDatosCliente = signal<boolean>(false);

  errorDatosCliente = signal<string>('');

  // ========================================
  // EDICIÓN DE PERFIL
  // ========================================

  edicionAbierta = signal<boolean>(false);

  guardandoEdicion = signal<boolean>(false);

  errorEdicion = signal<string>('');

  mensajeEdicion = signal<string>('');

  formularioEdicion: ActualizarCliente = {
    direccion: '',
    email: '',
    telefono: ''
  };

  // ========================================
  // TURNOS
  // ========================================

  turnos = signal<Turno[]>([]);

  cargandoTurnos = signal<boolean>(false);

  errorTurnos = signal<string>('');

  // ========================================
  // BILLETERA
  // ========================================

  saldoDisponible = signal<number | null>(null);

  cargandoBilletera = signal<boolean>(false);

  errorBilletera = signal<string>('');

  // ========================================
  // HISTORIAL DE TRATAMIENTOS
  // ========================================

  cantidadTratamientos = signal<number | null>(null);

  cargandoTratamientos = signal<boolean>(false);

  errorTratamientos = signal<string>('');

  // ========================================
  // FOTO DE PERFIL
  // ========================================

  fotoPerfilUrl = computed(() => {

    const foto = this.usuario()?.fotoPerfil;

    if(!foto){
      return '/img/default-profile.jpg';
    }

    if(/^https?:\/\//i.test(foto)){
      return foto;
    }

    return `http://localhost:8080/${foto.replace(/^\/+/, '')}`;
  });

  // ========================================
  // NOMBRE COMPLETO
  // ========================================

  nombreCompleto = computed(() => {

    const usuario = this.usuario();

    if(!usuario){
      return '';
    }

    return `${usuario.nombre} ${usuario.apellido}`.trim();
  });

  // ========================================
  // TURNOS PROXIMOS
  // ========================================

  turnosProximos = computed(() => {

    const ahora = Date.now();

    return this.turnos()
      .filter(turno => {

        const fechaInicio = new Date(
          turno.fechaHoraInicio
        ).getTime();

        const estadosValidos = [
          EstadoTurno.PENDIENTE,
          EstadoTurno.CONFIRMADO,
          EstadoTurno.REPROGRAMADO
        ];

        return (
          Number.isFinite(fechaInicio) &&
          fechaInicio > ahora &&
          estadosValidos.includes(turno.estadoTurno)
        );
      })
      .sort((a, b) => {

        const fechaA = new Date(
          a.fechaHoraInicio
        ).getTime();

        const fechaB = new Date(
          b.fechaHoraInicio
        ).getTime();

        return fechaA - fechaB;
      });
  });

  // ========================================
  // PROXIMO TURNO
  // ========================================

  proximoTurno = computed<Turno | null>(() => {

    return this.turnosProximos()[0] ?? null;
  });

  // ========================================
  // CANTIDAD DE TURNOS PROXIMOS
  // ========================================

  cantidadTurnosProximos = computed(() => {

    return this.turnosProximos().length;
  });

  // ========================================
  // CANTIDAD DE TURNOS REALIZADOS
  // ========================================

  cantidadTurnosRealizados = computed(() => {

    return this.turnos().filter(turno =>
      turno.estadoTurno === EstadoTurno.FINALIZADO
    ).length;
  });

  // ========================================
  // CONSTRUCTOR
  // ========================================

  constructor(
    private usuarioService: Usuario,
    private turnoService: TurnoService,
    private billeteraService: BilleteraService,
    private historialTratamientoService: HistorialTratamientoService,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef, private clienteService: ClienteService
  ) {}

  // ========================================
  // INICIALIZACION
  // ========================================

  ngOnInit(): void {

    this.cargarDatosPerfil();
  }

  // ========================================
  // CARGAR TODA LA INFORMACION
  // ========================================

  cargarDatosPerfil(): void {

    this.cargarPerfil();

    this.cargarDatosCliente();

    this.cargarTurnos();

    this.cargarBilletera();

    this.cargarHistorialTratamientos();
  }

  // ========================================
  // CARGAR PERFIL
  // ========================================

  cargarPerfil(): void {

    this.cargandoPerfil.set(true);

    this.errorPerfil.set('');

    this.usuarioService.obtenerMiPerfil().subscribe({

      next: (respuesta) => {

        this.usuario.set(respuesta);

        this.cargandoPerfil.set(false);

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(
          'Error al cargar el perfil de la clienta:',
          error
        );

        this.errorPerfil.set(
          'No se pudo cargar tu información personal.'
        );

        this.cargandoPerfil.set(false);

        this.cdr.detectChanges();
      }
    });
  }


  // ========================================
  // CARGAR DATOS DE CLIENTE
  // ========================================

  cargarDatosCliente(): void {

    this.cargandoDatosCliente.set(true);

    this.errorDatosCliente.set('');

    this.clienteService.miPerfil().subscribe({

      next: (respuesta) => {

        this.datosCliente.set(respuesta);

        this.cargandoDatosCliente.set(false);

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(
          'Error al cargar datos de la clienta:',
          error
        );

        this.errorDatosCliente.set(
          'No se pudieron cargar los datos adicionales del perfil.'
        );

        this.cargandoDatosCliente.set(false);

        this.cdr.detectChanges();
      }
    });
  }

  // ========================================
  // ABRIR EDICIÓN
  // ========================================

  abrirEdicion(): void {

    const cliente = this.datosCliente();

    if (!cliente || this.guardandoEdicion()) {
      return;
    }

    this.formularioEdicion = {
      direccion: cliente.direccion ?? '',
      email: cliente.email ?? '',
      telefono: cliente.telefono ?? ''
    };

    this.errorEdicion.set('');

    this.mensajeEdicion.set('');

    this.edicionAbierta.set(true);
  }

  // ========================================
  // CERRAR EDICIÓN
  // ========================================

  cerrarEdicion(): void {

    if (this.guardandoEdicion()) {
      return;
    }

    this.edicionAbierta.set(false);

    this.errorEdicion.set('');
  }

  // ========================================
  // GUARDAR CAMBIOS
  // ========================================

  guardarEdicion(): void {

    if (this.guardandoEdicion()) {
      return;
    }

    const clienteActual = this.datosCliente();

    if (!clienteActual) {
      return;
    }

    const datos: ActualizarCliente = {
      direccion: this.formularioEdicion.direccion.trim(),
      email: this.formularioEdicion.email.trim(),
      telefono: this.formularioEdicion.telefono.trim()
    };

    this.errorEdicion.set('');

    // ========================================
    // VALIDAR CORREO
    // ========================================

    const emailValido =
      /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(datos.email);

    if (!emailValido) {

      this.errorEdicion.set(
        'Ingresá un correo electrónico válido.'
      );

      return;
    }

    // ========================================
    // NO PERMITIR VACIAR CAMPOS EXISTENTES
    // El backend actual ignora cadenas vacías.
    // ========================================

    if (
      (clienteActual.telefono?.trim() && !datos.telefono) ||
      (clienteActual.direccion?.trim() && !datos.direccion)
    ) {

      this.errorEdicion.set(
        'No podés dejar vacíos los campos que ya tienen información.'
      );

      return;
    }

    // ========================================
    // VERIFICAR SI EXISTEN CAMBIOS
    // ========================================

    const sinCambios =
      datos.email === (clienteActual.email ?? '') &&
      datos.telefono === (clienteActual.telefono ?? '') &&
      datos.direccion === (clienteActual.direccion ?? '');

    if (sinCambios) {

      this.cerrarEdicion();

      return;
    }

    // ========================================
    // ACTUALIZAR
    // ========================================

    this.guardandoEdicion.set(true);

    this.clienteService.actualizarCliente(datos).subscribe({

      next: (respuesta) => {

        this.datosCliente.set(respuesta);

        // Actualizar los datos visibles del perfil.

        this.usuario.update(usuarioActual => {

          if (!usuarioActual) {
            return usuarioActual;
          }

          return {
            ...usuarioActual,
            email: respuesta.email,
            telefono: respuesta.telefono
          };
        });

        // Sincronizar el email con la sesión local.

        this.actualizarEmailSesion(respuesta.email);

        this.guardandoEdicion.set(false);

        this.edicionAbierta.set(false);

        this.errorEdicion.set('');

        this.mensajeEdicion.set(
          'Tu información se actualizó correctamente.'
        );

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(
          'Error al actualizar el perfil:',
          error
        );

        this.errorEdicion.set(
          'No se pudieron guardar los cambios. Intentá nuevamente.'
        );

        this.guardandoEdicion.set(false);

        this.cdr.detectChanges();
      }
    });
  }

  // ========================================
  // SINCRONIZAR EMAIL DE LA SESIÓN
  // ========================================

  private actualizarEmailSesion(email: string): void {

    try {

      const sesionGuardada = localStorage.getItem('usuario');

      if (!sesionGuardada) {
        return;
      }

      const sesion = JSON.parse(sesionGuardada);

      if (
        !sesion ||
        typeof sesion !== 'object' ||
        Array.isArray(sesion)
      ) {
        return;
      }

      sesion.email = email;

      localStorage.setItem(
        'usuario',
        JSON.stringify(sesion)
      );

    } catch (error) {

      console.warn(
        'No se pudo sincronizar el email de la sesión:',
        error
      );
    }
  }


  // ========================================
  // CARGAR TURNOS
  // ========================================

  cargarTurnos(): void {

    this.cargandoTurnos.set(true);

    this.errorTurnos.set('');

    this.turnos.set([]);

    this.turnoService.obtenerMisTurnosCliente().subscribe({

      next: (respuesta) => {

        this.turnos.set(respuesta);

        this.cargandoTurnos.set(false);

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(
          'Error al cargar los turnos de la clienta:',
          error
        );

        this.errorTurnos.set(
          'No se pudieron cargar tus turnos.'
        );

        this.cargandoTurnos.set(false);

        this.cdr.detectChanges();
      }
    });
  }

  // ========================================
  // CARGAR SALDO DE BILLETERA
  // ========================================

  cargarBilletera(): void {

    this.cargandoBilletera.set(true);

    this.errorBilletera.set('');

    this.saldoDisponible.set(null);

    this.billeteraService.consultarSaldo().subscribe({

      next: (saldo) => {

        this.saldoDisponible.set(saldo);

        this.cargandoBilletera.set(false);

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(
          'Error al consultar el saldo:',
          error
        );

        this.errorBilletera.set(
          'No se pudo consultar tu saldo.'
        );

        this.cargandoBilletera.set(false);

        this.cdr.detectChanges();
      }
    });
  }

  // ========================================
  // CARGAR HISTORIAL DE TRATAMIENTOS
  // ========================================

  cargarHistorialTratamientos(): void {

    this.cargandoTratamientos.set(true);

    this.errorTratamientos.set('');

    this.cantidadTratamientos.set(null);

    this.historialTratamientoService.misTratamientos().subscribe({

      next: (tratamientos) => {

        this.cantidadTratamientos.set(
          tratamientos.length
        );

        this.cargandoTratamientos.set(false);

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(
          'Error al cargar el historial de tratamientos:',
          error
        );

        this.errorTratamientos.set(
          'No se pudo consultar tu historial.'
        );

        this.cargandoTratamientos.set(false);

        this.cdr.detectChanges();
      }
    });
  }

  // ========================================
  // CAMBIAR FOTO DE PERFIL
  // ========================================

  cambiarFoto(event: Event): void {

    const input = event.target as HTMLInputElement;

    if(!input.files || input.files.length === 0){
      return;
    }

    if(this.actualizandoFoto() || this.eliminandoFoto()){
      return;
    }

    const foto = input.files[0];

    input.value = '';

    this.errorFoto.set('');

    if(!foto.type.startsWith('image/')){

      this.errorFoto.set(
        'Seleccioná un archivo de imagen válido.'
      );

      return;
    }

    this.actualizandoFoto.set(true);

    this.usuarioService.actualizarFotoPerfil(foto).subscribe({

      next: () => {

        this.actualizandoFoto.set(false);

        this.cargarPerfil();

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(
          'Error al actualizar la foto de perfil:',
          error
        );

        this.errorFoto.set(
          'No se pudo actualizar la foto de perfil.'
        );

        this.actualizandoFoto.set(false);

        this.cdr.detectChanges();
      }
    });
  }

  // ========================================
  // ELIMINAR FOTO DE PERFIL
  // ========================================

  eliminarFoto(): void {

    if(
      !this.usuario()?.fotoPerfil ||
      this.actualizandoFoto() ||
      this.eliminandoFoto()
    ){
      return;
    }

    const dialogRef = this.dialog.open(
      ConfirmacionDialog
    );

    dialogRef.afterClosed().subscribe(confirmado => {

      if(!confirmado){
        return;
      }

      this.errorFoto.set('');

      this.eliminandoFoto.set(true);

      this.usuarioService.eliminarFotoPerfil().subscribe({

        next: () => {

          this.eliminandoFoto.set(false);

          this.cargarPerfil();

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error al eliminar la foto:',
            error
          );

          this.errorFoto.set(
            'No se pudo eliminar la foto de perfil.'
          );

          this.eliminandoFoto.set(false);

          this.cdr.detectChanges();
        }
      });
    });
  }

  // ========================================
  // NOMBRES DE LOS SERVICIOS
  // ========================================

  obtenerServiciosTurno(turno: Turno): string {

    if(!turno.servicios || turno.servicios.length === 0){
      return 'Sin servicios registrados';
    }

    return turno.servicios
      .map(servicio => servicio.nombre)
      .join(', ');
  }

  // ========================================
  // NOMBRE DE LA PROFESIONAL
  // ========================================

  obtenerProfesionalTurno(turno: Turno): string {

    return `${turno.nombreEmpleado} ${turno.apellidoEmpleado}`.trim();
  }

  // ========================================
  // ESTADO LEGIBLE DEL TURNO
  // ========================================

  obtenerEstadoTurno(turno: Turno): string {

    return turno.estadoTurno.replace(/_/g, ' ');
  }
}
