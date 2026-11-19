import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CrearProducto } from '../models/crear-producto';
import { ObtenerProducto } from '../models/obtener-producto';
import { ActualizarProducto } from '../models/actualizar-producto';

@Injectable({
  providedIn: 'root',
})
export class ProductoService {

  private readonly apiUrl = 'http://localhost:8080/producto';

  constructor(private http: HttpClient) {}

  crearProducto(producto: CrearProducto): Observable<ObtenerProducto> {
    return this.http.post<ObtenerProducto>(`${this.apiUrl}/crear`, producto);
  }

  actualizarProducto(id: number, producto: ActualizarProducto): Observable<ObtenerProducto> {
    return this.http.put<ObtenerProducto>(`${this.apiUrl}/actualizar/${id}`, producto);
  }

  eliminarProducto(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/eliminar/${id}`, { responseType: 'text' });
  }

  obtenerPorId(id: number): Observable<ObtenerProducto> {
    return this.http.get<ObtenerProducto>(`${this.apiUrl}/id/${id}`);
  }

  listarProductos(): Observable<ObtenerProducto[]> {
    return this.http.get<ObtenerProducto[]>(`${this.apiUrl}/lista`);
  }

  actualizarFotoProducto(idProducto: number, foto: File): Observable<string> {

    const formData = new FormData();
    
    formData.append('foto', foto);

    return this.http.post(`${this.apiUrl}/${idProducto}/foto`, formData, { responseType: 'text' });
  }

  eliminarFotoProducto(idProducto: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${idProducto}/foto`, { responseType: 'text' });
  }

  aumentarStock(idProducto: number, cantidad: number): Observable<string> {
    return this.http.put(`${this.apiUrl}/aumentar-stock/${idProducto}/${cantidad}`, null, { responseType: 'text' });
  }
}
