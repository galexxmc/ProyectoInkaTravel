import { Routes } from '@angular/router';
import { LayoutComponent } from './shared/layout/layout.component';
import { LoginComponent } from './features/auth/pages/login/login.component';
import { RegistroComponent } from './features/auth/pages/registro/registro.component';
import { AdminLayoutComponent } from './shared/admin-layout/admin-layout.component';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [

    // --- 1. RUTAS PÚBLICAS (Sin Guards) ---
    // Estas rutas no tienen layout principal y son accesibles por todos.
    { path: 'login', component: LoginComponent },
    { path: 'registro', component: RegistroComponent },
    // (Se eliminaron las rutas de pago duplicadas de aquí)

    // --- 2. RUTAS DE ADMIN (Protegidas por adminGuard) ---
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
                path: 'usuarios', // RF-12
                loadComponent: () => import('./features/admin/pages/gestion-usuarios/gestion-usuarios.component').then(m => m.GestionUsuariosComponent)
            },
            {
                path: 'reservas', // RF-12
                loadComponent: () => import('./features/admin/pages/gestion-reservas/gestion-reservas.component').then(m => m.GestionReservasComponent)
            },
            {
                path: 'paquetes', // La tabla de gestión
                loadComponent: () => import('./features/admin/pages/gestion-paquetes/gestion-paquetes.component').then(m => m.GestionPaquetesComponent)
            },
            {
                path: 'paquetes/nuevo', // El formulario para crear
                loadComponent: () => import('./features/admin/components/paquete-form/paquete-form.component').then(m => m.PaqueteFormComponent)
            },
            {
                path: 'paquetes/editar/:id', // El formulario para editar
                loadComponent: () => import('./features/admin/components/paquete-form/paquete-form.component').then(m => m.PaqueteFormComponent)
            },
            {
                path: '',
                redirectTo: 'dashboard',
                pathMatch: 'full'
            }
        ]
    },

    // --- 3. RUTAS DE USUARIO (Protegidas por authGuard) ---
    // (Esta ruta 'path: ''' actúa como la ruta principal para usuarios logueados)
    {
        path: '', 
        component: LayoutComponent, // Usa el Layout de la Tienda
        canActivate: [authGuard],
        children: [
            {
                path: 'home',
                loadComponent: () => import('./features/home/pages/home/home.component').then(m => m.HomeComponent)
            },
            {
                path: 'paquetes', // Catálogo
                loadComponent: () => import('./features/paquetes/pages/paquetes-list/paquetes-list.component').then(m => m.PaquetesListComponent)
            },
            {
                path: 'paquetes/:id', // Detalle
                loadComponent: () => import('./features/paquetes/pages/paquete-detail/paquete-detail.component').then(m => m.PaqueteDetailComponent)
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
                path: 'pago-exitoso', // <-- Ubicación correcta (dentro del layout)
                loadComponent: () => import('./features/pagos/pages/pago-exitoso/pago-exitoso.component').then(m => m.PagoExitosoComponent)
            },
            {
                path: 'suscripcion-exitosa', // <-- Ubicación correcta (dentro del layout)
                loadComponent: () => import('./features/pagos/pages/suscripcion-exitosa/suscripcion-exitosa.component').then(m => m.SuscripcionExitosaComponent)
            },
            {
                path: '',
                redirectTo: 'home',
                pathMatch: 'full'
            },
        ]
    },

    // --- 4. RUTA COMODÍN (Wildcard) ---
    // Si ninguna ruta coincide, redirige al login.
    { path: '**', redirectTo: 'login' }
];