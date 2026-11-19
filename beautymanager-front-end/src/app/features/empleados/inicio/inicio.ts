import { ChangeDetectorRef, Component, computed, OnInit, signal } from '@angular/core';
import { Turno } from '../../../core/models/turno';
import { CONSEJOS_PROFESIONALES } from './data/consejos';
import { ArticuloProfesional, ARTICULOS_PROFESIONALES, CATEGORIAS_ARTICULOS_PROFESIONALES } from './data/articulos';
import { TAREAS_PREPARACION } from './data/preparacion';
import { TurnoService } from '../../../core/services/turno';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-inicio-empleado',
  imports: [MatIcon],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css',
})
export class InicioEmpleado implements OnInit {

  fechaActual = signal<Date>(new Date());

    fechaFormateada = computed(() => {

        return new Intl.DateTimeFormat('es-AR', {
            weekday: 'long',
            day: 'numeric',
            month: 'long',
            year: 'numeric'
        }).format(this.fechaActual());
    });


    //========================================
    //TURNOS DE LA EMPLEADA
    //========================================

    turnos = signal<Turno[]>([]);

    cargandoTurnos = signal<boolean>(false);

    errorTurnos = signal<string>('');


    //========================================
    //TURNOS DEL DÍA
    //========================================

    turnosHoy = computed(() => {

        const hoy = this.fechaActual();

        const estadosValidos = [
            'PENDIENTE',
            'CONFIRMADO',
            'EN_CURSO',
            'FINALIZADO',
            'AUSENTE'
        ];

        return this.turnos()
            .filter(turno => {

                return estadosValidos.includes(
                    String(turno.estadoTurno)
                ) && this.esMismoDia(
                    turno.fechaHoraInicio,
                    hoy
                );
            })
            .sort((a, b) => {

                return new Date(a.fechaHoraInicio).getTime() -
                    new Date(b.fechaHoraInicio).getTime();
            });
    });


    //========================================
    //CANTIDAD DE TURNOS DEL DÍA
    //========================================

    cantidadTurnosHoy = computed(() => {

        return this.turnosHoy().length;
    });


    //========================================
    //TURNOS PENDIENTES
    //========================================

    cantidadTurnosPendientes = computed(() => {

        return this.turnosHoy().filter(turno => {

            const estado = String(turno.estadoTurno);

            return estado === 'PENDIENTE' ||
                estado === 'CONFIRMADO';

        }).length;
    });


    //========================================
    //TURNOS FINALIZADOS
    //========================================

    cantidadTurnosFinalizados = computed(() => {

        return this.turnosHoy().filter(turno => {

            return String(turno.estadoTurno) === 'FINALIZADO';

        }).length;
    });


    //========================================
    //PRÓXIMA ATENCIÓN
    //========================================

    proximaAtencion = computed<Turno | null>(() => {

        const ahora = this.fechaActual().getTime();

        const disponibles = this.turnos()
            .filter(turno => {

                const estado = String(turno.estadoTurno);

                const inicio = new Date(
                    turno.fechaHoraInicio
                ).getTime();

                const fin = new Date(
                    turno.fechaHoraFin
                ).getTime();

                if (
                    !Number.isFinite(inicio) ||
                    !Number.isFinite(fin)
                ) {
                    return false;
                }

                //Atención actualmente en curso.
                if (estado === 'EN_CURSO') {

                    return fin > ahora;
                }

                //Próximas atenciones.
                return (
                    estado === 'PENDIENTE' ||
                    estado === 'CONFIRMADO'
                ) && inicio >= ahora;
            })
            .sort((a, b) => {

                const aEnCurso =
                    String(a.estadoTurno) === 'EN_CURSO';

                const bEnCurso =
                    String(b.estadoTurno) === 'EN_CURSO';

                if (aEnCurso !== bEnCurso) {

                    return aEnCurso ? -1 : 1;
                }

                return new Date(a.fechaHoraInicio).getTime() -
                    new Date(b.fechaHoraInicio).getTime();
            });

        return disponibles[0] ?? null;
    });


    //========================================
    //CONSEJOS PROFESIONALES
    //========================================

    readonly consejos = CONSEJOS_PROFESIONALES;


    //========================================
    //CONSEJO DEL DÍA
    //========================================

    readonly consejoDelDia = computed(() => {

        const hoy = this.fechaActual();

        const fechaNumerica =
            hoy.getFullYear() * 10000 +
            (hoy.getMonth() + 1) * 100 +
            hoy.getDate();

        const indice =
            fechaNumerica % this.consejos.length;

        return this.consejos[indice];
    });


    //========================================
    //BIBLIOTECA PROFESIONAL
    //========================================

    readonly articulos = ARTICULOS_PROFESIONALES;

    readonly categoriasArticulos =
        CATEGORIAS_ARTICULOS_PROFESIONALES;

    categoriaSeleccionada = signal<string>('Todos');

    articuloSeleccionado =
        signal<ArticuloProfesional | null>(null);


    //========================================
    //FILTRAR ARTÍCULOS
    //========================================

    articulosFiltrados = computed(() => {

        const categoria = this.categoriaSeleccionada();

        if (categoria === 'Todos') {

            return this.articulos;
        }

        return this.articulos.filter(
            articulo => articulo.categoria === categoria
        );
    });


    //========================================
    //PREPARACIÓN DE LA JORNADA
    //========================================

    readonly tareasPreparacion = TAREAS_PREPARACION;

    tareasCompletadas = signal<string[]>([]);


    //========================================
    //CANTIDAD DE TAREAS COMPLETADAS
    //========================================

    cantidadTareasCompletadas = computed(() => {

        return this.tareasCompletadas().length;
    });


    //========================================
    //PORCENTAJE DE PREPARACIÓN
    //========================================

    porcentajePreparacion = computed(() => {

        const total = this.tareasPreparacion.length;

        if (total === 0) {
            return 0;
        }

        return Math.round(
            this.cantidadTareasCompletadas() * 100 / total
        );
    });


    //========================================
    //PREPARACIÓN COMPLETADA
    //========================================

    preparacionCompletada = computed(() => {

        return this.tareasPreparacion.length > 0 &&
            this.cantidadTareasCompletadas() ===
                this.tareasPreparacion.length;
    });


    //========================================
    //CONSTRUCTOR
    //========================================

    constructor(
        private turnoService: TurnoService,
        private cdr: ChangeDetectorRef
    ) {}


    //========================================
    //INICIALIZACIÓN
    //========================================

    ngOnInit(): void {

        this.cargarTurnos();

        this.cargarProgresoPreparacion();
    }


    //========================================
    //CARGAR TURNOS DE LA EMPLEADA
    //========================================

    cargarTurnos(): void {

        if (this.cargandoTurnos()) {
            return;
        }

        this.cargandoTurnos.set(true);

        this.errorTurnos.set('');

        this.turnoService
            .obtenerMisTurnosEmpleado()
            .subscribe({

                next: (respuesta) => {

                    this.turnos.set(respuesta);

                    this.cargandoTurnos.set(false);

                    this.cdr.detectChanges();
                },

                error: (error) => {

                    console.error(
                        'Error al obtener los turnos de la empleada:',
                        error
                    );

                    this.turnos.set([]);

                    this.errorTurnos.set(
                        'No se pudieron consultar los turnos.'
                    );

                    this.cargandoTurnos.set(false);

                    this.cdr.detectChanges();
                }
            });
    }


    //========================================
    //ACTUALIZAR LA JORNADA
    //========================================

    actualizarJornada(): void {

        const fechaAnterior =
            this.obtenerFechaClave(this.fechaActual());

        const nuevaFecha = new Date();

        this.fechaActual.set(nuevaFecha);

        const fechaNueva =
            this.obtenerFechaClave(nuevaFecha);

        if (fechaAnterior !== fechaNueva) {

            this.cargarProgresoPreparacion();
        }

        this.cargarTurnos();
    }


    //========================================
    //COMPROBAR SI ES EL MISMO DÍA
    //========================================

    private esMismoDia(
        fechaTexto: string,
        referencia: Date
    ): boolean {

        const fecha = new Date(fechaTexto);

        if (!Number.isFinite(fecha.getTime())) {

            return false;
        }

        return fecha.getFullYear() ===
                referencia.getFullYear() &&
            fecha.getMonth() ===
                referencia.getMonth() &&
            fecha.getDate() ===
                referencia.getDate();
    }


    //========================================
    //FORMATEAR HORA
    //========================================

    obtenerHora(fechaTexto: string): string {

        const fecha = new Date(fechaTexto);

        if (!Number.isFinite(fecha.getTime())) {

            return '--:--';
        }

        return new Intl.DateTimeFormat('es-AR', {
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
        }).format(fecha);
    }


    //========================================
    //FORMATEAR FECHA
    //========================================

    obtenerFecha(fechaTexto: string): string {

        const fecha = new Date(fechaTexto);

        if (!Number.isFinite(fecha.getTime())) {

            return 'Fecha no disponible';
        }

        return new Intl.DateTimeFormat('es-AR', {
            day: 'numeric',
            month: 'long',
            year: 'numeric'
        }).format(fecha);
    }


    //========================================
    //SELECCIONAR CATEGORÍA
    //========================================

    seleccionarCategoria(categoria: string): void {

        if (!this.categoriasArticulos.includes(categoria)) {

            return;
        }

        this.categoriaSeleccionada.set(categoria);
    }


    //========================================
    //ABRIR ARTÍCULO
    //========================================

    abrirArticulo(articulo: ArticuloProfesional): void {

        this.articuloSeleccionado.set(articulo);
    }


    //========================================
    //CERRAR ARTÍCULO
    //========================================

    cerrarArticulo(): void {

        this.articuloSeleccionado.set(null);
    }


    //========================================
    //VERIFICAR TAREA COMPLETADA
    //========================================

    tareaCompletada(idTarea: string): boolean {

        return this.tareasCompletadas().includes(idTarea);
    }


    //========================================
    //MARCAR O DESMARCAR TAREA
    //========================================

    alternarTarea(idTarea: string): void {

        const existe = this.tareasPreparacion.some(
            tarea => tarea.id === idTarea
        );

        if (!existe) {
            return;
        }

        this.tareasCompletadas.update(actuales => {

            if (actuales.includes(idTarea)) {

                return actuales.filter(
                    id => id !== idTarea
                );
            }

            return [...actuales, idTarea];
        });

        this.guardarProgresoPreparacion();
    }


    //========================================
    //REINICIAR PREPARACIÓN
    //========================================

    reiniciarPreparacion(): void {

        this.tareasCompletadas.set([]);

        this.guardarProgresoPreparacion();
    }


    //========================================
    //OBTENER FECHA PARA ALMACENAMIENTO
    //========================================

    private obtenerFechaClave(fecha: Date): string {

        return (
            fecha.getFullYear() + '-' +
            String(fecha.getMonth() + 1).padStart(2, '0') +
            '-' +
            String(fecha.getDate()).padStart(2, '0')
        );
    }


    //========================================
    //OBTENER CLAVE DE ALMACENAMIENTO
    //========================================

    private obtenerClavePreparacion(): string {

        const fecha = this.obtenerFechaClave(
            this.fechaActual()
        );

        let idUsuario = 'anonimo';

        try {

            const sesion = localStorage.getItem('usuario');

            if (sesion) {

                const usuario = JSON.parse(sesion);

                if (usuario?.idUsuario != null) {

                    idUsuario = String(usuario.idUsuario);
                }
            }

        } catch {

            //Se conserva la clave predeterminada.
        }

        return (
            'beautymanager-preparacion-empleado-' +
            idUsuario + '-' + fecha
        );
    }


    //========================================
    //GUARDAR PROGRESO
    //========================================

    private guardarProgresoPreparacion(): void {

        try {

            localStorage.setItem(
                this.obtenerClavePreparacion(),
                JSON.stringify(this.tareasCompletadas())
            );

        } catch {

            //La lista continúa funcionando
            //aunque no se permita almacenar datos.
        }
    }


    //========================================
    //CARGAR PROGRESO
    //========================================

    private cargarProgresoPreparacion(): void {

        this.tareasCompletadas.set([]);

        try {

            const guardado = localStorage.getItem(
                this.obtenerClavePreparacion()
            );

            if (!guardado) {
                return;
            }

            const datos: unknown = JSON.parse(guardado);

            if (!Array.isArray(datos)) {
                return;
            }

            const idsValidos = this.tareasPreparacion.map(
                tarea => tarea.id
            );

            const completadas = datos.filter(
                (id): id is string =>
                    typeof id === 'string' &&
                    idsValidos.includes(id)
            );

            this.tareasCompletadas.set(
                [...new Set(completadas)]
            );

        } catch {

            this.tareasCompletadas.set([]);
        }
    }
}
