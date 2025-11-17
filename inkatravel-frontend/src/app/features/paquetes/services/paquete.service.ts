import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

// Importa las interfaces DTO que definimos
import { PaqueteDetalleResponseDTO, PaqueteTuristicoResponseDTO} from '../../../core/interfaces/paquete.interface';

@Injectable({
  providedIn: 'root'
})
export class PaqueteService {

  // URL base de tu API de paquetes en Spring Boot
  private baseUrl = 'http://localhost:8080/api/paquetes';
  
  // Inyecta el HttpClient
  private http = inject(HttpClient);

  constructor() { }

  /**
   * (RF-03 & RF-05) Obtiene el catálogo de paquetes, permitiendo filtros.
   * Llama a: GET /api/paquetes?region=Cusco&precioMax=1500
   * * @param filtros Un objeto opcional con claves como 'region', 'precioMin', etc.
   * @returns Un Observable con la lista de paquetes filtrados.
   */
  obtenerPaquetes(filtros: any = {}): Observable<PaqueteTuristicoResponseDTO[]> {
    let params = new HttpParams();
    
    // Si hay filtros, añade cada uno como un parámetro de consulta
    if (filtros) {
        Object.keys(filtros).forEach(key => {
            // Solo añade el parámetro si no es nulo o vacío
            if (filtros[key] !== null && filtros[key] !== undefined && filtros[key] !== '') {
                params = params.set(key, filtros[key]);
            }
        });
    }

    // Devuelve la llamada GET con los parámetros
    return this.http.get<PaqueteTuristicoResponseDTO[]>(this.baseUrl, { params });
  }

  /**
   * (RF-04 & RF-14) Obtiene el detalle de un paquete (incluye info del clima).
   * Llama a: GET /api/paquetes/{id}
   * * @param id El ID numérico del paquete.
   * @returns Un Observable con el DTO contenedor (paquete + clima).
   */
  obtenerDetallePaquete(id: number): Observable<PaqueteDetalleResponseDTO> {
    return this.http.get<PaqueteDetalleResponseDTO>(`${this.baseUrl}/${id}`);
  }

  /**
   * (RF-13) Obtiene recomendaciones personalizadas para el usuario logueado.
   * Llama a: GET /api/paquetes/recomendados (Requiere Token JWT)
   * * @returns Un Observable con la lista de paquetes recomendados.
   */
  obtenerRecomendaciones(): Observable<PaqueteTuristicoResponseDTO[]> {
    // Nota: Esta llamada fallará si no se envía el Token JWT.
    // Más adelante implementaremos un "Interceptor HTTP" para añadir
    // el token automáticamente a todas las peticiones protegidas.
    return this.http.get<PaqueteTuristicoResponseDTO[]>(`${this.baseUrl}/recomendados`);
  }
  // --- ¡NUEVOS MÉTODOS DE ADMIN! (RF-12) ---

/**
   * (RF-12) Crea un nuevo paquete. (Requiere token Admin)
    * Acepta FormData, que contiene el JSON y el archivo de imagen.
   * Llama a: POST /api/paquetes (con multipart/form-data)
   */
  crearPaquete(paqueteFormData: FormData): Observable<PaqueteTuristicoResponseDTO> {
    // HttpClient detecta FormData y usa automáticamente el encabezado Content-Type: multipart/form-data
    return this.http.post<PaqueteTuristicoResponseDTO>(this.baseUrl, paqueteFormData);
  }

  /**
   * (RF-12) Actualiza un paquete existente. (Requiere token Admin)
    * Acepta FormData, que contiene el JSON y opcionalmente el archivo de imagen.
   * Llama a: PUT /api/paquetes/{id} (con multipart/form-data)
   */
  actualizarPaquete(id: number, paqueteFormData: FormData): Observable<PaqueteTuristicoResponseDTO> {
    return this.http.put<PaqueteTuristicoResponseDTO>(`${this.baseUrl}/${id}`, paqueteFormData);
  }

  /**
   * (RF-12) Elimina un paquete. (Requiere token Admin)
   * Llama a: DELETE /api/paquetes/{id}
   */
  eliminarPaquete(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/${id}`, { responseType: 'text' });
  }
}