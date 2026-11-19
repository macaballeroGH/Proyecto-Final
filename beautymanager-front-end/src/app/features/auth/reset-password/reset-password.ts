import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { FormsModule, NgForm } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Auth } from '../../../core/services/auth';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-reset-password',
  imports: [
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    FormsModule,
    CommonModule, RouterLink
],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css',
})
export class ResetPassword implements OnInit, OnDestroy {

  token = '';

  passwordNueva = '';
  confirmarPasswordNueva = '';

  mensaje = '';
  error = '';

  procesando = false;
  restablecida = false;

  private temporizadorRedireccion:
    ReturnType<typeof setTimeout> | null = null;

  constructor(
    private route: ActivatedRoute,
    private auth: Auth,
    private router: Router
  ) {}

  ngOnInit(): void {

    this.token =
      this.route.snapshot.queryParamMap.get('token') ?? '';

    if (!this.token) {

      this.error =
        'El enlace de recuperación no es válido o no contiene un token.';

    }

  }

  get contrasenasNoCoinciden(): boolean {

    return this.passwordNueva !==
      this.confirmarPasswordNueva;

  }

  restablecerPassword(formulario: NgForm): void {

    if (this.procesando || this.restablecida) {
      return;
    }

    this.mensaje = '';
    this.error = '';

    // Verificar que exista el token.
    if (!this.token) {

      this.error =
        'El enlace de recuperación no es válido o no contiene un token.';

      return;

    }

    // Mostrar los errores de los campos obligatorios.
    if (formulario.invalid) {

      formulario.form.markAllAsTouched();
      return;

    }

    // Verificar que las contraseñas coincidan.
    if (this.contrasenasNoCoinciden) {

      formulario.controls[
        'confirmarPasswordNueva'
      ]?.markAsTouched();

      return;

    }

    this.procesando = true;

    this.auth.restablecerPassword({

      token: this.token,
      passwordNueva: this.passwordNueva,
      confirmarPasswordNueva: this.confirmarPasswordNueva

    }).subscribe({

      next: (respuesta) => {

        this.procesando = false;
        this.restablecida = true;

        this.mensaje =
          respuesta.mensaje ||
          'Tu contraseña se actualizó correctamente.';

        // Limpiar las contraseñas de memoria visual.
        this.passwordNueva = '';
        this.confirmarPasswordNueva = '';

        // Conservar la redirección original.
        this.temporizadorRedireccion = setTimeout(() => {

          this.router.navigate(['/login']);

        }, 2000);

      },

      error: (error: HttpErrorResponse) => {

        this.procesando = false;

        if (error.status === 0) {

          this.error =
            'No se pudo conectar con el servidor. Intentá nuevamente.';

        } else if (error.status === 400) {

          this.error =
            'No se pudo restablecer la contraseña. Revisá que el enlace sea válido y que la nueva contraseña cumpla los requisitos.';

        } else if (
          error.status === 401 ||
          error.status === 403 ||
          error.status === 410
        ) {

          this.error =
            'El enlace de recuperación puede ser inválido o haber expirado. Solicitá uno nuevo.';

        } else {

          this.error =
            'Ocurrió un problema al actualizar la contraseña. Intentá nuevamente.';

        }

        console.error(
          'Error al actualizar contraseña:',
          error.status
        );

      }

    });

  }

  ngOnDestroy(): void {

    if (this.temporizadorRedireccion !== null) {

      clearTimeout(this.temporizadorRedireccion);

    }

  }

}
