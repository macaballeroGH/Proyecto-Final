import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Auth } from '../../../core/services/auth';
import { RegistroRequest } from '../../../core/models/registro-request';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-registro',
  imports: [FormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, RouterLink],
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

  enviando = false;
  errorRegistro = '';

  constructor(private auth: Auth, private router: Router) {}

  registrar(formulario: NgForm): void {

    if (this.enviando) {
      return;
    }

    this.errorRegistro = '';

    // Eliminar espacios innecesarios.
    this.nombre = this.nombre.trim();
    this.apellido = this.apellido.trim();
    this.email = this.email.trim();
    this.telefono = this.telefono.trim();
    this.direccion = this.direccion.trim();

    // Sincronizar los valores con las validaciones.
    formulario.controls['nombre']?.setValue(this.nombre);
    formulario.controls['apellido']?.setValue(this.apellido);
    formulario.controls['email']?.setValue(this.email);

    // Mostrar los errores de los campos inválidos.
    if (formulario.invalid) {
      formulario.form.markAllAsTouched();
      return;
    }

    const usuario: RegistroRequest = {
      nombre: this.nombre,
      apellido: this.apellido,
      email: this.email,
      password: this.password,
      telefono: this.telefono,
      direccion: this.direccion,
      fechaNacimiento: this.fechaNacimiento
    };

    this.enviando = true;

    this.auth.registrar(usuario).subscribe({

      next: () => {
        this.enviando = false;
        this.router.navigate(['/login']);
      },

      error: (error: HttpErrorResponse) => {

        this.enviando = false;

        if (error.status === 409) {
          this.errorRegistro =
            'Ya existe una cuenta registrada con este correo electrónico.';
        } else if (error.status === 400) {
          this.errorRegistro =
            'No se pudo completar el registro. Revisá los datos ingresados.';
        } else {
          this.errorRegistro =
            'Ocurrió un problema al crear tu cuenta. Intentá nuevamente.';
        }

        console.error('Error en registro:', error);
      }

    });

  }
}
