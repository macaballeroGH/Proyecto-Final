import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ArticuloBelleza } from '../models/articulo-belleza';

@Injectable({
  providedIn: 'root',
})
export class ArticuloBellezaService {

  private apiUrl = 'http://localhost:8080/articulos-belleza';

  constructor(private http: HttpClient) {}

  obtenerArticulos(): Observable<ArticuloBelleza[]> {
    return this.http.get<ArticuloBelleza[]>(`${this.apiUrl}/listar`);
  }
}
