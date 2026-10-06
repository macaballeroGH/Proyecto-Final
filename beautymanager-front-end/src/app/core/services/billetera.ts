import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ObtenerBilletera } from '../models/obtener-billetera';
import { ObtenerMovimientoBilletera } from '../models/obtener-movimiento-billetera';
import { RecargarSaldo } from '../models/recargar-saldo';
import { DebitarSaldo } from '../models/debitar-saldo';

@Injectable({
  providedIn: 'root',
})
export class BilleteraService {

  private apiUrl = 'http://localhost:8080/billetera';

  private movimientoApiUrl = 'http://localhost:8080/movimiento-billetera';

  constructor(private http: HttpClient) {}

  obtenerBilletera(): Observable<ObtenerBilletera> {
    return this.http.get<ObtenerBilletera>(this.apiUrl);
  }

  consultarSaldo(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/saldo`);
  }

  recargarSaldo(recarga: RecargarSaldo): Observable<ObtenerBilletera> {
    return this.http.put<ObtenerBilletera>(`${this.apiUrl}/recargar`, recarga);
  }

  debitarSaldo(debito: DebitarSaldo): Observable<ObtenerBilletera> {
    return this.http.put<ObtenerBilletera>(`${this.apiUrl}/debitar`, debito);
  }

  obtenerMovimientos(): Observable<ObtenerMovimientoBilletera[]> {
    return this.http.get<ObtenerMovimientoBilletera[]>(`${this.movimientoApiUrl}/mis-movimientos`);
  }
}
