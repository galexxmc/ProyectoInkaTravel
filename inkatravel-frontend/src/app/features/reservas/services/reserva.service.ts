import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ReservaResponseDTO, ReservaRequestDTO } from '../../../core/interfaces/reserva.interface';
import { environment } from '../../../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class ReservaService {

  private baseUrl = `${environment.apiUrl}/reservas`;
  private http = inject(HttpClient);

  constructor() { }

  /**
   * (RF-08) Crea una nueva reserva PENDIENTE en el backend.
   * Llama a: POST /api/reservas (protegido por JWT)
   */
  crearReserva(reservaData: ReservaRequestDTO): Observable<ReservaResponseDTO> {
    return this.http.post<ReservaResponseDTO>(this.baseUrl, reservaData);
  }

  /**
   * (RF-11) Obtiene las reservas del usuario logueado.
   * Llama a: GET /api/reservas/mis-reservas (protegido por JWT)
   */
  obtenerMisReservas(): Observable<ReservaResponseDTO[]> {
    return this.http.get<ReservaResponseDTO[]>(`${this.baseUrl}/mis-reservas`);
  }
}