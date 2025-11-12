import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, RouterLink } from '@angular/router'; // Importar RouterOutlet y RouterLink
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink], // Añadir RouterOutlet y RouterLink
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent {
  
  private router = inject(Router);
  private authService = inject(AuthService);

  // --- ¡NUEVO! Getter para saber si es Admin ---
  get isAdmin(): boolean {
    const usuario = this.authService.getUsuarioInfo();
    return !!usuario && usuario.tipo === 'ADMIN';
  }

  // --- ¡NUEVO! ---
  // Variable para controlar el menú móvil
  isMobileMenuOpen: boolean = false;

  // --- ¡NUEVO! ---
  // Función para abrir/cerrar el menú
  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  // Lógica básica para cerrar sesión
  logout(): void {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_info');
    alert('Sesión cerrada.');
    this.router.navigateByUrl('/login');
  }

  // Lógica para verificar si está logueado (lo haremos más robusto luego)
  estaLogueado(): boolean {
    return !!localStorage.getItem('jwt_token');
  }
}