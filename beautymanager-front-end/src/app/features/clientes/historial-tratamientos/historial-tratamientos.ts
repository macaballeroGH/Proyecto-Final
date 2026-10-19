import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HistorialTratamientoService } from '../../../core/services/historial-tratamiento';
import { ObtenerHistorialTratamiento } from '../../../core/models/obtener-historial-tratamiento';

@Component({
  selector: 'app-historial-tratamientos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './historial-tratamientos.html',
  styleUrl: './historial-tratamientos.css',
})
export class HistorialTratamientos implements OnInit {

  tratamientos: ObtenerHistorialTratamiento[] = [];

  tratamientosFiltrados: ObtenerHistorialTratamiento[] = [];

  tratamientoSeleccionado: ObtenerHistorialTratamiento | null = null;

  fechaDesde = '';

  fechaHasta = '';

  categoriaSeleccionada = '';

  cargando = false;

  errorCarga = '';

  constructor(private historialTratamientoService: HistorialTratamientoService) {}

  ngOnInit(): void {
    this.cargarTratamientos();
  }

  cargarTratamientos(): void {

    this.cargando = true;

    this.errorCarga = '';

    this.historialTratamientoService.misTratamientos().subscribe({

      next: (respuesta) => {

        this.tratamientos = [...respuesta].sort((a, b) => new Date(b.fechaHoraTurno).getTime() - new Date(a.fechaHoraTurno).getTime());

        this.aplicarFiltros();

        this.cargando = false;
      },

      error: (error) => {

        console.error('Error al cargar el historial de tratamientos:', error);

        this.errorCarga = 'No se pudo cargar tu historial de tratamientos. Intente nuevamente.';

        this.tratamientos = [];

        this.tratamientosFiltrados = [];

        this.cargando = false;
      },
    });
  }

  get categoriasDisponibles(): string[] {
    return [
      ...new Set(this.tratamientos.flatMap((tratamiento) => (tratamiento.categorias ?? []).filter((categoria) => categoria != null && categoria.trim() !== ''))),
    ].sort((a, b) => a.localeCompare(b));
  }

  aplicarFiltros(): void {

    this.tratamientosFiltrados = this.tratamientos.filter((tratamiento) => {

      const fechaTurno = tratamiento.fechaHoraTurno?.substring(0, 10);

      if(!fechaTurno){
        return false;
      }

      if(this.fechaDesde && fechaTurno < this.fechaDesde){
        return false;
      }

      if(this.categoriaSeleccionada && !(tratamiento.categorias ?? []).includes(this.categoriaSeleccionada)){
        return false;
      }

      return true;
    });
  }

  limpiarFiltros(): void {

    this.fechaDesde = '';

    this.fechaHasta = '';

    this.categoriaSeleccionada = '';

    this.aplicarFiltros();
  }

  verDetalle(tratamiento: ObtenerHistorialTratamiento): void {
    this.tratamientoSeleccionado = tratamiento;
  }

  cerrarDetalle(): void {
    this.tratamientoSeleccionado = null;
  }
}
