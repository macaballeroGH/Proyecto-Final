import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { FormsModule, NgForm } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Auth } from '../../../core/services/auth';
import { Session } from '../../../core/services/session';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
@Component({
  selector: 'app-login',
  imports: [MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  email = '';
  password = '';

  iniciandoSesion = false;
  errorLogin = '';

  constructor(
    private auth: Auth,
    private session: Session,
    private router: Router
  ) {}

  iniciarSesion(formulario: NgForm): void {

    if (this.iniciandoSesion) {
      return;
    }

    this.errorLogin = '';

    // Eliminar espacios accidentales del correo.
    this.email = this.email.trim();

    formulario.controls['email']?.setValue(this.email);

    // No enviar si existen campos inválidos.
    if (formulario.invalid) {
      formulario.form.markAllAsTouched();
      return;
    }

    this.iniciandoSesion = true;

    this.auth.login(this.email, this.password).subscribe({

      next: (respuesta) => {

        this.iniciandoSesion = false;

        const usuario = {
          idUsuario: respuesta.idUsuario,
          nombre: respuesta.nombre,
          apellido: respuesta.apellido,
          email: respuesta.email,
          rol: respuesta.rol,
          token: respuesta.token
        };

        this.session.guardarSesion(usuario);

        switch (usuario.rol) {

          case 'ADMIN':
            this.router.navigate(['/admin/inicio']);
            break;

          case 'CLIENTE':
            this.router.navigate(['/cliente/inicio']);
            break;

          case 'EMPLEADO':
            this.router.navigate(['/empleado/inicio']);
            break;

        }

      },

      error: (error: HttpErrorResponse) => {

        this.iniciandoSesion = false;

        if (error.status === 401) {

          this.errorLogin =
            'El correo electrónico o la contraseña son incorrectos.';

        } else if (error.status === 403) {

          this.errorLogin =
            'No se pudo iniciar sesión con esta cuenta.';

        } else if (error.status === 0) {

          this.errorLogin =
            'No se pudo conectar con el servidor. Intentá nuevamente.';

        } else {

          this.errorLogin =
            'Ocurrió un problema al iniciar sesión. Intentá nuevamente.';

        }

        console.error('Error en login:', error);
      }

    });

  }
}
