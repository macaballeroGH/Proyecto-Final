import { Injectable } from '@angular/core';
import { ObtenerCompra } from '../models/obtener-compra';
import { Observable } from 'rxjs';
import { ConfirmarCompra } from '../models/confirmar-compra';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class CompraService {

  private readonly apiUrl = 'http://localhost:8080/compra';

  constructor(private http: HttpClient) {}

  //========================
  //Realizar compra
  //========================
  realizarCompra(
    solicitud: ConfirmarCompra
  ): Observable<ObtenerCompra> {

    return this.http.post<ObtenerCompra>(
      `${this.apiUrl}/realizar`,
      solicitud
    );
  }

  //========================
  //Obtener mis compras
  //========================
  obtenerMisCompras(): Observable<ObtenerCompra[]> {

    return this.http.get<ObtenerCompra[]>(
      `${this.apiUrl}/mis-compras`
    );
  }

  //========================
  //Obtener detalle de mi compra
  //========================
  obtenerMiCompra(
    idCompra: number
  ): Observable<ObtenerCompra> {

    return this.http.get<ObtenerCompra>(
      `${this.apiUrl}/mis-compras/${idCompra}`
    );
  }
}
