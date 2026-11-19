import { Injectable } from '@angular/core';
import { ObtenerCarrito } from '../models/obtener-carrito';
import { Observable } from 'rxjs';
import { ActualizarCantidadCarrito } from '../models/actualizar-cantidad-carrito';
import { AgregarProductoCarrito } from '../models/agregar-producto-carrito';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class CarritoService {

  private readonly apiUrl = 'http://localhost:8080/carrito';

  constructor(private http: HttpClient) {}

  //========================
  //Obtener mi carrito
  //========================
  obtenerMiCarrito(): Observable<ObtenerCarrito> {

    return this.http.get<ObtenerCarrito>(
      `${this.apiUrl}/mi-carrito`
    );
  }

  //========================
  //Agregar producto
  //========================
  agregarProducto(
    producto: AgregarProductoCarrito
  ): Observable<ObtenerCarrito> {

    return this.http.post<ObtenerCarrito>(
      `${this.apiUrl}/agregar-producto`,
      producto
    );
  }

  //========================
  //Actualizar cantidad
  //========================
  actualizarCantidad(
    producto: ActualizarCantidadCarrito
  ): Observable<ObtenerCarrito> {

    return this.http.put<ObtenerCarrito>(
      `${this.apiUrl}/actualizar-cantidad`,
      producto
    );
  }

  //========================
  //Eliminar producto
  //========================
  eliminarProducto(
    idProducto: number
  ): Observable<ObtenerCarrito> {

    return this.http.delete<ObtenerCarrito>(
      `${this.apiUrl}/eliminar-producto/${idProducto}`
    );
  }
}
