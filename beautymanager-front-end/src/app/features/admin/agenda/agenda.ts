import { Component, OnInit } from '@angular/core';
import { Router} from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { TurnoService } from '../../../core/services/turno';
import { Turno } from '../../../core/models/turno';
import { EstadoTurno } from '../../../core/enums/estadoTurno';
import { AgendaService } from '../../../core/services/agenda';
import { BloqueoAgendaService } from '../../../core/services/bloqueo-agenda';
import { BloqueoAgenda } from '../../../core/models/bloqueo-agenda';
import { Empleado } from '../../../core/models/empleado';
import { CrearTurno } from '../../../core/models/crear-turno';
import { ObtenerCliente } from '../../../core/models/obtener-cliente';
import { ObtenerServicio } from '../../../core/models/obtener-servicio';

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

  fechaNuevoTurno: string = '';
  
  horaNuevoTurno: string = '';

  duracionTotalFormulario: number = 0;

  horariosDisponibles: string[] = [];

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

  constructor(private turnoService: TurnoService, private agendaService: AgendaService, private bloqueoAgendaService: BloqueoAgendaService, private router: Router) {}

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
      },

      error: (error) => {

        console.error('Error al cargar los turnos de la agenda:', error);

        this.turnos = [];

        this.turnosFiltrados = [];

        this.turnosPorFranja.clear();
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
      },

      error: (error) => {

        console.error('Error al cargar los bloqueos de la agenda', error);
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
  }

  cerrarDetalle(): void {
    this.turnoSeleccionado = null;
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
    this.mostrarFormularioNuevoTurno = true;
  }

  cerrarNuevoTurno(): void {
    this.mostrarFormularioNuevoTurno = false;
  }

  abrirServicios(): void {
    console.log('Abrir servicios');
  }

  abrirCategorias(): void {
    console.log('Abrir categorias');
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
      },

      error: (error) => {

        console.error('Error al calcular la duracion total:', error);

        this.duracionTotalFormulario = 0;
      }
    });
  }

  obtenerPrecioTotalFormulario(): number {
    return this.servicios.filter(servicio => this.nuevoTurno.serviciosIds.includes(servicio.id)).reduce((total, servicio) => total + (servicio.precio ?? 0), 0);
  }

  formularioNuevoTurnoValido(): boolean {
    return(this.nuevoTurno.idCliente > 0 && this.nuevoTurno.idEmpleado > 0 && this.nuevoTurno.serviciosIds.length > 0 && this.fechaNuevoTurno !== '' && this.horaNuevoTurno !== '');
  }

  cargarHorariosDisponibles(): void {

    if(!this.nuevoTurno.idEmpleado || this.nuevoTurno.serviciosIds.length === 0 || !this.fechaNuevoTurno){

      this.horariosDisponibles = [];

      return;
    }

    this.agendaService.obtenerDisponibilidad(this.nuevoTurno.idEmpleado, this.nuevoTurno.serviciosIds, this.fechaNuevoTurno).subscribe({
      next: (horarios) => {
        this.horariosDisponibles = horarios;
      },
      error: (error) => {
        console.error('Error al cargar horarios disponibles:', error);
        this.horariosDisponibles = []
      }
    });
  }

  crearNuevoTurno(): void {

    if(!this.formularioNuevoTurnoValido()){
      return;
    }

    this.nuevoTurno.fechaHoraInicio = `${this.fechaNuevoTurno}T${this.horaNuevoTurno}:00`;

    this.turnoService.crearTurnoAdmin(this.nuevoTurno).subscribe({
      next: () => {
        this.cerrarNuevoTurno();

        this.cargarTurnos();
      },
      error: (error) => {
        console.error('Error al crear el turno:', error);
      }
    });
  }
}