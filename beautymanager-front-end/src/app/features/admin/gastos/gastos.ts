import { Component, OnInit, ChangeDetectorRef} from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { finalize } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { MovimientoFinanzas } from '../../../core/services/movimiento-finanzas';
import { TipoGasto } from '../../../core/services/tipo-gasto';
import { CrearGasto } from '../../../core/models/crear-gasto';
import { ActualizarMovimientoFinanzas } from '../../../core/models/actualizar-movimiento-finanzas';
import { FiltroGastos } from '../../../core/models/filtro-gastos';
import { ObtenerMovimientoFinanzas } from '../../../core/models/obtener-movimiento-finanzas';
import { ObtenerTipoGasto } from '../../../core/models/obtener-tipo-gasto';
import { EstadoMovimiento } from '../../../core/enums/estadoMovimiento';
import { TipoGastoEnum } from '../../../core/enums/tipoGasto';
import { MetodoPago } from '../../../core/enums/metodoPago';
import { TipoMovimiento } from '../../../core/enums/tipoMovimiento';
@Component({
  selector: 'app-gastos',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule],
  providers: [DatePipe, CurrencyPipe],
  templateUrl: './gastos.html',
  styleUrl: './gastos.css',
})
export class Gastos implements OnInit {

  gastos: ObtenerMovimientoFinanzas[] = [];

  tiposGasto: ObtenerTipoGasto[] = [];

  gastosFiltrados: ObtenerMovimientoFinanzas[] = [];

  cargando = false;

  guardando = false;

  error = '';

  mensajeExito = '';

  formularioAbierto = false;

  modoEdicion = false;

  gastoSeleccionado: ObtenerMovimientoFinanzas | null = null;

  gastoAEliminar: ObtenerMovimientoFinanzas | null = null;

  gastoDetalle: ObtenerMovimientoFinanzas | null = null;

  paginaActual = 1;

  elementosPorPagina = 8;

  fechaDesde = '';

  fechaHasta = '';

  nombreGasto: TipoGastoEnum | null = null;

  montoMinimo: number | null = null;

  montoMaximo: number | null = null;

  ordenarPor: 'fecha' | 'monto' = 'fecha';

  sentido: 'asc' | 'desc' = 'desc';

  fechaGasto = '';

  descripcion = '';

  monto: number | null = null;

  nombreGastoFormulario: TipoGastoEnum | null = null;

  metodoPagoFormulario: MetodoPago = MetodoPago.BILLETERA;

  constructor(private movimientoFinanzas: MovimientoFinanzas, private tipoGastoService: TipoGasto, private datePipe: DatePipe, private currencyPipe: CurrencyPipe, private cdr: ChangeDetectorRef) {}

  readonly metodosPago = [
    { valor: MetodoPago.EFECTIVO, etiqueta: 'Efectivo' },
    { valor: MetodoPago.TARJETA, etiqueta: 'Tarjeta' },
    { valor: MetodoPago.TRANSFERENCIA, etiqueta: 'Transferencia' },
    { valor: MetodoPago.BILLETERA, etiqueta: 'Billetera virtual' }
  ];

  ngOnInit(): void {

    this.cargarTiposGasto();

    this.cargarGastos();
  }

  cargarGastos(): void {
    this.cargando = true;
    this.error = '';

    const filtros: FiltroGastos = {ordenarPor: this.ordenarPor, sentido: this.sentido};

    if (this.fechaDesde) {
      filtros.fechaDesde = this.convertirFechaDesde(this.fechaDesde);
    }

    if (this.fechaHasta) {
      filtros.fechaHasta = this.convertirFechaHasta(this.fechaHasta);
    }

    if (this.nombreGasto !== null) {
      filtros.nombreGasto = this.nombreGasto;
    }

    if (this.montoMinimo !== null) {
      filtros.montoMinimo = this.montoMinimo;
    }

    if (this.montoMaximo !== null) {
      filtros.montoMaximo = this.montoMaximo;
    }

    this.movimientoFinanzas.listarGastos(filtros).pipe(
      finalize(() => {
        this.cargando = false;
        this.cdr.detectChanges();
      })
    ).subscribe({
        next: (respuesta: ObtenerMovimientoFinanzas[]) => {
          this.gastos = respuesta ?? [];
          this.gastosFiltrados = [...this.gastos];
          this.paginaActual = 1;

          this.cdr.detectChanges();
        },
        error: (error: HttpErrorResponse) => {
          this.error = this.obtenerMensajeError(
            error,
            'No se pudieron cargar los gastos.'
          );

        this.gastos = [];
        this.gastosFiltrados = [];

        this.cdr.detectChanges();
      }
    });
  }

  cargarTiposGasto(): void {

    this.tipoGastoService.listarOrdenados().subscribe({
      
      next: (respuesta: ObtenerTipoGasto[]) => {

        this.tiposGasto = respuesta ?? [];

        this.cdr.detectChanges();
      },

      error: (error: HttpErrorResponse) => {

        this.cdr.detectChanges();

        this.error = this.obtenerMensajeError(error, 'No se pudieron cargar las categorías de gastos.');
      }
    });
  }

  buscarGastos(): void {

    if (this.fechaDesde && this.fechaHasta && this.fechaDesde > this.fechaHasta) {

      this.error = 'La fecha inicial no puede ser posterior a la fecha final.';

      return;
    }

    if (this.montoMinimo !== null && this.montoMinimo < 0) {

      this.error = 'El monto mínimo no puede ser negativo.';

      return;
    }

    if (this.montoMaximo !== null && this.montoMaximo < 0) {

      this.error = 'El monto máximo no puede ser negativo.';

      return;
    }

    if (this.montoMinimo !== null && this.montoMaximo !== null && this.montoMinimo > this.montoMaximo) {

      this.error = 'El monto mínimo no puede superar al monto máximo.';

      return;
    }

    this.cargarGastos();
  }

  limpiarFiltros(): void {

    this.fechaDesde = '';

    this.fechaHasta = '';

    this.nombreGasto = null;

    this.montoMinimo = null;

    this.montoMaximo = null;

    this.ordenarPor = 'fecha';

    this.sentido = 'desc';

    this.cargarGastos();
  }

  cambiarOrden(criterio: 'fecha' | 'monto'): void {

    if (this.ordenarPor === criterio) {

      this.sentido = this.sentido === 'asc' ? 'desc' : 'asc';
    } else {

      this.ordenarPor = criterio;

      this.sentido = 'desc';
    }

    this.cargarGastos();
  }

  get totalPeriodo(): number {
    return this.gastos.reduce((total, gasto) => total + Number(gasto.monto || 0), 0);
  }

  get totalMesActual(): number {

    const ahora = new Date();

    return this.gastos.filter(gasto => {

      const fecha = new Date(gasto.fechaGasto);

      return fecha.getFullYear() === ahora.getFullYear() && fecha.getMonth() === ahora.getMonth();
    }).reduce((total, gasto) => total + Number(gasto.monto || 0), 0);
  }

  get promedioDiario(): number {

    if (this.gastos.length === 0) {
      return 0;
    }

    const fechas = this.gastos.map(gasto => this.fechaLocal(gasto.fechaGasto)).filter((fecha): fecha is Date => fecha !== null);

    if (fechas.length === 0) {
      return 0;
    }

    const dias = new Set(fechas.map(fecha => `${fecha.getFullYear()}-${fecha.getMonth()}-${fecha.getDate()}`));

    const total = this.gastos.reduce((suma, gasto) => suma + Number(gasto.monto || 0), 0);

    return dias.size > 0 ? total / dias.size : 0;
  }

  get gastoMayor(): number {

    if (this.gastos.length === 0) {
      return 0;
    }

    return Math.max(...this.gastos.map(gasto => Number(gasto.monto || 0)));
  }

  abrirFormulario(): void {

    this.limpiarFormulario();

    this.modoEdicion = false;

    this.gastoSeleccionado = null;

    this.formularioAbierto = true;
  }

  abrirEdicion(gasto: ObtenerMovimientoFinanzas): void {

    this.limpiarFormulario();

    this.gastoSeleccionado = gasto;

    this.modoEdicion = true;

    this.fechaGasto = this.convertirAFechaHoraLocal(gasto.fechaGasto);

    this.descripcion = gasto.descripcion;

    this.monto = Number(gasto.monto);

    this.nombreGastoFormulario = gasto.nombreGasto;

    this.metodoPagoFormulario = gasto.metodoPago;

    this.formularioAbierto = true;
  }

  cerrarFormulario(): void {

    if (this.guardando) {
      return;
    }

    this.formularioAbierto = false;

    this.gastoSeleccionado = null;

    this.modoEdicion = false;
  }

  limpiarFormulario(): void {

    this.fechaGasto = new Date(Date.now() - new Date().getTimezoneOffset() * 60000).toISOString().slice(0, 16);

    this.descripcion = '';

    this.monto = null;

    this.nombreGastoFormulario = null;

    this.metodoPagoFormulario = MetodoPago.BILLETERA;
  }

  guardarGasto(): void {

    this.error = '';

    this.mensajeExito = '';

    if (!this.validarFormulario()) {

      this.cerrarFormulario();

      return;
    }

    this.guardando = true;

    if (this.modoEdicion && this.gastoSeleccionado) {

      this.actualizarGastoSeleccionado();
    } else {

      this.registrarGasto();
    }
  }

  private registrarGasto(): void {

    const gasto: CrearGasto = {

      fechaGasto: this.convertirFechaHoraParaBackend(this.fechaGasto),

      monto: this.monto!,

      descripcion: this.descripcion.trim(),

      metodoPago: this.metodoPagoFormulario,

      nombreGasto: this.nombreGastoFormulario!
    };

    this.movimientoFinanzas.crearGasto(gasto).pipe(finalize(() => {

        this.guardando = false;

        this.formularioAbierto = false;

        this.cdr.detectChanges();
      })
    ).subscribe({

      next: () => {

        this.formularioAbierto = false;

        this.mensajeExito = 'El gasto se registró correctamente.';

        this.cargarGastos();

        this.cdr.detectChanges();
      },
      error: (error: HttpErrorResponse) => {

        this.cdr.detectChanges();

        const mensajeBackend = this.obtenerMensajeError(error, 'No se pudo registrar el gasto')

        if(this.metodoPagoFormulario === MetodoPago.BILLETERA && mensajeBackend.toLowerCase().includes('saldo insuficiente')) {
          this.error = 'No se pudo concretar el pago. El saldo de tu billetera es insuficiente';
        } else {
          this.error = mensajeBackend;
        }
      }
    });
  }

  private actualizarGastoSeleccionado(): void {

    const gastoOriginal = this.gastoSeleccionado!;

    const datos: ActualizarMovimientoFinanzas = {

      fechaGasto: this.convertirFechaHoraParaBackend(this.fechaGasto),

      monto: this.monto!,

      tipoMovimiento: TipoMovimiento.EGRESO,

      descripcion: this.descripcion.trim(),

      estado: this.estadoActual(gastoOriginal),

      nombreGasto: this.nombreGastoFormulario!,

      idCompra: gastoOriginal.idCompra,

      metodoPago: this.metodoPagoFormulario
    };

    this.movimientoFinanzas.actualizarMovimiento(gastoOriginal.idMovimiento, datos).pipe(finalize(() => {

        this.guardando = false;

        this.cdr.detectChanges();
      })
    ).subscribe({

      next: () => {

        this.formularioAbierto = false;

        this.gastoSeleccionado = null;

        this.mensajeExito = 'El gasto se actualizó correctamente.';

        this.cargarGastos();

        this.cdr.detectChanges();
      },

      error: (error: HttpErrorResponse) => {

        this.cdr.detectChanges();

        this.error = this.obtenerMensajeError(error, 'No se pudo actualizar el gasto.');
      }
    });
  }

  private validarFormulario(): boolean {

    if (!this.descripcion.trim() && this.monto === null && this.nombreGastoFormulario === null) {

      this.error = 'Ingrese todos los datos'

      return false;
    }

    if (!this.fechaGasto) {

      this.error = 'Seleccioná la fecha y hora del gasto.';

      return false;
    }

    if (!this.descripcion.trim()) {

      this.error = 'Ingresá una descripción para el gasto.';

      return false;
    }

    if (this.monto === null || !Number.isFinite(this.monto) || this.monto <= 0) {

      this.error = 'El importe debe ser mayor que cero.';

      return false;
    }

    if (this.nombreGastoFormulario === null) {

      this.error = 'Seleccioná una categoría de gasto.';

      return false;
    }

    if (!Object.values(MetodoPago).includes(this.metodoPagoFormulario)) {

      this.error = 'Seleccioná un método de pago válido.';

      return false;
    }

    return true;
  }

  verDetalle(gasto: ObtenerMovimientoFinanzas): void {
    this.gastoDetalle = gasto;
  }

  cerrarDetalle(): void {
    this.gastoDetalle = null;
  }

  solicitarEliminacion(gasto: ObtenerMovimientoFinanzas): void {
    this.gastoAEliminar = gasto;
  }

  cancelarEliminacion(): void {
    this.gastoAEliminar = null;
  }

  confirmarEliminacion(): void {

    if (!this.gastoAEliminar) {
      return;
    }

    const id = this.gastoAEliminar.idMovimiento;

    this.error = '';

    this.mensajeExito = '';

    this.movimientoFinanzas.eliminarMovimiento(id).subscribe({

      next: () => {

        this.gastoAEliminar = null;

        this.mensajeExito = 'El gasto se eliminó correctamente.';

        this.cargarGastos();

        this.cdr.detectChanges();
      },
      error: (err) => {

        this.cdr.detectChanges();

        this.error = this.obtenerMensajeError(err, 'No se pudo eliminar el gasto.');

        this.gastoAEliminar = null;
      }
    });
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.gastosFiltrados.length / this.elementosPorPagina));
  }

  get gastosPagina(): ObtenerMovimientoFinanzas[] {

    const inicio = (this.paginaActual - 1) * this.elementosPorPagina;

    return this.gastosFiltrados.slice(inicio, inicio + this.elementosPorPagina);
  }

  get totalGastos(): number {
    return this.gastosFiltrados.length;
  }

  cambiarPagina(pagina: number): void {

    if (pagina < 1 || pagina > this.totalPaginas) {
      return;
    }

    this.paginaActual = pagina;
  }

  get paginas(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, indice) => indice + 1);
  }

  formatearImporte(importe: number | null | undefined): string {
    return this.currencyPipe.transform(importe ?? 0, 'ARS', 'symbol', '1.2-2', 'es-AR') ?? '$0,00';
  }

  formatearFecha(fecha: string | null | undefined): string {
    return fecha ? this.datePipe.transform(fecha, 'dd/MM/yyyy', undefined, 'es-AR') ?? '—' : '—';
  }

  formatearHora(fecha: string | null | undefined): string {
    return fecha ? this.datePipe.transform(fecha, 'HH:mm', undefined, 'es-AR') ?? '—' : '—';
  }

  etiquetaMetodoPago(metodo: MetodoPago): string {

    const etiquetas: Record<MetodoPago, string> = {
      [MetodoPago.EFECTIVO]: 'Efectivo',
      [MetodoPago.TARJETA]: 'Tarjeta',
      [MetodoPago.TRANSFERENCIA]: 'Transferencia',
      [MetodoPago.BILLETERA]: 'Billetera virtual'
    };

    return etiquetas[metodo] ?? metodo;
  }

  etiquetaEstado(estado: EstadoMovimiento): string {

    const etiquetas: Record<EstadoMovimiento, string> = {
      [EstadoMovimiento.PENDIENTE]: 'Pendiente',
      [EstadoMovimiento.CONFIRMADO]: 'Confirmado',
      [EstadoMovimiento.ANULADO]: 'Anulado'
    };

    return etiquetas[estado] ?? estado;
  }

  private estadoActual(gasto: ObtenerMovimientoFinanzas): EstadoMovimiento {
    return gasto.estadoMovimiento;
  }

  private convertirFechaDesde(fecha: string): string {
    return `${fecha}T00:00:00`;
  }

  private convertirFechaHasta(fecha: string): string {
    return `${fecha}T23:59:59`;
  }

  private convertirAFechaHoraLocal(fecha: string): string {

    const valor = this.fechaLocal(fecha);

    if (!valor) {
      return '';
    }

    const dosDigitos = (numero: number): string => String(numero).padStart(2, '0');

    return `${valor.getFullYear()}-${dosDigitos(valor.getMonth() + 1)}-${dosDigitos(valor.getDate())}T${dosDigitos(valor.getHours())}:${dosDigitos(valor.getMinutes())}`;
  }

  private convertirFechaHoraParaBackend(fecha: string): string {
    return fecha.length === 16 ? `${fecha}:00` : fecha;
  }

  private fechaLocal(fecha: string): Date | null {

    if (!fecha) {
      return null;
    }

    const valor = new Date(fecha);

    return Number.isNaN(valor.getTime()) ? null : valor;
  }

  private obtenerMensajeError(err: any, mensajePredeterminado: string): string {
    return err?.error?.message ?? (typeof err?.error === 'string' ? err.error : null) ?? err?.message ?? mensajePredeterminado;
  }
}
