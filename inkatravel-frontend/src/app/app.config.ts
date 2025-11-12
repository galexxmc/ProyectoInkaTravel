import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http'; // <-- 1. IMPORTAR 'withInterceptors'
import { authInterceptor } from './core/interceptors/auth.interceptor'; // <-- 2. IMPORTAR TU INTERCEPTOR

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    
    // 3. Modifica esta línea:
    provideHttpClient(
      withInterceptors([
        authInterceptor // <-- 4. AÑADIR EL INTERCEPTOR
      ])
    )
  ]
};