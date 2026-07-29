import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Auth } from '../../../core/services/auth';
import { Session } from '../../../core/services/session';
import { Router, RouterLink } from '@angular/router';
@Component({
  selector: 'app-login',
  imports: [MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  email = '';
  password = '';

  constructor(
    private auth: Auth,
    private session: Session,
    private router: Router
  ) {}

  iniciarSesion(){
    this.auth.login(this.email, this.password)
      .subscribe({
        next: (respuesta) => {
          const usuario = {
            idUsuario: respuesta.idUsuario,
            nombre: respuesta.nombre,
            apellido: respuesta.apellido,
            email: respuesta.email,
            rol: respuesta.rol,
            token: respuesta.token
          };

          this.session.guardarSesion(usuario);

          switch(usuario.rol) {

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

          console.log('Login exitoso:', respuesta);
        },
        error: (error) => {
          console.error('Error en login:', error);
        }
      });
  }
}
