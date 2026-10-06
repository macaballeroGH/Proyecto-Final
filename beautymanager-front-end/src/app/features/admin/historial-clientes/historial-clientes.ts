import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { HistorialTratamientoService } from '../../../core/services/historial-tratamiento';
import { ObtenerHistorialTratamiento } from '../../../core/models/obtener-historial-tratamiento';
import { HistorialCliente } from '../../../core/models/historial-cliente';
import { ClienteService } from '../../../core/services/cliente';
import { ObtenerCliente } from '../../../core/models/obtener-cliente';
import { EspecialidadEmpleado } from '../../../core/enums/especialidadEmpleado';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-historial-clientes',
  standalone: true,
  imports: [FormsModule, MatIconModule],
  templateUrl: './historial-clientes.html',
  styleUrl: './historial-clientes.css',
})
export class HistorialClientes implements OnInit {

  clientesOriginales: HistorialCliente[] = [];

  clientesFiltrados: HistorialCliente[] = [];

  clientesPagina: HistorialCliente[] = [];

  cargandoLista = false;

  error = '';

  textoBusqueda = '';

  especialidadFiltro: EspecialidadEmpleado | '' = '';

  fechaDesdeFiltro = '';

  fechaHastaFiltro = '';

  especialidadFiltroTemporal: EspecialidadEmpleado | '' = '';

  fechaDesdeFiltroTemporal = '';

  fechaHastaFiltroTemporal = '';

  especialidades = Object.values(EspecialidadEmpleado);

  campoOrden: | 'nombre' | 'tratamiento' | 'ultimoTratamiento' | '' = '';

  direccionOrden: 'asc' | 'desc' = 'asc';

  paginaActual = 1;

  clientesPorPagina = 10;

  modalActivo: 'filtros' | 'historial' | null = null;

  clienteSeleccionado: HistorialCliente | null = null;

  tratamientosClienteSeleccionado: ObtenerHistorialTratamiento[] = [];

  constructor(private historialTratamientoService: HistorialTratamientoService, private clienteService: ClienteService) {}

  ngOnInit(): void {
    this.cargarHistorial();
  }

  cargarHistorial(): void {

    this.cargandoLista = true;

    this.error = '';

    forkJoin({
      clientes: this.clienteService.listarTodos(),
      tratamientos: this.historialTratamientoService.listarTodos()
    }).subscribe({

      next: ({ clientes, tratamientos }) => {

        this.construirListaClientes(clientes, tratamientos);

        this.cargandoLista = false;

        this.actualizarVista();
      },

      error: (error) => {

        console.error('Error al cargar clientes y tratamientos:', error);

        this.cargandoLista = false;

        this.error = 'No se pudo cargar el historial de clientes.';
      }
    });
  }

  construirListaClientes(clientes: ObtenerCliente[], tratamientos: ObtenerHistorialTratamiento[]): void {

    const tratamientosPorCliente = new Map<number, ObtenerHistorialTratamiento[]>();

    for(const tratamiento of tratamientos){
      
      const tratamientosCliente = tratamientosPorCliente.get(tratamiento.idCliente) || [];

      tratamientosCliente.push(tratamiento);

      tratamientosPorCliente.set(tratamiento.idCliente, tratamientosCliente);
    }

    this.clientesOriginales = clientes.map(cliente => {
      
      const tratamientosCliente = tratamientosPorCliente.get(cliente.idCliente) || [];

      return {
        idCliente: cliente.idCliente,
        nombreCliente: cliente.nombre,
        apellidoCliente: cliente.apellido,
        tratamientos: this.ordenarTratamientos(tratamientosCliente),
        pagos: []
      };
    });
  }

  actualizarVista(): void {

    let resultado = [...this.clientesOriginales];

    resultado = this.aplicarBusqueda(resultado);

    resultado = this.aplicarFiltros(resultado);

    resultado = this.aplicarOrdenamiento(resultado);

    this.clientesFiltrados = resultado;

    this.ajustarPaginaActual();

    this.actualizarPaginacion();

  }

  buscar(): void {

    this.paginaActual = 1;

    this.actualizarVista();
  }

  aplicarBusqueda(clientes: HistorialCliente[]): HistorialCliente[] {

    const texto = this.textoBusqueda.trim().toLowerCase();

    if(!texto) {
      return clientes;
    }

    return clientes.filter(cliente => {

      const nombreCompleto = `${cliente.nombreCliente} ${cliente.apellidoCliente}`.toLowerCase();

      return nombreCompleto.includes(texto);
    });
  }

  abrirFiltros(): void {

    this.especialidadFiltroTemporal = this.especialidadFiltro;

    this.fechaDesdeFiltroTemporal = this.fechaDesdeFiltro;

    this.fechaHastaFiltroTemporal = this.fechaHastaFiltro;

    this.modalActivo = 'filtros';
  }

  cancelarFiltros(): void {
    this.modalActivo = null;
  }

  confirmarFiltros(): void {

    if(this.fechaDesdeFiltroTemporal && this.fechaHastaFiltroTemporal && this.fechaDesdeFiltroTemporal > this.fechaHastaFiltroTemporal){
      return;
    }

    this.especialidadFiltro = this.especialidadFiltroTemporal;

    this.fechaDesdeFiltro = this.fechaDesdeFiltroTemporal;

    this.fechaHastaFiltro = this.fechaHastaFiltroTemporal;

    this.paginaActual = 1;

    this.modalActivo = null;

    this.actualizarVista();

  }

  limpiarFiltros(): void {

    this.especialidadFiltro = '';

    this.fechaDesdeFiltro = '';

    this.fechaHastaFiltro = '';

    this.especialidadFiltroTemporal = '';

    this.fechaDesdeFiltroTemporal = '';

    this.fechaHastaFiltroTemporal = '';

    this.paginaActual = 1;

    this.modalActivo = null;

    this.actualizarVista();

  }

  aplicarFiltros(clientes: HistorialCliente[]): HistorialCliente[] {

    return clientes.filter(cliente => {

      if (this.especialidadFiltro) {

        const tieneEspecialidad = cliente.tratamientos.some(tratamiento => tratamiento.especialidadEmpleado === this.especialidadFiltro);

        if (!tieneEspecialidad) {
          return false;
        }
      }

      if (this.fechaDesdeFiltro || this.fechaHastaFiltro) {

        const tieneTratamientoEnPeriodo = cliente.tratamientos.some(tratamiento => {

          const fechaTratamiento = new Date(tratamiento.fechaHoraTurno);

            if (isNaN(fechaTratamiento.getTime())){
              return false;
            }

            if(this.fechaDesdeFiltro){

              const fechaDesde = new Date(`${this.fechaDesdeFiltro}T00:00:00`);

              if(fechaTratamiento < fechaDesde){
                return false;
              }
            }

            if(this.fechaHastaFiltro){

              const fechaHasta = new Date(`${this.fechaHastaFiltro}T23:59:59`);

              if(fechaTratamiento > fechaHasta){
                return false;
              }
            }

          return true;
        });

        if (!tieneTratamientoEnPeriodo) {
          return false;
        }
      }

      return true;
    });
  }

  ordenarPor(campo: | 'nombre' | 'tratamiento' | 'ultimoTratamiento'): void {

    if(this.campoOrden === campo){
      this.direccionOrden = this.direccionOrden === 'asc' ? 'desc' : 'asc';
    }else{

      this.campoOrden = campo;

      this.direccionOrden = 'asc';
    }

    this.actualizarVista();
  }

  aplicarOrdenamiento(clientes: HistorialCliente[]): HistorialCliente[] {

    if(!this.campoOrden){
      return clientes;
    }

    return [...clientes].sort((a, b) => {

      let resultado = 0;

      if (this.campoOrden === 'nombre') {

        const nombreA = `${a.nombreCliente} ${a.apellidoCliente}`.toLowerCase();

        const nombreB = `${b.nombreCliente} ${b.apellidoCliente}`.toLowerCase();

        resultado = nombreA.localeCompare(nombreB);
      }

      if(this.campoOrden === 'tratamiento'){
        resultado = a.tratamientos.length - b.tratamientos.length;
      }

      if(this.campoOrden === 'ultimoTratamiento'){

        const fechaA = this.obtenerFechaUltimoTratamiento(a);

        const fechaB = this.obtenerFechaUltimoTratamiento(b);

        resultado = fechaA.getTime() - fechaB.getTime();
      }

      return this.direccionOrden === 'asc' ? resultado : -resultado;
    });
  }

  actualizarPaginacion(): void {

    const indiceInicio = (this.paginaActual - 1) * this.clientesPorPagina;

    const indiceFin = indiceInicio + this.clientesPorPagina;

    this.clientesPagina = this.clientesFiltrados.slice(indiceInicio, indiceFin);
  }

  cambiarPagina(pagina: number): void {

    if(pagina < 1 || pagina > this.totalPaginas){
      return;
    }

    this.paginaActual = pagina;

    this.actualizarPaginacion();
  }

  paginaAnterior(): void {

    if(this.paginaActual > 1){

      this.paginaActual--;

      this.actualizarPaginacion();
    }
  }

  paginaSiguiente(): void {

    if(this.paginaActual < this.totalPaginas){

      this.paginaActual++;

      this.actualizarPaginacion();
    }
  }

  cambiarCantidadPorPagina(cantidad: number): void {

    this.clientesPorPagina = cantidad;

    this.paginaActual = 1;

    this.actualizarPaginacion();

  }

  ajustarPaginaActual(): void {

    if(this.paginaActual > this.totalPaginas && this.totalPaginas > 0){
      this.paginaActual = this.totalPaginas;
    }

    if(this.totalPaginas === 0){
      this.paginaActual = 1;
    }
  }

  abrirHistorial(cliente: HistorialCliente): void {

    this.clienteSeleccionado = cliente;

    this.tratamientosClienteSeleccionado = this.ordenarTratamientos([...cliente.tratamientos]);

    this.modalActivo = 'historial';
  }

  cerrarModal(): void {

    this.modalActivo = null;

    this.clienteSeleccionado = null;

    this.tratamientosClienteSeleccionado = [];
  }

  ordenarTratamientos(tratamientos: ObtenerHistorialTratamiento[]): ObtenerHistorialTratamiento[] {

    return [...tratamientos].sort((a, b) => {

      const fechaA = new Date(a.fechaHoraTurno).getTime();

      const fechaB = new Date(b.fechaHoraTurno).getTime();

      return fechaB - fechaA;
    });
  }

  obtenerFechaUltimoTratamiento(cliente: HistorialCliente): Date {

    if (cliente.tratamientos.length === 0){
      return new Date(0);
    }

    const fecha = new Date(cliente.tratamientos[0].fechaHoraTurno);

    return isNaN(fecha.getTime()) ? new Date(0) : fecha;
  }

  obtenerNombreCompletoCliente(cliente: HistorialCliente): string {
    return `${cliente.nombreCliente} ${cliente.apellidoCliente}`;
  }

  obtenerNombreCompletoEmpleado(tratamiento: ObtenerHistorialTratamiento): string {
    return `${tratamiento.nombreEmpleado} ${tratamiento.apellidoEmpleado}`;
  }

  obtenerServicios(tratamiento: ObtenerHistorialTratamiento): string {

    if (!tratamiento.servicios || tratamiento.servicios.length === 0){
      return 'Sin servicios registrados';
    }

    return tratamiento.servicios.join(', ');
  }

  obtenerEspecialidadTexto(especialidad: EspecialidadEmpleado): string {
    return especialidad.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, letra => letra.toUpperCase());
  }

  formatearDuracion(duracion: number | null): string {

    if (duracion === null || duracion === undefined){
      return 'No registrada';
    }

    if(duracion < 60){
      return `${duracion} min`;
    }

    const horas = Math.floor(duracion / 60);

    const minutos = duracion % 60;

    if(minutos === 0){
      return `${horas} h`;
    }

    return `${horas} h ${minutos} min`;
  }

  formatearPrecio(precio: number | null): string {

    if (precio === null || precio === undefined){
      return 'No registrado';
    }

    return precio.toLocaleString('es-AR',{style: 'currency', currency: 'ARS'});
  }

  tieneObservacion(tratamiento: ObtenerHistorialTratamiento): boolean {
    return !!tratamiento.observacion?.trim();
  }

  obtenerObservacion(tratamiento: ObtenerHistorialTratamiento): string {
    return tratamiento.observacion?.trim() || 'Sin observaciones';
  }

  tieneProductos(tratamiento: ObtenerHistorialTratamiento): boolean {
    return !!tratamiento.productosUtilizados?.trim();
  }

  obtenerProductos(tratamiento: ObtenerHistorialTratamiento): string {
    return tratamiento.productosUtilizados?.trim() || 'Sin productos registrados';
  }

  tieneTratamientos(cliente: HistorialCliente): boolean {
    return cliente.tratamientos.length > 0;
  }

  get totalPaginas(): number {

    return Math.ceil(
      this.clientesFiltrados.length /
      this.clientesPorPagina
    );

  }

  get paginas(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, i) => i + 1);
  }

  get indiceInicio(): number {

    if(this.clientesFiltrados.length === 0){
      return 0;
    }

    return ((this.paginaActual - 1) * this.clientesPorPagina) + 1;
  }

  get indiceFin(): number {
    return Math.min(this.paginaActual * this.clientesPorPagina, this.clientesFiltrados.length);
  }

  get hayClientes(): boolean {
    return this.clientesOriginales.length > 0;
  }

  get hayResultados(): boolean {
    return this.clientesFiltrados.length > 0;
  }

  get hayFiltrosAplicados(): boolean {
    return (this.especialidadFiltro !== '' || this.fechaDesdeFiltro !== '' || this.fechaHastaFiltro !== '');
  }
}