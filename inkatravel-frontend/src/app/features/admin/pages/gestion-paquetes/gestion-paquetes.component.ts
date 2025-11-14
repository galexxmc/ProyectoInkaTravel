import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { PaqueteService } from '../../../paquetes/services/paquete.service';
import { AdminService } from '../../services/admin.service';
import { PaqueteTuristicoResponseDTO } from '../../../../core/interfaces/paquete.interface';
import { ModalConfirmacionComponent } from '../../../../shared/modal-confirmacion/modal-confirmacion.component';

@Component({
  selector: 'app-gestion-paquetes',
  standalone: true,
  imports: [
    CommonModule,
    ModalConfirmacionComponent // El modal reutilizable
  ],
  templateUrl: './gestion-paquetes.component.html',
  styleUrls: ['./gestion-paquetes.component.scss']
})
export class GestionPaquetesComponent implements OnInit {

  // Inyectamos ambos servicios
  private paqueteService = inject(PaqueteService); // Para Acciones (C,U,D)
  private adminService = inject(AdminService);     // Para Leer la tabla (R)
  private router = inject(Router); // Para navegar

  paquetes: PaqueteTuristicoResponseDTO[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  // --- Modal de Confirmación (Para Eliminar) ---
  showConfirmModal: boolean = false;
  paqueteIdParaEliminar: number | null = null;
  modalConfirmTitulo: string = 'Eliminar Paquete';
  modalConfirmMensaje: string = '¿Estás seguro de que quieres eliminar este paquete? Esta acción no se puede deshacer.';
  
  // --- Notificación "Toast" (Éxito/Error) ---
  showNotification: boolean = false;
  notificationTitle: string = '';
  notificationMessage: string = '';
  notificationType: 'success' | 'error' = 'success';
  private notificationTimer: any;

  ngOnInit(): void {
    this.cargarPaquetes();
  }

  /**
   * Carga TODOS los paquetes (activos e inactivos)
   * Llama al AdminService
   */
  cargarPaquetes(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.adminService.obtenerTodosLosPaquetes().subscribe({
      next: (data) => {
        this.paquetes = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar los paquetes.';
        this.isLoading = false;
      }
    });
  }

  // --- Lógica de Acciones ---

  irACrear(): void {
    this.router.navigate(['/admin/paquetes/nuevo']);
  }

  irAEditar(id: number): void {
    this.router.navigate(['/admin/paquetes/editar', id]);
  }

  abrirModalEliminar(id: number): void {
    this.paqueteIdParaEliminar = id;
    this.showConfirmModal = true;
  }

  cerrarModalConfirmacion(): void {
    this.showConfirmModal = false;
    this.paqueteIdParaEliminar = null;
  }

  /**
   * Muestra la notificación "toast" y la oculta después de 3 segundos.
   */
  mostrarNotificacion(titulo: string, mensaje: string, tipo: 'success' | 'error') {
    this.notificationTitle = titulo;
    this.notificationMessage = mensaje;
    this.notificationType = tipo;
    this.showNotification = true;

    if (this.notificationTimer) {
      clearTimeout(this.notificationTimer);
    }
    this.notificationTimer = setTimeout(() => {
      this.showNotification = false;
    }, 3000);
  }

  /**
   * Llama al PaqueteService para eliminar
   * y usa el "toast" para la respuesta.
   */
  ejecutarEliminacion(): void {
    if (!this.paqueteIdParaEliminar) return;

    // Usamos el PaqueteService (no AdminService) para la acción de eliminar
    this.paqueteService.eliminarPaquete(this.paqueteIdParaEliminar).subscribe({
      next: (mensajeExito: string) => {
        this.cargarPaquetes(); // Recarga la tabla
        this.mostrarNotificacion('¡Éxito!', mensajeExito, 'success');
      },
      error: (err) => {
        // Muestra el error amigable del backend (ej: "No se puede eliminar, tiene reservas")
        this.mostrarNotificacion('Error', err.error || 'No se pudo eliminar el paquete.', 'error');
      }
    });

    this.cerrarModalConfirmacion();
  }
}