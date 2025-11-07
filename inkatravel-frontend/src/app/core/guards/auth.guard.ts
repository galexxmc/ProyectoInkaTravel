import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  
  const token = localStorage.getItem('jwt_token');
  const router = inject(Router);

  // Si el token existe, el usuario está logueado
  if (token) {
    return true; // Permitir el acceso
  }

  // Si no hay token, redirigir al login
  alert('Acceso denegado. Por favor, inicie sesión.');
  router.navigate(['/login']);
  return false; // Bloquear el acceso
};