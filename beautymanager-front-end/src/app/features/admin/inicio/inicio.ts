import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ArticuloBelleza } from '../../../core/models/articulo-belleza';
import { ArticuloBellezaService } from '../../../core/services/articulo-belleza';
import { MatIcon } from '@angular/material/icon';


@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [CommonModule, MatIcon],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css',
})
export class InicioAdmin implements OnInit {

  articulos: ArticuloBelleza[] = [];

  categorias: string[] = [
    'Todos',
    'Maquillaje',
    'Cabello',
    'Uñas',
    'Cuidado de la piel',
    'Bienestar',
    'Tendencias'
  ];

  categoriaSeleccionada = 'Todos';

  cargando = false;
  error = false;

  constructor(private articuloBellezaService: ArticuloBellezaService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.obtenerArticulos();
  }

  obtenerArticulos(): void {

    this.cargando = true;
    this.error = false;

    this.articuloBellezaService.obtenerArticulos().subscribe({

      next: (articulos) => {
      console.log('ARTÍCULOS RECIBIDOS:', articulos);
      console.log('¿Es un array?', Array.isArray(articulos));

      this.articulos = articulos;
      this.cargando = false;

      this.cdr.detectChanges();

      console.log('CARGANDO:', this.cargando);
      console.log('CANTIDAD:', this.articulos.length);
    },

      error: (error) => {
        console.error('Error al obtener los artículos de belleza:', error);
        this.error = true;
        this.cargando = false;
      }

    });
  }

  seleccionarCategoria(categoria: string): void {
    this.categoriaSeleccionada = categoria;
  }

  get articulosFiltrados(): ArticuloBelleza[] {

    if (this.categoriaSeleccionada === 'Todos') {
      return this.articulos;
    }

    return this.articulos.filter(
      articulo => articulo.categoria === this.categoriaSeleccionada
    );
  }

  get articuloDestacado(): ArticuloBelleza | undefined {
    return this.articulosFiltrados[0];
  }

  get otrosArticulos(): ArticuloBelleza[] {
    return this.articulosFiltrados.slice(1);
  }
}
