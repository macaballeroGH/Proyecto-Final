import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router} from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { TurnoService } from '../../../core/services/turno';
import { Turno } from '../../../core/models/turno';
import { EstadoTurno } from '../../../core/enums/estadoTurno';
import { AgendaService } from '../../../core/services/agenda';
import { ServicioService } from '../../../core/services/servicio';
import { BloqueoAgendaService } from '../../../core/services/bloqueo-agenda';
import { BloqueoAgenda } from '../../../core/models/bloqueo-agenda';
import { Empleado } from '../../../core/models/empleado';
import { CrearTurno } from '../../../core/models/crear-turno';
import { ObtenerCliente } from '../../../core/models/obtener-cliente';
import { ObtenerServicio } from '../../../core/models/obtener-servicio';
import { ClienteService } from '../../../core/services/cliente';
import { EmpleadoService } from '../../../core/services/empleado';
import { ObtenerDisponibilidadReserva } from '../../../core/models/obtener-disponibilidad-reserva';
import { ReservaService } from '../../../core/services/reserva';
import { CrearReserva } from '../../../core/models/crear-reserva';
import { ReprogramarTurno } from '../../../core/models/reprogramar-turno';

interface AsignacionReserva {

  idLocal: number;

  idServicio: number;

  idEmpleado: number;

  fechaHoraInicio: string;

  empleadosDisponibles: ObtenerDisponibilidadReserva[];

  cargandoDisponibilidad: boolean;

  numeroConsulta: number;
}
@Component({
  selector: 'app-agenda',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule],
  templateUrl: './agenda.html',
  styleUrl: './agenda.css',
})
export class AgendaAdmin implements OnInit {

  turnos: Turno[] = [];

  bloqueos: BloqueoAgenda[] = []

  turnosPorFranja: Map<string, Turno[]> = new Map();

  horas: string[] = [];

  diasSemana: Date[] = [];

  fechaSemana: Date = new Date();

  mostrarSelectorFecha: boolean = false;

  turnoSeleccionado: Turno | null = null;

  mostrarFormularioReprogramacion: boolean = false;

  fechaReprogramacion: string = '';

  horaReprogramacion: string = '';

  motivoReprogramacion: string = '';

  enviandoPropuestaReprogramacion: boolean = false;

  errorReprogramacion: string = '';

  mensajeReprogramacion: string = '';

  mostrarFiltros: boolean = false;

  mostrarFormularioNuevoTurno: boolean = false;

  clientes: ObtenerCliente[] = [];

  empleados: Empleado[] = [];

  servicios: ObtenerServicio[] = [];

  nuevoTurno: CrearTurno = {
    idCliente: 0,
    idEmpleado: 0,
    serviciosIds: [],
    fechaHoraInicio: ''
  };

  procesandoAccionTurno: boolean = false;

  errorAccionTurno: string = '';

  mensajeAccionTurno: string = '';

  fechaNuevoTurno: string = '';
  
  horaNuevoTurno: string = '';

  duracionTotalFormulario: number = 0;

  horariosDisponibles: string[] = [];

  asignacionesReserva: AsignacionReserva[] = [];

  private siguienteIdAsignacion: number = 1;

  errorDisponibilidadReserva: string = '';

  creandoReserva: boolean = false;

  errorReserva: string = '';

  textoBusqueda: string = '';

  turnosFiltrados: Turno[] = [];

  filtros = {
    
    empleado: null as number | null,

    cliente: null as number | null,

    servicio: null as number | null,

    categoria: null as number | null,

    estado: null as EstadoTurno | null,

    mostrarCancelados: true
  };

  tipoSelector: 'dia' | 'meses' | 'anios' = 'dia';

  hoverTurno: Turno | null = null;

  mesSelector: Date = new Date();

  diasMes: Date[] = [];

  meses: string[] = [
    'Enero',
    'Febrero',
    'Marzo',
    'Abril',
    'Mayo',
    'Junio',
    'Julio',
    'Agosto',
    'Septiembre',
    'Octubre',
    'Noviembre',
    'Diciembre'
  ];

  anioSelector: number = new Date().getFullYear();

  aniosDisponibles: number[] = [];

  readonly EstadoTurno = EstadoTurno;

  readonly clasesEstado: Record<EstadoTurno, string> = {
    [EstadoTurno.PENDIENTE]: 'pendiente',
    [EstadoTurno.CONFIRMADO]: 'confirmado',
    [EstadoTurno.EN_CURSO]: 'en-curso',
    [EstadoTurno.FINALIZADO]: 'finalizado',
    [EstadoTurno.CANCELADO]: 'cancelado',
    [EstadoTurno.AUSENTE]: 'ausente',
    [EstadoTurno.RECHAZADO]: 'rechazado',
    [EstadoTurno.REPROGRAMADO]: 'reprogramado',
  }

  constructor(private agendaService: AgendaService, private bloqueoAgendaService: BloqueoAgendaService, private empleadoService: EmpleadoService, private clienteService: ClienteService, private servicioService: ServicioService, private reservaService: ReservaService, private turnoService: TurnoService, private cdr: ChangeDetectorRef, private router: Router) {}

  ngOnInit(): void {
    
    this.generarHorario();

    this.generarSemana();

    this.generarCalendarioMes();

    this.generarAnios();

    this.cargarTurnos();

    this.cargarBloqueos();
  }

  cargarTurnos(): void {
    
    const inicio = this.formatearFecha(this.diasSemana[0]);

    const fin = this.formatearFecha(this.diasSemana[6]);

    console.log('Inicio agenda:', inicio);

    console.log('Fin agenda:', fin);

    this.agendaService.obtenerAgenda(inicio, fin).subscribe({

      next: (data) => {

        this.turnos = [...data];

        this.aplicarFiltros();

        console.log('Turnos recibidos:', this.turnos);

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al cargar los turnos de la agenda:', error);

        this.turnos = [];

        this.turnosFiltrados = [];

        this.turnosPorFranja.clear();

        this.cdr.detectChanges();
      }
    });
  }

  cargarBloqueos(): void {

    const inicioSemana = new Date(this.diasSemana[0]);
    inicioSemana.setHours(0, 0, 0, 0);

    const finSemana = new Date(this.diasSemana[6]);
    finSemana.setHours(23, 59, 59, 999);

    this.bloqueoAgendaService.obtenerTodos().subscribe({

      next: (data) => {

        this.bloqueos = data.filter(bloqueo =>
          this.bloqueoPerteneceAlRango(bloqueo, inicioSemana, finSemana)
        );

        console.log('Bloqueos recibidos:', this.bloqueos);

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al cargar los bloqueos de la agenda', error);

        this.cdr.detectChanges();
      }
    });
  }

  private bloqueoPerteneceAlRango(bloqueo: BloqueoAgenda, inicioRango: Date, finRango: Date): boolean {

    const inicioBloqueo = new Date(bloqueo.inicio);

    const finBloqueo = new Date(bloqueo.fin);

    return (inicioBloqueo <= finRango && finBloqueo >= inicioRango);
  }

  private formatearFecha(fecha: Date): string {

    const anio = fecha.getFullYear();
    const mes = (fecha.getMonth() + 1).toString().padStart(2, '0');
    const dia = fecha.getDate().toString().padStart(2, '0');

    return `${anio}-${mes}-${dia}`;
  }

  private indexarTurnos(turnos: Turno[]): void {

    this.turnosPorFranja.clear();

    turnos.forEach(turno => {

      const fecha = new Date(turno.fechaHoraInicio);

      const clave = this.generarClave(fecha, fecha.getHours(), fecha.getMinutes());

      if(!this.turnosPorFranja.has(clave)){
        this.turnosPorFranja.set(clave, []);
      }

      this.turnosPorFranja.get(clave)?.push(turno);
    });
  }

  private generarClave(fecha: Date, hora: number, minuto: number): string {

    const anio = fecha.getFullYear();

    const mes = (fecha.getMonth() + 1).toString().padStart(2, '0');

    const dia = fecha.getDate().toString().padStart(2, '0');

    const h = hora.toString().padStart(2, '0');

    const m = minuto.toString().padStart(2, '0');

    return `${anio}-${mes}-${dia}_${h}:${m}`;
  }

  generarHorario(): void {

    this.horas = [];

    for(let hora = 8; hora <= 20; hora++){

      for(let minuto = 0; minuto < 60; minuto += 15){

        if(hora === 20 && minuto > 0){
          break;
        }

        const horaTexto = hora.toString().padStart(2, '0');

        const minutoTexto = minuto.toString().padStart(2, '0');

        this.horas.push(`${horaTexto}:${minutoTexto}`);
      }
    }
  }

  generarSemana(): void {
    
    this.diasSemana = [];

    const fecha = new Date(this.fechaSemana);

    const diaActual = fecha.getDay();

    const diferencia = diaActual === 0 ? -6 : 1 - diaActual;

    fecha.setDate(fecha.getDate() + diferencia);

    for(let i = 0; i < 7; i++){

      const dia = new Date(fecha);

      dia.setDate(fecha.getDate() + i);

      this.diasSemana.push(dia);
    }
  }

  abrirSelectorFecha(): void {

    this.mostrarSelectorFecha = !this.mostrarSelectorFecha;

    if(this.mostrarSelectorFecha){

      this.tipoSelector = 'dia';

      this.mesSelector = new Date(this.fechaSemana);

      this.generarCalendarioMes();

      this.generarAnios();
    }
  }

  cambiarVistaSelector(): void {

    if(this.tipoSelector === 'dia'){

      this.tipoSelector = 'meses';

    } else if(this.tipoSelector === 'meses'){

      this.tipoSelector = 'anios';

      this.generarAnios();

    }else{
      this.tipoSelector = 'dia';
    }
  }

  generarCalendarioMes(): void {

    this.diasMes = [];

    const anio = this.mesSelector.getFullYear();

    const mes = this.mesSelector.getMonth();

    const primerDia = new Date(anio, mes, 1);

    const ultimoDia = new Date(anio, mes + 1, 0);

    const diaInicio = primerDia.getDay() === 0 ? 6 : primerDia.getDay() - 1;

    for(let i = diaInicio; i > 0; i--){

      this.diasMes.push(new Date(anio, mes, 1 - i));
    }

    for(let i = 1; i <= ultimoDia.getDate(); i++){

      this.diasMes.push(new Date(anio, mes, i));
    }

    while(this.diasMes.length < 42){

      const ultimo = this.diasMes[this.diasMes.length - 1];

      const siguiente = new Date(ultimo);

      siguiente.setDate(siguiente.getDate() + 1);

      this.diasMes.push(siguiente);
    }
  }

  generarAnios(): void {

    this.aniosDisponibles = [];

    for(
      let i = this.anioSelector - 6;
      
      i <= this.anioSelector + 6;

      i++
    ){
      this.aniosDisponibles.push(i);
    }
  }

  seleccionarAnio(anio:number): void {

    this.anioSelector = anio;

    this.mesSelector = new Date(anio, this.mesSelector.getMonth(), 1);

    this.tipoSelector = 'meses';
  }

  seleccionarMes(mes:number): void {

    this.mesSelector = new Date(this.anioSelector, mes, 1);

    this.tipoSelector = 'dia';

    this.generarCalendarioMes();
  }

  seleccionarDia(fecha:Date): void {

    this.fechaSemana = new Date(fecha);

    this.generarSemana();

    this.mostrarSelectorFecha = false;

    this.cargarTurnos();

    this.cargarBloqueos();
  }

  obtenerTurnos(dia: Date, franja: string): Turno[] {

    const [hora, minuto] = franja.split(':').map(Number);

    const clave = this.generarClave(dia, hora, minuto);

    return this.turnosPorFranja.get(clave) ?? [];
  }

  semanaSiguiente(): void {

    this.fechaSemana.setDate(this.fechaSemana.getDate() + 7);

    this.fechaSemana = new Date(this.fechaSemana);

    this.generarSemana();

    this.cargarTurnos();

    this.cargarBloqueos();
  }

  semanaAnterior(): void {

    this.fechaSemana.setDate(this.fechaSemana.getDate() - 7);

    this.fechaSemana = new Date(this.fechaSemana);

    this.generarSemana();

    this.cargarTurnos();

    this.cargarBloqueos();
  }

  irAHoy(): void {

    this.fechaSemana = new Date();

    this.generarSemana();

    this.cargarTurnos();

    this.cargarBloqueos();
  }

  obtenerMesActual(): string {
    return `${this.meses[this.fechaSemana.getMonth()]} ${this.fechaSemana.getFullYear()}`;
  }

  esHoy(dia: Date): boolean {

    const hoy = new Date();

    return(
      dia.getDate() === hoy.getDate() &&
      dia.getMonth() === hoy.getMonth() &&
      dia.getFullYear() === hoy.getFullYear()
    );
  }

  private ordenarPorNombreYApellido<T extends { nombre: string; apellido: string }>(lista: T[]): T[] {

    return [...lista].sort((a, b) => {

      const comparacionNombre = a.nombre.localeCompare(b.nombre, 'es', {
        sensitivity: 'base'
      });

      if(comparacionNombre !== 0){
        return comparacionNombre;
      }

      return a.apellido.localeCompare(b.apellido, 'es', {
        sensitivity: 'base'
      });
    });
  }

  cargarEmpleados(): void {

    this.empleadoService.empleadosActivos().subscribe({

      next: (data) => {

        this.empleados = this.ordenarPorNombreYApellido(data);

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al cargar los empleados activos:', error);

        this.empleados = [];

        this.cdr.detectChanges();
      }
    });
  }

  cargarClientes(): void {

    this.clienteService.listarTodos().subscribe({

      next: (data) => {

        this.clientes = this.ordenarPorNombreYApellido(data);

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al cargar los clientes:', error);

        this.clientes = [];

        this.cdr.detectChanges();
      }
    });
  }

  cargarServicios(): void {

    this.servicioService.listarActivos().subscribe({

      next: (data) => {

        this.servicios = [...data].sort((a, b) => a.nombre.localeCompare(b.nombre, 'es', {sensitivity: 'base'}));

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al cargar los servicios activos:', error);

        this.servicios = [];

        this.cdr.detectChanges();
      }
    });
  }

  tieneTurnos(dia: Date, hora: string): boolean {
    return this.obtenerTurnos(dia, hora).length > 0;
  }

  obtenerDuracion(turno: Turno): number {

    const inicio = new Date(turno.fechaHoraInicio).getTime();

    const fin = new Date(turno.fechaHoraFin).getTime();

    return(fin - inicio) / 60000;
  }

  obtenerAlturaTurno(turno: Turno): number {
    return this.obtenerDuracion(turno) / 15;
  }

  obtenerHoraInicio(turno: Turno): string {

    const fecha = new Date(turno.fechaHoraInicio);

    return fecha.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit'});
  }

  obtenerHoraFin(turno: Turno): string {

    const fecha = new Date(turno.fechaHoraFin);

    return fecha.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit'});
  }

  obtenerHorario(turno: Turno): string {
    return `${this.obtenerHoraInicio(turno)} - ${this.obtenerHoraFin(turno)}`;
  }

  obtenerNombreCliente(turno: Turno): string {
    return `${turno.nombreCliente} ${turno.apellidoCliente}`;
  }

  obtenerNombreEmpleado(turno: Turno): string {
    return `${turno.nombreEmpleado} ${turno.apellidoEmpleado}`;
  }
  
  obtenerClaseEstado(turno: Turno): string {
    return this.clasesEstado[turno.estadoTurno] ?? 'turno-vacio';
  }

  empiezaEnFranja(turno: Turno, franja: string): boolean {

    const fecha = new Date(turno.fechaHoraInicio);

    const [hora, minuto] = franja.split(':').map(Number);

    return(
      fecha.getHours() === hora &&
      fecha.getMinutes() === minuto
    );
  }

  continuaEnFranja(turno: Turno, franja: string): boolean {

    const [hora, minuto] = franja.split(':').map(Number);

    const fechaInicio = new Date(turno.fechaHoraInicio);

    const fechaFin = new Date(turno.fechaHoraFin);

    const franjaActual = new Date(fechaInicio);

    franjaActual.setHours(hora, minuto, 0, 0);

    return franjaActual >= fechaInicio && franjaActual < fechaFin;
  }

  obtenerServicioPrincipal(turno: Turno): string {

    if(!turno.servicios || turno.servicios.length === 0){
      return '';
    }
    
    return turno.servicios[0].nombre;
  }

  tieneMasServicios(turno: Turno): boolean {
    return turno.servicios.length > 1;
  }

  obtenerServicios(turno: Turno): string {
    return turno.servicios.map(servicio => servicio.nombre).join(', ');
  }

  abrirDetalle(turno: Turno): void {

    this.turnoSeleccionado = turno;

    this.restablecerFormularioReprogramacion();

    this.mensajeReprogramacion = '';
  }

  cerrarDetalle(): void {

    if (this.enviandoPropuestaReprogramacion) {
      return;
    }

    this.turnoSeleccionado = null;

    this.restablecerFormularioReprogramacion();

    this.mensajeReprogramacion = '';
  }


  //========================================
  //Verificar si un turno puede gestionarse
  //========================================
  puedeGestionarTurno(turno: Turno): boolean {

    const estadoValido =
      turno.estadoTurno === EstadoTurno.PENDIENTE ||
      turno.estadoTurno === EstadoTurno.CONFIRMADO;

    const fechaInicio = new Date(turno.fechaHoraInicio);

    return estadoValido &&
      !Number.isNaN(fechaInicio.getTime()) &&
      fechaInicio.getTime() > Date.now();
  }


  //========================================
  //Confirmar, rechazar o cancelar
  //un turno desde ADMIN
  //========================================
  ejecutarAccionTurnoAdmin(
    accion: 'confirmar' | 'rechazar' | 'cancelar'
  ): void {

    const turno = this.turnoSeleccionado;

    if (
      !turno ||
      this.procesandoAccionTurno ||
      this.enviandoPropuestaReprogramacion ||
      !this.puedeGestionarTurno(turno)
    ) {
      return;
    }

    if (
      accion !== 'cancelar' &&
      turno.estadoTurno !== EstadoTurno.PENDIENTE
    ) {
      return;
    }

    if (accion === 'rechazar' || accion === 'cancelar') {

      const mensaje = accion === 'rechazar'
        ? '¿Querés rechazar este turno?'
        : '¿Querés cancelar este turno?';

      if (!window.confirm(mensaje)) {
        return;
      }
    }

    this.procesandoAccionTurno = true;
    this.errorAccionTurno = '';
    this.mensajeAccionTurno = '';

    const solicitud =
      accion === 'confirmar'
        ? this.turnoService.confirmarTurnoAdmin(turno.id)
        : accion === 'rechazar'
          ? this.turnoService.rechazarTurnoAdmin(turno.id)
          : this.turnoService.cancelarTurnoAdmin(turno.id);

    solicitud.subscribe({

      next: (turnoActualizado) => {

        this.procesandoAccionTurno = false;

        this.turnoSeleccionado = turnoActualizado;

        this.mensajeAccionTurno =
          accion === 'confirmar'
            ? 'El turno fue confirmado correctamente.'
            : accion === 'rechazar'
              ? 'El turno fue rechazado correctamente.'
              : 'El turno fue cancelado correctamente.';

        this.cargarTurnos();

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(
          'Error al gestionar el turno:',
          error
        );

        this.procesandoAccionTurno = false;

        this.errorAccionTurno =
          error?.error?.mensaje ||
          error?.error?.message ||
          'No se pudo realizar la operación.';

        this.cdr.detectChanges();
      }
    });
  }



  //========================================
  //Comprobar si el turno permite
  //proponer una reprogramación
  //========================================
  puedeReprogramarTurno(turno: Turno): boolean {

    const estadoValido =
      turno.estadoTurno === EstadoTurno.PENDIENTE ||
      turno.estadoTurno === EstadoTurno.CONFIRMADO;

    if (!estadoValido) {
      return false;
    }

    const inicioActual = new Date(
      turno.fechaHoraInicio
    );

    return (
      !Number.isNaN(inicioActual.getTime()) &&
      inicioActual.getTime() > Date.now()
    );
  }


  //========================================
  //Abrir formulario de reprogramación
  //========================================
  abrirFormularioReprogramacion(): void {

    if (
      !this.turnoSeleccionado ||
      !this.puedeReprogramarTurno(this.turnoSeleccionado)
    ) {
      return;
    }

    this.restablecerFormularioReprogramacion();

    this.mensajeReprogramacion = '';

    this.mostrarFormularioReprogramacion = true;
  }


  //========================================
  //Cerrar formulario de reprogramación
  //========================================
  cerrarFormularioReprogramacion(): void {

    if (this.enviandoPropuestaReprogramacion) {
      return;
    }

    this.restablecerFormularioReprogramacion();
  }


  //========================================
  //Restablecer formulario
  //========================================
  private restablecerFormularioReprogramacion(): void {

    this.mostrarFormularioReprogramacion = false;

    this.fechaReprogramacion = '';

    this.horaReprogramacion = '';

    this.motivoReprogramacion = '';

    this.errorReprogramacion = '';
  }


  //========================================
  //Validar formulario
  //========================================
  formularioReprogramacionValido(): boolean {

    const turno = this.turnoSeleccionado;

    if (
      !turno ||
      this.enviandoPropuestaReprogramacion ||
      !this.puedeReprogramarTurno(turno) ||
      !this.fechaReprogramacion ||
      !this.horaReprogramacion
    ) {
      return false;
    }

    const motivo = this.motivoReprogramacion.trim();

    if (
      motivo.length === 0 ||
      motivo.length > 500
    ) {
      return false;
    }

    const nuevaFechaHora = this.obtenerNuevaFechaHora();

    const nuevoInicio = new Date(nuevaFechaHora);

    const inicioOriginal = new Date(
      turno.fechaHoraInicio
    );

    if (
      Number.isNaN(nuevoInicio.getTime()) ||
      nuevoInicio.getTime() <= Date.now()
    ) {
      return false;
    }

    if (
      nuevoInicio.getTime() === inicioOriginal.getTime()
    ) {
      return false;
    }

    return true;
  }


  //========================================
  //Construir fecha para Spring Boot
  //========================================
  private obtenerNuevaFechaHora(): string {

    const hora = this.horaReprogramacion.substring(0, 5);

    return `${this.fechaReprogramacion}T${hora}:00`;
  }


  //========================================
  //Enviar propuesta de reprogramación
  //========================================
  proponerReprogramacion(): void {

    if (
      !this.formularioReprogramacionValido() ||
      !this.turnoSeleccionado
    ) {
      this.errorReprogramacion =
        'Verificá la nueva fecha, la hora y el motivo.';

      return;
    }

    const idTurno = this.turnoSeleccionado.id;

    const datos: ReprogramarTurno = {

      nuevaFechaHoraInicio: this.obtenerNuevaFechaHora(),

      motivo: this.motivoReprogramacion.trim()

    };

    this.enviandoPropuestaReprogramacion = true;

    this.errorReprogramacion = '';

    this.mensajeReprogramacion = '';

    this.turnoService
      .proponerReprogramacionAdmin(idTurno, datos)
      .subscribe({

        next: () => {

          this.enviandoPropuestaReprogramacion = false;

          this.restablecerFormularioReprogramacion();

          this.mensajeReprogramacion =
            'La propuesta de reprogramación fue enviada correctamente. ' +
            'El turno conservará su horario original hasta que la clienta acepte.';

          this.cargarTurnos();

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error al proponer reprogramación:',
            error
          );

          this.enviandoPropuestaReprogramacion = false;

          this.errorReprogramacion =
            this.obtenerErrorReprogramacion(
              error
            );

          this.cdr.detectChanges();
        }
      });
  }


  //========================================
  //Obtener mensaje de error
  //========================================
  private obtenerErrorReprogramacion(error: any): string {

    const respuesta = error?.error;

    if (
      typeof respuesta?.mensaje === 'string' &&
      respuesta.mensaje.trim()
    ) {
      return respuesta.mensaje;
    }

    if (
      typeof respuesta?.message === 'string' &&
      respuesta.message.trim()
    ) {
      return respuesta.message;
    }

    return 'No se pudo enviar la propuesta de reprogramación.';
  }


  mostrarEstado(turno: Turno): void {
    this.hoverTurno = turno;
  }

  ocultarEstado(): void {
    this.hoverTurno = null;
  }

  obtenerPrecioTotal(turno: Turno): string {
    return `$${turno.precioTotal ?? 0}`;
  }

  obtenerEstado(turno: Turno): string {
    return turno.estadoTurno.replace('_', ' ');
  }

  esMismoMes(dia: Date): boolean {
    return(dia.getMonth() === this.mesSelector.getMonth() && dia.getFullYear() === this.mesSelector.getFullYear());
  }

  obtenerFechaCompleta(turno: Turno): string {
    return new Date(turno.fechaHoraInicio).toLocaleDateString('es-AR', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  }

  hayTurnos(): boolean {
    return this.turnosFiltrados.length > 0;
  }

  abrirFiltros(): void {
    this.mostrarFiltros = true;
  }

  aplicarFiltros(): void {

    const texto = this.textoBusqueda.trim().toLowerCase();

    this.turnosFiltrados = this.turnos.filter(turno => {

      if(this.filtros.empleado !== null && turno.idEmpleado !== this.filtros.empleado){
        return false;
      }

      if(this.filtros.cliente !== null && turno.idCliente !== this.filtros.cliente){
        return false;
      }

      if(this.filtros.servicio !== null && !turno.servicios.some(servicio => servicio.id === this.filtros.servicio)){
        return false;
      }

      if(this.filtros.categoria !== null && !turno.servicios.some(servicio => servicio.idCategoria === this.filtros.categoria)){
        return false;
      }

      if(this.filtros.estado !== null && turno.estadoTurno !== this.filtros.estado){
        return false;
      }

      if(!this.filtros.mostrarCancelados && turno.estadoTurno === EstadoTurno.CANCELADO){
        return false;
      }

      if(texto){

        const coincideBusqueda = this.obtenerNombreCliente(turno).toLowerCase().includes(texto) || this.obtenerNombreEmpleado(turno).toLowerCase().includes(texto) || this.obtenerServicios(turno).toLowerCase().includes(texto);

        if(!coincideBusqueda){
          return false;
        }
      }

      return true;
    });

    this.indexarTurnosFiltrados();
  }

  private indexarTurnosFiltrados(): void {
    
    this.turnosPorFranja.clear();

    this.turnosFiltrados.forEach(turno => {

      const fecha = new Date(turno.fechaHoraInicio);

      const clave = this.generarClave(fecha, fecha.getHours(), fecha.getMinutes());

      if(!this.turnosPorFranja.has(clave)){
        this.turnosPorFranja.set(clave, []);
      }

      this.turnosPorFranja.get(clave)?.push(turno);
    });
  }

  limpiarFiltros(): void {

    this.filtros = {
      empleado: null,
      cliente: null,
      servicio: null,
      categoria: null,
      estado: null,
      mostrarCancelados: true
    };

    this.aplicarFiltros();
  }

  cerrarFiltros(): void {
    this.mostrarFiltros = false;
  }

  buscarTurnos(): void {
    this.aplicarFiltros();
  }

  abrirNuevoTurno(): void {

    this.nuevoTurno = {
      idCliente: 0,
      idEmpleado: 0,
      serviciosIds: [],
      fechaHoraInicio: ''
    };

    this.fechaNuevoTurno = '';

    this.horaNuevoTurno = '';

    this.duracionTotalFormulario = 0;

    this.horariosDisponibles = [];

    this.clientes = [];

    this.empleados = [];

    this.servicios = [];

    this.asignacionesReserva = [];

    this.errorDisponibilidadReserva = '';

    this.errorReserva = '';

    this.creandoReserva = false;

    this.mostrarFormularioNuevoTurno = true;

    this.agregarServicioReserva();

    this.cargarClientes();

    this.cargarServicios();
  }

  agregarServicioReserva(): void {

    this.asignacionesReserva.push({

      idLocal: this.siguienteIdAsignacion++,

      idServicio: 0,

      idEmpleado: 0,

      fechaHoraInicio: '',

      empleadosDisponibles: [],

      cargandoDisponibilidad: false,

      numeroConsulta: 0
    });
  }

  quitarServicioReserva(idLocal: number): void {

    if(this.asignacionesReserva.length <= 1){
      return;
    }

    this.asignacionesReserva = this.asignacionesReserva.filter(asignacion => asignacion.idLocal !== idLocal);
  }

  cambiarServicioReserva(asignacion: AsignacionReserva): void {

    asignacion.idEmpleado = 0;

    asignacion.fechaHoraInicio = '';

    asignacion.empleadosDisponibles = [];

    this.consultarEmpleadosReserva(asignacion);
  }

  cambiarFechaReserva(): void {

    this.errorDisponibilidadReserva = '';

    this.asignacionesReserva.forEach(asignacion => {

      asignacion.idEmpleado = 0;

      asignacion.fechaHoraInicio = '';

      asignacion.empleadosDisponibles = [];

      this.consultarEmpleadosReserva(asignacion);
    });
  }

  consultarEmpleadosReserva(asignacion: AsignacionReserva): void {

    const numeroConsulta = ++asignacion.numeroConsulta;

    asignacion.empleadosDisponibles = [];

    asignacion.cargandoDisponibilidad = false;

    if(!asignacion.idServicio || !this.fechaNuevoTurno){
      return;
    }

    this.errorDisponibilidadReserva = '';

    asignacion.cargandoDisponibilidad = true;

    this.agendaService.obtenerEmpleadosDisponibles([asignacion.idServicio], this.fechaNuevoTurno).subscribe({

      next: (empleados) => {

        if(numeroConsulta !== asignacion.numeroConsulta){
          return;
        }

        asignacion.empleadosDisponibles = this.ordenarPorNombreYApellido(empleados);

        asignacion.cargandoDisponibilidad = false;

        this.cdr.detectChanges();
      },

      error: (error) => {

        if(numeroConsulta !== asignacion.numeroConsulta){
          return;
        }

        console.error('Error al consultar empleados disponibles:', error);

        asignacion.empleadosDisponibles = [];

        asignacion.cargandoDisponibilidad = false;

        this.errorDisponibilidadReserva =
        'No se pudo consultar la disponibilidad de los empleados.';

        this.cdr.detectChanges();
      }
    });
  }

  cambiarEmpleadoReserva(asignacion: AsignacionReserva): void {

    asignacion.fechaHoraInicio = '';
  }

  obtenerHorariosAsignacion(asignacion: AsignacionReserva): string[] {

    const empleado = asignacion.empleadosDisponibles.find(empleado => empleado.idEmpleado === asignacion.idEmpleado);

    if(!empleado){
      return [];
    }

    const servicio = this.servicios.find(servicio => servicio.id === asignacion.idServicio);

    if(!servicio){
      return [];
    }

    const duracion = servicio.duracionMinutos;

    return empleado.horariosDisponibles.filter(horario => {

      const inicio = new Date(horario).getTime();

      const fin = inicio + duracion * 60000;

      return this.asignacionesReserva.every(otraAsignacion => {

        if(otraAsignacion.idLocal === asignacion.idLocal || !otraAsignacion.fechaHoraInicio){
          return true;
        }

        const otroServicio = this.servicios.find(servicio => servicio.id === otraAsignacion.idServicio);

        if(!otroServicio){
          return true;
        }

        const otroInicio = new Date(otraAsignacion.fechaHoraInicio).getTime();

        const otroFin = otroInicio + otroServicio.  duracionMinutos * 60000;

        return fin <= otroInicio || inicio >= otroFin;

      });

    }).sort();
  }

  cerrarNuevoTurno(): void {
    
    if(this.creandoReserva){
      return;
    }

    this.mostrarFormularioNuevoTurno = false;
  }

  abrirServicios(): void {
    this.router.navigate(['/admin/servicios']);
  }

  abrirCategorias(): void {
    this.router.navigate(['/admin/categorias']);
  }

  seleccionarServicio(idServicio: number, evento: Event): void {

    const input = evento.target as HTMLInputElement;

    if(input.checked){

      if(!this.nuevoTurno.serviciosIds.includes(idServicio)){
        this.nuevoTurno.serviciosIds.push(idServicio);
      }
    }else{
      this.nuevoTurno.serviciosIds = this.nuevoTurno.serviciosIds.filter(id => id !== idServicio);
    }

    this.calcularDuracionTotalFormulario();

    this.cargarHorariosDisponibles();
  }

  calcularDuracionTotalFormulario(): void {

    if(this.nuevoTurno.serviciosIds.length === 0){

      this.duracionTotalFormulario = 0;

      return;
    }

    this.agendaService.calcularDuracionTotal(this.nuevoTurno.serviciosIds).subscribe({

      next: (duracion) => {
        this.duracionTotalFormulario = duracion;

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al calcular la duracion total:', error);

        this.duracionTotalFormulario = 0;

        this.cdr.detectChanges();
      }
    });
  }

  servicioYaSeleccionado(idServicio: number, idLocal: number): boolean {

    return this.asignacionesReserva.some(asignacion => asignacion.idLocal !== idLocal && asignacion.idServicio === idServicio);
  }

  obtenerDuracionTotalReserva(): number {

    return this.asignacionesReserva.reduce((total, asignacion) => {

      const servicio = this.servicios.find(servicio => servicio.id === asignacion.idServicio);

      return total + (servicio?.duracionMinutos ?? 0);

    }, 0);
  }

  obtenerPrecioTotalFormulario(): number {
    return this.asignacionesReserva.reduce((total, asignacion) => {

      const servicio = this.servicios.find(servicio => servicio.id === asignacion.idServicio);

      return total + Number(servicio?.precio ?? 0);

    }, 0);
  }

  private haySuperposicionesReserva(): boolean {

  for(let i = 0; i < this.asignacionesReserva.length; i++){

    const actual = this.asignacionesReserva[i];

    const servicioActual = this.servicios.find(
      servicio => servicio.id === actual.idServicio
    );

    if(!servicioActual || !actual.fechaHoraInicio){
      continue;
    }

    const inicioActual = new Date(actual.fechaHoraInicio).getTime();

    const finActual = inicioActual + servicioActual.duracionMinutos * 60000;

    for(let j = i + 1; j < this.asignacionesReserva.length; j++){

      const siguiente = this.asignacionesReserva[j];

      const servicioSiguiente = this.servicios.find(
        servicio => servicio.id === siguiente.idServicio
      );

      if(!servicioSiguiente || !siguiente.fechaHoraInicio){
        continue;
      }

      const inicioSiguiente = new Date(
        siguiente.fechaHoraInicio
      ).getTime();

      const finSiguiente =
        inicioSiguiente + servicioSiguiente.duracionMinutos * 60000;

      if(inicioActual < finSiguiente && finActual > inicioSiguiente){
        return true;
      }
    }
  }

  return false;
}

  formularioNuevoTurnoValido(): boolean {

    if(
    this.creandoReserva ||
    this.nuevoTurno.idCliente <= 0 ||
    !this.fechaNuevoTurno ||
    this.asignacionesReserva.length === 0
  ){
    return false;
  }

  const idsServicios = this.asignacionesReserva.map(
    asignacion => asignacion.idServicio
  );

  if(new Set(idsServicios).size !== idsServicios.length){
    return false;
  }

  const asignacionesValidas = this.asignacionesReserva.every(asignacion => {

    if(
      asignacion.idServicio <= 0 ||
      asignacion.idEmpleado <= 0 ||
      !asignacion.fechaHoraInicio ||
      asignacion.cargandoDisponibilidad
    ){
      return false;
    }

    if(!asignacion.fechaHoraInicio.startsWith(`${this.fechaNuevoTurno}T`)){
      return false;
    }

    const empleado = asignacion.empleadosDisponibles.find(
      empleado => empleado.idEmpleado === asignacion.idEmpleado
    );

    return !!empleado &&
      empleado.horariosDisponibles.includes(asignacion.fechaHoraInicio);
  });

  return asignacionesValidas && !this.haySuperposicionesReserva();
  }


  //========================================
  //Explicar por qué no se puede crear
  //la reserva desde ADMIN
  //========================================
  obtenerMotivoBloqueoReserva(): string {

    if (this.creandoReserva) {
      return 'La reserva se está procesando.';
    }

    if (this.nuevoTurno.idCliente <= 0) {
      return 'Seleccioná una clienta.';
    }

    if (!this.fechaNuevoTurno) {
      return 'Seleccioná la fecha de la reserva.';
    }

    if (this.asignacionesReserva.length === 0) {
      return 'Agregá al menos un servicio.';
    }

    const idsServicios = this.asignacionesReserva.map(
      asignacion => asignacion.idServicio
    );

    if (new Set(idsServicios).size !== idsServicios.length) {
      return 'No podés seleccionar el mismo servicio dos veces.';
    }

    for (const asignacion of this.asignacionesReserva) {

      if (asignacion.idServicio <= 0) {
        return 'Seleccioná un servicio en cada sección.';
      }

      if (asignacion.cargandoDisponibilidad) {
        return 'Se está consultando la disponibilidad de las empleadas.';
      }

      if (asignacion.idEmpleado <= 0) {
        return 'Seleccioná una empleada para cada servicio.';
      }

      if (!asignacion.fechaHoraInicio) {
        return 'Seleccioná un horario disponible para cada servicio.';
      }

      if (
        !asignacion.fechaHoraInicio.startsWith(
          `${this.fechaNuevoTurno}T`
        )
      ) {
        return 'Uno de los horarios no corresponde a la fecha seleccionada.';
      }

      const empleado = asignacion.empleadosDisponibles.find(
        empleado => empleado.idEmpleado === asignacion.idEmpleado
      );

      if (!empleado) {
        return 'La empleada seleccionada ya no figura entre las disponibles.';
      }

      if (
        !empleado.horariosDisponibles.includes(
          asignacion.fechaHoraInicio
        )
      ) {
        return 'El horario seleccionado ya no figura entre los disponibles.';
      }
    }

    if (this.haySuperposicionesReserva()) {
      return 'Los horarios de los servicios se superponen.';
    }

    return '';
  }


  cargarHorariosDisponibles(): void {

    this.horaNuevoTurno = '';

    if(!this.nuevoTurno.idEmpleado || this.nuevoTurno.serviciosIds.length === 0 || !this.fechaNuevoTurno){

      this.horariosDisponibles = [];

      return;
    }

    this.horariosDisponibles = [];

    this.agendaService.obtenerDisponibilidad(this.nuevoTurno.idEmpleado, this.nuevoTurno.serviciosIds, this.fechaNuevoTurno).subscribe({

      next: (horarios) => {

        this.horariosDisponibles = [...new Set(
          horarios.map(horario => {

            const hora = horario.includes('T') ? horario.split('T')[1] : horario;

            return hora.substring(0, 5);
          })
        )].sort();

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al cargar horarios disponibles:', error);

        this.horariosDisponibles = [];

        this.cdr.detectChanges();
      }
    });
  }

  crearNuevoTurno(): void {

    if(!this.formularioNuevoTurnoValido()){
    return;
  }

  const reserva: CrearReserva = {

    idCliente: this.nuevoTurno.idCliente,

    turnos: this.asignacionesReserva.map(asignacion => ({

      idEmpleado: asignacion.idEmpleado,

      serviciosIds: [asignacion.idServicio],

      fechaHoraInicio: asignacion.fechaHoraInicio
    }))
  };

  this.creandoReserva = true;

  this.errorReserva = '';

  this.reservaService.crearReserva(reserva).subscribe({

    next: (idReserva) => {

      console.log('Reserva creada correctamente:', idReserva);

      this.creandoReserva = false;

      this.cerrarNuevoTurno();

      this.cargarTurnos();

      this.cdr.detectChanges();
    },

    error: (error) => {

      console.error('Error al crear la reserva:', error);

      this.creandoReserva = false;

      this.errorReserva =
        typeof error.error?.message === 'string'
          ? error.error.message
          : 'No se pudo crear la reserva. Verificá los datos seleccionados.';

      this.cdr.detectChanges();
    }
  });
  }
}