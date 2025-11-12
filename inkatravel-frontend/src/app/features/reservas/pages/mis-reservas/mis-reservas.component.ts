import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // Para *ngIf y *ngFor
import { ReservaService } from '../../services/reserva.service'; // El servicio que ya existe
import { RouterLink } from '@angular/router'; // Para enlazar de vuelta al catálogo

import { ReservaResponseDTO } from '../../../../core/interfaces/reserva.interface';

@Component({
  selector: 'app-mis-reservas',
  standalone: true,
  imports: [CommonModule, RouterLink], // Importamos RouterLink
  templateUrl: './mis-reservas.component.html',
  styleUrls: ['./mis-reservas.component.scss']
})
export class MisReservasComponent implements OnInit {

  // Inyectamos el servicio
  private reservaService = inject(ReservaService);

  // Variables para la vista
  reservas: ReservaResponseDTO[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  ngOnInit(): void {
    this.cargarMisReservas();
  }

  /**
   * (RF-11) Llama al servicio para obtener el historial de reservas
   * (El Interceptor de Auth se encarga del token)
   */
  cargarMisReservas(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.reservaService.obtenerMisReservas().subscribe({
      next: (data) => {
        this.reservas = data;
        this.isLoading = false;
      },
      error: (err) => {
        // Si el interceptor falla (token inválido) o el backend cae
        this.errorMessage = 'Error al cargar tus reservas. Intenta iniciar sesión de nuevo.';
        this.isLoading = false;
        console.error(err);
      }
    });
  }
}