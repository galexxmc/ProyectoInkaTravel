import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PuntosService } from '../../services/puntos.service';
import { PuntoFidelidadResponseDTO } from '../../../../core/interfaces/puntos.interface';

@Component({
  selector: 'app-historial-puntos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './historial-puntos.component.html',
  styleUrls: ['./historial-puntos.component.scss']
})
export class HistorialPuntosComponent implements OnInit {

  private puntosService = inject(PuntosService);

  historial: PuntoFidelidadResponseDTO[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  // (Opcional) Podríamos obtener el total de puntos desde el AuthService/localStorage
  // totalPuntos: number = 0; 

  ngOnInit(): void {
    this.cargarHistorial();
  }

  /**
   * (RF-11) Llama al servicio para obtener el historial de puntos
   */
  cargarHistorial(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.puntosService.obtenerMiHistorial().subscribe({
      next: (data) => {
        this.historial = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar tu historial de puntos.';
        this.isLoading = false;
        console.error(err);
      }
    });
  }
}