import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PaqueteService } from '../../../paquetes/services/paquete.service';
// (Opcional) Importar el pipe para el itinerario
// import { ReplaceNewlinesPipe } from 'src/app/core/pipes/replace-newlines.pipe';

@Component({
  selector: 'app-paquete-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule, // ¡Importante para los formularios!
    RouterLink,
    // ReplaceNewlinesPipe 
  ],
  templateUrl: './paquete-form.component.html',
  styleUrls: ['./paquete-form.component.scss']
})
export class PaqueteFormComponent implements OnInit {

  // --- Inyecciones ---
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute); // Para leer el :id de la URL
  private paqueteService = inject(PaqueteService);

  // --- Estado del Componente ---
  paqueteForm!: FormGroup;
  isEditMode: boolean = false;
  paqueteId: number | null = null;
  isLoading: boolean = false;
  errorMessage: string = ''; // Para errores del formulario
  pageTitle: string = 'Crear Nuevo Paquete';

  // --- ¡NUEVO! Notificación "Toast" (Éxito) ---
  showNotification: boolean = false;
  notificationTitle: string = '¡Éxito!';
  notificationMessage: string = '';
  notificationType: 'success' | 'error' = 'success'; // (Solo usamos 'success' aquí)
  private notificationTimer: any;

  constructor() { }

  ngOnInit(): void {
    // 1. Inicializar el formulario (vacío)
    this.paqueteForm = this.fb.group({
      nombre: ['', Validators.required],
      descripcion: ['', Validators.required],
      precio: [null, [Validators.required, Validators.min(0)]],
      region: ['', Validators.required],
      categoria: ['', Validators.required],
      itinerario: ['', Validators.required],
      disponibilidad: [true],
      latitud: [null], 
      longitud: [null],
      fechaInicio: [null],
      fechaFin: [null]
    });

    // 2. Comprobar si estamos en modo "Editar"
    this.paqueteId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.paqueteId) {
      this.isEditMode = true;
      this.pageTitle = 'Editar Paquete';
      this.isLoading = true;
      
      // 3. Si es modo "Editar", cargar los datos del paquete
      this.paqueteService.obtenerDetallePaquete(this.paqueteId).subscribe({
        next: (data) => {
          // Rellenamos el formulario con los datos
          this.paqueteForm.patchValue(data.paquete); 
          this.isLoading = false;
        },
        error: (err) => {
          this.errorMessage = 'No se pudo cargar el paquete para editar.';
          this.isLoading = false;
        }
      });
    }
  }

  /**
   * (NUEVO) Muestra la notificación "toast"
   */
  mostrarNotificacion(titulo: string, mensaje: string, tipo: 'success' | 'error') {
    this.notificationTitle = titulo;
    this.notificationMessage = mensaje;
    this.notificationType = tipo;
    this.showNotification = true;

    if (this.notificationTimer) {
      clearTimeout(this.notificationTimer);
    }
    this.notificationTimer = setTimeout(() => {
      this.showNotification = false;
    }, 3000);
  }

  /**
   * (RF-12) Se llama al guardar el formulario
   */
  onSubmit(): void {
    if (this.paqueteForm.invalid) {
      // Marcar todos los campos como "tocados" para que muestren sus errores
      this.paqueteForm.markAllAsTouched();
      this.errorMessage = 'Por favor, completa todos los campos obligatorios.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    if (this.isEditMode && this.paqueteId) {
      // --- MODO EDITAR ---
      this.paqueteService.actualizarPaquete(this.paqueteId, this.paqueteForm.value).subscribe({
        next: () => this.handleSuccess('Paquete actualizado exitosamente.'),
        error: (err) => this.handleError(err)
      });
    } else {
      // --- MODO CREAR ---
      this.paqueteService.crearPaquete(this.paqueteForm.value).subscribe({
        next: () => this.handleSuccess('Paquete creado exitosamente.'),
        error: (err) => this.handleError(err)
      });
    }
  }

  // --- Funciones de Ayuda (ACTUALIZADAS) ---

  private handleSuccess(message: string): void {
    this.isLoading = false;
    this.mostrarNotificacion('¡Éxito!', message, 'success'); // Muestra el Toast
    
    // Vuelve a la tabla de gestión
    setTimeout(() => {
      this.router.navigate(['/admin/paquetes']);
    }, 2000); // Espera 2 seg para que se vea el toast
  }

  private handleError(err: any): void {
    this.isLoading = false;
    // Muestra el error en el formulario
    this.errorMessage = err.error || 'Ocurrió un error al guardar el paquete.';
  }
}