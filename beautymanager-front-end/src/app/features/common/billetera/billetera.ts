import { Component, OnInit, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BilleteraService } from '../../../core/services/billetera';
import { ObtenerBilletera } from '../../../core/models/obtener-billetera';
import { ObtenerMovimientoBilletera } from '../../../core/models/obtener-movimiento-billetera';
import { RecargarSaldo } from '../../../core/models/recargar-saldo';
import { MovimientoBilletera } from '../../../core/enums/movimientoBilletera';
import { MatIconModule } from "@angular/material/icon";
import { Router } from '@angular/router';

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

  private rutaAnterior: string | null = null;

  constructor(
  private billeteraService: BilleteraService,
  private router: Router
) {

  const navegacion = this.router.currentNavigation();

  const navegacionAnterior =
    navegacion?.previousNavigation;

  const urlAnterior =
    navegacionAnterior?.finalUrl ??
    navegacionAnterior?.extractedUrl;

  if (urlAnterior) {

    const ruta = this.router.serializeUrl(urlAnterior);

    const rutaInterna =
      /^\/(cliente|admin|empleado)(\/|$|\?)/.test(ruta);

    const esBilletera =
      /^\/(cliente|admin|empleado)\/billetera(\/|$|\?)/.test(ruta);

    if (rutaInterna && !esBilletera) {
      this.rutaAnterior = ruta;
    }

  }

}

  ngOnInit(): void {

    this.cargarBilletera();

    this.cargarMovimientos();

  }

  // =========================================
  // VOLVER A LA PÁGINA ANTERIOR
  // =========================================

  volver(): void {

  // Regresar a la pantalla anterior,
  // cuando Angular pueda identificarla.

  if (this.rutaAnterior) {

    this.router.navigateByUrl(this.rutaAnterior);

    return;
  }

  // Si no existe una ruta anterior válida,
  // identificar el rol de la sesión.

  let rol: string | undefined;

  try {

    const usuarioGuardado =
      localStorage.getItem('usuario');

    if (usuarioGuardado) {

      const usuario = JSON.parse(usuarioGuardado);

      rol = usuario.rol;

    }

  } catch (error) {

    console.error(
      'No se pudo recuperar la sesión:',
      error
    );

  }

  switch (rol) {

    case 'ADMIN':
      this.router.navigate(['/admin/inicio']);
      break;

    case 'EMPLEADO':
      this.router.navigate(['/empleado/inicio']);
      break;

    case 'CLIENTE':
      this.router.navigate(['/cliente/inicio']);
      break;

    default:
      this.router.navigate(['/login']);
      break;

  }

}

  // =========================================
  // CARGAR BILLETERA
  // =========================================

  cargarBilletera(): void {

    this.cargandoBilletera.set(true);

    this.errorBilletera.set(false);

    this.billeteraService.obtenerBilletera().subscribe({

      next: (billetera) => {

        this.billetera.set(billetera);

        this.cargandoBilletera.set(false);

      },

      error: (error) => {

        console.error(
          'Error al obtener la billetera:',
          error
        );

        this.errorBilletera.set(true);

        this.cargandoBilletera.set(false);

      }

    });

  }

  // =========================================
  // CARGAR MOVIMIENTOS
  // =========================================

  cargarMovimientos(): void {

    this.cargandoMovimientos.set(true);

    this.errorMovimientos.set(false);

    this.billeteraService.obtenerMovimientos().subscribe({

      next: (movimientos) => {

        this.movimientos.set(movimientos);

        this.ultimosMovimientos.set(
          movimientos.slice(0, 3)
        );

        this.cargandoMovimientos.set(false);

      },

      error: (error) => {

        console.error(
          'Error al obtener los movimientos:',
          error
        );

        this.errorMovimientos.set(true);

        this.cargandoMovimientos.set(false);

      }

    });

  }

  // =========================================
  // MODAL DE RECARGA
  // =========================================

  abrirModalRecarga(): void {

    this.montoRecarga = null;

    this.errorRecarga = '';

    this.mostrarModalRecarga = true;

  }

  cerrarModalRecarga(): void {

    if (this.recargandoSaldo) {
      return;
    }

    this.mostrarModalRecarga = false;

    this.montoRecarga = null;

    this.errorRecarga = '';

  }

  // =========================================
  // RECARGAR SALDO
  // =========================================

  recargarSaldo(): void {

    this.errorRecarga = '';

    if (
      this.montoRecarga === null ||
      this.montoRecarga === undefined ||
      !Number.isFinite(this.montoRecarga)
    ) {

      this.errorRecarga = 'Ingresá un monto válido.';

      return;
    }

    if (this.montoRecarga < this.montoMinimo) {

      this.errorRecarga =
        `El monto mínimo es $${this.montoMinimo}.`;

      return;
    }

    if (this.montoRecarga > this.montoMaximo) {

      this.errorRecarga =
        `El monto máximo es $${this.montoMaximo}.`;

      return;
    }

    if (this.recargandoSaldo) {
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

        console.error(
          'Error al recargar saldo:',
          error
        );

        this.recargandoSaldo = false;

        this.errorRecarga =
          error?.error?.message ||
          'No se pudo realizar la recarga. Intentá nuevamente.';

      }

    });

  }

  // =========================================
  // MODAL DE MOVIMIENTOS
  // =========================================

  verTodosLosMovimientos(): void {

    this.mostrarModalMovimientos.set(true);

  }

  cerrarModalMovimientos(): void {

    this.mostrarModalMovimientos.set(false);

  }

  // =========================================
  // VISIBILIDAD DEL SALDO
  // =========================================

  alternarVisibilidadSaldo(): void {

    this.mostrarSaldo.set(!this.mostrarSaldo());

  }

  // =========================================
  // CONSULTAS AUXILIARES
  // =========================================

  hayMovimientos(): boolean {

    return this.movimientos().length > 0;

  }
}
