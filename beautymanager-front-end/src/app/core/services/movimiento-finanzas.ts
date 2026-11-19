import { Injectable} from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ActualizarMovimientoFinanzas } from '../models/actualizar-movimiento-finanzas';
import { CrearGasto } from '../models/crear-gasto';
import { CrearMovimientoFinanzas } from '../models/crear-movimiento-finanzas';
import { FiltroGastos } from '../models/filtro-gastos';
import { ObtenerMovimientoFinanzas } from '../models/obtener-movimiento-finanzas';
import { EstadoMovimiento } from '../enums/estadoMovimiento';
import { TipoMovimiento } from '../enums/tipoMovimiento';
import { TipoGastoEnum } from '../enums/tipoGasto';

@Injectable({
  providedIn: 'root',
})
export class MovimientoFinanzas {
  
  private readonly apiUrl = 'http://localhost:8080/movimiento-finanzas';

  constructor(private http: HttpClient) {}

  crearMovimiento(movimiento: CrearMovimientoFinanzas): Observable<ObtenerMovimientoFinanzas> {
    return this.http.post<ObtenerMovimientoFinanzas>(`${this.apiUrl}/crear`, movimiento);
  }

  crearGasto(gasto: CrearGasto): Observable<ObtenerMovimientoFinanzas> {
    return this.http.post<ObtenerMovimientoFinanzas>(`${this.apiUrl}/gastos/crear`, gasto);
  }

  actualizarMovimiento(id: number, movimiento: ActualizarMovimientoFinanzas): Observable<ObtenerMovimientoFinanzas> {
    return this.http.put<ObtenerMovimientoFinanzas>(`${this.apiUrl}/actualizar/${id}`, movimiento);
  }

  buscarPorId(id: number): Observable<ObtenerMovimientoFinanzas> {
    return this.http.get<ObtenerMovimientoFinanzas>(`${this.apiUrl}/id/${id}`);
  }

  listarTodos(): Observable<ObtenerMovimientoFinanzas[]> {
    return this.http.get<ObtenerMovimientoFinanzas[]>(`${this.apiUrl}/listar`);
  }

  listarGastos(filtros?: FiltroGastos): Observable<ObtenerMovimientoFinanzas[]> {

    let params = new HttpParams();

    if (filtros) {
      Object.entries(filtros).forEach(([clave, valor]) => {
        if (valor !== undefined && valor !== null && valor !== '') {
          params = params.set(clave, String(valor));
        }
      });
    }

    return this.http.get<ObtenerMovimientoFinanzas[]>(`${this.apiUrl}/gastos/listar`, { params });
  }
  
  eliminarMovimiento(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/eliminar/${id}`, { responseType: 'text' });
  }

  buscarPorFecha(fecha: string): Observable<ObtenerMovimientoFinanzas[]> {

    const params = new HttpParams().set('fecha', fecha);

    return this.http.get<ObtenerMovimientoFinanzas[]>(`${this.apiUrl}/fecha`, { params });
  }

  buscarPorRangoFechas(inicio: string, fin: string): Observable<ObtenerMovimientoFinanzas[]> {

    const params = new HttpParams().set('inicio', inicio).set('fin', fin);

    return this.http.get<ObtenerMovimientoFinanzas[]>(`${this.apiUrl}/rango-fechas`, { params });
  }

  buscarPorTipoMovimiento(tipoMovimiento: TipoMovimiento): Observable<ObtenerMovimientoFinanzas[]> {
    return this.http.get<ObtenerMovimientoFinanzas[]>(`${this.apiUrl}/tipo/${tipoMovimiento}`);
  }

  buscarPorUsuario(idUsuario: number): Observable<ObtenerMovimientoFinanzas[]> {
    return this.http.get<ObtenerMovimientoFinanzas[]>(`${this.apiUrl}/usuario/${idUsuario}`);
  }

  buscarPorTipoGasto(tipoGasto: TipoGastoEnum): Observable<ObtenerMovimientoFinanzas[]> {
    return this.http.get<ObtenerMovimientoFinanzas[]>(`${this.apiUrl}/tipo-gasto/${tipoGasto}`);
  }

  buscarPorCompra(idCompra: number): Observable<ObtenerMovimientoFinanzas[]> {
    return this.http.get<ObtenerMovimientoFinanzas[]>(`${this.apiUrl}/compra/${idCompra}`);
  }

  buscarPorEstado(estado: EstadoMovimiento): Observable<ObtenerMovimientoFinanzas[]> {
    return this.http.get<ObtenerMovimientoFinanzas[]>(`${this.apiUrl}/estado/${estado}`);
  }

  buscarPorDescripcion(descripcion: string): Observable<ObtenerMovimientoFinanzas[]> {

    const params = new HttpParams().set('descripcion', descripcion);

    return this.http.get<ObtenerMovimientoFinanzas[]>(`${this.apiUrl}/descripcion`, { params });
  }

  totalIngresos(inicio: string, fin: string): Observable<number> {

    const params = new HttpParams().set('inicio', inicio).set('fin', fin);

    return this.http.get<number>(`${this.apiUrl}/total-ingresos`, { params });
  }

  totalEgresos(inicio: string, fin: string): Observable<number> {

    const params = new HttpParams().set('inicio', inicio).set('fin', fin);

    return this.http.get<number>(`${this.apiUrl}/total-egresos`, { params });
  }

  balance(inicio: string, fin: string): Observable<number> {

    const params = new HttpParams().set('inicio', inicio).set('fin', fin);

    return this.http.get<number>(`${this.apiUrl}/balance`, { params });
  }
}
