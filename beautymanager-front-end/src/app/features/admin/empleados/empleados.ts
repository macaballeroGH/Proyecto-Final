import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { EmpleadoService } from '../../../core/services/empleado';
import { Empleado } from '../../../core/models/empleado';
import { CrearEmpleado } from '../../../core/models/crear-empleado';
import { ActualizarEmpleado } from '../../../core/models/actualizar-empleado';
import { EspecialidadEmpleado } from '../../../core/enums/especialidadEmpleado';
import { EstadoEmpleado } from '../../../core/enums/estadoEmpleado';
import { HorarioEmpleado } from '../../../core/models/horario-empleado';
import { CrearHorarioEmpleado } from '../../../core/models/crear-horario-empleado';
import { HorarioFormulario } from '../../../core/models/horario-formulario';
import { HorarioEmpleadoService } from '../../../core/services/horario-empleado';

@Component({
  selector: 'app-empleados',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, DatePipe, MatIconModule],
  templateUrl: './empleados.html',
  styleUrl: './empleados.css',
})
export class Empleados implements OnInit {

  empleadosOriginales: Empleado[] = [];
  empleadosFiltrados: Empleado[] = [];
  empleadosPagina: Empleado[] = [];

  cargandoLista = false;
  procesandoOperacion = false;

  error = '';
  mensajeExito = '';

  textoBusqueda = '';

  especialidadFiltro: EspecialidadEmpleado | '' = '';
  estadoFiltro: EstadoEmpleado | '' = '';

  especialidadFiltroTemporal: EspecialidadEmpleado | '' = '';
  estadoFiltroTemporal: EstadoEmpleado | '' = '';

  campoOrden: | 'nombre' | 'especialidad' | 'estadoEmpleado' | 'fechaAlta' | 'telefono' | '' = '';
  direccionOrden: 'asc' | 'desc' = 'asc';

  paginaActual = 1;
  empleadosPorPagina = 10;

  especialidades = Object.values(EspecialidadEmpleado);
  estados = Object.values(EstadoEmpleado);

  menuEmpleadoAbierto: number | null = null;

  modalActivo: | 'filtros' | 'nuevo' | 'editar' | 'estado' | 'eliminar' | null = null;
  confirmacionActiva: | 'filtros' | 'alta' | 'edicion' | 'estado' | 'baja' | 'eliminar' | null = null;

  empleadoSeleccionado: Empleado | null = null;
  
  nuevoEstado: EstadoEmpleado | null = null;

  formularioNuevoEmpleado: FormGroup;
  formularioEditarEmpleado: FormGroup;

  horariosFormulario: Record<number, HorarioFormulario> = {
    1: { seleccionado: false, bloques: [{ horaInicio: '08:00', horaFin: '13:00' }] },
    2: { seleccionado: false, bloques: [{ horaInicio: '08:00', horaFin: '13:00' }] },
    3: { seleccionado: false, bloques: [{ horaInicio: '08:00', horaFin: '13:00' }] },
    4: { seleccionado: false, bloques: [{ horaInicio: '08:00', horaFin: '13:00' }] },
    5: { seleccionado: false, bloques: [{ horaInicio: '08:00', horaFin: '13:00' }] },
    6: { seleccionado: false, bloques: [{ horaInicio: '08:00', horaFin: '13:00' }] },
  };

  diasSemana = [
    { numero: 1, nombre: 'Lunes' },
    { numero: 2, nombre: 'Martes' },
    { numero: 3, nombre: 'Miercoles' },
    { numero: 4, nombre: 'Jueves' },
    { numero: 5, nombre: 'Viernes' },
    { numero: 6, nombre: 'Sabado' },
  ];

  constructor(private empleadoService: EmpleadoService, private horarioEmpleadoService: HorarioEmpleadoService, private fb: FormBuilder, private cdr: ChangeDetectorRef) {

    this.formularioNuevoEmpleado = this.fb.group({

      nombre: [
        '',
        [Validators.required, Validators.minLength(2), Validators.maxLength(50)]
      ],

      apellido: [
        '',
        [Validators.required, Validators.minLength(2), Validators.maxLength(50)]
      ],

      email: [
        '',
        [Validators.required, Validators.email, Validators.maxLength(150)]
      ],

      password: [
        '',
        [Validators.required, Validators.minLength(12)]
      ],

      telefono: [
        '',
        [Validators.required, Validators.minLength(8), Validators.maxLength(10)]
      ],

      especialidad: [
        '',
        Validators.required
      ]
    });

    this.formularioEditarEmpleado = this.fb.group({

      especialidad: [
        '',
        Validators.required
      ]
    });
  }

  ngOnInit(): void {
    this.cargarEmpleados();
  }

  cargarEmpleados(): void {

    this.cargandoLista = true;
    this.error = '';

    this.empleadoService.listarTodos().subscribe({

      next: (empleados) => {

        this.empleadosOriginales = empleados;

        this.cargandoLista = false;

        this.actualizarVista();

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error al cargar empleados:', error);

        this.cargandoLista = false;

        this.error = 'No se pudieron cargar los empleados.';

        this.cdr.detectChanges();
      }
    });
  }

  actualizarVista(): void {

    let resultado = [...this.empleadosOriginales];

    resultado = this.aplicarBusqueda(resultado);
    resultado = this.aplicarFiltros(resultado);
    resultado = this.aplicarOrdenamiento(resultado);

    this.empleadosFiltrados = resultado;

    this.ajustarPaginaActual();

    this.actualizarPaginacion();
  }

  buscar(): void {
    
    this.paginaActual = 1;

    this.actualizarVista();
  }

  aplicarBusqueda(empleados: Empleado[]): Empleado[] {

    const texto = this.textoBusqueda.trim().toLowerCase();

    if(!texto){
      return empleados;
    }

    return empleados.filter(empleado => {

      const nombreCompleto = `${empleado.nombre} ${empleado.apellido}`.toLowerCase();

      const nombre = empleado.nombre.toLowerCase();

      const apellido = empleado.apellido.toLowerCase();

      const email = empleado.email.toLowerCase();

      const telefono = empleado.telefono?.toLowerCase() ?? '';

      return (nombre.includes(texto) || apellido.includes(texto) || nombreCompleto.includes(texto) || email.includes(texto) || telefono.includes(texto));
    });
  }

  abrirFiltros(): void {

    this.especialidadFiltroTemporal = this.especialidadFiltro;

    this.estadoFiltroTemporal = this.estadoFiltro;

    this.cerrarMenuEmpleado();

    this.modalActivo = 'filtros';
  }

  cancelarFiltros(): void {

    this.modalActivo = null;

    this.especialidadFiltroTemporal = '';

    this.estadoFiltroTemporal = '';
  }

  solicitarAplicarFiltros(): void {
    this.confirmacionActiva = 'filtros';
  }

  confimarAplicarFiltros(): void {

    this.especialidadFiltro = this.especialidadFiltroTemporal;

    this.estadoFiltro = this.estadoFiltroTemporal;

    this.paginaActual = 1;

    this.actualizarVista();

    this.confirmacionActiva = null;

    this.modalActivo = null;
  }

  cancelarConfirmacionFiltros(): void {
    this.confirmacionActiva = null;
  }

  limpiarFiltros(): void {

    this.especialidadFiltroTemporal = '';

    this.estadoFiltroTemporal = '';
  }

  aplicarFiltros(empleados: Empleado[]): Empleado[] {

    return empleados.filter(empleado => {

      const coincideEspecialidad = !this.especialidadFiltro || empleado.especialidad === this.especialidadFiltro;

      const coincideEstado = !this.estadoFiltro || empleado.estadoEmpleado === this.estadoFiltro;

      return coincideEspecialidad && coincideEstado;
    });
  }

  ordenarPor(campo: | 'nombre' | 'especialidad' | 'estadoEmpleado' | 'fechaAlta' | 'telefono'): void {

    if(this.campoOrden === campo){

      this.direccionOrden = this.direccionOrden === 'asc' ? 'desc' : 'asc';
    } else {

      this.campoOrden = campo;

      this.direccionOrden = 'asc';
    }

    this.actualizarVista();
  }

  aplicarOrdenamiento(empleados: Empleado[]): Empleado[] {

    if(!this.campoOrden){
      return empleados;
    }

    return [...empleados].sort((a, b) => {

      let valorA: string = '';
      let valorB: string = '';

      switch (this.campoOrden) {

        case 'nombre':
          valorA = `${a.nombre} ${a.apellido}`.toLowerCase();

          valorB = `${b.nombre} ${b.apellido}`.toLowerCase();
        break;

        case 'especialidad':
          valorA = a.especialidad?.toString().toLowerCase() ?? '';

          valorB = b.especialidad?.toString().toLowerCase() ?? '';
        break;

        case 'estadoEmpleado':
          valorA = a.estadoEmpleado?.toString().toLowerCase() ?? '';

          valorB = b.estadoEmpleado?.toString().toLowerCase() ?? '';
        break;

        case 'fechaAlta':
          valorA = a.fechaAlta ?? '';

          valorB = b.fechaAlta ?? '';
        break;

        case 'telefono':
          valorA = a.telefono?.toLowerCase() ?? '';

          valorB = b.telefono?.toLowerCase() ?? '';
        break;
      }

      const comparacion = valorA.localeCompare(valorB);

      return this.direccionOrden === 'asc' ? comparacion : -comparacion;
    });
  }

  actualizarPaginacion(): void {

    const inicio = (this.paginaActual - 1) * this.empleadosPorPagina;

    const fin = inicio + this.empleadosPorPagina;

    this.empleadosPagina = this.empleadosFiltrados.slice(inicio, fin);
  }

  cambiarPagina(pagina: number): void {

    if(pagina < 1 || pagina > this.totalPaginas){
      return;
    }

    this.paginaActual = pagina;

    this.actualizarPaginacion();
  }

  paginaAnterior(): void {

    this.cambiarPagina(this.paginaActual - 1);
  }

  paginaSiguiente(): void {

    this.cambiarPagina(this.paginaActual + 1);
  }

  cambiarCantidadPorPagina(cantidad: number): void {

    this.empleadosPorPagina = cantidad;

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

  get totalPaginas(): number {
    return Math.ceil(this.empleadosFiltrados.length / this.empleadosPorPagina);
  }

  get paginas(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, indice) => indice + 1);
  }

  get indiceInicio(): number {

    if(this.empleadosFiltrados.length === 0){
      return 0;
    }

    return((this.paginaActual - 1) * this.empleadosPorPagina) + 1;
  }

  get indiceFin(): number {
    return Math.min(this.paginaActual * this.empleadosPorPagina, this.empleadosFiltrados.length);
  }

  alternarMenuEmpleado(idEmpleado: number): void {

    if(this.menuEmpleadoAbierto === idEmpleado){

      this.menuEmpleadoAbierto = null;
    } else {

      this.menuEmpleadoAbierto = idEmpleado;
    }
  }

  cerrarMenuEmpleado(): void {
    this.menuEmpleadoAbierto = null;
  }

  abrirCambiarEstado(empleado: Empleado): void {

    this.empleadoSeleccionado = empleado;

    this.nuevoEstado = empleado.estadoEmpleado;

    this.cerrarMenuEmpleado();

    this.modalActivo = 'estado';
  }

  cancelarCambiarEstado(): void {

    this.modalActivo = null;

    this.nuevoEstado = null;

    this.empleadoSeleccionado = null;
  }

  solicitarCambioEstado(): void {

    if(!this.empleadoSeleccionado || !this.nuevoEstado){
      return;
    }

    this.confirmacionActiva = 'estado';
  }

  confirmarCambioEstado(): void {

    if(!this.empleadoSeleccionado || !this.nuevoEstado){
      return;
    }

    this.procesandoOperacion = true;
    
    this.error = '';

    this.empleadoService.cambiarEstado(this.empleadoSeleccionado.idEmpleado, this.nuevoEstado).subscribe({
      next: () => {

        this.procesandoOperacion = false;

        this.mostrarMensajeExito('Estado del empleado actualizado correctamente.');

        this.confirmacionActiva = null;

        this.modalActivo = null;

        this.limpiarSeleccion();

        this.cargarEmpleados();
      },

      error: (error) => {

        console.error('Error al cambiar estado:', error);

        this.procesandoOperacion = false;

        this.error = 'No se puedo cambiar el estado del empleado.';

        this.confirmacionActiva = null;
      }
    });
  }

  cancelarConfirmacionEstado(): void {
    this.confirmacionActiva = null;
  }

  abrirNuevoEmpleado(): void {

    this.cerrarMenuEmpleado();

    this.formularioNuevoEmpleado.reset();

    this.formularioNuevoEmpleado.patchValue({
      nombre: '',
      apellido: '',
      email: '',
      password: '',
      telefono: '',
      especialidad: ''
    });

    this.modalActivo = 'nuevo';
  }

  cancelarNuevoEmpleado(): void {

    this.modalActivo = null;

    this.confirmacionActiva = null;

    this.formularioNuevoEmpleado.reset();
  }

  agregarBloqueHorario(dia: number): void {

    this.horariosFormulario[dia].bloques.push({
      horaInicio: '08:00',
      horaFin: '13:00'
    });
  }

  eliminarBloqueHorario(dia: number, indice: number): void {

    const bloques = this.horariosFormulario[dia].bloques;

    if(bloques.length <= 1){
      return;
    }
  }

  solicitarAlta(): void {

    if(this.formularioNuevoEmpleado.invalid){

      this.formularioNuevoEmpleado.markAllAsTouched();

      return;
    }

    this.confirmacionActiva = 'alta';
  }

  confirmarAlta(): void {

    if(this.formularioNuevoEmpleado.invalid){

      this.formularioNuevoEmpleado.markAllAsTouched();

      this.confirmacionActiva = null;

      return;
    }

    const empleado: CrearEmpleado = this.formularioNuevoEmpleado.getRawValue();

    this.procesandoOperacion = true;

    this.error = '';

    this.empleadoService.crearEmpleado(empleado).subscribe({

      next: () => {

        this.procesandoOperacion = false;

        this.mostrarMensajeExito('El empleado fue dado de alta correctamente.');

        this.confirmacionActiva = null;

        this.modalActivo = null;

        this.formularioNuevoEmpleado.reset();

        this.cargarEmpleados();
      },

      error: (error) => {

        console.error('Error al dar de alta:', error);

        this.procesandoOperacion = false;

        this.error = 'No se pudo dar de alta al empleado.';

        this.confirmacionActiva = null;
      }
    });
  }

  cancelarConfirmacionAlta(): void {
    this.confirmacionActiva = null;
  }

  abrirEditar(empleado: Empleado): void {

    this.empleadoSeleccionado = empleado;

    this.formularioEditarEmpleado.reset();

    this.formularioEditarEmpleado.patchValue({
      especialidad: empleado.especialidad
    });

    this.cerrarMenuEmpleado();

    this.modalActivo = 'editar';
  }

  cancelarEditar(): void {

    this.modalActivo = null;

    this.confirmacionActiva = null;

    this.formularioEditarEmpleado.reset();

    this.empleadoSeleccionado = null;
  }

  solicitarGuardarEdicion(): void {

    if(this.formularioEditarEmpleado.invalid){
      
      this.formularioEditarEmpleado.markAllAsTouched();

      return;
    }

    this.confirmacionActiva = 'edicion';
  }

  confirmarGuardarEdicion(): void {

    if(!this.empleadoSeleccionado || this.formularioEditarEmpleado.invalid){
      return;
    }

    const datos: ActualizarEmpleado = this.formularioEditarEmpleado.getRawValue();

    this.procesandoOperacion = true;

    this.error = '';

    this.empleadoService.actualizarEmpleado(this.empleadoSeleccionado.idEmpleado, datos).subscribe({

      next: () => {

        this.procesandoOperacion = false;

        this.mostrarMensajeExito('La información del empleado fue actualizada correctamente.');

        this.confirmacionActiva = null;

        this.modalActivo = null;

        this.formularioEditarEmpleado.reset();

        this.empleadoSeleccionado = null;

        this.cargarEmpleados();
      },

      error: (error) => {

        console.error('Error al editar el empleado:', error);

        this.procesandoOperacion = false;

        this.error = 'No se pudo actualizar la información del empleado.';

        this.confirmacionActiva = null;
      }
    });
  }

  cancelarConfirmacionEdicion(): void {
    this.confirmacionActiva = null;
  }

  abrirDarDeBaja(empleado: Empleado): void {

    this.empleadoSeleccionado = empleado;

    this.cerrarMenuEmpleado();

    this.confirmacionActiva = 'baja';
  }

  cancelarDarDeBaja(): void {

    this.confirmacionActiva = null;

    this.empleadoSeleccionado = null;
  }

  confirmaDarDeBaja(): void {

    if(!this.empleadoSeleccionado){
      return;
    }

    this.procesandoOperacion = true;

    this.error = '';

    this.empleadoService.darDeBaja(this.empleadoSeleccionado.idEmpleado).subscribe({

      next: () => {

        this.procesandoOperacion = false;

        this.mostrarMensajeExito('El empleado fue dado de baja correctamente.');

        this.confirmacionActiva = null;

        this.limpiarSeleccion();

        this.cargarEmpleados();
      },

      error: (error) => {

        console.error('Error al dar de baja al empleado:', error);

        this.procesandoOperacion = false;

        this.error = 'No se pudo dar de baja al empleado.';

        this.confirmacionActiva = null;
      }
    });
  }

  abrirEliminar(empleado: Empleado): void {

    this.empleadoSeleccionado = empleado;

    this.cerrarMenuEmpleado();

    this.confirmacionActiva = 'eliminar';
  }

  cancelarEliminar(): void {

    this.confirmacionActiva = null;

    this.empleadoSeleccionado = null;
  }

  confirmarEliminar(): void {

    if(!this.empleadoSeleccionado){
      return;
    }

    this.procesandoOperacion = true;

    this.error = '';

    this.empleadoService.eliminarEmpleado(this.empleadoSeleccionado.idEmpleado).subscribe({

      next: () => {

        this.procesandoOperacion = false;

        this.mostrarMensajeExito('La cuenta del empleado fue eliminada correctamente.');

        this.confirmacionActiva = null;

        this.limpiarSeleccion();

        this.cargarEmpleados();
      },

      error: (error) => {

        console.error('Error al eliminar la cuenta del empleado:', error);

        this.procesandoOperacion = false;

        this.error = 'No se pudo eliminar la cuenta del empleado.';

        this.confirmacionActiva = null;
      }
    });
  }

  limpiarSeleccion(): void {

    this.empleadoSeleccionado = null;

    this.nuevoEstado = null;
  }

  mostrarMensajeExito(mensaje: string): void {

    this.mensajeExito = mensaje;

    setTimeout(() => {

      this.mensajeExito = '';
    }, 4000);
  }

  obtenerNombreEstado(estado: EstadoEmpleado): string {

    switch (estado) {

      case EstadoEmpleado.ACTIVO:
        return 'Activo';

      case EstadoEmpleado.INACTIVO:
        return 'Inactivo';

      case EstadoEmpleado.LICENCIA:
        return 'Licencia';

      case EstadoEmpleado.SUSPENDIDO:
        return 'Suspendido';

      default: 
        return estado;
    }
  }

  obtenerNombreEspecialidad(especialidad: EspecialidadEmpleado): string {
    return especialidad.replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, letra => letra.toUpperCase());
  }

  campoNuevoEmpleadoInvalido(campo: string): boolean {

    const control = this.formularioNuevoEmpleado.get(campo);

    return !!(control && control.invalid && (control.touched || control.dirty));
  }

  campoEditarEmpleadoInvalido(campo: string): boolean {
    
    const control = this.formularioEditarEmpleado.get(campo);

    return !!(control && control.invalid && (control.touched || control.dirty));
  }

  contarPorEstado(estado: EstadoEmpleado): number {
    return this.empleadosOriginales.filter(empleado => empleado.estadoEmpleado === estado).length;
  }

  get nombreNuevoEmpleado() {
    return this.formularioNuevoEmpleado.get('nombre');
  }

  get apellidoNuevoEmpleado() {
    return this.formularioNuevoEmpleado.get('apellido');
  }

  get emailNuevoEmpleado() {
    return this.formularioNuevoEmpleado.get('email');
  }

  get passwordNuevoEmpleado() {
    return this.formularioNuevoEmpleado.get('password');
  }

  get telefonoNuevoEmpleado() {
    return this.formularioNuevoEmpleado.get('telefono');
  }

  get especialidadNuevoEmpleado() {
    return this.formularioNuevoEmpleado.get('especialidad');
  }

  get hayEmpleados(): boolean {
    return this.empleadosOriginales.length > 0;
  }

  get hayResultados(): boolean {
    return this.empleadosFiltrados.length > 0;
  }

  get hayFiltrosAplicados(): boolean {
    return !!(this.especialidadFiltro || this.estadoFiltro || this.textoBusqueda.trim());
  }

  cerrarModal(): void {

    if(this.procesandoOperacion){
      return;
    }

    this.modalActivo = null;

    this.confirmacionActiva = null;

    this.limpiarSeleccion();
  }
}
