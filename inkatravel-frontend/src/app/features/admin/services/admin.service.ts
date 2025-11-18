import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

// Importamos la nueva interfaz de métricas del Dashboard
import { DashboardMetricsDTO, MonthlySaleDTO } from '../../../core/interfaces/admin.interface'; 

// Importamos los DTOs que ya tenemos (Se mantienen)
import { UsuarioResponseDTO } from '../../../core/interfaces/usuario.interface';
import { ReservaResponseDTO } from '../../../core/interfaces/reserva.interface';
import { PagoResponseDTO } from '../../../core/interfaces/pago.interface';
import { PaqueteTuristicoResponseDTO } from '../../../core/interfaces/paquete.interface';

import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private baseUrl = `${environment.apiUrl}/admin`;
  private http = inject(HttpClient);

  constructor() { }

  // --- Gestión de Usuarios (RF-12) ---

  /** (RF-12) Obtiene la lista de TODOS los usuarios */
  obtenerTodosLosUsuarios(): Observable<UsuarioResponseDTO[]> {
    return this.http.get<UsuarioResponseDTO[]>(`${this.baseUrl}/usuarios`);
  }

  /** (RF-12) Actualiza el rol de un usuario */
  actualizarRolUsuario(id: number, nuevoRol: string): Observable<UsuarioResponseDTO> {
    const dto = { nuevoRol: nuevoRol };
    return this.http.put<UsuarioResponseDTO>(`${this.baseUrl}/usuarios/${id}/rol`, dto);
  }

  /** (RF-12) Desactiva (banea) a un usuario */
  desactivarUsuario(id: number): Observable<string> { 
    return this.http.delete(`${this.baseUrl}/usuarios/${id}`, { responseType: 'text' });
  }

  /** (RF-12) Reactiva a un usuario */
  habilitarUsuario(id: number): Observable<string> { 
    return this.http.put(`${this.baseUrl}/usuarios/${id}/habilitar`, {}, { responseType: 'text' });
  }

  // --- Gestión de Reservas (RF-12) ---

  /** (RF-12) Obtiene la lista de TODAS las reservas */
  obtenerTodasLasReservas(): Observable<ReservaResponseDTO[]> {
    return this.http.get<ReservaResponseDTO[]>(`${this.baseUrl}/reservas`);
  }

  /** (RF-09 Manual) Confirma un pago pendiente */
  confirmarPago(reservaId: number): Observable<PagoResponseDTO> {
    return this.http.post<PagoResponseDTO>(`${this.baseUrl}/reservas/${reservaId}/confirmar`, {});
  }

  /** (NUEVO) Obtiene TODOS los paquetes (activos e inactivos) */
  obtenerTodosLosPaquetes(): Observable<PaqueteTuristicoResponseDTO[]> {
    return this.http.get<PaqueteTuristicoResponseDTO[]>(`${this.baseUrl}/paquetes`);
  }

  
  // ===================================================================
  // --- Dashboard Métricas ---

  /**
   * Obtiene todas las métricas clave para el Dashboard.
   * Llama a: GET /api/admin/metrics
   */
  getDashboardMetrics(): Observable<DashboardMetricsDTO> {
    // Esta ruta ya está protegida en el backend para solo administradores
    return this.http.get<DashboardMetricsDTO>(`${this.baseUrl}/metrics`);
  }
  // ===================================================================

  /**
   * (NUEVO) Obtiene los datos de ventas de los últimos 6 meses.
   */
  getMonthlySalesData(): Observable<MonthlySaleDTO[]> {
    return this.http.get<MonthlySaleDTO[]>(`${this.baseUrl}/sales/monthly`);
  }
  
}