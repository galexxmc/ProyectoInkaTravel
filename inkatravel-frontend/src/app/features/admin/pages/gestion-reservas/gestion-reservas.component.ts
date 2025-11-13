import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../services/admin.service';
import { ReservaResponseDTO } from '../../../../core/interfaces/reserva.interface';
// Importamos el componente del Modal para poder usarlo
import { ModalConfirmacionComponent } from '../../../../shared/modal-confirmacion/modal-confirmacion.component';

@Component({
  selector: 'app-gestion-reservas',
  standalone: true,
  imports: [
    CommonModule, 
    ModalConfirmacionComponent // ¡Añadimos el Modal a las importaciones!
  ],
  templateUrl: './gestion-reservas.component.html',
  styleUrls: ['./gestion-reservas.component.scss']
})
export class GestionReservasComponent implements OnInit {

  // Inyectamos el servicio de Admin
  private adminService = inject(AdminService);

  // Variables para la vista
  reservas: ReservaResponseDTO[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  // --- Control del Modal de CONFIRMACIÓN ---
  showConfirmModal: boolean = false;
  reservaIdParaConfirmar: number | null = null;
  modalConfirmTitulo: string = 'Confirmar Pago';
  modalConfirmMensaje: string = '¿Estás seguro de que quieres confirmar este pago manualmente? Esta acción dará puntos y enviará un email al usuario.';

  // --- ¡ACTUALIZADO! Notificación "Toast" (Éxito/Error) ---
  showNotification: boolean = false;
  notificationTitle: string = '';
  notificationMessage: string = '';
  notificationType: 'success' | 'error' = 'success';
  private notificationTimer: any; // Para el setTimeout

  // --- Estado de Carga (mientras se envía el email) ---
  isConfirmingPayment: boolean = false;

  ngOnInit(): void {
    this.cargarReservas();
  }

  /**
   * (RF-12) Carga todas las reservas desde el backend
   */
  cargarReservas(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.adminService.obtenerTodasLasReservas().subscribe({
      next: (data) => {
        // Ordenamos para ver las PENDIENTES primero
        this.reservas = data.sort((a, b) => {
          if (a.estado === 'PENDIENTE' && b.estado !== 'PENDIENTE') return -1;
          if (a.estado !== 'PENDIENTE' && b.estado === 'PENDIENTE') return 1;
          return 0; // Mantenemos el orden original para otros estados
        });
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar las reservas.';
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  // --- Lógica de Modales ---

  /**
   * 1. Se llama al hacer clic en el botón "Confirmar Pago" de una reserva.
   */
  abrirModalConfirmacion(id: number): void {
    this.reservaIdParaConfirmar = id; // Guarda el ID
    this.showConfirmModal = true;           // Muestra el modal
  }

  /**
   * 2. Se llama si el usuario hace clic en "Cancelar" en el modal.
   */
  cerrarModalConfirmacion(): void {
    this.showConfirmModal = false;
    this.reservaIdParaConfirmar = null;
  }

  /**
   * (NUEVO) Muestra la notificación "toast" y la oculta después de 3 segundos.
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
   * 3. Se llama si el usuario hace clic en "Confirmar" en el modal.
   * (ACTUALIZADO para usar el Toast)
   */
  ejecutarConfirmacion(): void {
    if (!this.reservaIdParaConfirmar) return;

    const idParaConfirmar = this.reservaIdParaConfirmar;
    
    this.cerrarModalConfirmacion(); // Cierra el modal de confirmación
    this.isConfirmingPayment = true; // ¡MUESTRA EL LOADING!

    this.adminService.confirmarPago(idParaConfirmar).subscribe({
      next: (pagoConfirmado) => {
        this.isConfirmingPayment = false; // Oculta el loading
        this.cargarReservas(); // Recarga la lista
        
        // ¡MUESTRA LA NOTIFICACIÓN "TOAST"!
        this.mostrarNotificacion(
          '¡Éxito!',
          `Reserva #${pagoConfirmado.reservaId} confirmada. Email enviado.`,
          'success'
        );
      },
      error: (err) => {
        this.isConfirmingPayment = false; // Oculta el loading
        
        // ¡MUESTRA LA NOTIFICACIÓN "TOAST" DE ERROR!
        this.mostrarNotificacion(
          'Error',
          err.error || 'Ocurrió un error al confirmar.',
          'error'
        );
      }
    });
  }
}