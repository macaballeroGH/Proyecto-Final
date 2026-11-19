import { Component, OnInit, ChangeDetectorRef, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AgendaService } from '../../../core/services/agenda';
import { ObtenerReprogramacionTurno } from '../../../core/models/obtener-reprogramacion-turno';
import { TurnoService } from '../../../core/services/turno';
import { Turno } from '../../../core/models/turno';
import { EstadoTurno } from '../../../core/enums/estadoTurno';


@Component({
  selector: 'app-agenda-cliente',
  imports: [CommonModule, FormsModule, MatIconModule],
  templateUrl: './agenda.html',
  styleUrl: './agenda.css',
})
export class AgendaCliente implements OnInit {

  horas: string[] = [];

  diasSemana: Date[] = [];

  fechaInicioSemana: Date = new Date();

  fechaSeleccionada: Date = new Date();

  mostrarSelectorFecha: boolean = false;

  tipoSelector: 'dia' | 'meses' | 'anios' = 'dia';

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

  disponibilidadPorDia: { [fecha: string]: string[] } = {};

  cargandoDisponibilidad: boolean = false;

  errorDisponibilidad: string = '';

  propuestasPendientes: ObtenerReprogramacionTurno[] = [];

  cargandoPropuestas: boolean = false;

  idPropuestaProcesando: number | null = null;

  errorPropuestas: string = '';

  errorAccionPropuesta: string = '';

  mensajePropuesta: string = '';

  turnosPropios: Turno[] = [];

  cargandoTurnos: boolean = false;

  errorTurnos: string = '';

  turnoSeleccionado: Turno | null = null;

  readonly EstadoTurno = EstadoTurno;

  readonly alturaFranja = 48;

  constructor(private agendaService: AgendaService, private turnoService: TurnoService, private router: Router, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {

    this.generarHorario();

    this.inicializarSemana();

    this.generarCalendarioMes();

    this.generarAnios();

    this.cargarDisponibilidadSemana();

    this.cargarPropuestasPendientes();

    this.cargarMisTurnos();
  }


  //========================================
  //Cargar propuestas pendientes
  //========================================
  cargarPropuestasPendientes(): void {

    this.cargandoPropuestas = true;

    this.errorPropuestas = '';

    this.turnoService
      .obtenerReprogramacionesPendientesCliente()
      .subscribe({

        next: (propuestas) => {

          this.propuestasPendientes = [...propuestas];

          this.cargandoPropuestas = false;

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error al cargar propuestas de reprogramación:',
            error
          );

          this.propuestasPendientes = [];

          this.errorPropuestas =
            'No se pudieron cargar las propuestas de reprogramación.';

          this.cargandoPropuestas = false;

          this.cdr.detectChanges();
        }
      });
  }


  //========================================
  //Aceptar propuesta de reprogramación
  //========================================
  aceptarReprogramacion(idReprogramacion: number): void {

    if (this.idPropuestaProcesando !== null) {
      return;
    }

    this.idPropuestaProcesando = idReprogramacion;

    this.errorAccionPropuesta = '';

    this.mensajePropuesta = '';

    this.turnoService
      .aceptarReprogramacion(idReprogramacion)
      .subscribe({

        next: () => {

          this.idPropuestaProcesando = null;

          this.mensajePropuesta =
            'Aceptaste la reprogramación. Tu turno ya tiene el nuevo horario.';

          //Actualizar propuestas y disponibilidad
          this.cargarPropuestasPendientes();

          this.cargarDisponibilidadSemana();

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error al aceptar la reprogramación:',
            error
          );

          this.idPropuestaProcesando = null;

          this.errorAccionPropuesta =
            this.obtenerMensajeErrorReprogramacion(
              error,
              'No se pudo aceptar la reprogramación.'
            );

          this.cdr.detectChanges();
        }
      });
  }


  //========================================
  //Rechazar propuesta de reprogramación
  //========================================
  rechazarReprogramacion(idReprogramacion: number): void {

    if (this.idPropuestaProcesando !== null) {
      return;
    }

    this.idPropuestaProcesando = idReprogramacion;

    this.errorAccionPropuesta = '';

    this.mensajePropuesta = '';

    this.turnoService
      .rechazarReprogramacion(idReprogramacion)
      .subscribe({

        next: () => {

          this.idPropuestaProcesando = null;

          this.mensajePropuesta =
            'Rechazaste la propuesta. Tu turno conserva su horario original.';

          //Actualizar propuestas y disponibilidad
          this.cargarPropuestasPendientes();

          this.cargarDisponibilidadSemana();

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error al rechazar la reprogramación:',
            error
          );

          this.idPropuestaProcesando = null;

          this.errorAccionPropuesta =
            this.obtenerMensajeErrorReprogramacion(
              error,
              'No se pudo rechazar la reprogramación.'
            );

          this.cdr.detectChanges();
        }
      });
  }


  //========================================
  //Obtener mensaje de error del backend
  //========================================
  private obtenerMensajeErrorReprogramacion(
    error: any,
    mensajePredeterminado: string
  ): string {

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

    return mensajePredeterminado;
  }


  generarHorario(): void {

    this.horas = [];

    for (let hora = 8; hora < 20; hora++) {

      for (let minuto = 0; minuto < 60; minuto += 30) {

        const horaTexto = hora.toString().padStart(2, '0');

        const minutoTexto = minuto.toString().padStart(2, '0');

        this.horas.push(`${horaTexto}:${minutoTexto}`);
      }
    }
  }

  inicializarSemana(): void {

    const hoy = new Date();

    hoy.setHours(0, 0, 0, 0);

    this.fechaInicioSemana = new Date(hoy);

    this.generarDiasSemana();

    this.fechaSeleccionada = new Date(hoy);
  }

  private obtenerLunesSemana(fecha: Date): Date {

    const lunes = new Date(fecha);

    lunes.setHours(0, 0, 0, 0);

    const diaSemana = lunes.getDay();

    const diasRetroceder = diaSemana === 0 ? 6 : diaSemana - 1;

    lunes.setDate(lunes.getDate() - diasRetroceder);

    return lunes;
  }

  generarDiasSemana(): void {

    this.diasSemana = [];

    const hoy = new Date();

    hoy.setHours(0, 0, 0, 0);

    const lunesActual = this.obtenerLunesSemana(hoy);

    const lunesMostrado = this.obtenerLunesSemana(this.fechaInicioSemana);

    const esSemanaActual = this.esMismaSemanaActual(this.fechaInicioSemana);

    const inicio = esSemanaActual || lunesMostrado < lunesActual ? new Date(hoy) : new Date(lunesMostrado);

    const lunesReferencia = lunesMostrado < lunesActual ? lunesActual : lunesMostrado;

    const domingo = new Date(lunesReferencia);

    domingo.setDate(domingo.getDate() + 6);

    this.fechaInicioSemana = new Date(inicio);

    for(const dia = new Date(inicio); dia <= domingo; dia.setDate(dia.getDate() + 1)){
      this.diasSemana.push(new Date(dia));
    }
  }

  semanaSiguiente(): void {

    if(this.diasSemana.length === 0){
      return;
    }

    const ultimoDia = this.diasSemana[this.diasSemana.length - 1];

    const siguienteLunes = new Date(ultimoDia);

    siguienteLunes.setDate(siguienteLunes.getDate() + 1);

    siguienteLunes.setHours(0, 0, 0, 0);

    this.fechaInicioSemana = siguienteLunes;

    this.generarDiasSemana();

    this.cargarDisponibilidadSemana();
  }

  semanaAnterior(): void {

    const hoy = new Date();

    hoy.setHours(0, 0, 0, 0);

    const lunesActual = this.obtenerLunesSemana(hoy);

    const lunesMostrado = this.obtenerLunesSemana(this.fechaInicioSemana);

    if(lunesMostrado <= lunesActual){
      return;
    }

    const lunesAnterior = new Date(lunesMostrado);

    lunesAnterior.setDate(lunesAnterior.getDate() - 7);

    this.fechaInicioSemana = lunesAnterior <= lunesActual ? new Date(hoy) : lunesAnterior;

    this.generarDiasSemana();

    this.cargarDisponibilidadSemana();
  }

  irAHoy(): void {

    const hoy = new Date();

    hoy.setHours(0, 0, 0, 0);

    this.fechaInicioSemana = new Date(hoy);

    this.fechaSeleccionada = new Date(hoy);

    this.generarDiasSemana();

    this.cargarDisponibilidadSemana();
  }

  cargarDisponibilidadSemana(): void {

    this.cargandoDisponibilidad = true;
    this.errorDisponibilidad = '';

    this.disponibilidadPorDia = {};

    const diasConsultados = [...this.diasSemana];

    const consultas = diasConsultados.map(dia => {

        const fecha = this.formatearFecha(dia);

        return this.agendaService.obtenerDisponibilidadGeneral(fecha);
    });

    if (consultas.length === 0) {

        this.cargandoDisponibilidad = false;
        return;
    }

    forkJoin(consultas).subscribe({

        next: (resultados) => {

            const nuevaDisponibilidad: {
                [fecha: string]: string[]
            } = {};

            diasConsultados.forEach((dia, index) => {

                const fecha = this.formatearFecha(dia);

                nuevaDisponibilidad[fecha] = resultados[index];
            });

            this.disponibilidadPorDia = nuevaDisponibilidad;

            this.cargandoDisponibilidad = false;

            this.cdr.detectChanges();
        },

        error: (error) => {

            console.error(
                'Error al cargar la disponibilidad:',
                error
            );

            this.disponibilidadPorDia = {};

            this.errorDisponibilidad =
                'No se pudo consultar la disponibilidad. Intentá nuevamente.';

            this.cargandoDisponibilidad = false;

            this.cdr.detectChanges();
        }
    });
  }

  estaDisponible(dia: Date, hora: string): boolean {

    if (this.tengoTurnoEnFranja(dia, hora)) {
        return false;
    }

    const fecha = this.formatearFecha(dia);

    const disponibilidad = this.disponibilidadPorDia[fecha] ?? [];

    const fechaHora = `${fecha}T${hora}:00`;

    return disponibilidad.some(
        fechaDisponible => fechaDisponible === fechaHora
    );
  }

  seleccionarHorario(dia: Date, hora: string): void {

    if (!this.estaDisponible(dia, hora)) {
      return;
    }

    const fecha = this.formatearFecha(dia);

    this.fechaSeleccionada = new Date(dia);

    this.router.navigate(['/cliente/reserva-turno'], {
      queryParams: {
        fecha: fecha,
        hora: hora
      }
    });
  }

  abrirSelectorFecha(): void {

    this.mostrarSelectorFecha = !this.mostrarSelectorFecha;

    if (this.mostrarSelectorFecha) {

      this.tipoSelector = 'dia';

      this.mesSelector = new Date(this.fechaSeleccionada);

      this.anioSelector = this.mesSelector.getFullYear();

      this.generarCalendarioMes();

      this.generarAnios();
    }
  }

  cambiarVistaSelector(): void {

    if (this.tipoSelector === 'dia') {

      this.tipoSelector = 'meses';

    } else if (this.tipoSelector === 'meses') {

      this.tipoSelector = 'anios';

      this.generarAnios();

    } else {

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

    for (let i = diaInicio; i > 0; i--) {

      this.diasMes.push(new Date(anio, mes, 1 - i));
    }

    for (let i = 1; i <= ultimoDia.getDate(); i++) {

      this.diasMes.push(new Date(anio, mes, i));
    }

    while (this.diasMes.length < 42) {

      const ultimo = this.diasMes[this.diasMes.length - 1];

      const siguiente = new Date(ultimo);

      siguiente.setDate(siguiente.getDate() + 1);

      this.diasMes.push(siguiente);
    }
  }

  generarAnios(): void {

    const anioActual = new Date().getFullYear();

    this.aniosDisponibles = [anioActual + 1];
  }

  seleccionarAnio(anio: number): void {

    this.anioSelector = anio;

    this.mesSelector = new Date(anio, this.mesSelector.getMonth(), 1);

    this.tipoSelector = 'meses';
  }

  seleccionarMes(mes: number): void {

    this.mesSelector = new Date(this.anioSelector, mes, 1);

    this.tipoSelector = 'dia';

    this.generarCalendarioMes();
  }

  seleccionarDia(fecha: Date): void {

    if (this.esFechaPasada(fecha)) {
      return;
    }

    this.fechaSeleccionada = new Date(fecha);

    this.mostrarSelectorFecha = false;

    this.fechaInicioSemana = new Date(fecha);

    this.generarDiasSemana();

    this.cargarDisponibilidadSemana();
  }

  obtenerMesActual(): string {

    if (this.diasSemana.length === 0) {
      return '';
    }

    const primerDia = this.diasSemana[0];

    const ultimoDia = this.diasSemana[this.diasSemana.length - 1];

    const mesPrimerDia = primerDia.toLocaleDateString('es-AR', { month: 'long' });

    const mesUltimoDia = ultimoDia.toLocaleDateString('es-AR', { month: 'long' });

    const anioPrimerDia = primerDia.getFullYear();

    const anioUltimoDia = ultimoDia.getFullYear();

    if (mesPrimerDia === mesUltimoDia && anioPrimerDia === anioUltimoDia) {
      return `${mesPrimerDia} ${anioPrimerDia}`;
    }

    if (anioPrimerDia === anioUltimoDia) {
      return `${mesPrimerDia} - ${mesUltimoDia} ${anioPrimerDia}`;
    }

    return `${mesPrimerDia} ${anioPrimerDia} - ${mesUltimoDia} ${anioUltimoDia}`;
  }

  obtenerRangoSemana(): string {

    if (this.diasSemana.length === 0) {
      return '';
    }

    const primerDia = this.diasSemana[0];

    const ultimoDia = this.diasSemana[this.diasSemana.length - 1];

    const diaInicio = primerDia.getDate();

    const diaFin = ultimoDia.getDate();

    const mesInicio = primerDia.toLocaleDateString('es-AR', { month: 'short' });

    const mesFin = ultimoDia.toLocaleDateString('es-AR', { month: 'short' });

    const anioInicio = primerDia.getFullYear();

    const anioFin = ultimoDia.getFullYear();

    if (mesInicio === mesFin && anioInicio === anioFin) {
      return `${diaInicio} - ${diaFin} de ${mesInicio} ${anioInicio}`;
    }

    if (anioInicio === anioFin) {
      return `${diaInicio} de ${mesInicio} - ${diaFin} de ${mesFin} ${anioInicio}`;
    }

    return `${diaInicio} de ${mesInicio} ${anioInicio} - ${diaFin} de ${mesFin} ${anioFin}`;
  }

  formatearFecha(fecha: Date): string {

    const anio = fecha.getFullYear();

    const mes = (fecha.getMonth() + 1).toString().padStart(2, '0');

    const dia = fecha.getDate().toString().padStart(2, '0');

    return `${anio}-${mes}-${dia}`;
  }

  esHoy(fecha: Date): boolean {

    const hoy = new Date();

    return fecha.getFullYear() === hoy.getFullYear() && fecha.getMonth() === hoy.getMonth() && fecha.getDate() === hoy.getDate();
  }

  esFechaSeleccionada(fecha: Date): boolean {

    return fecha.getFullYear() === this.fechaSeleccionada.getFullYear() && fecha.getMonth() === this.fechaSeleccionada.getMonth() && fecha.getDate() === this.fechaSeleccionada.getDate();
  }

  esFechaPasada(fecha: Date): boolean {

    const hoy = new Date();

    hoy.setHours(0, 0, 0, 0);

    const fechaComparar = new Date(fecha);

    fechaComparar.setHours(0, 0, 0, 0);

    return fechaComparar < hoy;
  }

  private esMismaSemanaActual(fecha: Date): boolean {

    const hoy = new Date();

    const lunesActual = this.obtenerLunesSemana(hoy);

    const lunesComparado = this.obtenerLunesSemana(fecha);

    return lunesActual.getTime() === lunesComparado.getTime();
  }


  cargarMisTurnos(): void {

    this.cargandoTurnos = true;
    this.errorTurnos = '';

    this.turnoService.obtenerMisTurnosCliente().subscribe({

      next: (turnos: Turno[]) => {

        this.turnosPropios = [...turnos].sort((a, b) => new Date(a.fechaHoraInicio).getTime() - new Date(b.fechaHoraInicio).getTime());

        this.cargandoTurnos = false;
        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al cargar mis turnos:', error);

        this.errorTurnos = 'No se pudieron cargar tus reservas. Intentá nuevamente.';

        this.cargandoTurnos = false;
        this.cdr.detectChanges();
      }
    });
  }


  //========================================
  //TURNOS VIGENTES EN EL CALENDARIO
  //========================================

  private ocupaAgendaCliente(turno: Turno): boolean {

    return turno.estadoTurno !== EstadoTurno.CANCELADO && turno.estadoTurno !== EstadoTurno.RECHAZADO;
  }


  //========================================
  //TURNOS QUE COMIENZAN EN UNA FRANJA
  //========================================

  obtenerTurnosPropiosEnFranja(dia: Date, hora: string): Turno[] {

    const fecha = this.formatearFecha(dia);

    const [h, m] = hora.split(':').map(Number);

    const minutosFranja = h * 60 + m;

    return this.turnosPropios.filter(turno => {

        if (!this.ocupaAgendaCliente(turno)) {
            return false;
        }

        const inicio = new Date(turno.fechaHoraInicio);

        const minutosInicio =
            inicio.getHours() * 60 + inicio.getMinutes();

        const franjaInicio =
            Math.floor(minutosInicio / 30) * 30;

        return this.formatearFecha(inicio) === fecha &&
               franjaInicio === minutosFranja;
    });
  }


  //========================================
  //SUPERPOSICIÓN CON UNA RESERVA PROPIA
  //========================================

  tengoTurnoEnFranja(dia: Date, hora: string): boolean {

    const fecha = this.formatearFecha(dia);

    const inicioFranja = new Date(`${fecha}T${hora}:00`);

    const finFranja = new Date(
        inicioFranja.getTime() + 30 * 60000
    );

    return this.turnosPropios.some(turno => {

        if (!this.ocupaAgendaCliente(turno)) {
            return false;
        }

        const inicio = new Date(turno.fechaHoraInicio);
        const fin = new Date(turno.fechaHoraFin);

        return inicio < finFranja && fin > inicioFranja;
    });
  }


  //========================================
  //ALTURA Y POSICIÓN DE TARJETA
  //========================================

  obtenerAlturaTarjeta(turno: Turno): number {

    const inicio = new Date(turno.fechaHoraInicio).getTime();
    const fin = new Date(turno.fechaHoraFin).getTime();

    const duracionMinutos = Math.max(
        0,
        (fin - inicio) / 60000
    );

    return Math.max(
        24,
        duracionMinutos / 30 * this.alturaFranja - 3
    );
  }

  obtenerDesfaseTarjeta(turno: Turno): number {

    const inicio = new Date(turno.fechaHoraInicio);

    return (inicio.getMinutes() % 30) / 30 *
        this.alturaFranja;
  }


  //========================================
  //DATOS VISIBLES DEL TURNO
  //========================================

  obtenerHorarioTurno(turno: Turno): string {

    const inicio = new Date(turno.fechaHoraInicio);
    const fin = new Date(turno.fechaHoraFin);

    const opciones: Intl.DateTimeFormatOptions = {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
    };

    return `${inicio.toLocaleTimeString('es-AR', opciones)} - ` +
           `${fin.toLocaleTimeString('es-AR', opciones)}`;
  }

  obtenerNombreEmpleadoTurno(turno: Turno): string {

    return `${turno.nombreEmpleado} ${turno.apellidoEmpleado}`.trim();
  }

  obtenerServiciosTurno(turno: Turno): string {

    return turno.servicios
        .map(servicio => servicio.nombre)
        .join(', ');
  }

  obtenerEstadoTurno(turno: Turno): string {

    return String(turno.estadoTurno).replace(/_/g, ' ');
  }

  obtenerClaseTurno(turno: Turno): string {

    return `turno-propio-${String(turno.estadoTurno).toLowerCase()}`;
  }


  //========================================
  //DETALLE DE TURNO
  //========================================

  abrirDetalleTurnoCliente(turno: Turno): void {
    this.turnoSeleccionado = turno;
  }

  cerrarDetalleTurnoCliente(evento?: Event): void {

    evento?.preventDefault();
    this.turnoSeleccionado = null;
  }

  @ViewChild('dialogTurnoCliente')
  set dialogTurnoCliente(elemento: ElementRef<HTMLDialogElement> | undefined) {

    if (elemento && !elemento.nativeElement.open) {
        elemento.nativeElement.showModal();
    }
  }


}