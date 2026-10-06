import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { AgendaService } from '../../../core/services/agenda';


@Component({
  selector: 'app-agenda-cliente',
  imports: [CommonModule, FormsModule, MatIconModule],
  templateUrl: './agenda.html',
  styleUrl: './agenda.css',
})
export class AgendaCliente implements OnInit {

  horas: string[] = [];

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

  disponibilidad: string[] = [];

  constructor(private agendaService: AgendaService) {}

  ngOnInit(): void {
    
    this.generarHorario();

    this.generarCalendarioMes();

    this.generarAnios();

    this.cargarDisponibilidad();
  }

  generarHorario(): void {

    this.horas = [];

    for(let hora = 8; hora <= 20; hora++) {
      
      for(let minuto = 0; minuto < 60; minuto += 30){

        if(hora === 20 && minuto > 0){

          break;
        }

        const horaTexto = hora.toString().padStart(2, '0');

        const minutoTexto = minuto.toString().padStart(2, '0');

        this.horas.push(`${horaTexto}:${minutoTexto}`);
      }
    }
  }

  abrirSelectorFecha(): void {

    this.mostrarSelectorFecha = !this.mostrarSelectorFecha;

    if(this.mostrarSelectorFecha){

      this.tipoSelector = 'dia';

      this.mesSelector = new Date(this.fechaSeleccionada);

      this.anioSelector = this.mesSelector.getFullYear();

      this.generarCalendarioMes();

      this.generarAnios();
    }
  }

  cambiarVistaSelector(): void {

    if(this.tipoSelector === 'dia'){

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

    for(let i = diaInicio; i > 0; i--){

      this.diasMes.push(new Date(anio, mes, 1 - i));
    }

    for (let i = 1; i <= ultimoDia.getDate(); i++) {

      this.diasMes.push(
        new Date(anio, mes, i)
      );
    }

    while (this.diasMes.length < 42) {

      const ultimo =
        this.diasMes[this.diasMes.length - 1];

      const siguiente = new Date(ultimo);

      siguiente.setDate(
        siguiente.getDate() + 1
      );

      this.diasMes.push(siguiente);
    }
  }

  generarAnios(): void {

    const anioActual = new Date().getFullYear();

    this.aniosDisponibles = [
      anioActual + 1
    ];
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

    this.fechaSeleccionada = new Date(fecha);

    this.mostrarSelectorFecha = false;

    this.cargarDisponibilidad();
  }

  
  cargarDisponibilidad(): void {

    const fecha = this.formatearFecha(
      this.fechaSeleccionada
    );

    this.agendaService.obtenerDisponibilidadGeneral(fecha).subscribe({

      next: (data) => {
        this.disponibilidad = data;
      },

      error: (error) => {

        console.error('Error al cargar la disponibilidad:', error);

        this.disponibilidad = [];
      }
    });
  }

  estaDisponible(hora: string): boolean {

    const fechaHora = `${this.formatearFecha(this.fechaSeleccionada)}T${hora}:00`;

    return this.disponibilidad.some(fecha => fecha.startsWith(fechaHora));
  }

  seleccionarHorario(hora: string): void {

    if (!this.estaDisponible(hora)) {
      return;
    }

    console.log('Horario seleccionado:', this.formatearFecha(this.fechaSeleccionada), hora);
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
}