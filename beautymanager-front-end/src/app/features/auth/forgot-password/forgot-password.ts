import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { FormsModule, NgForm } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Auth } from '../../../core/services/auth';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  imports: [
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    FormsModule,
    RouterLink
  ],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {

  email = '';

  mensaje = '';
  error = '';

  enviando = false;

  constructor(
    private auth: Auth
  ) {}

  solicitarRecuperacion(formulario: NgForm): void {

    if (this.enviando) {
      return;
    }

    this.mensaje = '';
    this.error = '';

    // Eliminar espacios accidentales del correo.
    this.email = this.email.trim();

    formulario.controls['email']?.setValue(this.email);

    // Mostrar errores y evitar solicitudes inválidas.
    if (formulario.invalid) {
      formulario.form.markAllAsTouched();
      return;
    }

    this.enviando = true;

    this.auth.solicitarRecuperacionPassword({
      email: this.email
    }).subscribe({

      next: (respuesta) => {

        this.enviando = false;

        // Conservar la redirección existente.
        if (respuesta?.linkRecuperacion) {

          window.location.href = respuesta.linkRecuperacion;
          return;

        }

        this.mensaje =
          'La solicitud de recuperación fue procesada.';

      },

      error: (error) => {

        this.enviando = false;

        this.error =
          'No se pudo completar la solicitud de recuperación. Intentá nuevamente.';

        console.error(
          'Error al solicitar recuperación:',
          error
        );

      }

    });

  }
}
