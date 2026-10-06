import { Component, OnInit, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BilleteraService } from '../../../core/services/billetera';
import { ObtenerBilletera } from '../../../core/models/obtener-billetera';
import { ObtenerMovimientoBilletera } from '../../../core/models/obtener-movimiento-billetera';
import { RecargarSaldo } from '../../../core/models/recargar-saldo';
import { MovimientoBilletera } from '../../../core/enums/movimientoBilletera';
import { MatIconModule } from "@angular/material/icon";

@Component({
  selector: 'app-billetera',
  imports: [MatIconModule, CurrencyPipe, DatePipe, FormsModule],
  templateUrl: './billetera.html',
  styleUrl: './billetera.css',
})
export class Billetera implements OnInit{

  billetera = signal<ObtenerBilletera | null>(null);

  movimientos = signal<ObtenerMovimientoBilletera[]>([]);

  ultimosMovimientos = signal<ObtenerMovimientoBilletera[]>([]);

  cargandoBilletera = signal(true);

  cargandoMovimientos = signal(true);

  errorBilletera = signal(false);

  errorMovimientos = signal(false);

  mostrarModalMovimientos = signal(false);

  mostrarSaldo = signal(true);

  mostrarModalRecarga = false;

  montoRecarga: number | null = null;

  recargandoSaldo = false;

  errorRecarga = '';

  readonly montoMinimo = 1;

  readonly montoMaximo = 1000000;

  readonly MovimientoBilletera = MovimientoBilletera;

  constructor(private billeteraService: BilleteraService) {}

  ngOnInit(): void {
    
    this.cargarBilletera();

    this.cargarMovimientos();
  }

  cargarBilletera(): void {

    this.cargandoBilletera.set(true);

    this.errorBilletera.set(false);

    this.billeteraService.obtenerBilletera().subscribe({

      next: (billetera) => {

        this.billetera.set(billetera);

        this.cargandoBilletera.set(false);
      },

      error: (error) => {

        console.error('Error al obtener la billetera:', error);

        this.errorBilletera.set(true);

        this.cargandoBilletera.set(false);
      }
    });
  }

  cargarMovimientos(): void {

    this.cargandoMovimientos.set(true);

    this.errorMovimientos.set(false);

    this.billeteraService.obtenerMovimientos().subscribe({

      next: (movimientos) => {

        console.log('MOVIMIENTOS RECIBIDOS:', movimientos);
        console.log('TIPO:', typeof movimientos);
        console.log('ES ARRAY:', Array.isArray(movimientos));

        this.movimientos.set(movimientos);

        this.ultimosMovimientos.set(movimientos.slice(0, 3));

        this.cargandoMovimientos.set(false);

        console.log('cargandoMovimientos:', this.cargandoMovimientos);
        console.log('movimientos:', this.movimientos);
        console.log('ultimosMovimientos:', this.ultimosMovimientos);
      },

      error: (error) => {

        console.error('Error al obtener los movimientos:', error);

        this.errorMovimientos.set(true);

        this.cargandoMovimientos.set(false);
      }
    });
  }

  abrirModalRecarga(): void {

    this.montoRecarga = null;

    this.errorRecarga = '';

    this.mostrarModalRecarga = true;
  }

  cerrarModalRecarga(): void {

    if(this.recargandoSaldo){
      return;
    }

    this.mostrarModalRecarga = false;

    this.montoRecarga = null;

    this.errorRecarga = '';
  }

  recargarSaldo(): void {

    this.errorRecarga = '';

    if(this.montoRecarga === null || this.montoRecarga === undefined){

      this.errorRecarga = 'Ingrese un monto.';

      return;
    }

    if(this.montoRecarga < this.montoMinimo){

      this.errorRecarga = `El monto minimo es $${this.montoMinimo}.`;

      return;
    }

    if(this.montoRecarga > this.montoMaximo){
      
      this.errorRecarga = `El monto maximo es $${this.montoMaximo}.`;

      return;
    }

    if(this.recargandoSaldo){
      return;
    }

    const recarga: RecargarSaldo = {
      monto: this.montoRecarga
    };

    this.recargandoSaldo = true;

    this.billeteraService.recargarSaldo(recarga).subscribe({

      next: (billetera) => {

        this.billetera.set(billetera);

        this.recargandoSaldo = false;

        this.mostrarModalRecarga = false;

        this.montoRecarga = null;

        this.errorRecarga = '';

        this.cargarMovimientos();
      },

      error: (error) => {

        console.error('Error al recargar saldo:', error);

        this.recargandoSaldo = false;

        this.errorRecarga = error?.error?.message || 'No se pudo realizar la recarga. Intente nuevamente.';
      }
    });
  }

  verTodosLosMovimientos(): void {
    this.mostrarModalMovimientos.set(true);
  }

  cerrarModalMovimientos(): void {
    this.mostrarModalMovimientos.set(false);
  }

  alternarVisibilidadSaldo(): void {
    this.mostrarSaldo.set(!this.mostrarSaldo());
  }

  hayMovimientos(): boolean {
    return this.movimientos().length > 0;
  }
}
