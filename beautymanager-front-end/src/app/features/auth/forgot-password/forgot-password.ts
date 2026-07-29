import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Auth } from '../../../core/services/auth';

@Component({
  selector: 'app-forgot-password',
  imports: [
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    FormsModule
  ],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {

  email = '';

  mensaje = '';

  error = '';

  constructor(
    private auth: Auth,
  ) {}

  solicitarRecuperacion() {

    this.auth.solicitarRecuperacionPassword({

      email: this.email

    }).subscribe({

      next: (respuesta) => {

        console.log('Recuperación solicitada:', respuesta);

        window.location.href = respuesta.linkRecuperacion;

      },

      error: (error) => {

        this.error = 'No se pudo generar el enlace de recuperación.';

        console.error('Error al solicitar recuperación:', error);

      }
    });
  }
}
