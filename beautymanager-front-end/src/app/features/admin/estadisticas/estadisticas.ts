import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BaseChartDirective } from 'ng2-charts';
import { ChartData, ChartOptions } from 'chart.js';

import { EstadisticasService } from '../../../core/services/estadisticas';
import { FiltoEstadisticas } from '../../../core/models/filtro-estadisticas';
import { ObtenerEstadisticas } from '../../../core/models/obtener-estadisticas';
import { TipoPeriodoEstadistica } from '../../../core/enums/tipoPeriodoEstadistica';
import { ColoresGraficosEstadisticas, ColorIngresosEstadisticas, ColorIngresosFondoEstadisticas, ColorTurnosEstadisticas, ColorSinDatosEstadisticas } from '../../../core/config/estadisticas-graficos';

@Component({
  selector: 'app-estadisticas',
  imports: [FormsModule, BaseChartDirective, DecimalPipe],
  templateUrl: './estadisticas.html',
  styleUrl: './estadisticas.css',
})
export class Estadisticas implements OnInit {

  estadisticas: ObtenerEstadisticas | null = null;

  cargando = false;

  error = false;

  filtro: FiltoEstadisticas = {fechaInicio: '', fechaFin: '', tipoPeriodo: TipoPeriodoEstadistica.MES, comparar: false};

  readonly TipoPeriodoEstadistica = TipoPeriodoEstadistica;

  ingresosChartData: ChartData<'line'> = {labels: [], datasets: [{label: 'Ingresos', data: []}]};

  turnosChartData: ChartData<'bar'> = {labels: [], datasets: [{label: 'Turnos realizados', data: []}]};

  serviciosChartData: ChartData<'doughnut'> = {labels: [], datasets: [{data: [], backgroundColor: ColoresGraficosEstadisticas}]};

  productosChartData: ChartData<'doughnut'> = {labels: [], datasets: [{data: [], backgroundColor: ColoresGraficosEstadisticas}]};

  ingresosChartOptions: ChartOptions<'line'> = {
    responsive: true,

    maintainAspectRatio: false,

    plugins: {

      legend: {

        display: false
      },

      tooltip: {

        callbacks: {

          label: (context) => {

            const valor = context.parsed.y ?? 0;

            return `Ingresos: $${valor.toLocaleString('es-AR')}`;
          }
        }
      }
    }
  };

  turnosChartOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,

    plugins: {
      legend: {
        display: false
      },

      tooltip: {
        callbacks: {
          label: (context) => {
            const valor = context.parsed.y ?? 0;

            return `Turnos: ${valor.toLocaleString('es-AR')}`;
          }
        }
      }
    }
  };

  serviciosChartOptions: ChartOptions<'doughnut'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'bottom',
        labels: {
          filter: (legendItem) => {
            return legendItem.text !== 'Sin datos para este período';
          }
        }
      },
      tooltip: {
        callbacks: {
          label: (context) => {
            if (context.label === 'Sin datos para este período') {
              return 'Sin datos para este período';
            }

            const nombre = context.label ?? '';
            const cantidad = context.parsed ?? 0;

            return `${nombre}: ${cantidad.toLocaleString('es-AR')}`;
          }
        }
      }
    }
  };

  productosChartOptions: ChartOptions<'doughnut'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'bottom',
        labels: {
          filter: (legendItem) => {
            return legendItem.text !== 'Sin datos para este período';
          }
        }
      },
      tooltip: {
        callbacks: {
          label: (context) => {
            if (context.label === 'Sin datos para este período') {
              return 'Sin datos para este período';
            }

            const nombre = context.label ?? '';
            const cantidad = context.parsed ?? 0;

            return `${nombre}: ${cantidad.toLocaleString('es-AR')}`;
          }
        }
      }
    }
  };

  constructor(private estadisticasService: EstadisticasService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.inicializarFechas();
    this.cargarEstadisticas();
  }

  cargarEstadisticas(): void {

    this.cargando = true;
    this.error = false;

    this.estadisticasService
      .obtenerEstadisticas(this.filtro)
      .subscribe({
        next: (respuesta) => {

          console.log('RESPUESTA RECIBIDA:', respuesta);

          console.log('SERVICIOS RECIBIDOS:', respuesta.serviciosMasSolicitados);

          this.estadisticas = respuesta;

          console.log('ANTES DE ACTUALIZAR GRAFICOS');

          this.actualizarDatosGraficos();

          console.log('DESPUES DE ACTUALIZAR GRAFICOS');

          this.cargando = false;

          this.cdr.detectChanges();

          console.log('CARGANDO DESPUES:', this.cargando);
        },

        error: (error) => {

          console.error(
            'Error al obtener estadísticas:',
            error
          );

          this.estadisticas = null;
          this.error = true;
          this.cargando = false;
        }
      });
  }

  aplicarFiltros(): void {

    if (!this.filtro.fechaInicio || !this.filtro.fechaFin) {
      return;
    }

    this.cargarEstadisticas();
  }

  cambiarPeriodo(): void {

    const fechaBase = this.filtro.fechaInicio
      ? this.crearFecha(this.filtro.fechaInicio)
      : new Date();

    switch (this.filtro.tipoPeriodo) {

      case TipoPeriodoEstadistica.SEMANA:
        this.establecerSemana(fechaBase);
        break;

      case TipoPeriodoEstadistica.MES:
        this.establecerMes(fechaBase);
        break;

      case TipoPeriodoEstadistica.ANIO:
        this.establecerAnio(fechaBase);
        break;

      case TipoPeriodoEstadistica.PERSONALIZADO:
        break;
    }
  }

  private establecerSemana(fechaBase: Date): void {

    const diaSemana = fechaBase.getDay();

    const diferencia = diaSemana === 0
      ? -6
      : 1 - diaSemana;

    const fechaInicio = new Date(fechaBase);

    fechaInicio.setDate(
      fechaBase.getDate() + diferencia
    );

    const fechaFin = new Date(fechaInicio);

    fechaFin.setDate(
      fechaInicio.getDate() + 6
    );

    this.filtro.fechaInicio =
      this.formatearFecha(fechaInicio);

    this.filtro.fechaFin =
      this.formatearFecha(fechaFin);
  }

  private establecerMes(fechaBase: Date): void {

    const fechaInicio = new Date(fechaBase.getFullYear(), fechaBase.getMonth(), 1);

    const fechaFin = new Date(fechaBase.getFullYear(), fechaBase.getMonth() + 1, 0);

    this.filtro.fechaInicio = this.formatearFecha(fechaInicio);

    this.filtro.fechaFin = this.formatearFecha(fechaFin);
  }

  private establecerAnio(fechaBase: Date): void {

    const fechaInicio = new Date(fechaBase.getFullYear(), 0, 1);

    const fechaFin = new Date(fechaBase.getFullYear(), 11, 31);

    this.filtro.fechaInicio = this.formatearFecha(fechaInicio);

    this.filtro.fechaFin = this.formatearFecha(fechaFin);
  }

  cambiarPeriodoDesdeFecha(): void {

    if(!this.filtro.fechaInicio){
      return;
    }

    if(this.filtro.tipoPeriodo === TipoPeriodoEstadistica.PERSONALIZADO){
      return;
    }

    const fechaBase = this.crearFecha(this.filtro.fechaInicio);

    switch (this.filtro.tipoPeriodo){

      case TipoPeriodoEstadistica.SEMANA:
        this.establecerSemana(fechaBase);
        break;
      
      case TipoPeriodoEstadistica.MES:
        this.establecerMes(fechaBase);
        break;
      
      case TipoPeriodoEstadistica.ANIO:
        this.establecerAnio(fechaBase);
        break;
    }
  }

  private actualizarDatosGraficos(): void {

    if (!this.estadisticas) {
      this.limpiarDatosGraficos();
      return;
    }

    this.actualizarIngresos();
    this.actualizarTurnos();
    this.actualizarServicios();
    this.actualizarProductos();
  }

  private actualizarIngresos(): void {

    if (!this.estadisticas) {
      return;
    }

    this.ingresosChartData = {
      labels: this.estadisticas.ingresos.map(
        dato => dato.periodo
      ),

      datasets: [
        {
          label: 'Ingresos',

          data: this.estadisticas.ingresos.map(
            dato => dato.valor
          ),

          borderColor: ColorIngresosEstadisticas,

          backgroundColor: ColorIngresosFondoEstadisticas,

          pointBackgroundColor: ColorIngresosEstadisticas,

          pointBorderColor: ColorIngresosEstadisticas,

          tension: 0.3
        }
      ]
    };
  }

  private actualizarTurnos(): void {

    if (!this.estadisticas) {
      return;
    }

    this.turnosChartData = {
      labels: this.estadisticas.turnos.map(
        dato => dato.periodo
      ),

      datasets: [
        {
          label: 'Turnos realizados',
          data: this.estadisticas.turnos.map(
            dato => dato.valor
          ),

          backgroundColor: ColorTurnosEstadisticas,

          borderColor: ColorTurnosEstadisticas
        }
      ]
    };
  }

  private actualizarServicios(): void {

    if(!this.estadisticas) return;

    const servicios = this.estadisticas.serviciosMasSolicitados;

    if(servicios.length === 0){

      this.serviciosChartData = {

        labels: ['Sin datos para este periodo'],

        datasets: [
          {
            data: [1],

            backgroundColor: [ColorSinDatosEstadisticas],

            hoverBackgroundColor: [ColorSinDatosEstadisticas]
          }
        ]
      };

      return;
    }

    this.serviciosChartData  = {

      labels: servicios.map(servicio => servicio.nombre),

      datasets: [
        {
          data: servicios.map(servicio => servicio.cantidad),

          backgroundColor: ColoresGraficosEstadisticas
        }
      ]
    };
  }

  private actualizarProductos(): void {

    if(!this.estadisticas) return;

    const productos = this.estadisticas.productosMasVendidos;

    if(productos.length === 0){

      this.productosChartData = {

        labels: ['Sin datos para este periodo'],

        datasets: [
          {
            data: [1],

            backgroundColor: [ColorSinDatosEstadisticas],

            hoverBackgroundColor: [ColorSinDatosEstadisticas]
          }
        ]
      };
      return;
    }

    this.productosChartData = {

      labels: productos.map(producto => producto.nombre),

      datasets: [
        {
          data: productos.map(producto => producto.cantidad),

          backgroundColor: ColoresGraficosEstadisticas
        }
      ]
    };
  }

  private limpiarDatosGraficos(): void {

    this.ingresosChartData = {labels: [], datasets: [{label: 'Ingresos', data: []}]};

    this.turnosChartData = {labels: [], datasets: [{label: 'Turnos realizados', data: []}]};

    this.serviciosChartData = {labels: [], datasets: [{data: []}]};

    this.productosChartData = {labels: [], datasets: [{data: []}]};
  }

  get hayDatos(): boolean {

    if (!this.estadisticas) {
      return false;
    }

    return (this.estadisticas.ingresos.length > 0 || this.estadisticas.turnos.length > 0 || this.estadisticas.serviciosMasSolicitados.length > 0 || this.estadisticas.productosMasVendidos.length > 0);
  }

  get ingresosTotales(): number {

    if (!this.estadisticas) {
      return 0;
    }

    return this.estadisticas.ingresos.reduce((total, dato) => total + dato.valor, 0);
  }

  get turnosTotales(): number {

    if (!this.estadisticas) {
      return 0;
    }

    return this.estadisticas.turnos.reduce((total, dato) => total + dato.valor, 0);
  }

  get serviciosTotales(): number {

    if (!this.estadisticas) {
      return 0;
    }

    return this.estadisticas.serviciosMasSolicitados.reduce((total, servicio) => total + servicio.cantidad, 0);
  }

  get productosTotales(): number {

    if (!this.estadisticas) {
      return 0;
    }

    return this.estadisticas.productosMasVendidos.reduce((total, producto) => total + producto.cantidad, 0);
  }

  get tieneComparacion(): boolean {
    return this.filtro.comparar;
  }

  get variacionIngresos(): number | null {
    return this.estadisticas ?.comparacionIngresos?.variacion ?? null;
  }

  get variacionTurnos(): number | null {
    return this.estadisticas ?.comparacionTurnos?.variacion ?? null;
  }

  private inicializarFechas(): void {

    const hoy = new Date();

    switch (this.filtro.tipoPeriodo) {

      case TipoPeriodoEstadistica.SEMANA:

        this.filtro.fechaInicio = this.formatearFecha(hoy);

        this.establecerSemana(hoy);
      break;

      case TipoPeriodoEstadistica.MES:

        this.filtro.fechaInicio = this.formatearFecha(hoy);

        this.establecerMes(hoy);
      break;

      case TipoPeriodoEstadistica.ANIO:

        this.filtro.fechaInicio = this.formatearFecha(hoy);

        this.establecerAnio(hoy);
      break;

      case TipoPeriodoEstadistica.PERSONALIZADO:

        this.filtro.fechaInicio = this.formatearFecha(hoy);

        this.filtro.fechaFin = this.formatearFecha(hoy);
      break;
    }
  }

  private crearFecha(fecha: string): Date {

    return new Date(`${fecha}T00:00:00`);
  }

  private formatearFecha(fecha: Date): string {

    const anio = fecha.getFullYear();

    const mes = String(fecha.getMonth() + 1).padStart(2, '0');

    const dia = String(fecha.getDate()).padStart(2, '0');

    return `${anio}-${mes}-${dia}`;
  }
}