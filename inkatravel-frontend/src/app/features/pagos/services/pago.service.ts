import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

// Interface para la respuesta del link de pago
interface PagoLinkResponse {
  url: string;
}

@Injectable({
  providedIn: 'root'
})
export class PagoService {

  private baseUrl = 'http://localhost:8080/api/pagos';
  private http = inject(HttpClient);

  constructor() { }

  /**
   * (RF-09) Pide al backend el link de pago para un paquete.
   * Llama a: POST /api/pagos/crear-checkout/{reservaId} (protegido)
   */
  crearLinkDePago(reservaId: number): Observable<PagoLinkResponse> {
    return this.http.post<PagoLinkResponse>(`${this.baseUrl}/crear-checkout/${reservaId}`, {});
  }

  /**
   * (RF-06) Pide al backend el link de pago para la suscripción.
   * Llama a: POST /api/pagos/crear-checkout-premium (protegido)
   */
  crearLinkSuscripcion(): Observable<PagoLinkResponse> {
    return this.http.post<PagoLinkResponse>(`${this.baseUrl}/crear-checkout-premium`, {});
  }
}