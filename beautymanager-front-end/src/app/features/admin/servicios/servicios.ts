import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { ActualizarServicio } from '../../../core/models/actualizar-servicio';
import { ObtenerServicio } from '../../../core/models/obtener-servicio';
import { EstadoServicio } from '../../../core/enums/estadoServicio';
import { CrearServicio } from '../../../core/models/crear-servicio';
import { ObtenerCategoriaServicio } from '../../../core/models/obtener-categoria-servicio';
import { forkJoin } from 'rxjs';
import { ServicioService } from '../../../core/services/servicio';
import { CategoriaServicioService } from '../../../core/services/categoria-servicio';
import { Router } from '@angular/router';

@Component({
  selector: 'app-servicios',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule],
  templateUrl: 'servicios.html',
  styleUrls: ['./servicios.css', '../categoria/categoria.css']
})
export class ServiciosAdmin implements OnInit {

  servicios: ObtenerServicio[] = [];

  serviciosFiltrados: ObtenerServicio[] = [];

  cargandoServicios: boolean = false;

  errorCarga: string = '';

  mensajeExito: string = '';

  categorias: ObtenerCategoriaServicio[] = [];

  cargandoCategorias: boolean = false;

  errorCategorias: string = '';

  readonly estadosServicio: {
    valor: EstadoServicio;
    etiqueta: string;
  }[] = [
    { valor: EstadoServicio.ACTIVO as EstadoServicio, etiqueta: 'Activo' },
    { valor: EstadoServicio.INACTIVO as EstadoServicio, etiqueta: 'Inactivo' }
  ];

  textoBusqueda: string = '';

  estadoFiltro: EstadoServicio | '' = '';

  categoriaFiltro: number | '' = '';

  paginaActual: number = 1;

  elementosPorPagina: number = 10;

  mostrarFormulario: boolean = false;

  modoEdicion: boolean = false;

  servicioEditandoId: number | null = null;

  guardandoServicio: boolean = false;

  errorFormulario: string = '';

  formularioServicio: CrearServicio = {
    nombre: '',
    descripcion: '',
    precio: 0,
    duracionMinutos: 30,
    idCategoria: 0,
    estado: EstadoServicio.ACTIVO
  };

  menuAccionesId: number | null = null;

  mostrarConfirmacionEstado: boolean = false;

  servicioCambioEstado: ObtenerServicio | null = null;

  nuevoEstado: EstadoServicio = EstadoServicio.ACTIVO;

  cambiandoEstado: boolean = false;

  errorEstado: string = '';

  constructor(private servicioService: ServicioService, private categoriaServicioService: CategoriaServicioService, private router: Router, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {

    this.cargarServicios();

    this.cargarCategorias();
  }

  cargarServicios(): void {

    this.cargandoServicios = true;

    this.errorCarga = '';

    forkJoin({
      activos: this.servicioService.listarActivos(),
      inactivos: this.servicioService.listarInactivos()
    }).subscribe({

      next: ({ activos, inactivos }) => {

        const todos = [...activos, ...inactivos];

        this.servicios = [...new Map(
          todos.map(servicio => [servicio.id, servicio])
        ).values()].sort((a, b) =>
          a.nombre.localeCompare(b.nombre, 'es', {
            sensitivity: 'base'
          })
        );

        this.aplicarFiltros();

        this.cargandoServicios = false;

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al cargar servicios:', error);

        this.errorCarga = 'No se pudieron cargar los servicios.';

        this.servicios = [];

        this.serviciosFiltrados = [];

        this.cargandoServicios = false;

        this.cdr.detectChanges();
      }
    });
  }

  cargarCategorias(): void {

    this.cargandoCategorias = true;

    this.errorCategorias = '';

    this.categoriaServicioService.listarTodas().subscribe({

      next: (data) => {

        this.categorias = [...data].sort((a, b) =>
          a.nombreCategoria.localeCompare(
            b.nombreCategoria,
            'es',
            { sensitivity: 'base' }
          )
        );

        this.cargandoCategorias = false;

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al cargar categorias:', error);

        this.errorCategorias = 'No se pudieron cargar las categorías.';

        this.categorias = [];

        this.cargandoCategorias = false;

        this.cdr.detectChanges();
      }
    });
  }

  get categoriasDisponibles(): ObtenerCategoriaServicio[] {

    return this.categorias.filter(categoria => {

      const activa = String(categoria.estado) === 'ACTIVA';

      const categoriaActual =
        this.modoEdicion &&
        categoria.idCategoria === this.formularioServicio.idCategoria;

      return activa || categoriaActual;
    });
  }

  aplicarFiltros(): void {

    const texto = this.textoBusqueda
      .trim()
      .toLocaleLowerCase('es');

    this.serviciosFiltrados = this.servicios.filter(servicio => {

      const coincideEstado =
        this.estadoFiltro === '' ||
        servicio.estado === this.estadoFiltro;

      const coincideCategoria =
        this.categoriaFiltro === '' ||
        servicio.idCategoria === this.categoriaFiltro;

      const nombre = servicio.nombre.toLocaleLowerCase('es');

      const descripcion = (servicio.descripcion ?? '')
        .toLocaleLowerCase('es');

      const nombreCategoria = (servicio.nombreCategoria ?? '')
        .toLocaleLowerCase('es');

      const coincideBusqueda =
        texto === '' ||
        nombre.includes(texto) ||
        descripcion.includes(texto) ||
        nombreCategoria.includes(texto);

      return coincideEstado && coincideCategoria && coincideBusqueda;
    });

    this.paginaActual = 1;
  }

  filtrarPorEstado(estado: EstadoServicio | ''): void {

    this.estadoFiltro = estado;

    this.aplicarFiltros();
  }

  limpiarFiltros(): void {

    this.textoBusqueda = '';

    this.estadoFiltro = '';

    this.categoriaFiltro = '';

    this.aplicarFiltros();
  }

  contarPorEstado(estado: EstadoServicio): number {

    return this.servicios.filter(
      servicio => servicio.estado === estado
    ).length;
  }

  obtenerNombreEstado(estado: EstadoServicio): string {

    return this.estadosServicio.find(
      item => item.valor === estado
    )?.etiqueta ?? estado;
  }

  get totalPaginas(): number {

    return Math.ceil(
      this.serviciosFiltrados.length / this.elementosPorPagina
    );
  }

  get serviciosPaginados(): ObtenerServicio[] {

    const inicio = (this.paginaActual - 1) * this.elementosPorPagina;

    return this.serviciosFiltrados.slice(
      inicio,
      inicio + this.elementosPorPagina
    );
  }

  cambiarPagina(pagina: number): void {

    if(pagina < 1 || pagina > this.totalPaginas){
      return;
    }

    this.paginaActual = pagina;
  }

  abrirNuevoServicio(): void {

    this.modoEdicion = false;

    this.servicioEditandoId = null;

    this.errorFormulario = '';

    this.mensajeExito = '';

    this.menuAccionesId = null;

    this.formularioServicio = {
      nombre: '',
      descripcion: '',
      precio: 0,
      duracionMinutos: 30,
      idCategoria: 0,
      estado: EstadoServicio.ACTIVO
    };

    this.mostrarFormulario = true;
  }

  abrirEditarServicio(servicio: ObtenerServicio): void {

    this.modoEdicion = true;

    this.servicioEditandoId = servicio.id;

    this.errorFormulario = '';

    this.mensajeExito = '';

    this.menuAccionesId = null;

    this.formularioServicio = {
      nombre: servicio.nombre,
      descripcion: servicio.descripcion ?? '',
      precio: servicio.precio,
      duracionMinutos: servicio.duracionMinutos,
      idCategoria: servicio.idCategoria,
      estado: servicio.estado
    };

    this.mostrarFormulario = true;
  }

  cerrarFormulario(): void {

    if(this.guardandoServicio){
      return;
    }

    this.mostrarFormulario = false;

    this.errorFormulario = '';

    this.servicioEditandoId = null;
  }

  formularioValido(): boolean {

    const categoriaValida = this.categoriasDisponibles.some(
      categoria => categoria.idCategoria === this.formularioServicio.idCategoria
    );

    return (
      !this.guardandoServicio &&
      this.formularioServicio.nombre.trim().length > 0 &&
      Number(this.formularioServicio.precio) > 0 &&
      Number.isFinite(Number(this.formularioServicio.precio)) &&
      Number.isInteger(Number(this.formularioServicio.duracionMinutos)) &&
      Number(this.formularioServicio.duracionMinutos) > 0 &&
      categoriaValida
    );
  }

  guardarServicio(): void {

    if(!this.formularioValido()){
      return;
    }

    const datos: CrearServicio = {
      nombre: this.formularioServicio.nombre.trim(),
      descripcion: this.formularioServicio.descripcion.trim(),
      precio: Number(this.formularioServicio.precio),
      duracionMinutos: Number(this.formularioServicio.duracionMinutos),
      idCategoria: Number(this.formularioServicio.idCategoria),
      estado: this.formularioServicio.estado
    };

    this.guardandoServicio = true;

    this.errorFormulario = '';

    const esEdicion = this.modoEdicion;

    const peticion = esEdicion && this.servicioEditandoId !== null
      ? this.servicioService.actualizarServicio(
          this.servicioEditandoId,
          datos as ActualizarServicio
        )
      : this.servicioService.crearServicio(datos);

    peticion.subscribe({

      next: () => {

        this.guardandoServicio = false;

        this.cerrarFormulario();

        this.mensajeExito = esEdicion
          ? 'El servicio se actualizó correctamente.'
          : 'El servicio se creó correctamente.';

        this.cargarServicios();

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al guardar servicio:', error);

        this.guardandoServicio = false;

        this.cdr.detectChanges();

        this.errorFormulario = this.obtenerMensajeError(
          error,
          'No se pudo guardar el servicio.'
        );
      }
    });
  }

  alternarMenuAcciones(idServicio: number): void {

    this.menuAccionesId =
      this.menuAccionesId === idServicio
        ? null
        : idServicio;
  }

  cerrarMenuAcciones(): void {

    this.menuAccionesId = null;
  }

  solicitarCambioEstado(
    servicio: ObtenerServicio,
    estado: EstadoServicio
  ): void {

    this.menuAccionesId = null;

    if(servicio.estado === estado){
      return;
    }

    this.servicioCambioEstado = servicio;

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

    this.servicioCambioEstado = null;

    this.errorEstado = '';
  }

  confirmarCambioEstado(): void {

    if(!this.servicioCambioEstado || this.cambiandoEstado){
      return;
    }

    const servicio = this.servicioCambioEstado;

    const datos: ActualizarServicio = {
      nombre: servicio.nombre,
      descripcion: servicio.descripcion ?? '',
      precio: servicio.precio,
      duracionMinutos: servicio.duracionMinutos,
      idCategoria: servicio.idCategoria,
      estado: this.nuevoEstado
    };

    this.cambiandoEstado = true;

    this.errorEstado = '';

    this.servicioService.actualizarServicio(
      servicio.id,
      datos
    ).subscribe({

      next: () => {

        this.cambiandoEstado = false;

        this.cancelarCambioEstado();

        this.mensajeExito = 'El estado del servicio se actualizó correctamente.';

        this.cargarServicios();

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al cambiar estado:', error);

        this.cambiandoEstado = false;

        this.cdr.detectChanges();

        this.errorEstado = this.obtenerMensajeError(
          error,
          'No se pudo actualizar el estado del servicio.'
        );
      }
    });
  }

  private obtenerMensajeError(
    error: unknown,
    mensajePredeterminado: string
  ): string {

    if(error instanceof HttpErrorResponse){

      const respuesta = error.error;

      if(
        respuesta &&
        typeof respuesta === 'object' &&
        typeof respuesta.message === 'string'
      ){
        return respuesta.message;
      }

      if(
        typeof respuesta === 'string' &&
        respuesta.trim().length > 0 &&
        !respuesta.trim().startsWith('<')
      ){
        return respuesta;
      }
    }

    return mensajePredeterminado;
  }

  volverAgenda(): void {

    this.router.navigate(['/admin/agenda']);
  }
}
