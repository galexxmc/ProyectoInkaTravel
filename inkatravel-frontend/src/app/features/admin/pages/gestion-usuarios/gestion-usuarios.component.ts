import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../services/admin.service';
import { UsuarioResponseDTO } from '../../../../core/interfaces/usuario.interface';
import { ModalConfirmacionComponent } from '../../../../shared/modal-confirmacion/modal-confirmacion.component';
// ¡Importante! Añade FormsModule para que funcione el [(ngModel)] del modal de rol
import { FormsModule } from '@angular/forms';   

@Component({
  selector: 'app-gestion-usuarios',
  standalone: true,
  imports: [
    CommonModule,
    ModalConfirmacionComponent, // El modal reutilizable
    FormsModule // Para el <select> del modal de rol
  ],
  templateUrl: './gestion-usuarios.component.html',
  styleUrls: ['./gestion-usuarios.component.scss']
})
export class GestionUsuariosComponent implements OnInit {

  private adminService = inject(AdminService);

  // Variables para la tabla
  usuarios: UsuarioResponseDTO[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  // --- Modal de CONFIRMACIÓN (Desactivar/Habilitar) ---
  showConfirmModal: boolean = false;
  usuarioIdParaAccion: number | null = null;
  accionParaConfirmar: 'desactivar' | 'habilitar' | null = null;
  modalConfirmTitulo: string = '';
  modalConfirmMensaje: string = '';
  
  // --- Notificación "Toast" (Éxito/Error) ---
  showNotification: boolean = false;
  notificationTitle: string = ''; // Título para el toast
  notificationMessage: string = ''; // Mensaje para el toast
  notificationType: 'success' | 'error' = 'success';
  private notificationTimer: any; // Para el setTimeout

  // --- Modal de CAMBIAR ROL ---
  showRoleModal: boolean = false;
  usuarioIdParaRol: number | null = null;
  nuevoRolSeleccionado: 'GRATIS' | 'PREMIUM' | 'ADMIN' = 'GRATIS'; 
  usuarioNombreParaRol: string = '';
  modalRoleTitulo: string = 'Cambiar Rol de Usuario';


  ngOnInit(): void {
    this.cargarUsuarios();
  }

  /**
   * Carga la lista de todos los usuarios desde el backend
   */
  cargarUsuarios(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.adminService.obtenerTodosLosUsuarios().subscribe({
      next: (data) => {
        this.usuarios = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar los usuarios.';
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  // --- Lógica de Modales y Notificaciones ---

  // Cierra el modal de "Confirmación"
  cerrarModalConfirmacion(): void {
    this.showConfirmModal = false;
    this.usuarioIdParaAccion = null;
    this.accionParaConfirmar = null;
  }

  // Cierra el modal de "Rol"
  cerrarModalRol(): void {
    this.showRoleModal = false;
    this.usuarioIdParaRol = null;
    this.usuarioNombreParaRol = '';
  }

  /**
   * Muestra la notificación "toast" y la oculta después de 3 segundos.
   */
  mostrarNotificacion(titulo: string, mensaje: string, tipo: 'success' | 'error') {
    this.notificationTitle = titulo;
    this.notificationMessage = mensaje;
    this.notificationType = tipo;
    this.showNotification = true;

    // Limpiar timer anterior si existe
    if (this.notificationTimer) {
      clearTimeout(this.notificationTimer);
    }

    // Ocultar después de 3 segundos
    this.notificationTimer = setTimeout(() => {
      this.showNotification = false;
    }, 3000);
  }


  // --- ACCIONES DE ADMIN ---

  /**
   * 1. Abrir Modal de Rol
   */
  abrirModalRol(usuario: UsuarioResponseDTO): void {
    // No permitimos cambiar el rol de un Admin (por seguridad)
    if (usuario.tipo === 'ADMIN') {
        this.mostrarNotificacion('Acción No Permitida', 'No se puede modificar el rol de otro Administrador.', 'error');
        return;
    }
    this.usuarioIdParaRol = usuario.id;
    this.usuarioNombreParaRol = usuario.nombre;
    this.nuevoRolSeleccionado = usuario.tipo; 
    this.showRoleModal = true;
  }

  /**
   * 2. Ejecutar Cambio de Rol
   * (Se llama desde el modal de Rol)
   */
  ejecutarCambioDeRol(): void {
    if (!this.usuarioIdParaRol) return;

    this.adminService.actualizarRolUsuario(this.usuarioIdParaRol, this.nuevoRolSeleccionado).subscribe({
      next: () => {
        this.cargarUsuarios(); // Recarga la tabla
        this.mostrarNotificacion('¡Éxito!', 'Rol actualizado exitosamente.', 'success');
      },
      error: (err) => {
        this.mostrarNotificacion('Error', err.error || 'No se pudo actualizar el rol.', 'error');
      }
    });
    this.cerrarModalRol(); // Cierra el modal de Rol
  }

  /**
   * 3. Desactivar (ABRE EL MODAL de confirmación)
   */
  desactivar(id: number): void {
    this.usuarioIdParaAccion = id;
    this.accionParaConfirmar = 'desactivar';
    this.modalConfirmTitulo = 'Desactivar Usuario';
    this.modalConfirmMensaje = '¿Estás seguro de que quieres desactivar (banear) a este usuario? No podrá iniciar sesión.';
    this.showConfirmModal = true;
  }
  
  /**
   * 4. Habilitar (ABRE EL MODAL de confirmación)
   */
  habilitar(id: number): void {
    this.usuarioIdParaAccion = id;
    this.accionParaConfirmar = 'habilitar';
    this.modalConfirmTitulo = 'Habilitar Usuario';
    this.modalConfirmMensaje = '¿Estás seguro de que quieres reactivar esta cuenta?';
    this.showConfirmModal = true;
  }

  /**
   * 5. Ejecuta la confirmación de Habilitar/Desactivar
   */
  ejecutarAccion(): void {
    if (!this.usuarioIdParaAccion || !this.accionParaConfirmar) return;

    let servicio: any;
    let mensajeExito: string = ''; // Mensaje de éxito personalizado

    if (this.accionParaConfirmar === 'desactivar') {
      servicio = this.adminService.desactivarUsuario(this.usuarioIdParaAccion);
      mensajeExito = 'Usuario desactivado exitosamente.';
    } else {
      servicio = this.adminService.habilitarUsuario(this.usuarioIdParaAccion);
      mensajeExito = 'Usuario habilitado exitosamente.';
    }

    this.cerrarModalConfirmacion();

    servicio.subscribe({
      next: (backendMsg: string) => { // El backend devuelve un string
        this.cargarUsuarios();
        this.mostrarNotificacion('¡Éxito!', backendMsg, 'success'); // Usamos el mensaje del backend
      },
      error: (err: any) => {
        this.mostrarNotificacion('Error', err.error || 'Ocurrió un error.', 'error');
      }
    });
  }
}