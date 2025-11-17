import { Component, OnInit, inject, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaqueteService } from '../../../paquetes/services/paquete.service';
import { PaqueteTuristicoResponseDTO } from '../../../../core/interfaces/paquete.interface';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule, 
    RouterLink 
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit, OnDestroy { 

  private paqueteService = inject(PaqueteService);

  // --- Propiedades para el Slider ---
  currentSlideIndex: number = 0; 
  private autoSlideInterval: number = 5000;
  private intervalId: any; 

  // --- Propiedad para el Control Direccional (Nueva) ---
  public slideDirection: 'left' | 'right' = 'left'; 
  // -----------------------------------------------------

slides = [
    { 
        title: '¡Convierte InkaPuntos en GRANDES Descuentos!', 
        subtitle: 'Canjea tus puntos acumulados para obtener mejores precios en todas tus reservas. ¡El ahorro está garantizado!', 
        image: 'assets/images/promo_points.jpg', 
        cta: 'Historial de Puntos',
        // DESTINO 1: Puntos / Perfil (Requiere Login)
        link: '/mis-puntos' 
    },
    { 
        title: 'Hazte PREMIUM: Gana más y Ahorra el Doble', 
        subtitle: 'Cambia de cuenta gratuita a Premium para desbloquear descuentos exclusivos y multiplicar tus puntos.', 
        image: 'assets/images/promo_vip.jpg', 
        cta: '¡Subir de Nivel!',
        // DESTINO 2: Suscripción / Perfil (Requiere Login)
        link: '/perfil'
    },
    { 
        title: 'Clientes PREMIUM: ¡Viaje TODO PAGADO!', 
        subtitle: 'Participa en nuestro Sorteo Mensual y gana un paquete turístico de lujo sin costo alguno.', 
        image: 'assets/images/promo_raffle.jpg', 
        cta: 'Hazte PREMIUM',
        // DESTINO 3: Página de reglas (Requiere Login)
        link: '/perfil' 
    },
];

  sponsors = [
    { name: 'LATAM Airlines', logo: 'assets/logos/latam.svg' }, 
    { name: 'Mercado Pago', logo: 'assets/logos/mercadopago.svg' }, 
    { name: 'PromPerú', logo: 'assets/logos/promperu.svg' }, 
    { name: 'Airbnb', logo: 'assets/logos/airbnb.svg' } ,
    { name: 'MovilBus', logo: 'assets/logos/movilbus.svg' } 
  ];

  // Estado del componente de Paquetes Destacados
  public isLoading: boolean = true;
  public errorMessage: string = '';
  public paquetesDestacados: PaqueteTuristicoResponseDTO[] = [];

  constructor() { }

  ngOnInit(): void {
    this.cargarPaquetesDestacados();
    this.startAutoSlide(); // Inicia el avance automático
  }
  
  /**
   * Limpia el temporizador al salir de la página.
   */
  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId); 
    }
  }

  // --- MÉTODOS DEL SLIDER ---
  
  startAutoSlide(): void {
    this.intervalId = setInterval(() => {
      this.nextSlide();
    }, this.autoSlideInterval);
  }

  /**
   * Mueve el slider al siguiente índice (desliza hacia la izquierda).
   */
  nextSlide() {
    this.slideDirection = 'left'; // Marcamos la dirección para la animación
    this.currentSlideIndex = (this.currentSlideIndex + 1) % this.slides.length;
  }

  /**
   * Mueve el slider al índice anterior (desliza hacia la derecha).
   */
  prevSlide() {
    this.slideDirection = 'right'; // Marcamos la dirección para la animación
    this.currentSlideIndex = (this.currentSlideIndex - 1 + this.slides.length) % this.slides.length;
  }
  // --------------------------

  cargarPaquetesDestacados(): void {
    this.paqueteService.obtenerPaquetes({ disponibilidad: true }).subscribe({
      next: (data) => {
        this.paquetesDestacados = data.slice(0, 3); 
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'No se pudieron cargar los paquetes destacados.';
        this.isLoading = false;
      }
    });
  }
}