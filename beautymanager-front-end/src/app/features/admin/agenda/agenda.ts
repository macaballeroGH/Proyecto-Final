import { Component, OnInit } from '@angular/core';
import { TurnoService } from '../../../core/services/turno';
import { Turno } from '../../../core/models/turno';

@Component({
  selector: 'app-agenda',
  imports: [],
  templateUrl: './agenda.html',
  styleUrl: './agenda.css',
})
export class Agenda implements OnInit {

  turnos: Turno[] = [];

  constructor(private turnoService: TurnoService) {}

  ngOnInit(): void {
    this.cargarTurnos();
  }

  cargarTurnos(): void {

    this.turnoService.obtenerTodosLosTurnos().subscribe({

      next: (data) => {

        this.turnos = data;

        console.log('Turnos recibidos:', this.turnos);
      },

      error: (error) => {
        console.error('Error al cargar turnos', error);
      }
    });
  }
}
