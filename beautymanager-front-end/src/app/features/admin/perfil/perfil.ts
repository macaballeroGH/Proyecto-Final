import { Component, OnInit, signal } from "@angular/core";
import { MatCardModule } from "@angular/material/card";
import { MatButtonModule } from "@angular/material/button";
import { Usuario } from "../../../core/services/usuario";
import { UsuarioPerfil } from "../../../core/models/usuario-perfil";
import { CommonModule } from "@angular/common";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { ConfirmacionDialog } from "../../../shared/components/dialogs/confirmacion-dialog/confirmacion-dialog";


@Component({
  selector: 'app-perfil',
  imports: [MatCardModule, MatButtonModule, CommonModule, MatDialogModule],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class PerfilAdmin implements OnInit {

  usuario = signal<UsuarioPerfil | null>(null);

  constructor(private usuarioService: Usuario, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.cargarPerfil();
  }

  cargarPerfil(): void {

    this.usuarioService.obtenerMiPerfil().subscribe({

      next: (respuesta) => {

        this.usuario.set(respuesta);

        console.log('Perfil cargado:', this.usuario());
      },

      error: (error) => {

        console.error('Error al obtener perfil:', error);
      }
    });
  }

  cambiarFoto(event: Event): void {

    const input = event.target as HTMLInputElement;

    if(!input.files || input.files.length === 0){
      return;
    }

    const foto = input.files[0];

    this.usuarioService.actualizarFotoPerfil(foto).subscribe({

      next: () => {

        console.log("Foto actualizada correctamente");

        this.cargarPerfil();
      },

      error: (error) => {

        console.error("Error al actualizar foto", error);
      }
    });
  }

  eliminarFoto(): void {

    const dialogRef = this.dialog.open(ConfirmacionDialog);

    dialogRef.afterClosed().subscribe((confirmado) => {

      if(!confirmado) {
        return;
      }

      this.usuarioService.eliminarFotoPerfil().subscribe({

        next: () => {

          console.log("Foto eliminada correctamente");

          this.cargarPerfil();
        },

        error: (error) => {

          console.error("Error al eliminar foto", error);
        }
      });
    });
  }
}