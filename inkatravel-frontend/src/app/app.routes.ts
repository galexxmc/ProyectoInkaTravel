import { Routes } from '@angular/router';
import { LayoutComponent } from './shared/layout/layout.component';
import { LoginComponent } from './features/auth/pages/login/login.component';
import { RegistroComponent } from './features/auth/pages/registro/registro.component';
import { AdminLayoutComponent } from './shared/admin-layout/admin-layout.component';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [

    // --- 1. RUTAS DE AUTENTICACIÓN (Sin Layout) ---
    { path: 'login', component: LoginComponent },
    { path: 'registro', component: RegistroComponent },

    // --- 2. RUTAS DE ADMIN (Protegidas por adminGuard) ---
    {
        path: 'admin',
        component: AdminLayoutComponent,
        canActivate: [adminGuard],       
        children: [
            { path: 'dashboard', loadComponent: () => import('./features/admin/pages/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent) },
            { path: 'usuarios', loadComponent: () => import('./features/admin/pages/gestion-usuarios/gestion-usuarios.component').then(m => m.GestionUsuariosComponent) },
            { path: 'reservas', loadComponent: () => import('./features/admin/pages/gestion-reservas/gestion-reservas.component').then(m => m.GestionReservasComponent) },
            { path: 'paquetes', loadComponent: () => import('./features/admin/pages/gestion-paquetes/gestion-paquetes.component').then(m => m.GestionPaquetesComponent) },
            { path: 'paquetes/nuevo', loadComponent: () => import('./features/admin/components/paquete-form/paquete-form.component').then(m => m.PaqueteFormComponent) },
            { path: 'paquetes/editar/:id', loadComponent: () => import('./features/admin/components/paquete-form/paquete-form.component').then(m => m.PaqueteFormComponent) },
            { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
        ]
    },

    // --- 3. RUTAS DE LA TIENDA (Layout Principal) ---
    {
        path: '', 
        component: LayoutComponent, 
        // ¡IMPORTANTE! Quitamos el guard del padre para que el layout sea público
        // canActivate: [authGuard],  <-- ELIMINADO
        children: [
            
            // === A. PÁGINAS PÚBLICAS (Cualquiera puede verlas) ===
            {
                path: 'home',
                loadComponent: () => import('./features/home/pages/home/home.component').then(m => m.HomeComponent)
            },
            {
                path: 'paquetes', // Catálogo
                loadComponent: () => import('./features/paquetes/pages/paquetes-list/paquetes-list.component').then(m => m.PaquetesListComponent)
            },
            {
                path: 'paquetes/:id', // Detalle del paquete
                loadComponent: () => import('./features/paquetes/pages/paquete-detail/paquete-detail.component').then(m => m.PaqueteDetailComponent)
            },

            // === B. PÁGINAS PRIVADAS (Protegidas individualmente) ===
            // Aquí sí agregamos canActivate: [authGuard]
            {
                path: 'mis-reservas',
                canActivate: [authGuard], 
                loadComponent: () => import('./features/reservas/pages/mis-reservas/mis-reservas.component').then(m => m.MisReservasComponent)
            },
            {
                path: 'mis-puntos',
                canActivate: [authGuard],
                loadComponent: () => import('./features/puntos/pages/historial-puntos/historial-puntos.component').then(m => m.HistorialPuntosComponent)
            },
            {
                path: 'perfil',
                canActivate: [authGuard],
                loadComponent: () => import('./features/usuario/pages/perfil/perfil.component').then(m => m.PerfilComponent)
            },
            {
                path: 'pago-exitoso',
                canActivate: [authGuard],
                loadComponent: () => import('./features/pagos/pages/pago-exitoso/pago-exitoso.component').then(m => m.PagoExitosoComponent)
            },
            {
                path: 'suscripcion-exitosa',
                canActivate: [authGuard],
                loadComponent: () => import('./features/pagos/pages/suscripcion-exitosa/suscripcion-exitosa.component').then(m => m.SuscripcionExitosaComponent)
            },
            
            // Redirección por defecto al Home
            {
                path: '',
                redirectTo: 'home',
                pathMatch: 'full'
            },
        ]
    },

    // --- 4. RUTA COMODÍN ---
    // Si se pierde, lo mandamos al Home en lugar del Login (más amigable)
    { path: '**', redirectTo: 'home' } 
];