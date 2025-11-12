import { Routes } from '@angular/router';
import { LayoutComponent } from './shared/layout/layout.component';
import { LoginComponent } from './features/auth/pages/login/login.component';
import { RegistroComponent } from './features/auth/pages/registro/registro.component';

import { AdminLayoutComponent } from './shared/admin-layout/admin-layout.component';

import { authGuard } from './core/guards/auth.guard';

import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [

    // --- RUTAS PÚBLICAS (Sin Header/Footer) ---
    { path: 'login', component: LoginComponent },
    { path: 'registro', component: RegistroComponent },

    // --- RUTAS CON LAYOUT (Protegidas o con Navegación) ---
    {
        path: '', // Ruta raíz
        component: LayoutComponent, // Usa el Layout
        canActivate: [authGuard],
        // Aquí irán las rutas hijas que se muestran dentro del <router-outlet> del layout
        children: [
            {
                path: 'home',
                loadComponent: () => import('./features/home/pages/home/home.component').then(m => m.HomeComponent)
            },
            {
                // Ruta para el Catálogo
                path: 'paquetes',
                loadComponent: () => import('./features/paquetes/pages/paquetes-list/paquetes-list.component').then(m => m.PaquetesListComponent)
            },
            {
                // Ruta para el Detalle (usa un parámetro dinámico ':id')
                path: 'paquetes/:id',
                loadComponent: () => import('./features/paquetes/pages/paquete-detail/paquete-detail.component').then(m => m.PaqueteDetailComponent)
            },
            {
                path: 'pago-exitoso',
                loadComponent: () => import('./features/pagos/pages/pago-exitoso/pago-exitoso.component').then(m => m.PagoExitosoComponent)
            },
            {
                path: 'suscripcion-exitosa',
                loadComponent: () => import('./features/pagos/pages/suscripcion-exitosa/suscripcion-exitosa.component').then(m => m.SuscripcionExitosaComponent)
            },
            {
                path: 'mis-reservas',
                loadComponent: () => import('./features/reservas/pages/mis-reservas/mis-reservas.component').then(m => m.MisReservasComponent)
            },
            {
                path: 'mis-puntos',
                loadComponent: () => import('./features/puntos/pages/historial-puntos/historial-puntos.component').then(m => m.HistorialPuntosComponent)
            },
            {
                path: 'perfil',
                loadComponent: () => import('./features/usuario/pages/perfil/perfil.component').then(m => m.PerfilComponent)
            },
            {
                // La ruta base vacía redirige al home (si está logueado)
                path: '',
                redirectTo: 'home',
                pathMatch: 'full'
            },
            // Añade aquí las futuras rutas de Paquetes, Perfil, etc.
        ]
    },

    // --- ¡NUEVO! RUTAS DE ADMIN (Protegidas por adminGuard) ---
    {
        path: 'admin',
        component: AdminLayoutComponent, // Usa el Layout de Admin
        canActivate: [adminGuard],       // ¡Protegido por el AdminGuard!
        children: [
            {
                path: 'dashboard',
                loadComponent: () => import('./features/admin/pages/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent)
            },
            {
                path: 'usuarios', // RF-12 [cite: 713]
                loadComponent: () => import('./features/admin/pages/gestion-usuarios/gestion-usuarios.component').then(m => m.GestionUsuariosComponent)
            },
            {
                path: 'reservas', // RF-12 [cite: 727]
                loadComponent: () => import('./features/admin/pages/gestion-reservas/gestion-reservas.component').then(m => m.GestionReservasComponent)
            },
            {
                path: '',
                redirectTo: 'dashboard',
                pathMatch: 'full'
            }
        ]
    },
    // --- FIN DE RUTAS ADMIN ---

    // Ruta de comodín para manejo de errores 404
    { path: '**', redirectTo: 'home' }
];