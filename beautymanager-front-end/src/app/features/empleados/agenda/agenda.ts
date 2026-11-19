import { Component, OnInit, ChangeDetectorRef, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { forkJoin, Observable, of, catchError } from 'rxjs';

import { TurnoService } from '../../../core/services/turno';
import { HorarioEmpleadoService } from '../../../core/services/horario-empleado';

import { Turno } from '../../../core/models/turno';
import { HorarioEmpleado } from '../../../core/models/horario-empleado';
import { EstadoTurno } from '../../../core/enums/estadoTurno';
import { ReprogramarTurno } from '../../../core/models/reprogramar-turno';
import { FormularioTratamiento } from '../formulario-tratamiento/formulario-tratamiento';
import { RegistrarTratamiento } from '../../../core/models/registrar-tratamiento';
import { HistorialTratamientoService } from '../../../core/services/historial-tratamiento';
import { ObtenerServicio } from '../../../core/models/obtener-servicio';
import { ServicioService } from '../../../core/services/servicio';

type AccionTurno =
    | 'aceptar'
    | 'rechazar'
    | 'iniciar'
    | 'finalizar'
    | 'ausente';


@Component({
  selector: 'app-agenda-empleado',
  imports: [CommonModule, FormsModule, MatIconModule, FormularioTratamiento],
  templateUrl: './agenda.html',
  styleUrl: './agenda.css',
})
export class AgendaEmpleado implements OnInit {

    turnos: Turno[] = [];

    horariosEmpleado: HorarioEmpleado[] = [];

    serviciosDisponibles: ObtenerServicio[] = [];

    errorServicios: string = '';

    turnosFiltrados: Turno[] = [];

    turnosPorFranja: Map<string, Turno[]> = new Map();

    cargandoAgenda: boolean = false;

    errorCarga: string = '';


    //========================================
    //CALENDARIO
    //========================================

    vista: 'dia' | 'semana' = 'semana';

    fechaReferencia: Date = new Date();

    fechaSeleccionadaTexto: string = '';

    diasSemana: Date[] = [];

    horas: string[] = [];

    readonly alturaFranja: number = 36;

    readonly meses: string[] = [
        'Enero', 'Febrero', 'Marzo',
        'Abril', 'Mayo', 'Junio',
        'Julio', 'Agosto', 'Septiembre',
        'Octubre', 'Noviembre', 'Diciembre'
    ];


    //========================================
    //ESTADOS Y FILTROS
    //========================================

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
    };

    textoBusqueda: string = '';

    filtroEstado: EstadoTurno | null = null;

    mostrarCanceladosYRechazados: boolean = true;

    mostrarFiltros: boolean = false;


    //========================================
    //DETALLE Y ACCIONES
    //========================================

    turnoSeleccionado: Turno | null = null;

    accionPendiente: AccionTurno | null = null;

    ejecutandoAccion: boolean = false;

    mensajeAccion: string = '';

    errorAccion: string = '';

    historialPendiente: boolean = false;

    mostrarFormularioTratamiento: boolean = false;

    enviandoTratamiento: boolean = false;

    errorTratamiento: string = '';

    mostrarFormularioReprogramacion: boolean = false;

    fechaReprogramacion: string = '';

    horaReprogramacion: string = '';

    motivoReprogramacion: string = '';

    enviandoReprogramacion: boolean = false;

    errorReprogramacion: string = '';

    mensajeReprogramacion: string = '';

    errorHorarios: string = '';


    constructor(
        private turnoService: TurnoService,
        private horarioEmpleadoService: HorarioEmpleadoService, private historialTratamientoService: HistorialTratamientoService, private servicioService: ServicioService,
        private cdr: ChangeDetectorRef
    ) {}


    //========================================
    //INICIALIZACIÓN
    //========================================

    ngOnInit(): void {

        this.fechaSeleccionadaTexto =
            this.formatearFecha(this.fechaReferencia);

        this.generarSemana();

        this.cargarDatos();
    }


    //========================================
    //CARGAR TURNOS Y HORARIOS
    //========================================

    cargarDatos(): void {

        this.cargandoAgenda = true;

        this.errorCarga = '';

        this.errorHorarios = '';

        this.errorServicios = '';

        const solicitudTurnos =
            this.turnoService.obtenerMisTurnosEmpleado().pipe(

                catchError(error => {

                    console.error(
                        'Error al obtener los turnos de la empleada:',
                        error
                    );

                    this.errorCarga =
                        'No se pudieron cargar tus turnos. ' +
                        'Intentá nuevamente.';

                    return of([] as Turno[]);
                })
            );

        const solicitudHorarios =
            this.horarioEmpleadoService.obtenerMisHorarios().pipe(

                catchError(error => {

                    console.error(
                        'Error al obtener los horarios laborales:',
                        error
                    );

                    this.errorHorarios =
                        'No se pudieron cargar tus horarios laborales.';

                    return of([] as HorarioEmpleado[]);
                })
            );

            const solicitudServicios =
            this.servicioService.listarActivos().pipe(

                catchError(error => {

                    console.error(
                        'Error al obtener los servicios activos:',
                        error
                    );

                    this.errorServicios =
                        'No se pudo cargar el catálogo de servicios. ' +
                        'Intentá actualizar la agenda.';

                    return of([] as ObtenerServicio[]);
                })
            );

        forkJoin({

            turnos: solicitudTurnos,

            horarios: solicitudHorarios,

            servicios: solicitudServicios

        }).subscribe({

            next: ({ turnos, horarios, servicios }) => {

                //====================================
                //TURNOS
                //====================================

                this.turnos = [...turnos].sort(
                    (a, b) =>
                        new Date(a.fechaHoraInicio).getTime() -
                        new Date(b.fechaHoraInicio).getTime()
                );

                console.log(
                    'Turnos recibidos para la empleada:',
                    this.turnos
                );

                //====================================
                //HORARIOS LABORALES
                //====================================

                this.horariosEmpleado = [...horarios].sort(
                    (a, b) =>
                        a.diaSemana - b.diaSemana ||
                        a.horaInicio.localeCompare(b.horaInicio)
                );

                console.log(
                    'Horarios laborales recibidos:',
                    this.horariosEmpleado
                );

                this.serviciosDisponibles = [...servicios].sort(
                    (a, b) =>
                        a.nombre.localeCompare(
                            b.nombre,
                            'es'
                        )
                );

                console.log(
                    'Servicios activos disponibles:',
                    this.serviciosDisponibles
                );

                //====================================
                //ACTUALIZAR TURNO SELECCIONADO
                //====================================

                if (this.turnoSeleccionado) {

                    const id = this.turnoSeleccionado.id;

                    this.turnoSeleccionado =
                        this.turnos.find(
                            turno => turno.id === id
                        ) ?? null;
                }

                //====================================
                //ACTUALIZAR CALENDARIO
                //====================================

                this.actualizarCalendario();

                console.log(
                    'Turnos visibles en el período:',
                    this.turnosFiltrados
                );

                console.log(
                    'Fechas visibles:',
                    this.diasVisibles.map(
                        dia => this.formatearFecha(dia)
                    )
                );

                this.cargandoAgenda = false;

                this.cdr.detectChanges();
            }

        });
    }


    //========================================
    //GENERAR SEMANA
    //========================================

    generarSemana(): void {

        this.diasSemana = [];

        const fecha = new Date(this.fechaReferencia);

        fecha.setHours(0, 0, 0, 0);

        const diaActual = fecha.getDay();

        const diferencia = diaActual === 0
            ? -6
            : 1 - diaActual;

        fecha.setDate(fecha.getDate() + diferencia);

        for (let i = 0; i < 7; i++) {

            const dia = new Date(fecha);

            dia.setDate(fecha.getDate() + i);

            this.diasSemana.push(dia);
        }
    }


    //========================================
    //DÍAS VISIBLES
    //========================================

    get diasVisibles(): Date[] {

        if (this.vista === 'dia') {
            return [this.fechaReferencia];
        }

        return this.diasSemana;
    }


    //========================================
    //CAMBIAR VISTA
    //========================================

    cambiarVista(vista: 'dia' | 'semana'): void {

        this.vista = vista;

        this.actualizarCalendario();
    }


    //========================================
    //NAVEGACIÓN
    //========================================

    periodoAnterior(): void {

        const fecha = new Date(this.fechaReferencia);

        fecha.setDate(
            fecha.getDate() - (this.vista === 'dia' ? 1 : 7)
        );

        this.cambiarFecha(fecha);
    }

    periodoSiguiente(): void {

        const fecha = new Date(this.fechaReferencia);

        fecha.setDate(
            fecha.getDate() + (this.vista === 'dia' ? 1 : 7)
        );

        this.cambiarFecha(fecha);
    }

    irAHoy(): void {

        this.cambiarFecha(new Date());
    }

    cambiarFecha(fecha: Date): void {

        this.fechaReferencia = new Date(fecha);

        this.fechaSeleccionadaTexto =
            this.formatearFecha(this.fechaReferencia);

        this.generarSemana();

        this.actualizarCalendario();
    }

    seleccionarFecha(): void {

        if (!this.fechaSeleccionadaTexto) {
            return;
        }

        const [anio, mes, dia] =
            this.fechaSeleccionadaTexto.split('-').map(Number);

        const fecha = new Date(anio, mes - 1, dia);

        if (
            fecha.getFullYear() !== anio ||
            fecha.getMonth() !== mes - 1 ||
            fecha.getDate() !== dia
        ) {
            return;
        }

        this.cambiarFecha(fecha);
    }


    //========================================
    //FECHAS
    //========================================

    private formatearFecha(fecha: Date): string {

        const anio = fecha.getFullYear();

        const mes = String(fecha.getMonth() + 1).padStart(2, '0');

        const dia = String(fecha.getDate()).padStart(2, '0');

        return `${anio}-${mes}-${dia}`;
    }

    obtenerMesActual(): string {

        return `${this.meses[this.fechaReferencia.getMonth()]} ${this.fechaReferencia.getFullYear()}`;
    }

    esHoy(dia: Date): boolean {

        const hoy = new Date();

        return (
            dia.getDate() === hoy.getDate() &&
            dia.getMonth() === hoy.getMonth() &&
            dia.getFullYear() === hoy.getFullYear()
        );
    }

    private obtenerNumeroDia(fecha: Date): number {

        const dia = fecha.getDay();

        return dia === 0 ? 7 : dia;
    }


    //========================================
    //HORARIOS LABORALES
    //========================================

    obtenerHorariosDia(dia: Date): HorarioEmpleado[] {

        const numeroDia = this.obtenerNumeroDia(dia);

        return this.horariosEmpleado.filter(
            horario => horario.diaSemana === numeroDia
        );
    }

    esDiaLaboral(dia: Date): boolean {

        return this.obtenerHorariosDia(dia).length > 0;
    }

    private convertirAMinutos(hora: string): number {

        const [horas, minutos] = hora.split(':').map(Number);

        return horas * 60 + minutos;
    }

    private formatearMinutos(minutos: number): string {

        const hora = Math.floor(minutos / 60);

        const minuto = minutos % 60;

        return (
            `${String(hora).padStart(2, '0')}:` +
            `${String(minuto).padStart(2, '0')}`
        );
    }

    esFranjaLaboral(dia: Date, franja: string): boolean {

        const minuto = this.convertirAMinutos(franja);

        return this.obtenerHorariosDia(dia).some(horario => {

            const inicio =
                this.convertirAMinutos(horario.horaInicio);

            const fin =
                this.convertirAMinutos(horario.horaFin);

            return minuto >= inicio && minuto < fin;
        });
    }

    obtenerTextoHorarioDia(dia: Date): string {

        const horarios = this.obtenerHorariosDia(dia);

        if (horarios.length === 0) {
            return 'Día sin jornada laboral';
        }

        return horarios
            .map(horario =>
                `${horario.horaInicio.substring(0, 5)} - ${horario.horaFin.substring(0, 5)}`
            )
            .join(' / ');
    }


    //========================================
    //GENERAR HORAS DINÁMICAS
    //========================================

    generarHorario(): void {

        this.horas = [];

        const dias = this.diasVisibles;

        const horarios = dias.flatMap(
            dia => this.obtenerHorariosDia(dia)
        );

        const turnos = this.turnos.filter(turno => {

            const fecha = new Date(turno.fechaHoraInicio);

            return dias.some(dia =>
                this.formatearFecha(dia) === this.formatearFecha(fecha)
            );
        });

        const inicios: number[] = [];

        const finales: number[] = [];

        //Rango de las jornadas laborales.
        horarios.forEach(horario => {

            inicios.push(
                this.convertirAMinutos(horario.horaInicio)
            );

            finales.push(
                this.convertirAMinutos(horario.horaFin)
            );
        });

        //Incluir turnos fuera de la jornada habitual.
        turnos.forEach(turno => {

            const inicio = new Date(turno.fechaHoraInicio);

            const fin = new Date(turno.fechaHoraFin);

            inicios.push(
                inicio.getHours() * 60 + inicio.getMinutes()
            );

            finales.push(
                fin.getHours() * 60 + fin.getMinutes()
            );
        });

        if (inicios.length === 0 || finales.length === 0) {
            return;
        }

        let inicio = Math.floor(
            Math.min(...inicios) / 15
        ) * 15;

        let fin = Math.ceil(
            Math.max(...finales) / 15
        ) * 15;

        inicio = Math.max(0, inicio);

        fin = Math.min(1440, fin);

        for (let minuto = inicio; minuto < fin; minuto += 15) {

            this.horas.push(
                this.formatearMinutos(minuto)
            );
        }
    }


    //========================================
    //ACTUALIZAR CALENDARIO
    //========================================

    actualizarCalendario(): void {

        this.generarHorario();

        this.aplicarFiltros();
    }


    //========================================
    //FILTROS
    //========================================

    aplicarFiltros(): void {

        const texto = this.textoBusqueda
            .trim()
            .toLowerCase();

        const fechasVisibles = new Set(
            this.diasVisibles.map(dia => this.formatearFecha(dia))
        );

        this.turnosFiltrados = this.turnos.filter(turno => {

            const fecha = this.formatearFecha(
                new Date(turno.fechaHoraInicio)
            );

            if (!fechasVisibles.has(fecha)) {
                return false;
            }

            if (
                this.filtroEstado !== null &&
                turno.estadoTurno !== this.filtroEstado
            ) {
                return false;
            }

            if (
                !this.mostrarCanceladosYRechazados &&
                (
                    turno.estadoTurno === EstadoTurno.CANCELADO ||
                    turno.estadoTurno === EstadoTurno.RECHAZADO
                )
            ) {
                return false;
            }

            if (texto) {

                const cliente =
                    this.obtenerNombreCliente(turno).toLowerCase();

                const servicios =
                    this.obtenerServicios(turno).toLowerCase();

                if (
                    !cliente.includes(texto) &&
                    !servicios.includes(texto)
                ) {
                    return false;
                }
            }

            return true;
        });

        this.indexarTurnos();
    }

    limpiarFiltros(): void {

        this.filtroEstado = null;

        this.textoBusqueda = '';

        this.mostrarCanceladosYRechazados = true;

        this.aplicarFiltros();
    }


    //========================================
    //INDEXAR TURNOS
    //========================================

    private generarClave(fecha: Date, hora: string): string {

        return `${this.formatearFecha(fecha)}_${hora}`;
    }

    private indexarTurnos(): void {

        this.turnosPorFranja.clear();

        this.turnosFiltrados.forEach(turno => {

            const fecha = new Date(turno.fechaHoraInicio);

            const minutosTotales =
                fecha.getHours() * 60 + fecha.getMinutes();

            const inicioFranja =
                Math.floor(minutosTotales / 15) * 15;

            const hora = this.formatearMinutos(inicioFranja);

            const clave = this.generarClave(fecha, hora);

            if (!this.turnosPorFranja.has(clave)) {
                this.turnosPorFranja.set(clave, []);
            }

            this.turnosPorFranja.get(clave)?.push(turno);
        });
    }

    obtenerTurnos(dia: Date, franja: string): Turno[] {

        return this.turnosPorFranja.get(
            this.generarClave(dia, franja)
        ) ?? [];
    }


    //========================================
    //INFORMACIÓN DE LOS TURNOS
    //========================================

    obtenerNombreCliente(turno: Turno): string {

        return `${turno.nombreCliente} ${turno.apellidoCliente}`;
    }

    obtenerServicios(turno: Turno): string {

        return turno.servicios
            .map(servicio => servicio.nombre)
            .join(', ');
    }

    obtenerServicioPrincipal(turno: Turno): string {

        return turno.servicios[0]?.nombre ?? 'Sin servicio';
    }

    obtenerHorario(turno: Turno): string {

        const inicio = new Date(turno.fechaHoraInicio);

        const fin = new Date(turno.fechaHoraFin);

        const formato = (fecha: Date) =>
            fecha.toLocaleTimeString('es-AR', {
                hour: '2-digit',
                minute: '2-digit',
                hour12: false
            });

        return `${formato(inicio)} - ${formato(fin)}`;
    }

    obtenerDuracion(turno: Turno): number {

        const inicio = new Date(turno.fechaHoraInicio).getTime();

        const fin = new Date(turno.fechaHoraFin).getTime();

        return Math.max(0, (fin - inicio) / 60000);
    }

    obtenerAlturaTurno(turno: Turno): number {

        return Math.max(
            30,
            (this.obtenerDuracion(turno) / 15) *
                this.alturaFranja - 4
        );
    }

    obtenerDesfaseInicio(turno: Turno): number {

        const fecha = new Date(turno.fechaHoraInicio);

        return (
            (fecha.getMinutes() % 15) / 15 *
            this.alturaFranja
        ) + 2;
    }

    obtenerClaseEstado(turno: Turno): string {

        return this.clasesEstado[turno.estadoTurno];
    }

    obtenerEstado(turno: Turno): string {

        return turno.estadoTurno.replace('_', ' ');
    }

    obtenerFechaCompleta(turno: Turno): string {

        return new Date(turno.fechaHoraInicio).toLocaleDateString(
            'es-AR',
            {
                weekday: 'long',
                day: 'numeric',
                month: 'long',
                year: 'numeric'
            }
        );
    }

    obtenerCantidadTurnos(): number {

        return this.turnosFiltrados.length;
    }

    obtenerCantidadPendientes(): number {

        return this.turnosFiltrados.filter(
            turno => turno.estadoTurno === EstadoTurno.PENDIENTE
        ).length;
    }


    
    //========================================
    //ABRIR DETALLE
    //========================================
    abrirDetalle(turno: Turno): void {

        if (
            this.ejecutandoAccion ||
            this.enviandoReprogramacion
        ) {
            return;
        }

        this.turnoSeleccionado = turno;

        this.accionPendiente = null;

        this.errorAccion = '';

        this.mensajeAccion = '';

        this.historialPendiente = false;

        this.mostrarFormularioTratamiento = false;

        this.restablecerFormularioReprogramacion();

        this.mensajeReprogramacion = '';
    }


    //========================================
    //CERRAR DETALLE
    //========================================
    cerrarDetalle(): void {

        if (this.ejecutandoAccion || this.enviandoReprogramacion || this.enviandoTratamiento) {
            return;
        }

        this.turnoSeleccionado = null;

        this.accionPendiente = null;

        this.historialPendiente = false;

        this.mostrarFormularioTratamiento = false;

        this.restablecerFormularioReprogramacion();

        this.mensajeReprogramacion = '';
    }



    //========================================
    //VALIDACIONES DE ACCIONES
    //========================================

    puedeAceptar(turno: Turno): boolean {

        return turno.estadoTurno === EstadoTurno.PENDIENTE;
    }

    puedeRechazar(turno: Turno): boolean {

        return turno.estadoTurno === EstadoTurno.PENDIENTE;
    }

    puedeIniciar(turno: Turno): boolean {

        if (turno.estadoTurno !== EstadoTurno.CONFIRMADO) {
            return false;
        }

        const ahora = Date.now();

        const inicio = new Date(turno.fechaHoraInicio).getTime();

        const fin = new Date(turno.fechaHoraFin).getTime();

        return ahora >= inicio && ahora <= fin;
    }

    puedeFinalizar(turno: Turno): boolean {

        return turno.estadoTurno === EstadoTurno.EN_CURSO;
    }

    puedeMarcarAusente(turno: Turno): boolean {

        if (turno.estadoTurno !== EstadoTurno.CONFIRMADO) {
            return false;
        }

        return Date.now() >=
            new Date(turno.fechaHoraInicio).getTime();
    }

    puedeReprogramar(turno: Turno): boolean {

        const estadoValido =
            turno.estadoTurno === EstadoTurno.PENDIENTE ||
            turno.estadoTurno === EstadoTurno.CONFIRMADO;

        if (!estadoValido) {
            return false;
        }

        const inicio = new Date(
            turno.fechaHoraInicio
        ).getTime();

        return (
            Number.isFinite(inicio) &&
            inicio > Date.now()
        );
    }


    //========================================
    //ABRIR FORMULARIO DE REPROGRAMACIÓN
    //========================================
    abrirFormularioReprogramacion(): void {

        if (
            !this.turnoSeleccionado ||
            !this.puedeReprogramar(this.turnoSeleccionado) ||
            this.ejecutandoAccion ||
            this.enviandoReprogramacion ||
            this.accionPendiente !== null
        ) {
            return;
        }

        this.restablecerFormularioReprogramacion();

        this.mensajeReprogramacion = '';

        this.mostrarFormularioReprogramacion = true;
    }


    //========================================
    //CERRAR FORMULARIO
    //========================================
    cerrarFormularioReprogramacion(): void {

        if (this.enviandoReprogramacion) {
            return;
        }

        this.restablecerFormularioReprogramacion();
    }


    //========================================
    //RESTABLECER FORMULARIO
    //========================================
    private restablecerFormularioReprogramacion(): void {

        this.mostrarFormularioReprogramacion = false;

        this.fechaReprogramacion = '';

        this.horaReprogramacion = '';

        this.motivoReprogramacion = '';

        this.errorReprogramacion = '';
    }


    //========================================
    //OBTENER NUEVA FECHA Y HORA
    //========================================
    private obtenerNuevaFechaHora(): string {

        return (
            `${this.fechaReprogramacion}T` +
            `${this.horaReprogramacion.substring(0, 5)}:00`
        );
    }


    //========================================
    //VALIDAR FORMULARIO
    //========================================
    formularioReprogramacionValido(): boolean {

        const turno = this.turnoSeleccionado;

        if (
            !turno ||
            !this.mostrarFormularioReprogramacion ||
            !this.puedeReprogramar(turno) ||
            this.ejecutandoAccion ||
            this.enviandoReprogramacion ||
            !this.fechaReprogramacion ||
            !this.horaReprogramacion
        ) {
            return false;
        }

        const motivo = this.motivoReprogramacion.trim();

        if (
            motivo.length < 1 ||
            motivo.length > 500
        ) {
            return false;
        }

        const nuevoInicio = new Date(
            this.obtenerNuevaFechaHora()
        ).getTime();

        const inicioActual = new Date(
            turno.fechaHoraInicio
        ).getTime();

        if (
            !Number.isFinite(nuevoInicio) ||
            nuevoInicio <= Date.now()
        ) {
            return false;
        }

        if (nuevoInicio === inicioActual) {
            return false;
        }

        return true;
    }


    //========================================
    //ENVIAR PROPUESTA DE REPROGRAMACIÓN
    //========================================
    proponerReprogramacion(): void {

        if (
            !this.formularioReprogramacionValido() ||
            !this.turnoSeleccionado
        ) {
            this.errorReprogramacion =
                'Verificá la fecha, la hora y el motivo.';

            return;
        }

        const idTurno = this.turnoSeleccionado.id;

        const datos: ReprogramarTurno = {

            nuevaFechaHoraInicio: this.obtenerNuevaFechaHora(),

            motivo: this.motivoReprogramacion.trim()
        };

        this.enviandoReprogramacion = true;

        this.errorReprogramacion = '';

        this.mensajeReprogramacion = '';

        this.turnoService
            .proponerReprogramacionEmpleado(idTurno, datos)
            .subscribe({

                next: () => {

                    this.enviandoReprogramacion = false;

                    this.restablecerFormularioReprogramacion();

                    this.mensajeReprogramacion =
                        'La propuesta fue enviada correctamente. ' +
                        'El turno conservará su horario original ' +
                        'hasta que la clienta acepte el cambio.';

                    this.cargarDatos();

                    this.cdr.detectChanges();
                },

                error: (error) => {

                    console.error(
                        'Error al enviar la propuesta:',
                        error
                    );

                    this.enviandoReprogramacion = false;

                    const respuesta = error?.error;

                    this.errorReprogramacion =
                        respuesta?.mensaje ||
                        respuesta?.message ||
                        'No se pudo enviar la propuesta. ' +
                        'Verificá el horario seleccionado.';

                    this.cdr.detectChanges();
                }
            });
    }



    //========================================
    //CONFIRMAR CAMBIO DE ESTADO
    //========================================

    solicitarAccion(accion: AccionTurno): void {

        if (
    !this.turnoSeleccionado ||
    this.ejecutandoAccion ||
    this.enviandoReprogramacion ||
    this.mostrarFormularioReprogramacion
) {
    return;
}

        const turno = this.turnoSeleccionado;

        const permitida = {

            aceptar: this.puedeAceptar(turno),

            rechazar: this.puedeRechazar(turno),

            iniciar: this.puedeIniciar(turno),

            finalizar: this.puedeFinalizar(turno),

            ausente: this.puedeMarcarAusente(turno)

        }[accion];

        if (!permitida) {

            this.errorAccion =
                'Esta acción no está disponible para el turno.';

            return;
        }

        this.errorAccion = '';

        //====================================
        //Finalizar turno mediante tratamiento
        //====================================

        if (accion === 'finalizar') {

            this.accionPendiente = null;

            this.historialPendiente = false;

            this.errorTratamiento = '';

            this.mostrarFormularioTratamiento = true;

            return;
        }

        //====================================
        //Confirmar las demás acciones
        //====================================

        this.accionPendiente = accion;
    }

    cancelarAccion(): void {

        this.accionPendiente = null;
    }

    cerrarFormularioTratamiento(): void {

        if (this.enviandoTratamiento) {
            return;
        }

        this.mostrarFormularioTratamiento = false;

        this.errorTratamiento = '';

        this.errorAccion = '';
    }

    prepararTratamiento(datos: RegistrarTratamiento): void {

        if (
            !this.turnoSeleccionado ||
            !this.mostrarFormularioTratamiento ||
            this.enviandoTratamiento
        ) {
            return;
        }

        if (
            !this.puedeFinalizar(this.turnoSeleccionado) ||
            datos.idTurno !== this.turnoSeleccionado.id
        ) {
            this.errorTratamiento =
                'El turno seleccionado no es válido para registrar el tratamiento.';

            return;
        }

        //====================================
        //Preparar envío
        //====================================

        this.enviandoTratamiento = true;

        this.errorTratamiento = '';

        this.errorAccion = '';

        this.mensajeAccion = '';

        //====================================
        //Registrar tratamiento
        //====================================

        this.historialTratamientoService
            .registrarTratamiento(datos)
            .subscribe({

                next: () => {

                    //================================
                    //Finalizar estado de envío
                    //================================

                    this.enviandoTratamiento = false;

                    this.mostrarFormularioTratamiento = false;

                    this.historialPendiente = false;

                    this.accionPendiente = null;

                    //================================
                    //Mostrar confirmación
                    //================================

                    this.mensajeAccion =
                        'El tratamiento se registró correctamente ' +
                        'y el turno fue finalizado.';

                    //================================
                    //Actualizar agenda
                    //================================

                    this.cargarDatos();

                    this.cdr.detectChanges();
                },

                error: (error) => {

                    //================================
                    //Registrar error
                    //================================

                    console.error(
                        'Error al registrar el tratamiento:',
                        error
                    );

                    //================================
                    //Restablecer estado
                    //================================

                    this.enviandoTratamiento = false;

                    //================================
                    //Mostrar error del backend
                    //================================

                    this.errorTratamiento =
                        error?.error?.mensaje ??
                        error?.error?.message ??
                        'No se pudo registrar el tratamiento. ' +
                        'Verificá los datos e intentá nuevamente.';

                    this.cdr.detectChanges();
                }
            });
    }

    obtenerTextoAccion(accion: AccionTurno): string {

        const textos: Record<AccionTurno, string> = {
            aceptar: 'aceptar',
            rechazar: 'rechazar',
            iniciar: 'iniciar',
            finalizar: 'finalizar',
            ausente: 'marcar como ausente'
        };

        return textos[accion];
    }


    //========================================
    //EJECUTAR ACCIÓN
    //========================================

    confirmarAccion(): void {

        if (
            !this.turnoSeleccionado ||
            !this.accionPendiente ||
            this.ejecutandoAccion
        ) {
            return;
        }

        const idTurno = this.turnoSeleccionado.id;

        const accion = this.accionPendiente;

        let solicitud: Observable<unknown>;

        switch (accion) {

            case 'aceptar':
                solicitud = this.turnoService.aceptarTurno(idTurno);
                break;

            case 'rechazar':
                solicitud = this.turnoService.rechazarTurno(idTurno);
                break;

            case 'iniciar':
                solicitud = this.turnoService.iniciarTurno(idTurno);
                break;

            case 'finalizar':
                this.accionPendiente = null;
                this.mostrarFormularioTratamiento = true;
                return;

            case 'ausente':
                solicitud = this.turnoService.marcarAusente(idTurno);
                break;
        }

        this.ejecutandoAccion = true;

        this.errorAccion = '';

        solicitud.subscribe({

            next: () => {

                this.ejecutandoAccion = false;

                this.accionPendiente = null;

                this.mensajeAccion =
                    'El estado del turno se actualizó correctamente.';

                this.cargarDatos();

                this.cdr.detectChanges();
            },

            error: (error) => {

                console.error(
                    'Error al modificar el estado del turno:',
                    error
                );

                this.ejecutandoAccion = false;

                this.accionPendiente = null;

                this.errorAccion =
                    error?.error?.mensaje ??
                    error?.error?.message ??
                    'No se pudo actualizar el turno. Intentá nuevamente.';

                this.cdr.detectChanges();
            }

        });
    }


    //========================================
    //MOSTRAR MODAL SOBRE TODO EL LAYOUT
    //========================================

    @ViewChild('dialogDetalle')
    set dialogDetalle(elemento: ElementRef<HTMLDialogElement> | undefined) {

        if (elemento && !elemento.nativeElement.open) {
            elemento.nativeElement.showModal();
        }
    }

    //========================================
    //CONTROLAR CIERRE CON ESCAPE
    //========================================

    manejarCancelacionDialogo(evento: Event): void {

        evento.preventDefault();

        this.cerrarDetalle();
    }

}
