import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PaqueteService } from '../../services/paquete.service';
import { PaqueteDetalleResponseDTO } from '../../../../core/interfaces/paquete.interface';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReservaService } from '../../../reservas/services/reserva.service';
import { PagoService } from '../../../pagos/services/pago.service';
import { ReservaRequestDTO } from '../../../../core/interfaces/reserva.interface';

// Importaciones para el cálculo en vivo
import { AuthService } from '../../../../core/services/auth.service';
import { UsuarioResponseDTO } from '../../../../core/interfaces/usuario.interface';
import { Subscription } from 'rxjs'; // Para limpiar la suscripción

// Importación del Pipe (opcional, si lo creaste)
import { ReplaceNewlinesPipe } from '../../../../core/pipes/replace-newlines.pipe';

@Component({
  selector: 'app-paquete-detail',
  standalone: true,
  imports: [
    CommonModule, 
    RouterLink,
    ReactiveFormsModule,
    ReplaceNewlinesPipe // Descomenta si creaste el Pipe
  ],
  templateUrl: './paquete-detail.component.html',
  styleUrls: ['./paquete-detail.component.scss']
})
export class PaqueteDetailComponent implements OnInit, OnDestroy {

  // --- Inyecciones de Servicios ---
  private route = inject(ActivatedRoute);
  private paqueteService = inject(PaqueteService);
  private reservaService = inject(ReservaService);
  private pagoService = inject(PagoService);
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);

  // --- Variables de Estado ---
  detalle: PaqueteDetalleResponseDTO | null = null;
  isLoading: boolean = true;
  errorMessage: string = '';
  
  // --- Formulario de Reserva ---
  reservaForm!: FormGroup;

  // --- Variables para Cálculo en Vivo ---
  usuarioActual: UsuarioResponseDTO | null = null;
  totalBruto: number = 0;
  descuentoSoles: number = 0;
  totalNeto: number = 0;

  // Factores de descuento (replicando lógica del backend)
  private readonly FACTOR_CANJE_GRATIS = 0.10;
  private readonly FACTOR_CANJE_PREMIUM = 0.15;

  // Suscripción para limpiar
  private formChangesSubscription?: Subscription;

  constructor() { }

  ngOnInit(): void {
    // 1. Obtenemos el usuario actual PRIMERO
    this.usuarioActual = this.authService.getUsuarioInfo();

    // 2. Inicializar el formulario de reserva
    this.reservaForm = this.fb.group({
      cantidadViajeros: [1, [Validators.required, Validators.min(1)]],
      puntosAUsar: [0, [Validators.required, Validators.min(0)]]
    });

    // 3. Cargar datos del paquete
    this.cargarDatosPaquete();
    
    // 4. Escuchar cambios en el formulario para recalcular totales
    this.formChangesSubscription = this.reservaForm.valueChanges.subscribe(valores => {
      this.calcularTotales(valores);
    });
  }

  ngOnDestroy(): void {
    // Limpiamos la suscripción para evitar fugas de memoria al salir del componente
    this.formChangesSubscription?.unsubscribe();
  }

  /**
   * Carga los datos del paquete (incluyendo clima) desde la API
   */
  cargarDatosPaquete(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.errorMessage = 'ID de paquete no encontrado.';
      this.isLoading = false;
      return;
    }

    this.paqueteService.obtenerDetallePaquete(+id).subscribe({
      next: (data) => {
        this.detalle = data;
        // Calculamos los totales iniciales (con 1 viajero, 0 puntos)
        this.calcularTotales(this.reservaForm.value); 
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar el detalle del paquete.';
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  /**
   * Calcula los totales (bruto, descuento, neto) en tiempo real
   * cada vez que el formulario cambia.
   */
  calcularTotales(valores: any): void {
    if (!this.detalle || !this.usuarioActual) return; // Aún no se cargan los datos

    const cantidad = valores.cantidadViajeros || 0;
    const puntos = valores.puntosAUsar || 0;

    // 1. Calcular Total Bruto
    this.totalBruto = this.detalle.paquete.precio * cantidad;

    // 2. Calcular Descuento
    let factorDescuento = (this.usuarioActual.tipo === 'PREMIUM' || this.usuarioActual.tipo === 'ADMIN') 
                          ? this.FACTOR_CANJE_PREMIUM 
                          : this.FACTOR_CANJE_GRATIS;
    
    this.descuentoSoles = puntos * factorDescuento;

    // 3. Validar Descuento (no puede ser mayor que el total)
    if (this.descuentoSoles > this.totalBruto) {
      this.descuentoSoles = this.totalBruto;
    }
    
    // 4. Validar Puntos (no puede usar más de los que tiene)
    if (puntos > this.usuarioActual.puntosAcumulados) {
      this.reservaForm.get('puntosAUsar')?.setErrors({ 'puntosInsuficientes': true });
    } else {
      // Limpia el error si ya es válido
      this.reservaForm.get('puntosAUsar')?.setErrors(null);
    }

    // 5. Calcular Total Neto
    this.totalNeto = this.totalBruto - this.descuentoSoles;
  }

  /**
   * (RF-08 y RF-09) Flujo de compra completo.
   * Se llama al presionar el botón "Reservar Ahora".
   */
  reservarAhora() {
    // 1. Validar que el formulario y el paquete existan
    if (this.reservaForm.invalid || !this.detalle || !this.usuarioActual) {
      alert('Por favor, revisa los datos del formulario.');
      return;
    }

    const datosFormulario = this.reservaForm.value;

    // 2. Validación final de puntos (redundante, pero segura)
    if (datosFormulario.puntosAUsar > this.usuarioActual.puntosAcumulados) {
      alert(`Error: Solo tienes ${this.usuarioActual.puntosAcumulados} puntos disponibles.`);
      return;
    }

    // 3. Crear el DTO de Reserva con los datos del formulario
    const datosReserva: ReservaRequestDTO = {
      paqueteId: this.detalle.paquete.id,
      cantidadViajeros: datosFormulario.cantidadViajeros,
      puntosAUsar: datosFormulario.puntosAUsar
    };
    
    console.log('Iniciando proceso de reserva con:', datosReserva);

    // 4. Crear la Reserva PENDIENTE en el backend
    this.reservaService.crearReserva(datosReserva).subscribe({
      next: (reservaCreada) => {
        console.log('Reserva PENDIENTE creada (ID):', reservaCreada.id);
        
        // 5. Si la reserva se crea, pedir el link de pago
        this.pagoService.crearLinkDePago(reservaCreada.id!).subscribe({
          next: (linkResponse) => {
            console.log('Link de Mercado Pago obtenido:', linkResponse.url);
            
            // 6. Redirigir al usuario al checkout de Mercado Pago
            window.location.href = linkResponse.url;
          },
          error: (err) => {
            console.error('Error al crear el link de pago:', err);
            alert('Error al contactar a Mercado Pago. Intente más tarde.');
          }
        });
      },
      error: (err) => {
        console.error('Error al crear la reserva:', err.error);
        // Mostramos el error del backend (ej: "No puedes usar más puntos de los que tienes")
        alert(`Error: ${err.error}`); 
      }
    });
  }
}