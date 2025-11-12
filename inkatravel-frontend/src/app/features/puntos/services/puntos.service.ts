import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { PuntoFidelidadResponseDTO } from '../../../core/interfaces/puntos.interface';

@Injectable({
  providedIn: 'root'
})
export class PuntosService {

  private baseUrl = 'http://localhost:8080/api/puntos';
  private http = inject(HttpClient);

  constructor() { }

  /**
   * (RF-11) Obtiene el historial de puntos del usuario logueado.
   * Llama a: GET /api/puntos/historial (protegido por JWT)
   */
  obtenerMiHistorial(): Observable<PuntoFidelidadResponseDTO[]> {
    return this.http.get<PuntoFidelidadResponseDTO[]>(`${this.baseUrl}/historial`);
  }
}