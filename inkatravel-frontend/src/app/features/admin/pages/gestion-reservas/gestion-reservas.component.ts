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
  // (Asegúrate de que estas líneas existan)
  showConfirmModal: boolean = false;
  reservaIdParaConfirmar: number | null = null;
  modalConfirmTitulo: string = 'Confirmar Pago';
  modalConfirmMensaje: string = '¿Estás seguro de que quieres confirmar este pago manualmente? Esta acción dará puntos y enviará un email al usuario.';

  // --- Control del Modal de INFORMACIÓN (Éxito/Error) ---
  showInfoModal: boolean = false;
  modalInfoTitulo: string = '';
  modalInfoMensaje: string = '';

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
          return 0;
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
   * 1. Se llama al hacer clic en el botón "Confirmar Pago".
   */
  abrirModalConfirmacion(id: number): void {
    this.reservaIdParaConfirmar = id; // <-- Aquí se guarda el ID
    this.showConfirmModal = true;
  }

  /**
   * 2. Se llama si el usuario hace clic en "Cancelar" en el modal.
   */
  cerrarModalConfirmacion(): void {
    this.showConfirmModal = false;
    this.reservaIdParaConfirmar = null; // <-- Aquí se resetea a null
  }

  /**
   * 3. Cierra el modal de "Éxito/Error"
   */
  cerrarModalInfo(): void {
    this.showInfoModal = false;
  }

  /**
   * 4. Se llama si el usuario hace clic en "Confirmar" en el modal.
   */
  ejecutarConfirmacion(): void {
    // Esta guarda previene el 'null'
    if (!this.reservaIdParaConfirmar) {
      console.error("Error: ID de reserva es nulo. No se puede confirmar.");
      return; 
    }

    const idParaConfirmar = this.reservaIdParaConfirmar; // Guardamos el ID por si acaso
    this.cerrarModalConfirmacion(); // Cierra el modal de confirmación
    this.isConfirmingPayment = true; // MUESTRA EL LOADING

    this.adminService.confirmarPago(idParaConfirmar).subscribe({ // Usa el ID guardado
      next: () => {
        this.isConfirmingPayment = false;
        this.cargarReservas(); 
        
        this.modalInfoTitulo = '¡Éxito!';
        this.modalInfoMensaje = 'Reserva confirmada exitosamente. El email ha sido enviado.';
        this.showInfoModal = true;
      },
      error: (err) => {
        this.isConfirmingPayment = false;
        
        this.modalInfoTitulo = 'Error';
        // Usamos err.error (que es el mensaje del backend) o el mensaje genérico
        this.modalInfoMensaje = err.error || 'Ocurrió un error al confirmar.';
        this.showInfoModal = true;
      }
    });
  }
}