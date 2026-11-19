import { Component,OnInit, computed, signal, ChangeDetectorRef } from '@angular/core';

import { MatIcon } from '@angular/material/icon';
import { ConsejoBelleza, CONSEJOS_BELLEZA } from './data/consejos';
import { ArticuloInicio, ARTICULOS_BELLEZA, CATEGORIAS_ARTICULOS } from './data/articulos';
import { RUTINAS_BELLEZA } from './data/rutinas';
import { INSPIRACIONES_BELLEZA } from './data/inspiraciones';

@Component({
  selector: 'app-inicio-cliente',
  imports: [MatIcon],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css',
})
export class InicioCliente {

  readonly consejos = CONSEJOS_BELLEZA;

    readonly articulos = ARTICULOS_BELLEZA;

    readonly categorias = CATEGORIAS_ARTICULOS;

    readonly rutinas = RUTINAS_BELLEZA;

    readonly inspiraciones = INSPIRACIONES_BELLEZA;


    //========================
    //Consejo del día
    //========================
    readonly consejoDelDia = signal<ConsejoBelleza>(
        this.consejos[0]
    );


    //========================
    //Artículos
    //========================
    categoriaSeleccionada = signal('Todos');

    articuloSeleccionado =
        signal<ArticuloInicio | null>(null);


    //========================
    //Filtrar artículos
    //========================
    readonly articulosFiltrados = computed(() => {

        const categoria = this.categoriaSeleccionada();

        if(categoria === 'Todos') {

            return this.articulos;
        }

        return this.articulos.filter(
            articulo => articulo.categoria === categoria
        );
    });

    //========================
    //Rutinas de autocuidado
    //========================
    rutinaSeleccionada = signal('manana');

    progresoRutinas =
        signal<Record<string, string[]>>({});


    //========================
    //Rutina actual
    //========================
    readonly rutinaActual = computed(() => {

        return this.rutinas.find(
            rutina => rutina.id === this.rutinaSeleccionada()
        ) ?? this.rutinas[0];
    });


    //========================
    //Cantidad de pasos completados
    //========================
    readonly cantidadPasosCompletados = computed(() => {

        const rutina = this.rutinaActual();

        return this.progresoRutinas()[rutina.id]?.length ?? 0;
    });


    //========================
    //Porcentaje de rutina
    //========================
    readonly porcentajeRutina = computed(() => {

        const total = this.rutinaActual().pasos.length;

        if(total === 0) {
            return 0;
        }

        return Math.round(
            this.cantidadPasosCompletados() * 100 / total
        );
    });


    //========================
    //Constructor
    //========================
    constructor(
        private cdr: ChangeDetectorRef
    ) {}


    //========================
    //Inicialización
    //========================
    ngOnInit(): void {

        this.seleccionarConsejoDelDia();

        this.cargarProgresoRutinas();

        this.cdr.markForCheck();
    }


    //========================
    //Seleccionar consejo del día
    //========================
    private seleccionarConsejoDelDia(): void {

        const hoy = new Date();

        const fecha =
            hoy.getFullYear() * 10000 +
            (hoy.getMonth() + 1) * 100 +
            hoy.getDate();

        const indice = fecha % this.consejos.length;

        this.consejoDelDia.set(
            this.consejos[indice]
        );
    }


    //========================
    //Seleccionar categoría
    //========================
    seleccionarCategoria(categoria: string): void {

        if(!this.categorias.includes(categoria)) {
            return;
        }

        this.categoriaSeleccionada.set(categoria);

        this.cdr.markForCheck();
    }


    //========================
    //Abrir artículo
    //========================
    abrirArticulo(articulo: ArticuloInicio): void {

        this.articuloSeleccionado.set(articulo);

        this.cdr.markForCheck();
    }


    //========================
    //Cerrar artículo
    //========================
    cerrarArticulo(): void {

        this.articuloSeleccionado.set(null);

        this.cdr.markForCheck();
    }

    //========================
    //Seleccionar rutina
    //========================
    seleccionarRutina(idRutina: string): void {

        const existe = this.rutinas.some(
            rutina => rutina.id === idRutina
        );

        if(!existe) {
            return;
        }

        this.rutinaSeleccionada.set(idRutina);

        this.cdr.markForCheck();
    }


    //========================
    //Verificar paso completado
    //========================
    pasoCompletado(idPaso: string): boolean {

        const idRutina = this.rutinaSeleccionada();

        const pasos =
            this.progresoRutinas()[idRutina] ?? [];

        return pasos.includes(idPaso);
    }


    //========================
    //Marcar o desmarcar paso
    //========================
    alternarPaso(idPaso: string): void {

        const rutina = this.rutinaActual();

        const pasoExiste = rutina.pasos.some(
            paso => paso.id === idPaso
        );

        if(!pasoExiste) {
            return;
        }

        const completados =
            this.progresoRutinas()[rutina.id] ?? [];

        const nuevosCompletados =
            completados.includes(idPaso)
                ? completados.filter(
                    id => id !== idPaso
                )
                : [...completados, idPaso];

        this.progresoRutinas.update(progreso => ({
            ...progreso,
            [rutina.id]: nuevosCompletados
        }));

        this.guardarProgresoRutinas();

        this.cdr.markForCheck();
    }


    //========================
    //Reiniciar rutina
    //========================
    reiniciarRutina(): void {

        const idRutina = this.rutinaSeleccionada();

        this.progresoRutinas.update(progreso => ({
            ...progreso,
            [idRutina]: []
        }));

        this.guardarProgresoRutinas();

        this.cdr.markForCheck();
    }


    //========================
    //Obtener clave de almacenamiento
    //========================
    private obtenerClaveProgreso(): string {

        const hoy = new Date();

        const fecha =
            hoy.getFullYear() + '-' +
            String(hoy.getMonth() + 1).padStart(2, '0') + '-' +
            String(hoy.getDate()).padStart(2, '0');

        let idUsuario = 'anonimo';

        try {

            const sesion = localStorage.getItem('usuario');

            if(sesion) {

                const usuario = JSON.parse(sesion);

                if(usuario?.idUsuario != null) {

                    idUsuario = String(usuario.idUsuario);
                }
            }

        } catch {

            //Se utiliza la clave general.
        }

        return (
            'beautymanager-rutinas-' +
            idUsuario + '-' + fecha
        );
    }


    //========================
    //Guardar progreso de rutinas
    //========================
    private guardarProgresoRutinas(): void {

        try {

            localStorage.setItem(
                this.obtenerClaveProgreso(),
                JSON.stringify(this.progresoRutinas())
            );

        } catch {

            //La rutina continúa funcionando
            //aunque el navegador no permita guardar.
        }
    }


    //========================
    //Recuperar progreso de rutinas
    //========================
    private cargarProgresoRutinas(): void {

        try {

            const guardado = localStorage.getItem(
                this.obtenerClaveProgreso()
            );

            if(!guardado) {
                return;
            }

            const progreso = JSON.parse(guardado);

            if(typeof progreso !== 'object' ||
               progreso === null ||
               Array.isArray(progreso)) {

                return;
            }

            const progresoValidado:
                Record<string, string[]> = {};

            for(const rutina of this.rutinas) {

                const valores = progreso[rutina.id];

                if(!Array.isArray(valores)) {
                    continue;
                }

                const idsValidos = rutina.pasos.map(
                    paso => paso.id
                );

                progresoValidado[rutina.id] = valores.filter(
                    (id: unknown): id is string =>
                        typeof id === 'string' &&
                        idsValidos.includes(id)
                );
            }

            this.progresoRutinas.set(progresoValidado);

        } catch {

            this.progresoRutinas.set({});
        }
    }
}
