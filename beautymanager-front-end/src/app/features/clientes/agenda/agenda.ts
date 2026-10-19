import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AgendaService } from '../../../core/services/agenda';


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


  constructor(private agendaService: AgendaService, private router: Router, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {

    this.generarHorario();

    this.inicializarSemana();

    this.generarCalendarioMes();

    this.generarAnios();

    this.cargarDisponibilidadSemana();
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

  generarDiasSemana(): void {

    this.diasSemana = [];

    const inicio = new Date(this.fechaInicioSemana);

    const hoy = new Date();

    hoy.setHours(0, 0, 0, 0);

    const esSemanaActual = this.esMismaSemanaActual(inicio);


    if (esSemanaActual) {

      const diaSemana = inicio.getDay();

      const diasRestantes = 7 - diaSemana;

      for (let i = 0; i < diasRestantes; i++) {

        const dia = new Date(inicio);

        dia.setDate(inicio.getDate() + i);

        this.diasSemana.push(dia);
      }

      return;
    }

    const diaSemana = inicio.getDay();

    const diferenciaLunes = diaSemana === 0 ? -6 : 1 - diaSemana;

    inicio.setDate(inicio.getDate() + diferenciaLunes);

    for (let i = 0; i < 7; i++) {

      const dia = new Date(inicio);

      dia.setDate(inicio.getDate() + i);

      this.diasSemana.push(dia);
    }
  }

  semanaSiguiente(): void {

    const ultimaFecha = this.diasSemana[this.diasSemana.length - 1];

    const siguienteLunes = new Date(ultimaFecha);

    siguienteLunes.setDate(ultimaFecha.getDate() + 1);

    this.fechaInicioSemana = siguienteLunes;

    this.generarDiasSemana();

    this.cargarDisponibilidadSemana();
  }

  semanaAnterior(): void {

    const hoy = new Date();

    hoy.setHours(0, 0, 0, 0);

    if (this.esMismaSemanaActual(this.fechaInicioSemana)) {
      return;
    }

    const inicioAnterior = new Date(this.fechaInicioSemana);

    inicioAnterior.setDate(inicioAnterior.getDate() - 7);

    if (inicioAnterior < hoy) {

      this.fechaInicioSemana = new Date(hoy);

    } else {

      this.fechaInicioSemana = inicioAnterior;
    }

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

    this.disponibilidadPorDia = {};

    const diasConsultados = [...this.diasSemana];

    console.log('FECHAS QUE ESTAN CONSULTANDO:', diasConsultados.map(dia => this.formatearFecha(dia)));

    const consultas = diasConsultados.map(dia => {

      const fecha = this.formatearFecha(dia);

      return this.agendaService.obtenerDisponibilidadGeneral(fecha);
    });

    if(consultas.length === 0){
      return;
    }

    forkJoin(consultas).subscribe({

      next: (resultados) => {

        const nuevaDisponibilidad: { [fecha: string]: string[] } = {};

        diasConsultados.forEach((dia, index) => {

          const fecha = this.formatearFecha(dia);

          nuevaDisponibilidad[fecha] = resultados[index];
        });

        this.disponibilidadPorDia = nuevaDisponibilidad;
        
        this.cdr.detectChanges();

        console.log('DISPONIBILIDAD CARGADA:', this.disponibilidadPorDia);

        console.log('FECHAS MOSTRADAS:', this.diasSemana.map(dia => this.formatearFecha(dia)));

        console.log('DISPONIBILIDAD DEL PRIMER DIA:', this.disponibilidadPorDia[this.formatearFecha(this.diasSemana[0])]);
      },

      error: (error) => {

        console.error('Error al cargar la disponibilidad:', error);

        this.disponibilidadPorDia = {};
      }
    });
  }

  estaDisponible(dia: Date, hora: string): boolean {

    const fecha = this.formatearFecha(dia);
    const disponibilidad = this.disponibilidadPorDia[fecha] ?? [];
    const fechaHora = `${fecha}T${hora}:00`;

    const disponible = disponibilidad.some(fechaDisponible => fechaDisponible === fechaHora);

    if (fecha === '2026-10-12' && hora === '08:00') {
      console.log('COMPROBACIÓN FINAL', {
        fecha,
        hora,
        cantidadHorarios: disponibilidad.length,
        primerHorario: disponibilidad[0],
        fechaHora,
        disponible
      });
    }

    return disponible;
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

  private formatearFecha(fecha: Date): string {

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

    hoy.setHours(0, 0, 0, 0);

    const inicioSemanaActual = new Date(hoy);

    const diaSemana = inicioSemanaActual.getDay();

    if (diaSemana === 0) {

      inicioSemanaActual.setDate(inicioSemanaActual.getDate());

    } else {

      inicioSemanaActual.setDate(inicioSemanaActual.getDate() - (diaSemana - 1));
    }

    const fechaComparar = new Date(fecha);

    fechaComparar.setHours(0, 0, 0, 0);

    return fechaComparar >= inicioSemanaActual && fechaComparar <= hoy;
  }
}