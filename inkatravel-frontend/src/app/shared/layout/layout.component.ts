import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, RouterLink } from '@angular/router'; // Importar RouterOutlet y RouterLink

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink], // Añadir RouterOutlet y RouterLink
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent {
  
  private router = inject(Router);

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