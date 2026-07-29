import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Auth } from '../../../core/services/auth';
import { RegistroRequest } from '../../../core/models/registro-request';
import { Router } from '@angular/router';

@Component({
  selector: 'app-registro',
  imports: [FormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './registro.html',
  styleUrl: './registro.css',
})
export class Registro {

  nombre = '';
  apellido = '';
  email = '';
  password = '';
  telefono = '';
  direccion = '';
  fechaNacimiento = '';

  constructor(
    private auth: Auth,
    private router: Router
  ) {}

  registrar() {

    const usuario: RegistroRequest = {
      nombre: this.nombre,
      apellido: this.apellido,
      email: this.email,
      password: this.password,
      telefono: this.telefono,
      direccion: this.direccion,
      fechaNacimiento: this.fechaNacimiento
    };

    this.auth.registrar(usuario)
      .subscribe({
        next: (respuesta) => {
          console.log('Registro exitoso:', respuesta);

          this.router.navigate(['/login']);
        },
        error: (error) => {
          console.error('Error en registro:', error);
        }
      });
  }
}
