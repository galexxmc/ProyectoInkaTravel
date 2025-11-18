import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { UsuarioResponseDTO } from '../../../core/interfaces/usuario.interface';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private baseUrl = `${environment.apiUrl}/usuarios`;
  
  private http = inject(HttpClient);

  constructor() { }

  /**
   * (RF-11) Obtiene el perfil del usuario logueado.
   * Llama a: GET /api/usuarios/perfil (protegido por JWT)
   */
  getMiPerfil(): Observable<UsuarioResponseDTO> {
    return this.http.get<UsuarioResponseDTO>(`${this.baseUrl}/perfil`);
  }
}