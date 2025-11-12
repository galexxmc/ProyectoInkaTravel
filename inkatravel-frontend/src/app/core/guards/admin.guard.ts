import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service'; // Importa tu AuthService

export const adminGuard: CanActivateFn = (route, state) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  const usuario = authService.getUsuarioInfo(); // Obtiene el usuario de localStorage

  // 1. ¿Está logueado?
  if (!usuario) {
    router.navigate(['/login']);
    return false;
  }

  // 2. ¿Es ADMIN?
  if (usuario.tipo === 'ADMIN') {
    return true; // ¡Acceso permitido!
  }

  // 3. Si está logueado pero NO es Admin
  alert('Acceso denegado. No tienes permisos de administrador.');
  router.navigate(['/home']); // Lo mandamos al home
  return false;
};