import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink, Router } from '@angular/router'; // <-- Importar Router
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';

// Servicios
import { PaqueteService } from '../../services/paquete.service';
import { ReservaService } from '../../../reservas/services/reserva.service';
import { PagoService } from '../../../pagos/services/pago.service';
import { AuthService } from '../../../../core/services/auth.service';

// Interfaces
import { PaqueteDetalleResponseDTO } from '../../../../core/interfaces/paquete.interface';
import { ReservaRequestDTO } from '../../../../core/interfaces/reserva.interface';
import { UsuarioResponseDTO } from '../../../../core/interfaces/usuario.interface';

@Component({
  selector: 'app-paquete-detail',
  standalone: true,
  imports: [
    CommonModule, 
    RouterLink,
    ReactiveFormsModule,
  ],
  templateUrl: './paquete-detail.component.html',
  styleUrls: ['./paquete-detail.component.scss']
})
export class PaqueteDetailComponent implements OnInit, OnDestroy {

  // --- Inyecciones ---
  private route = inject(ActivatedRoute);
  private router = inject(Router); // <-- Inyectamos Router para navegar al login
  private paqueteService = inject(PaqueteService);
  private reservaService = inject(ReservaService);
  private pagoService = inject(PagoService);
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);

  // --- Variables de Estado ---
  public showLoginModal: boolean = false; // <-- Controla el Modal
  public detalle: PaqueteDetalleResponseDTO | null = null;
  public isLoading: boolean = true;
  public errorMessage: string = '';

  // --- Formulario y Cálculos ---
  reservaForm!: FormGroup;
  usuarioActual: UsuarioResponseDTO | null = null;
  totalBruto: number = 0;
  descuentoSoles: number = 0;
  totalNeto: number = 0;

  // Factores (Lógica de Negocio)
  private readonly FACTOR_CANJE_GRATIS = 0.10;
  private readonly FACTOR_CANJE_PREMIUM = 0.15;
  
  private formChangesSubscription?: Subscription;

  constructor() { }

  ngOnInit(): void {
    // 1. Obtener Usuario
    this.usuarioActual = this.authService.getUsuarioInfo();

    // 2. Inicializar Formulario
    this.reservaForm = this.fb.group({
      cantidadViajeros: [1, [Validators.required, Validators.min(1)]],
      puntosAUsar: [0, [Validators.required, Validators.min(0)]]
    });

    // 3. Cargar Paquete
    this.cargarDatosPaquete();
    
    // 4. Suscripción a cambios
    this.formChangesSubscription = this.reservaForm.valueChanges.subscribe(valores => {
      this.calcularTotales(valores);
    });
  }

  ngOnDestroy(): void {
    this.formChangesSubscription?.unsubscribe();
  }

  // --- LÓGICA DE CARGA Y CÁLCULO (Se mantiene igual que tu código original) ---

  cargarDatosPaquete(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.errorMessage = 'ID no encontrado.';
      this.isLoading = false;
      return;
    }

    this.paqueteService.obtenerDetallePaquete(+id).subscribe({
      next: (data) => {
        this.detalle = data;
        this.calcularTotales(this.reservaForm.value); 
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar el paquete.';
        this.isLoading = false;
      }
    });
  }

  calcularTotales(valores: any): void {
    if (!this.detalle) return; 

    // Si no hay usuario, usamos valores por defecto para mostrar el precio base
    const tipoUsuario = this.usuarioActual ? this.usuarioActual.tipo : 'GRATIS';
    const puntosDisponibles = this.usuarioActual ? this.usuarioActual.puntosAcumulados : 0;

    const cantidad = valores.cantidadViajeros || 0;
    const puntos = valores.puntosAUsar || 0;

    this.totalBruto = this.detalle.paquete.precio * cantidad;

    let factorDescuento = (tipoUsuario === 'PREMIUM' || tipoUsuario === 'ADMIN') 
                          ? this.FACTOR_CANJE_PREMIUM 
                          : this.FACTOR_CANJE_GRATIS;
    
    this.descuentoSoles = puntos * factorDescuento;

    if (this.descuentoSoles > this.totalBruto) {
      this.descuentoSoles = this.totalBruto;
    }
    
    // Validar Puntos solo si hay usuario logueado
    if (this.usuarioActual && puntos > puntosDisponibles) {
      this.reservaForm.get('puntosAUsar')?.setErrors({ 'puntosInsuficientes': true });
    } else {
      this.reservaForm.get('puntosAUsar')?.setErrors(null);
    }

    this.totalNeto = this.totalBruto - this.descuentoSoles;
  }

  // --- MÉTODOS DEL MODAL DE SEGURIDAD (NUEVOS) ---

  cerrarModal() {
    this.showLoginModal = false;
  }

  irALogin() {
    this.router.navigate(['/login']);
  }

  irARegistro() {
    this.router.navigate(['/registro']);
  }

  // --- FLUJO DE RESERVA ---

  reservarAhora() {
    // 1. VERIFICACIÓN DE SEGURIDAD (Muro de Login)
    // Si no hay usuario o token, abrimos el modal y detenemos el proceso.
    if (!this.usuarioActual || !localStorage.getItem('jwt_token')) {
        this.showLoginModal = true;
        return;
    }

    // 2. Validaciones de Formulario
    if (this.reservaForm.invalid) {
      alert('Por favor, revisa los datos del formulario.');
      return;
    }

    const datosFormulario = this.reservaForm.value;

    if (datosFormulario.puntosAUsar > this.usuarioActual.puntosAcumulados) {
      alert(`Error: Solo tienes ${this.usuarioActual.puntosAcumulados} puntos disponibles.`);
      return;
    }

    // 3. Crear DTO
    const datosReserva: ReservaRequestDTO = {
      paqueteId: this.detalle!.paquete.id,
      cantidadViajeros: datosFormulario.cantidadViajeros,
      puntosAUsar: datosFormulario.puntosAUsar
    };
    
    // 4. Flujo de Reserva (Igual que tu código original)
    this.reservaService.crearReserva(datosReserva).subscribe({
      next: (reservaCreada) => {
        this.pagoService.crearLinkDePago(reservaCreada.id!).subscribe({
          next: (linkResponse) => {
            window.location.href = linkResponse.url;
          },
          error: (err) => {
            console.error(err);
            alert('Error al contactar a Mercado Pago.');
          }
        });
      },
      error: (err) => {
        console.error(err);
        alert('Error al crear la reserva: ' + (err.error?.message || 'Intente nuevamente')); 
      }
    });
  }
}