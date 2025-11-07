import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaqueteService } from '../../services/paquete.service';
import { PaqueteTuristicoResponseDTO } from '../../../../core/interfaces/paquete.interface';
import { RouterLink } from '@angular/router'; // Para los enlaces de "Ver Detalle"

@Component({
  selector: 'app-paquetes-list',
  standalone: true,
  imports: [CommonModule, RouterLink], // Añadir RouterLink
  templateUrl: './paquetes-list.component.html',
  styleUrls: ['./paquetes-list.component.scss']
})
export class PaquetesListComponent implements OnInit {

  // Inyectamos el servicio
  private paqueteService = inject(PaqueteService);

  // Array para almacenar los paquetes que vienen del backend
  paquetes: PaqueteTuristicoResponseDTO[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  // Aquí guardaremos los filtros (por ahora vacío)
  filtros = {
    region: null,
    precioMin: null,
    precioMax: null
  };
  
  constructor() { }

  ngOnInit(): void {
    // Cuando el componente se carga, llama al método para cargar paquetes
    this.cargarPaquetes();
  }

  /**
   * (RF-03 & RF-05) Llama al servicio para obtener los paquetes
   */
  cargarPaquetes(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.paqueteService.obtenerPaquetes(this.filtros).subscribe({
      next: (data) => {
        this.paquetes = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar los paquetes. Intente más tarde.';
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  // --- Lógica de Filtros (La añadiremos aquí) ---
  
  // onFiltroRegionChange(event: any) {
  //   this.filtros.region = event.target.value;
  //   this.cargarPaquetes();
  // }
}