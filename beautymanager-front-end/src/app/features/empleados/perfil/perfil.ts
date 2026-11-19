import { CommonModule } from '@angular/common';
import {
    ChangeDetectorRef,
    Component,
    computed,
    OnInit,
    signal
} from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';

import { Usuario } from '../../../core/services/usuario';
import { HistorialTratamientoService } from '../../../core/services/historial-tratamiento';

import { UsuarioPerfil } from '../../../core/models/usuario-perfil';

import { ConfirmacionDialog } from '../../../shared/components/dialogs/confirmacion-dialog/confirmacion-dialog';
import { Empleado } from '../../../core/models/empleado';
import { EmpleadoService } from '../../../core/services/empleado';

@Component({
  selector: 'app-perfil-empleado',
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatDialogModule],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class PerfilEmpleado implements OnInit {

  usuario = signal<UsuarioPerfil | null>(null);

    cargandoPerfil = signal<boolean>(false);

    errorPerfil = signal<string>('');


    //========================================
    //INFORMACIÓN DE LA EMPLEADA
    //========================================

    empleado = signal<Empleado | null>(null);

    cargandoEmpleado = signal<boolean>(false);

    errorEmpleado = signal<string>('');


    //========================================
    //ESPECIALIDAD
    //========================================

    especialidadTexto = computed(() => {

        const especialidad = this.empleado()?.especialidad;

        if (!especialidad) {
            return 'No registrada';
        }

        return especialidad
            .replace(/_/g, ' ')
            .toLowerCase()
            .replace(/\b\w/g, letra => letra.toUpperCase());
    });



    //========================================
    //FOTOGRAFÍA DE PERFIL
    //========================================

    actualizandoFoto = signal<boolean>(false);

    eliminandoFoto = signal<boolean>(false);

    errorFoto = signal<string>('');


    //========================================
    //ACTIVIDAD PERSONAL
    //========================================

    cantidadTratamientos = signal<number | null>(null);

    cargandoTratamientos = signal<boolean>(false);

    errorTratamientos = signal<string>('');


    //========================================
    //NOMBRE COMPLETO
    //========================================

    nombreCompleto = computed(() => {

        const datos = this.usuario();

        if (!datos) {
            return '';
        }

        return `${datos.nombre} ${datos.apellido}`.trim();
    });


    //========================================
    //FOTO DE PERFIL
    //========================================

    fotoPerfilUrl = computed(() => {

        const foto = this.usuario()?.fotoPerfil;

        if (!foto) {
            return '/img/default-profile.jpg';
        }

        if (/^https?:\/\//i.test(foto)) {
            return foto;
        }

        return `http://localhost:8080/${foto.replace(/^\/+/, '')}`;
    });


    //========================================
    //CONSTRUCTOR
    //========================================

    constructor(
        private usuarioService: Usuario, private empleadoService: EmpleadoService,
        private historialTratamientoService: HistorialTratamientoService,
        private dialog: MatDialog,
        private cdr: ChangeDetectorRef
    ) {}


    //========================================
    //INICIALIZACIÓN
    //========================================

    ngOnInit(): void {

      this.cargarDatosPerfil();
    }


    //========================================
    //CARGAR INFORMACIÓN DEL PERFIL
    //========================================

    cargarDatosPerfil(): void {

      this.cargarPerfil();

      this.cargarEmpleado();

      this.cargarTratamientos();
    }


    //========================================
    //CARGAR INFORMACIÓN DE LA EMPLEADA
    //========================================

    cargarEmpleado(): void {

        if (this.cargandoEmpleado()) {
            return;
        }

        this.cargandoEmpleado.set(true);

        this.errorEmpleado.set('');

        //====================================
        //CONSULTAR EMPLEADA AUTENTICADA
        //====================================

        this.empleadoService.obtenerMiPerfil().subscribe({

            next: (respuesta) => {

                this.empleado.set(respuesta);

                this.cargandoEmpleado.set(false);

                this.cdr.detectChanges();
            },

            error: (error) => {

                console.error(
                    'Error al cargar los datos de la empleada:',
                    error
                );

                this.empleado.set(null);

                this.errorEmpleado.set(
                    'No se pudo consultar tu especialidad.'
                );

                this.cargandoEmpleado.set(false);

                this.cdr.detectChanges();
            }
        });
    }



    //========================================
    //OBTENER INFORMACIÓN PERSONAL
    //========================================

    cargarPerfil(): void {

        if (this.cargandoPerfil()) {
            return;
        }

        this.cargandoPerfil.set(true);

        this.errorPerfil.set('');

        this.usuarioService.obtenerMiPerfil().subscribe({

            next: (respuesta) => {

                this.usuario.set(respuesta);

                this.cargandoPerfil.set(false);

                this.cdr.detectChanges();
            },

            error: (error) => {

                console.error(
                    'Error al cargar el perfil de la empleada:',
                    error
                );

                this.errorPerfil.set(
                    'No se pudo cargar tu información personal.'
                );

                this.cargandoPerfil.set(false);

                this.cdr.detectChanges();
            }
        });
    }


    //========================================
    //CARGAR TRATAMIENTOS REALIZADOS
    //========================================

    cargarTratamientos(): void {

        if (this.cargandoTratamientos()) {
            return;
        }

        this.cargandoTratamientos.set(true);

        this.errorTratamientos.set('');

        this.historialTratamientoService
            .misTratamientosEmpleado()
            .subscribe({

                next: (tratamientos) => {

                    this.cantidadTratamientos.set(
                        tratamientos.length
                    );

                    this.cargandoTratamientos.set(false);

                    this.cdr.detectChanges();
                },

                error: (error) => {

                    console.error(
                        'Error al consultar los tratamientos de la empleada:',
                        error
                    );

                    this.cantidadTratamientos.set(null);

                    this.errorTratamientos.set(
                        'No se pudieron consultar tus tratamientos.'
                    );

                    this.cargandoTratamientos.set(false);

                    this.cdr.detectChanges();
                }
            });
    }


    //========================================
    //CAMBIAR FOTO DE PERFIL
    //========================================

    cambiarFoto(event: Event): void {

        const input = event.target as HTMLInputElement;

        if (!input.files || input.files.length === 0) {
            return;
        }

        if (
            this.actualizandoFoto() ||
            this.eliminandoFoto()
        ) {
            return;
        }

        const foto = input.files[0];

        input.value = '';

        this.errorFoto.set('');

        //====================================
        //VALIDAR ARCHIVO
        //====================================

        if (!foto.type.startsWith('image/')) {

            this.errorFoto.set(
                'Seleccioná un archivo de imagen válido.'
            );

            return;
        }

        //====================================
        //ACTUALIZAR FOTO
        //====================================

        this.actualizandoFoto.set(true);

        this.usuarioService
            .actualizarFotoPerfil(foto)
            .subscribe({

                next: () => {

                    this.actualizandoFoto.set(false);

                    this.cargarPerfil();

                    this.cdr.detectChanges();
                },

                error: (error) => {

                    console.error(
                        'Error al actualizar la foto de perfil:',
                        error
                    );

                    this.errorFoto.set(
                        'No se pudo actualizar la foto de perfil.'
                    );

                    this.actualizandoFoto.set(false);

                    this.cdr.detectChanges();
                }
            });
    }


    //========================================
    //ELIMINAR FOTO DE PERFIL
    //========================================

    eliminarFoto(): void {

        if (
            !this.usuario()?.fotoPerfil ||
            this.actualizandoFoto() ||
            this.eliminandoFoto()
        ) {
            return;
        }

        //====================================
        //SOLICITAR CONFIRMACIÓN
        //====================================

        const dialogRef = this.dialog.open(
            ConfirmacionDialog
        );

        dialogRef.afterClosed().subscribe(confirmado => {

            if (!confirmado) {
                return;
            }

            this.errorFoto.set('');

            this.eliminandoFoto.set(true);

            //================================
            //ELIMINAR FOTO
            //================================

            this.usuarioService
                .eliminarFotoPerfil()
                .subscribe({

                    next: () => {

                        this.eliminandoFoto.set(false);

                        this.cargarPerfil();

                        this.cdr.detectChanges();
                    },

                    error: (error) => {

                        console.error(
                            'Error al eliminar la foto de perfil:',
                            error
                        );

                        this.errorFoto.set(
                            'No se pudo eliminar la foto de perfil.'
                        );

                        this.eliminandoFoto.set(false);

                        this.cdr.detectChanges();
                    }
                });
        });
    }
}
