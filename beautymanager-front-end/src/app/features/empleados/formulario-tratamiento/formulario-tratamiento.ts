import { CommonModule } from '@angular/common';
import { Component, OnInit, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { Turno } from '../../../core/models/turno';
import { ObtenerServicio } from '../../../core/models/obtener-servicio';
import { RegistrarTratamiento } from '../../../core/models/registrar-tratamiento';
import { ServicioTratamientoFormulario } from '../../../core/models/servicio-tratamiento-formulario';
import { ResultadoTratamiento } from '../../../core/enums/resultadoTratamiento';

@Component({
  selector: 'app-formulario-tratamiento',
  imports: [CommonModule, FormsModule, MatIconModule],
  templateUrl: './formulario-tratamiento.html',
  styleUrl: './formulario-tratamiento.css',
})
export class FormularioTratamiento implements OnInit {

  @Input({ required: true })
    turno!: Turno;

    @Input()
    serviciosDisponibles: ObtenerServicio[] = [];

    @Input()
    enviando: boolean = false;

    @Input()
    errorServidor: string = '';

    //========================================
    //EVENTOS
    //========================================

    @Output()
    guardarTratamiento = new EventEmitter<RegistrarTratamiento>();

    @Output()
    cancelar = new EventEmitter<void>();

    //========================================
    //FORMULARIO
    //========================================

    observacion: string = '';

    duracionReal: number | null = null;

    productosUtilizados: string = '';

    resultadoTratamiento: ResultadoTratamiento | null = null;

    serviciosRealizados: ServicioTratamientoFormulario[] = [];

    errorFormulario: string = '';

    //========================================
    //RESULTADOS DISPONIBLES
    //========================================

    readonly resultados: {
        valor: ResultadoTratamiento;
        texto: string;
    }[] = [
        {
            valor: ResultadoTratamiento.FINALIZADO_CON_EXITO,
            texto: 'Finalizado con éxito'
        },
        {
            valor: ResultadoTratamiento.FINALIZADO_CON_OBSERVACIONES,
            texto: 'Finalizado con observaciones'
        },
        {
            valor: ResultadoTratamiento.CLIENTE_NO_CONFORME,
            texto: 'Clienta no conforme'
        }
    ];

  ngOnInit(): void {
    this.inicializarFormulario();
  }

  private inicializarFormulario(): void {

        this.observacion = '';

        this.duracionReal = null;

        this.productosUtilizados = '';

        this.resultadoTratamiento = null;

        this.errorFormulario = '';

        this.serviciosRealizados = this.turno.servicios.map(
            servicio => ({

                idServicioRealizado: servicio.id,

                idTurnoServicioReservado:
                    servicio.idTurnoServicio ?? null,

                idServicioOriginal: servicio.id,

                motivoCambio: ''
            })
        );
    }

    //========================================
    //OBTENER OPCIONES DE SERVICIOS
    //========================================

    obtenerOpcionesServicios(): ObtenerServicio[] {

        const servicios = new Map<number, ObtenerServicio>();

        //====================================
        //Servicios originalmente reservados
        //====================================

        for (const servicio of this.turno?.servicios ?? []) {

            servicios.set(
                servicio.id,
                servicio
            );
        }

        //====================================
        //Servicios disponibles del catálogo
        //====================================

        for (const servicio of this.serviciosDisponibles) {

            servicios.set(
                servicio.id,
                servicio
            );
        }

        return Array.from(servicios.values());
    }

    //========================================
    //AGREGAR SERVICIO
    //========================================

    agregarServicio(): void {

        if (this.enviando) {
            return;
        }

        this.serviciosRealizados.push({

            idServicioRealizado: null,

            idTurnoServicioReservado: null,

            idServicioOriginal: null,

            motivoCambio: ''
        });
    }

    //========================================
    //ELIMINAR SERVICIO AGREGADO
    //========================================

    eliminarServicioAgregado(indice: number): void {

        if (this.enviando) {
            return;
        }

        const servicio = this.serviciosRealizados[indice];

        if (!servicio) {
            return;
        }

        //====================================
        //Solo eliminar servicios adicionales
        //====================================

        if (servicio.idServicioOriginal !== null) {
            return;
        }

        this.serviciosRealizados.splice(indice, 1);
    }

    //========================================
    //DETECTAR CAMBIO DE SERVICIO
    //========================================

    huboCambioServicio(
        servicio: ServicioTratamientoFormulario
    ): boolean {

        return (
            servicio.idServicioOriginal === null ||
            servicio.idServicioOriginal !==
                servicio.idServicioRealizado
        );
    }

    //========================================
    //VALIDAR FORMULARIO
    //========================================

    formularioValido(): boolean {

        //====================================
        //Validar turno y envío
        //====================================

        if (!this.turno || this.enviando) {
            return false;
        }

        //====================================
        //Validar duración real
        //====================================

        if (
            this.duracionReal === null ||
            !Number.isInteger(this.duracionReal) ||
            this.duracionReal <= 0
        ) {
            return false;
        }

        //====================================
        //Validar resultado
        //====================================

        if (!this.resultadoTratamiento) {
            return false;
        }

        //====================================
        //Validar longitud de textos
        //====================================

        if (
            this.observacion.length > 3500 ||
            this.productosUtilizados.length > 2500
        ) {
            return false;
        }

        //====================================
        //Validar servicios
        //====================================

        if (this.serviciosRealizados.length === 0) {
            return false;
        }

        const reservasProcesadas = new Set<number>();

        return this.serviciosRealizados.every(servicio => {

            //================================
            //Identificador de servicio
            //================================

            if (
                servicio.idServicioRealizado === null ||
                !Number.isInteger(servicio.idServicioRealizado) ||
                servicio.idServicioRealizado <= 0
            ) {
                return false;
            }

            //================================
            //Servicio originalmente reservado
            //================================

            if (
                servicio.idServicioOriginal !== null &&
                servicio.idTurnoServicioReservado === null
            ) {
                return false;
            }

            //================================
            //Evitar reservas duplicadas
            //================================

            if (servicio.idTurnoServicioReservado !== null) {

                if (
                    reservasProcesadas.has(
                        servicio.idTurnoServicioReservado
                    )
                ) {
                    return false;
                }

                reservasProcesadas.add(
                    servicio.idTurnoServicioReservado
                );
            }

            //================================
            //Validar motivo del cambio
            //================================

            const motivo = servicio.motivoCambio.trim();

            if (motivo.length > 500) {
                return false;
            }

            if (
                this.huboCambioServicio(servicio) &&
                motivo.length === 0
            ) {
                return false;
            }

            return true;
        });
    }

    //========================================
    //ENVIAR FORMULARIO
    //========================================

    enviarFormulario(): void {

        //====================================
        //Validar información
        //====================================

        if (!this.formularioValido()) {

            this.errorFormulario =
                'Completá los campos obligatorios y verificá ' +
                'los servicios, la duración y los motivos de cambio.';

            return;
        }

        this.errorFormulario = '';

        //====================================
        //Preparar datos para el backend
        //====================================

        const datos: RegistrarTratamiento = {

            idTurno: this.turno.id,

            observacion:
                this.observacion.trim() || null,

            duracionReal: this.duracionReal!,

            productosUtilizados:
                this.productosUtilizados.trim() || null,

            resultadoTratamiento: this.resultadoTratamiento!,

            serviciosRealizados: this.serviciosRealizados.map(
                servicio => ({

                    idServicioRealizado:
                        servicio.idServicioRealizado!,

                    idTurnoServicioReservado:
                        servicio.idTurnoServicioReservado,

                    motivoCambio: this.huboCambioServicio(servicio)
                        ? servicio.motivoCambio.trim()
                        : null
                })
            )
        };

        //====================================
        //Enviar datos al componente padre
        //====================================

        this.guardarTratamiento.emit(datos);
    }

    //========================================
    //CANCELAR FORMULARIO
    //========================================

    cancelarFormulario(): void {

        if (this.enviando) {
            return;
        }

        this.cancelar.emit();
    }
}
