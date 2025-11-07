import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// (Importante: Necesitarás crear esta interfaz, la usaremos después)
// import { UsuarioResponseDTO } from '../interfaces/usuario.interface';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  // La URL de tu backend (Asegúrate de que tu Spring Boot esté corriendo)
  private baseUrl = 'http://localhost:8080/api/usuarios';

  constructor(private http: HttpClient) { }

  /**
   * (RF-02) Llama al endpoint de Login del backend.
   *
   * @param credenciales Objeto con 'correo' y 'contrasena'
   * @returns Un Observable con la respuesta (Token y Usuario)
   */
  login(credenciales: any): Observable<any> {
    // Llama a: POST http://localhost:8080/api/usuarios/login
    return this.http.post(`${this.baseUrl}/login`, credenciales);
  }

  /**
   * (RF-01) Llama al endpoint de Registro del backend.
   *
   * @param usuario Objeto con 'nombre', 'correo', 'contrasena', etc.
   * @returns Un Observable con el usuario creado
   */
  registro(usuario: any): Observable<any> {
    // Llama a: POST http://localhost:8080/api/usuarios/registro
    return this.http.post(`${this.baseUrl}/registro`, usuario);
  }

  // --- Manejo del Token (Lo añadiremos después) ---

  // guardarToken(token: string) { ... }
  // getToken(): string | null { ... }
  // estaLogueado(): boolean { ... }
  // logout() { ... }
}