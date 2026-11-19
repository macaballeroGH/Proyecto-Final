import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { ObtenerServicio } from '../../../core/models/obtener-servicio';
import { ObtenerCategoriaServicio } from '../../../core/models/obtener-categoria-servicio';
import { ObtenerDisponibilidadReserva } from '../../../core/models/obtener-disponibilidad-reserva';
import { ActivatedRoute, Router } from '@angular/router';
import { ServicioService } from '../../../core/services/servicio';
import { CategoriaServicioService } from '../../../core/services/categoria-servicio';
import { AgendaService } from '../../../core/services/agenda';
import { BilleteraService } from '../../../core/services/billetera';
import { ReservaService } from '../../../core/services/reserva';
import { CrearReservaCliente } from '../../../core/models/crear-reserva-cliente';
import { forkJoin } from 'rxjs';


interface GrupoServiciosReserva {
  idCategoria: number;
  nombreCategoria: string;
  servicios: ObtenerServicio[];
}

interface AsignacionServicioCliente {

  idServicio: number;

  idEmpleado: number;

  fechaHoraInicio: string;

  empleadosDisponibles: ObtenerDisponibilidadReserva[];
}

@Component({
  selector: 'app-reserva-turno',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule],
  templateUrl: './reserva-turno.html',
  styleUrl: './reserva-turno.css',
})
export class ReservaTurno implements OnInit {

  fecha: string = '';

  hora: string = '';

  fechaHoraSeleccionada: Date | null = null;

  fechaLegible: string = '';

  errorHorario: string = '';

  //========================
  //Pasos de la reserva
  //========================

  pasoActual: 1 | 2 | 3 = 1;

  //========================
  //Servicios y categorias
  //========================

  servicios: ObtenerServicio[] = [];

  categorias: ObtenerCategoriaServicio[] = [];

  gruposServicios: GrupoServiciosReserva[] = [];

  serviciosSeleccionadosIds: number[] = [];

  cargandoServicios: boolean = false;

  errorCarga: string = '';

  saldoDisponible: number | null = null;

  cargandoSaldo: boolean = false;

  errorSaldo: string = '';

  procesandoPago: boolean = false;

errorPago: string = '';

idReservaConfirmada: number | null = null;

private claveIdempotencia: string | null = null;

  //========================
  //Busqueda
  //========================

  textoBusqueda: string = '';

  //========================
  //Asignacion de empleadas
  //========================

  asignacionesReservaCliente: AsignacionServicioCliente[] = [];

  cargandoEmpleadas: boolean = false;

  errorEmpleadas: string = '';

  constructor(private route: ActivatedRoute, private router: Router, private servicioService: ServicioService, private categoriaServicioService: CategoriaServicioService, private agendaService: AgendaService, private billeteraService: BilleteraService, private reservaService: ReservaService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {

    this.obtenerFechaHora();

    if(!this.errorHorario){
      this.cargarServicios();
    }
  }

  //========================
  //Obtener fecha y hora
  //========================

  private obtenerFechaHora(): void {

    const fecha = this.route.snapshot.queryParamMap.get('fecha');

    const hora = this.route.snapshot.queryParamMap.get('hora');

    if(
      !fecha ||
      !hora ||
      !/^\d{4}-\d{2}-\d{2}$/.test(fecha) ||
      !/^([01]\d|2[0-3]):[0-5]\d$/.test(hora)
    ){
      this.errorHorario = 'No se recibió una fecha y hora válidas desde la agenda.';
      return;
    }

    const [anio, mes, dia] = fecha.split('-').map(Number);

    const [horas, minutos] = hora.split(':').map(Number);

    const fechaHora = new Date(
      anio,
      mes - 1,
      dia,
      horas,
      minutos,
      0,
      0
    );

    //Comprobar que la fecha exista realmente.
    if(
      fechaHora.getFullYear() !== anio ||
      fechaHora.getMonth() !== mes - 1 ||
      fechaHora.getDate() !== dia ||
      fechaHora.getHours() !== horas ||
      fechaHora.getMinutes() !== minutos
    ){
      this.errorHorario = 'La fecha u hora seleccionada no es válida.';
      return;
    }

    const ahora = new Date();

    const minimo = new Date(
      ahora.getTime() + 2 * 60 * 60 * 1000
    );

    const maximo = new Date(
      ahora.getTime() + 3 * 24 * 60 * 60 * 1000
    );

    if(fechaHora < minimo){

      this.errorHorario =
        'Los turnos deben reservarse con al menos dos horas de anticipación.';

      return;
    }

    if(fechaHora > maximo){

      this.errorHorario =
        'Solo se permiten reservas con hasta tres días de anticipación.';

      return;
    }

    this.fecha = fecha;

    this.hora = hora;

    this.fechaHoraSeleccionada = fechaHora;

    this.fechaLegible = new Intl.DateTimeFormat('es-AR', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    }).format(fechaHora);
  }

  //========================
  //Cargar servicios activos
  //========================

  cargarServicios(): void {

    this.cargandoServicios = true;

    this.errorCarga = '';

    forkJoin({
      servicios: this.servicioService.listarActivos(),
      categorias: this.categoriaServicioService.listarActivas()
    }).subscribe({

      next: ({ servicios, categorias }) => {

        this.servicios = [...servicios];

        this.categorias = [...categorias].sort((a, b) =>
          a.nombreCategoria.localeCompare(
            b.nombreCategoria,
            'es',
            { sensitivity: 'base' }
          )
        );

        this.organizarServiciosPorCategoria();

        this.cargandoServicios = false;

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al cargar servicios:', error);

        this.errorCarga =
          'No se pudieron cargar los servicios disponibles.';

        this.servicios = [];

        this.categorias = [];

        this.gruposServicios = [];

        this.cargandoServicios = false;

        this.cdr.detectChanges();
      }
    });
  }

  //========================
  //Organizar por categoria
  //========================

  private organizarServiciosPorCategoria(): void {

    this.gruposServicios = this.categorias.map(categoria => {

      const serviciosCategoria = this.servicios
        .filter(servicio =>
          servicio.idCategoria === categoria.idCategoria
        )
        .sort((a, b) =>
          a.nombre.localeCompare(
            b.nombre,
            'es',
            { sensitivity: 'base' }
          )
        );

      return {
        idCategoria: categoria.idCategoria,
        nombreCategoria: categoria.nombreCategoria,
        servicios: serviciosCategoria
      };

    }).filter(grupo => grupo.servicios.length > 0);
  }

  //========================
  //Filtrar servicios
  //========================

  obtenerServiciosFiltrados(
    grupo: GrupoServiciosReserva
  ): ObtenerServicio[] {

    const texto = this.textoBusqueda
      .trim()
      .toLocaleLowerCase('es');

    if(!texto){
      return grupo.servicios;
    }

    return grupo.servicios.filter(servicio => {

      const nombre = servicio.nombre.toLocaleLowerCase('es');

      const descripcion = (servicio.descripcion ?? '')
        .toLocaleLowerCase('es');

      return nombre.includes(texto) ||
             descripcion.includes(texto) ||
             grupo.nombreCategoria.toLocaleLowerCase('es').includes(texto);
    });
  }

  //========================
  //Seleccionar servicio
  //========================

  alternarServicio(idServicio: number): void {

    if(this.pasoActual !== 1){
      return;
    }

    const existe = this.serviciosSeleccionadosIds.includes(idServicio);

    if(existe){

      this.serviciosSeleccionadosIds =
        this.serviciosSeleccionadosIds.filter(
          id => id !== idServicio
        );

    } else {

      const servicioDisponible = this.gruposServicios.some(
        grupo => grupo.servicios.some(
          servicio => servicio.id === idServicio
        )
      );

      if(!servicioDisponible){
        return;
      }

      this.serviciosSeleccionadosIds.push(idServicio);
    }

    this.reiniciarIntentoPago();
  }

  //========================
  //Comprobar seleccion
  //========================

  estaSeleccionado(idServicio: number): boolean {

    return this.serviciosSeleccionadosIds.includes(idServicio);
  }

  //========================
  //Obtener servicios elegidos
  //========================

  get serviciosSeleccionados(): ObtenerServicio[] {

    return this.serviciosSeleccionadosIds
    .map(id => this.servicios.find(servicio => servicio.id === id))
    .filter((servicio): servicio is ObtenerServicio => servicio !== undefined);
  }

  //========================
  //Cantidad de servicios
  //========================

  get cantidadServicios(): number {

    return this.serviciosSeleccionados.length;
  }

  //========================
  //Duracion total
  //========================

  get duracionTotal(): number {

    return this.serviciosSeleccionados.reduce(
      (total, servicio) =>
        total + Number(servicio.duracionMinutos),
      0
    );
  }

  //========================
  //Precio total
  //========================

  get precioTotal(): number {

    return this.serviciosSeleccionados.reduce(
      (total, servicio) =>
        total + Number(servicio.precio),
      0
    );
  }

  //========================
  //Validar primer paso
  //========================

  puedeContinuar(): boolean {

    return (
      !this.errorHorario &&
      !this.cargandoServicios &&
      !this.errorCarga &&
      this.cantidadServicios > 0
    );
  }

  //========================
  //Continuar
  //========================

  continuar(): void {

    if(!this.puedeContinuar()){
      return;
    }

    this.pasoActual = 2;

    this.cargarEmpleadasDisponibles();
  }

  
  //========================
  //Cargar empleadas disponibles
  //========================

  cargarEmpleadasDisponibles(): void {

    const seleccionados = this.serviciosSeleccionados;

    if(seleccionados.length === 0 || !this.fecha){
      return;
    }

    this.cargandoEmpleadas = true;

    this.errorEmpleadas = '';

    this.asignacionesReservaCliente = [];

    const consultas = seleccionados.map(servicio =>
      this.agendaService.obtenerEmpleadosDisponibles(
      [servicio.id],
      this.fecha
    )
  );

  forkJoin(consultas).subscribe({

    next: (resultados) => {

      this.asignacionesReservaCliente = seleccionados.map(
        (servicio, index) => ({
          idServicio: servicio.id,
          idEmpleado: 0,
          fechaHoraInicio: '',
          empleadosDisponibles: [...resultados[index]].sort((a, b) =>
            a.nombre.localeCompare(b.nombre, 'es', {
              sensitivity: 'base'
            }) || a.apellido.localeCompare(b.apellido, 'es', {
              sensitivity: 'base'
            })
          )
        })
      );

      this.cargandoEmpleadas = false;

      this.cdr.detectChanges();
    },

    error: (error) => {

      console.error('Error al consultar empleadas:', error);

      this.errorEmpleadas =
        'No se pudo consultar la disponibilidad de las empleadas.';

      this.cargandoEmpleadas = false;

      this.cdr.detectChanges();
    }
  });
}


//========================
//Obtener empleadas compatibles
//========================

obtenerEmpleadasParaServicio(
  indice: number
): ObtenerDisponibilidadReserva[] {

  const asignacion = this.asignacionesReservaCliente[indice];

  if(!asignacion){
    return [];
  }

  return asignacion.empleadosDisponibles.filter(empleado =>
    this.obtenerHorariosEmpleado(
      indice,
      empleado.idEmpleado
    ).length > 0
  );
}


//========================
//Obtener horarios de empleada
//========================

obtenerHorariosEmpleado(
  indice: number,
  idEmpleado: number
): string[] {

  const asignacion = this.asignacionesReservaCliente[indice];

  if(!asignacion || idEmpleado <= 0){
    return [];
  }

  const empleado = asignacion.empleadosDisponibles.find(
    item => item.idEmpleado === idEmpleado
  );

  if(!empleado){
    return [];
  }

  const horarios = empleado.horariosDisponibles.filter(
    horario => horario.substring(0, 10) === this.fecha
  );

  //El primer servicio comienza a la hora
  //seleccionada en la agenda.

  if(indice === 0){

    const horarioInicial = `${this.fecha}T${this.hora}`;

    return horarios.filter(
      horario => horario.substring(0, 16) === horarioInicial
    );
  }

  //Los siguientes servicios deben comenzar
  //despues de finalizar el anterior.

  const anterior = this.asignacionesReservaCliente[indice - 1];

  if(!anterior || !anterior.fechaHoraInicio || anterior.idEmpleado <= 0){
    return [];
  }

  const servicioAnterior = this.servicios.find(
    servicio => servicio.id === anterior.idServicio
  );

  if(!servicioAnterior){
    return [];
  }

  const inicioAnterior = new Date(
    anterior.fechaHoraInicio
  ).getTime();

  const finAnterior =
    inicioAnterior + servicioAnterior.duracionMinutos * 60000;

  const minimoAnticipacion = Date.now() + 2 * 60 * 60 * 1000;

  const maximoAnticipacion = Date.now() + 3 * 24 * 60 * 60 * 1000;

  return horarios.filter(horario => {

    const inicio = new Date(horario).getTime();

    return (
      inicio >= finAnterior &&
      inicio >= minimoAnticipacion &&
      inicio <= maximoAnticipacion
    );

  }).sort();
}


//========================
//Cambiar empleada asignada
//========================

cambiarEmpleadoAsignacion(indice: number): void {

  const asignacion = this.asignacionesReservaCliente[indice];

  if(!asignacion){
    return;
  }

  asignacion.fechaHoraInicio = '';

  //Para el primer servicio, el horario
  //ya fue seleccionado en la agenda.

  if(indice === 0 && asignacion.idEmpleado > 0){

    const horarios = this.obtenerHorariosEmpleado(
      indice,
      asignacion.idEmpleado
    );

    asignacion.fechaHoraInicio = horarios[0] ?? '';
  }

  this.limpiarAsignacionesPosteriores(indice);

  this.reiniciarIntentoPago();
}


//========================
//Cambiar horario
//========================

cambiarHorarioAsignacion(indice: number): void {

  this.limpiarAsignacionesPosteriores(indice);

  this.reiniciarIntentoPago();
}


//========================
//Limpiar asignaciones posteriores
//========================

private limpiarAsignacionesPosteriores(indice: number): void {

  for(
    let i = indice + 1;
    i < this.asignacionesReservaCliente.length;
    i++
  ){

    this.asignacionesReservaCliente[i].idEmpleado = 0;

    this.asignacionesReservaCliente[i].fechaHoraInicio = '';
  }
}


//========================
//Obtener nombre de empleada
//========================

obtenerNombreEmpleada(
  asignacion: AsignacionServicioCliente
): string {

  const empleada = asignacion.empleadosDisponibles.find(
    item => item.idEmpleado === asignacion.idEmpleado
  );

  if(!empleada){
    return 'Sin asignar';
  }

  return `${empleada.nombre} ${empleada.apellido}`;
}


//========================
//Validar asignaciones
//========================

asignacionesValidas(): boolean {

  if(
    this.cargandoEmpleadas ||
    this.errorEmpleadas ||
    this.asignacionesReservaCliente.length !== this.cantidadServicios
  ){
    return false;
  }

  return this.asignacionesReservaCliente.every(
    (asignacion, indice) => {

      if(
        asignacion.idEmpleado <= 0 ||
        !asignacion.fechaHoraInicio
      ){
        return false;
      }

      const horarios = this.obtenerHorariosEmpleado(
        indice,
        asignacion.idEmpleado
      );

      return horarios.includes(asignacion.fechaHoraInicio);
    }
  );
}


//========================
//Avanzar a confirmacion
//========================

continuarAConfirmacion(): void {

  if(!this.asignacionesValidas()){
    return;
  }

  this.pasoActual = 3;

  this.cargarSaldoBilletera();
}


//========================
//Consultar saldo disponible
//========================

cargarSaldoBilletera(): void {

  this.cargandoSaldo = true;

  this.errorSaldo = '';

  this.saldoDisponible = null;

  this.billeteraService.consultarSaldo().subscribe({

    next: (saldo) => {

      this.saldoDisponible = saldo;

      this.cargandoSaldo = false;

      this.cdr.detectChanges();
    },

    error: (error) => {

      console.error('Error al consultar saldo:', error);

      this.errorSaldo = 'No se pudo consultar el saldo de tu billetera.';

      this.cargandoSaldo = false;

      this.cdr.detectChanges();
    }
  });
}


  //========================
  //Confirmar reserva y pagar
  //========================

  confirmarYPagar(): void {

    //Evitar solicitudes duplicadas
    if(this.procesandoPago || this.idReservaConfirmada !== null){
      return;
    }

    this.errorPago = '';

    //Validar paso actual
    if(this.pasoActual !== 3){
      return;
    }

    //Validar asignaciones
    if(!this.asignacionesValidas()){

      this.errorPago =
        'Debes completar correctamente la seleccion de empleadas y horarios.';

      return;
    }

    //Validar consulta del saldo
    if(this.cargandoSaldo || this.saldoDisponible === null){

      this.errorPago =
        'No se pudo verificar el saldo de tu billetera.';

      return;
    }

    //Validar saldo suficiente
    if(!this.saldoSuficiente){

      this.errorPago =
        'No tienes saldo suficiente para pagar la reserva.';

      return;
    }

    //========================
    //Preparar turnos
    //========================

    const turnos = this.asignacionesReservaCliente.map(
      asignacion => ({

        idEmpleado: asignacion.idEmpleado,

        serviciosIds: [asignacion.idServicio],

        fechaHoraInicio: asignacion.fechaHoraInicio

      })
    );

    //========================
    //Generar clave unica
    //========================

    //Se genera una sola vez y se conserva
    //para los reintentos de esta operacion.

    if(!this.claveIdempotencia){

      this.claveIdempotencia = crypto.randomUUID();
    }

    //========================
    //Preparar solicitud
    //========================

    const reserva: CrearReservaCliente = {

      turnos: turnos,

      claveIdempotencia: this.claveIdempotencia

    };

    //========================
    //Procesar pago
    //========================

    this.procesandoPago = true;

    this.reservaService.confirmarYPagar(reserva).subscribe({

      next: (idReserva) => {

        this.idReservaConfirmada = idReserva;

        this.procesandoPago = false;

        this.errorPago = '';

        console.log(
          'Reserva confirmada y pagada. ID:',
          idReserva
        );

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(
          'Error al confirmar y pagar la reserva:',
          error
        );

        this.errorPago =
          'No se pudo confirmar el resultado del pago. Podes reintentar sin generar una operacion nueva.';

        this.procesandoPago = false;

        //No eliminamos la clave: el backend
        //podria haber procesado el pago.

        this.cdr.detectChanges();
      }

    });
  }

  //========================
  //Reiniciar intento de pago
  //cuando cambia la reserva
  //========================

  private reiniciarIntentoPago(): void {

    this.claveIdempotencia = null;

    this.errorPago = '';

    this.idReservaConfirmada = null;
  }


//========================
//Comprobar saldo suficiente
//========================

get saldoSuficiente(): boolean {

  return this.saldoDisponible !== null &&
         this.saldoDisponible >= this.precioTotal;
}


//========================
//Calcular saldo restante
//========================

get saldoRestante(): number {

  if(this.saldoDisponible === null){
    return 0;
  }

  return Math.max(
    0,
    this.saldoDisponible - this.precioTotal
  );
}


//========================
//Calcular dinero faltante
//========================

get dineroFaltante(): number {

  if(this.saldoDisponible === null){
    return 0;
  }

  return Math.max(
    0,
    this.precioTotal - this.saldoDisponible
  );
}



//========================
//Volver a empleadas
//========================

volverAEmpleadas(): void {

  this.pasoActual = 2;
}


  //========================
  //Volver a los servicios
  //========================

  volverAServicios(): void {

    this.pasoActual = 1;
  }

  //========================
  //Regresar a la agenda
  //========================

  volverAgenda(): void {

    this.router.navigate(['/cliente/agenda']);
  }
}
