import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { ActivatedRoute, Router } from '@angular/router';
import { Auth } from '../../../core/services/auth';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reset-password',
  imports: [
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    FormsModule,
    CommonModule
],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css',
})
export class ResetPassword {

  token = '';

  passwordNueva = '';

  confirmarPasswordNueva = '';

  mensaje = '';

  error = '';

  constructor(
    private route: ActivatedRoute,
    private auth: Auth,
    private router: Router
  ) {}

  ngOnInit(){

    this.token = this.route.snapshot.queryParamMap.get('token') ?? '';

    console.log('Token recuperación:', this.token);
  }

  restablecerPassword() {

    this.auth.restablecerPassword({

      token: this.token,

      passwordNueva: this.passwordNueva,

      confirmarPasswordNueva: this.confirmarPasswordNueva

    }).subscribe({

      next: (respuesta) => {

        console.log('Contraseña actualizada:', respuesta);

        this.mensaje = respuesta.mensaje;

        setTimeout(() => {

          this.router.navigate(['/login']);

        }, 2000)

      },

      error: (error) => {

        this.error = 'No se pudo actualizar la contraseña.';

        console.error('Error al actualizar contraseña:', error);

      }
    });
  }
}
