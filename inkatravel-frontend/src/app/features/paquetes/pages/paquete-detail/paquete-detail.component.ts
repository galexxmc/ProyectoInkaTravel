import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PaqueteService } from '../../services/paquete.service';
import { PaqueteDetalleResponseDTO } from '../../../../core/interfaces/paquete.interface';

@Component({
  selector: 'app-paquete-detail',
  standalone: true,
  imports: [CommonModule, RouterLink], // Añadir RouterLink
  templateUrl: './paquete-detail.component.html',
  styleUrls: ['./paquete-detail.component.scss']
})
export class PaqueteDetailComponent implements OnInit {

  // Inyectamos los servicios
  private route = inject(ActivatedRoute);
  private paqueteService = inject(PaqueteService);

  // Variables para almacenar los datos
  detalle: PaqueteDetalleResponseDTO | null = null;
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor() { }

  ngOnInit(): void {
    // 1. Leer el 'id' de la URL (ej: /paquetes/1)
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      // 2. Llamar al servicio con el ID
      this.paqueteService.obtenerDetallePaquete(+id).subscribe({ // +id convierte string a number
        next: (data) => {
          this.detalle = data;
          this.isLoading = false;
        },
        error: (err) => {
          this.errorMessage = 'Error al cargar el detalle del paquete.';
          this.isLoading = false;
          console.error(err);
        }
      });
    } else {
      this.errorMessage = 'ID de paquete no encontrado.';
      this.isLoading = false;
    }
  }

  // (PENDIENTE) Lógica para reservar
  reservarAhora(paqueteId: number) {
    console.log('Reservar paquete:', paqueteId);
    // Aquí llamaremos al ReservaService en el futuro
    alert('¡Funcionalidad de reserva en construcción!');
  }
}