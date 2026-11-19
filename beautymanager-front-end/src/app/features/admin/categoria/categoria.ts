import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';

import { CategoriaServicioService } from '../../../core/services/categoria-servicio';

import { ObtenerCategoriaServicio } from '../../../core/models/obtener-categoria-servicio';

import { CrearCategoriaServicio } from '../../../core/models/crear-categoria-servicio';
import { ActualizarCategoriaServicio } from '../../../core/models/actualizar-categoria-servicio';
import { EstadoCategoriaServicio } from '../../../core/enums/estadoCategoriaServicio';

@Component({
  selector: 'app-categoria',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule],
  templateUrl: './categoria.html',
  styleUrl: './categoria.css',
})
export class CategoriaAdmin implements OnInit {

  categorias: ObtenerCategoriaServicio[] = [];

  categoriasFiltradas: ObtenerCategoriaServicio[] = [];

  cargandoCategorias: boolean = false;

  errorCarga: string = '';

  mensajeExito: string = '';

  readonly estadosCategoria: {
    valor: EstadoCategoriaServicio;
    etiqueta: string;
  }[] = [
    { valor: EstadoCategoriaServicio.ACTIVA, etiqueta: 'Activa' },
    { valor: EstadoCategoriaServicio.INACTIVA, etiqueta: 'Inactiva' },
    { valor: EstadoCategoriaServicio.OCULTA, etiqueta: 'Oculta' },
    { valor: EstadoCategoriaServicio.ARCHIVADA, etiqueta: 'Archivada' }
  ];

  textoBusqueda: string = '';

  estadoFiltro: EstadoCategoriaServicio | '' = '';

  paginaActual: number = 1;

  elementosPorPagina: number = 10;

  mostrarFormulario: boolean = false;

  modoEdicion: boolean = false;

  categoriaEditandoId: number | null = null;

  guardandoCategoria: boolean = false;

  errorFormulario: string = '';

  formularioCategoria: CrearCategoriaServicio = {
    nombreCategoria: '',
    descripcion: '',
    estado: EstadoCategoriaServicio.ACTIVA
  };

  menuAccionesId: number | null = null;

  mostrarConfirmacionEstado: boolean = false;

  categoriaCambioEstado: ObtenerCategoriaServicio | null = null;

  nuevoEstado: EstadoCategoriaServicio = EstadoCategoriaServicio.ACTIVA;

  cambiandoEstado: boolean = false;

  errorEstado: string = '';

  constructor(private categoriaServicioService: CategoriaServicioService, private router: Router, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.cargarCategorias();
  }

  cargarCategorias(): void {

    this.cargandoCategorias = true;

    this.errorCarga = '';

    this.categoriaServicioService.listarTodas().subscribe({

      next: (data) => {

        this.categorias = [...data].sort((a, b) => a.nombreCategoria.localeCompare(b.nombreCategoria, 'es', { sensitivity: 'base' }));

        this.aplicarFiltros();

        this.cargandoCategorias = false;

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al cargar categorias:', error);

        this.errorCarga = 'No se pudieron cargar las categorías.';

        this.categorias = [];

        this.categoriasFiltradas = [];

        this.cargandoCategorias = false;

        this.cdr.detectChanges();
      }

    });
  }

  aplicarFiltros(): void {

    const texto = this.textoBusqueda.trim().toLocaleLowerCase('es');

    this.categoriasFiltradas = this.categorias.filter(categoria => {

      const coincideEstado = this.estadoFiltro === '' || categoria.estado === this.estadoFiltro;

      const nombre = categoria.nombreCategoria.toLocaleLowerCase('es');

      const descripcion = (categoria.descripcion ?? '').toLocaleLowerCase('es');

      const coincideBusqueda = texto === '' || nombre.includes(texto) || descripcion.includes(texto);

      return coincideEstado && coincideBusqueda;
    });

    this.paginaActual = 1;
  }

  filtrarPorEstado(estado: EstadoCategoriaServicio | ''): void {

    this.estadoFiltro = estado;

    this.aplicarFiltros();
  }

  limpiarFiltros(): void {

    this.textoBusqueda = '';

    this.estadoFiltro = '';

    this.aplicarFiltros();
  }

  contarPorEstado(estado: EstadoCategoriaServicio): number {
    return this.categorias.filter(categoria => categoria.estado === estado).length;
  }

  obtenerNombreEstado(estado: EstadoCategoriaServicio): string {
    return this.estadosCategoria.find(item => item.valor === estado)?.etiqueta ?? estado;
  }

  get totalPaginas(): number {
    return Math.ceil(this.categoriasFiltradas.length / this.elementosPorPagina);
  }

  get categoriasPaginadas(): ObtenerCategoriaServicio[] {

    const inicio = (this.paginaActual - 1) * this.elementosPorPagina;

    return this.categoriasFiltradas.slice(inicio, inicio + this.elementosPorPagina);
  }

  cambiarPagina(pagina: number): void {

    if(pagina < 1 || pagina > this.totalPaginas){
      return;
    }

    this.paginaActual = pagina;
  }

  abrirNuevaCategoria(): void {

    this.modoEdicion = false;

    this.categoriaEditandoId = null;

    this.errorFormulario = '';

    this.mensajeExito = '';

    this.menuAccionesId = null;

    this.formularioCategoria = {
      nombreCategoria: '',
      descripcion: '',
      estado: EstadoCategoriaServicio.ACTIVA
    };

    this.mostrarFormulario = true;
  }

  abrirEditarCategoria(categoria: ObtenerCategoriaServicio): void {

    this.modoEdicion = true;

    this.categoriaEditandoId = categoria.idCategoria;

    this.errorFormulario = '';

    this.mensajeExito = '';

    this.menuAccionesId = null;

    this.formularioCategoria = {
      nombreCategoria: categoria.nombreCategoria,
      descripcion: categoria.descripcion ?? '',
      estado: categoria.estado
    };

    this.mostrarFormulario = true;
  }

  cerrarFormulario(): void {

    if(this.guardandoCategoria){
      return;
    }

    this.mostrarFormulario = false;

    this.errorFormulario = '';

    this.categoriaEditandoId = null;
  }

  formularioValido(): boolean {
    return (this.formularioCategoria.nombreCategoria.trim().length > 0 && !this.guardandoCategoria);
  }

  guardarCategoria(): void {

    if(!this.formularioValido()){
      return;
    }

    const datos: CrearCategoriaServicio = {
      nombreCategoria: this.formularioCategoria.nombreCategoria.trim(),
      descripcion: this.formularioCategoria.descripcion.trim(),
      estado: this.formularioCategoria.estado
    };

    this.guardandoCategoria = true;

    this.errorFormulario = '';

    const esEdicion = this.modoEdicion;

    const peticion = esEdicion && this.categoriaEditandoId !== null ? this.categoriaServicioService.actualizarCategoria(this.categoriaEditandoId, datos as ActualizarCategoriaServicio) : this.categoriaServicioService.crearCategoria(datos);

    peticion.subscribe({

      next: () => {

        this.guardandoCategoria = false;

        this.cerrarFormulario();

        this.mensajeExito = esEdicion ? 'La categoría se actualizó correctamente.' : 'La categoría se creó correctamente.';

        this.cargarCategorias();

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al guardar categoria:', error);

        this.guardandoCategoria = false;

        this.cdr.detectChanges();

        this.errorFormulario = this.obtenerMensajeError(error, 'No se pudo guardar la categoría.');
      }

    });
  }

  alternarMenuAcciones(idCategoria: number): void {
   this.menuAccionesId = this.menuAccionesId === idCategoria ? null : idCategoria;
  }

  cerrarMenuAcciones(): void {

    this.menuAccionesId = null;
  }

  solicitarCambioEstado(categoria: ObtenerCategoriaServicio, estado: EstadoCategoriaServicio): void {

    this.menuAccionesId = null;

    if(categoria.estado === estado){
      return;
    }

    this.categoriaCambioEstado = categoria;

    this.nuevoEstado = estado;

    this.errorEstado = '';

    this.mensajeExito = '';

    this.mostrarConfirmacionEstado = true;
  }

  cancelarCambioEstado(): void {

    if(this.cambiandoEstado){
      return;
    }

    this.mostrarConfirmacionEstado = false;

    this.categoriaCambioEstado = null;

    this.errorEstado = '';
  }

  confirmarCambioEstado(): void {

    if(!this.categoriaCambioEstado || this.cambiandoEstado){
      return;
    }

    const categoria = this.categoriaCambioEstado;

    const datos: ActualizarCategoriaServicio = {
      nombreCategoria: categoria.nombreCategoria,
      descripcion: categoria.descripcion ?? '',
      estado: this.nuevoEstado
    };

    this.cambiandoEstado = true;

    this.errorEstado = '';

    this.categoriaServicioService.actualizarCategoria(categoria.idCategoria, datos).subscribe({

      next: () => {

        this.cambiandoEstado = false;

        this.cancelarCambioEstado();

        this.mensajeExito = 'El estado de la categoría se actualizó correctamente.';

        this.cargarCategorias();

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al cambiar estado:', error);

        this.cambiandoEstado = false;

        this.cdr.detectChanges();

        this.errorEstado = this.obtenerMensajeError(error, 'No se pudo actualizar el estado de la categoría.');
      }
    });
  }

  private obtenerMensajeError(error: unknown, mensajePredeterminado: string): string {

    if(error instanceof HttpErrorResponse){

      const respuesta = error.error;

      if(respuesta && typeof respuesta === 'object' && typeof respuesta.message === 'string'){
        return respuesta.message;
      }

      if(typeof respuesta === 'string' && respuesta.trim().length > 0 && !respuesta.trim().startsWith('<')){
        return respuesta;
      }
    }
    return mensajePredeterminado;
  }

  volverAgenda(): void {
    this.router.navigate(['/admin/agenda']);
  }
}
