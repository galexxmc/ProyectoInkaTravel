import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Interceptor funcional (Angular 17+)
 * Añade el token JWT a todas las peticiones salientes.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {

  // 1. Obtener el token guardado
  const token = localStorage.getItem('jwt_token');

  // 2. Si no hay token (ej: en Login o Registro), deja pasar la petición
  if (!token) {
    return next(req);
  }

  // 3. Si SÍ hay token, clonar la petición y añadir la cabecera
  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  // 4. Dejar pasar la petición MODIFICADA
  return next(authReq);
};