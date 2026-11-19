import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { ObtenerHistorialTratamiento } from '../../../core/models/obtener-historial-tratamiento';
import { ResultadoTratamiento } from '../../../core/enums/resultadoTratamiento';
import { HistorialTratamientoService } from '../../../core/services/historial-tratamiento';

@Component({
  selector: 'app-tratamientos',
  imports: [CommonModule, FormsModule, MatIconModule],
  templateUrl: './tratamientos.html',
  styleUrl: './tratamientos.css',
})
export class TratamientosEmpleado implements OnInit {

  tratamientos: ObtenerHistorialTratamiento[] = [];

    tratamientosFiltrados: ObtenerHistorialTratamiento[] = [];

    tratamientoSeleccionado: ObtenerHistorialTratamiento | null = null;

    //========================================
    //ESTADOS DE CARGA
    //========================================

    cargandoTratamientos: boolean = false;

    errorCarga: string = '';

    //========================================
    //FILTROS
    //========================================

    textoBusqueda: string = '';

    filtroResultado: ResultadoTratamiento | '' = '';

    fechaDesde: string = '';

    fechaHasta: string = '';

    mostrarFiltros: boolean = false;

    readonly ResultadoTratamiento = ResultadoTratamiento;

    constructor(
        private historialTratamientoService: HistorialTratamientoService,
        private cdr: ChangeDetectorRef
    ) {}


    //========================================
    //INICIALIZACIÓN
    //========================================

    ngOnInit(): void {

        this.cargarTratamientos();
    }


    //========================================
    //CARGAR MIS TRATAMIENTOS
    //========================================

    cargarTratamientos(): void {

        if (this.cargandoTratamientos) {
            return;
        }

        this.cargandoTratamientos = true;

        this.errorCarga = '';

        this.historialTratamientoService
            .misTratamientosEmpleado()
            .subscribe({

                next: (tratamientos) => {

                    //================================
                    //ORDENAR DEL MÁS RECIENTE
                    //================================

                    this.tratamientos = [...tratamientos].sort(
                        (a, b) =>
                            new Date(b.fecha).getTime() -
                            new Date(a.fecha).getTime()
                    );

                    //================================
                    //APLICAR FILTROS
                    //================================

                    this.aplicarFiltros();

                    //================================
                    //ACTUALIZAR DETALLE ABIERTO
                    //================================

                    if (this.tratamientoSeleccionado) {

                        const id =
                            this.tratamientoSeleccionado.idHistorial;

                        this.tratamientoSeleccionado =
                            this.tratamientos.find(
                                tratamiento =>
                                    tratamiento.idHistorial === id
                            ) ?? null;
                    }

                    this.cargandoTratamientos = false;

                    this.cdr.detectChanges();
                },

                error: (error) => {

                    console.error(
                        'Error al cargar los tratamientos:',
                        error
                    );

                    this.tratamientos = [];

                    this.tratamientosFiltrados = [];

                    this.tratamientoSeleccionado = null;

                    this.cargandoTratamientos = false;

                    this.errorCarga =
                        error?.error?.mensaje ??
                        error?.error?.message ??
                        'No se pudieron cargar tus tratamientos. ' +
                        'Intentá nuevamente.';

                    this.cdr.detectChanges();
                }
            });
    }


    //========================================
    //APLICAR FILTROS
    //========================================

    aplicarFiltros(): void {

        const texto = this.textoBusqueda
            .trim()
            .toLowerCase();

        this.tratamientosFiltrados =
            this.tratamientos.filter(tratamiento => {

                //================================
                //FILTRAR POR RESULTADO
                //================================

                if (
                    this.filtroResultado &&
                    tratamiento.resultadoTratamiento !==
                        this.filtroResultado
                ) {
                    return false;
                }

                //================================
                //FILTRAR POR FECHA
                //================================

                const fechaTratamiento =
                    tratamiento.fecha.substring(0, 10);

                if (
                    this.fechaDesde &&
                    fechaTratamiento < this.fechaDesde
                ) {
                    return false;
                }

                if (
                    this.fechaHasta &&
                    fechaTratamiento > this.fechaHasta
                ) {
                    return false;
                }

                //================================
                //FILTRAR POR TEXTO
                //================================

                if (texto) {

                    const cliente =
                        this.obtenerNombreCliente(tratamiento)
                            .toLowerCase();

                    const serviciosReservados =
                        (tratamiento.servicios ?? [])
                            .join(' ')
                            .toLowerCase();

                    const serviciosRealizados =
                        (tratamiento.serviciosRealizados ?? [])
                            .map(servicio =>
                                servicio.nombreServicioRealizado
                            )
                            .join(' ')
                            .toLowerCase();

                    const coincide =
                        cliente.includes(texto) ||
                        serviciosReservados.includes(texto) ||
                        serviciosRealizados.includes(texto);

                    if (!coincide) {
                        return false;
                    }
                }

                return true;
            });
    }


    //========================================
    //LIMPIAR FILTROS
    //========================================

    limpiarFiltros(): void {

        this.textoBusqueda = '';

        this.filtroResultado = '';

        this.fechaDesde = '';

        this.fechaHasta = '';

        this.aplicarFiltros();
    }


    //========================================
    //MOSTRAR / OCULTAR FILTROS
    //========================================

    alternarFiltros(): void {

        this.mostrarFiltros = !this.mostrarFiltros;
    }


    //========================================
    //RESUMEN DE TRATAMIENTOS
    //========================================

    get totalTratamientos(): number {

        return this.tratamientos.length;
    }


    get tratamientosExitosos(): number {

        return this.tratamientos.filter(
            tratamiento =>
                tratamiento.resultadoTratamiento ===
                ResultadoTratamiento.FINALIZADO_CON_EXITO
        ).length;
    }


    get tratamientosConObservaciones(): number {

        return this.tratamientos.filter(
            tratamiento =>
                tratamiento.resultadoTratamiento ===
                ResultadoTratamiento.FINALIZADO_CON_OBSERVACIONES
        ).length;
    }


    get clientesNoConformes(): number {

        return this.tratamientos.filter(
            tratamiento =>
                tratamiento.resultadoTratamiento ===
                ResultadoTratamiento.CLIENTE_NO_CONFORME
        ).length;
    }


    //========================================
    //INFORMACIÓN DE LA CLIENTA
    //========================================

    obtenerNombreCliente(
        tratamiento: ObtenerHistorialTratamiento
    ): string {

        return [
            tratamiento.nombreCliente,
            tratamiento.apellidoCliente
        ]
            .filter(Boolean)
            .join(' ') || 'Clienta sin nombre';
    }


    //========================================
    //SERVICIOS REALIZADOS
    //========================================

    obtenerServiciosRealizados(
        tratamiento: ObtenerHistorialTratamiento
    ): string {

        const servicios =
            tratamiento.serviciosRealizados ?? [];

        if (servicios.length === 0) {
            return 'Sin detalle de servicios realizados';
        }

        return servicios
            .map(servicio => servicio.nombreServicioRealizado)
            .join(', ');
    }


    //========================================
    //CANTIDAD DE SERVICIOS REALIZADOS
    //========================================

    obtenerCantidadServicios(
        tratamiento: ObtenerHistorialTratamiento
    ): number {

        return tratamiento.serviciosRealizados?.length ?? 0;
    }


    //========================================
    //IDENTIFICAR CAMBIOS DE SERVICIOS
    //========================================

    tieneCambiosServicios(
        tratamiento: ObtenerHistorialTratamiento
    ): boolean {

        return (tratamiento.serviciosRealizados ?? [])
            .some(servicio =>
                servicio.motivoCambio != null &&
                servicio.motivoCambio.trim().length > 0
            );
    }


    //========================================
    //TEXTO DEL RESULTADO
    //========================================

    obtenerTextoResultado(
        resultado: ResultadoTratamiento | null
    ): string {

        switch (resultado) {

            case ResultadoTratamiento.FINALIZADO_CON_EXITO:
                return 'Finalizado con éxito';

            case ResultadoTratamiento.FINALIZADO_CON_OBSERVACIONES:
                return 'Con observaciones';

            case ResultadoTratamiento.CLIENTE_NO_CONFORME:
                return 'Clienta no conforme';

            default:
                return 'Sin resultado registrado';
        }
    }


    //========================================
    //CLASE CSS DEL RESULTADO
    //========================================

    obtenerClaseResultado(
        resultado: ResultadoTratamiento | null
    ): string {

        switch (resultado) {

            case ResultadoTratamiento.FINALIZADO_CON_EXITO:
                return 'exitoso';

            case ResultadoTratamiento.FINALIZADO_CON_OBSERVACIONES:
                return 'observaciones';

            case ResultadoTratamiento.CLIENTE_NO_CONFORME:
                return 'no-conforme';

            default:
                return 'sin-resultado';
        }
    }


    //========================================
    //FORMATEAR FECHA Y HORA
    //========================================

    formatearFechaHora(fecha: string): string {

        if (!fecha) {
            return 'Fecha no disponible';
        }

        const valor = new Date(fecha);

        if (Number.isNaN(valor.getTime())) {
            return 'Fecha no disponible';
        }

        return valor.toLocaleString('es-AR', {
            day: '2-digit',
            month: 'long',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
        });
    }


    //========================================
    //ABRIR DETALLE DEL TRATAMIENTO
    //========================================

    abrirDetalle(
        tratamiento: ObtenerHistorialTratamiento
    ): void {

        this.tratamientoSeleccionado = tratamiento;
    }


    //========================================
    //CERRAR DETALLE DEL TRATAMIENTO
    //========================================

    cerrarDetalle(): void {

        this.tratamientoSeleccionado = null;
    }
}
