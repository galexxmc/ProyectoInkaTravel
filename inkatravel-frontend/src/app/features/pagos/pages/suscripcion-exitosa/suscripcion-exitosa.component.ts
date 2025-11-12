import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UsuarioService } from '../../../usuario/services/usuario.service'; // Importa el servicio de usuario

@Component({
  selector: 'app-suscripcion-exitosa',
  standalone: true,
  imports: [CommonModule, ],
  templateUrl: './suscripcion-exitosa.component.html',
  styleUrls: ['./suscripcion-exitosa.component.scss']
})
export class SuscripcionExitosaComponent implements OnInit {

  private router = inject(Router);
  private usuarioService = inject(UsuarioService); // Inyecta el servicio

  isLoading: boolean = true;
  errorMessage: string = '';

  ngOnInit(): void {
    // 1. Llamar al backend para obtener los datos FRESCOS del usuario
    // (El interceptor de JWT añadirá el token automáticamente)
    this.usuarioService.getMiPerfil().subscribe({
      next: (usuarioActualizado) => {
        // 2. ¡LA CLAVE! Actualizamos el localStorage
        localStorage.setItem('user_info', JSON.stringify(usuarioActualizado));
        
        console.log('¡Suscripción exitosa! localStorage actualizado.', usuarioActualizado);
        this.isLoading = false;
      },
      error: (err) => {
        // Esto no debería pasar si el usuario está logueado
        this.errorMessage = 'Error al verificar tu suscripción. Intenta recargar.';
        this.isLoading = false;
      }
    });
  }

  irAlPerfil(): void {
    // Usamos el href para forzar la salida de ngrok (como en pago-exitoso)
    window.location.href = 'http://localhost:4200/perfil';
  }
}