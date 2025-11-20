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
  private route = inject(ActivatedRoute); 
  private paqueteService = inject(PaqueteService);

  // --- Estado del Componente ---
  paqueteForm!: FormGroup;
  errorMessage: string = '';

  // Estas propiedades ya las tenías, ¡perfecto!
  selectedFile: File | null = null;
  imagePreviewUrl: string | ArrayBuffer | null = null;
  currentImageUrl: string | null = null; // URL de la imagen si estamos editando
  
  paqueteId: number | null = Number(this.route.snapshot.paramMap.get('id')) || null;
  isEditMode: boolean = !!this.paqueteId; 
  pageTitle: string = this.isEditMode ? 'Editar Paquete' : 'Crear Nuevo Paquete';
  
  public isPageLoading: boolean = this.isEditMode; 
  public isSubmitting: boolean = false; 

  // --- Notificación "Toast" ---
  showNotification: boolean = false;
  notificationTitle: string = '¡Éxito!';
  notificationMessage: string = '';
  notificationType: 'success' | 'error' = 'success';
  private notificationTimer: any;

  constructor() { }

ngOnInit(): void {
    // 1. Inicializar el formulario
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

    // 2. Lógica de Carga de Datos
    if (this.isEditMode && this.paqueteId) {
      this.paqueteService.obtenerDetallePaquete(this.paqueteId).subscribe({
        next: (data) => {
          this.paqueteForm.patchValue(data.paquete); 
          
          // --- CORRECCIÓN AQUÍ ---
          if (data.paquete.imagenUrl) {
            // Si usas Cloudinary, la URL ya viene completa (https://...).
            // Si no, y es local, deberías usar environment.apiUrl en lugar de escribirlo a mano.
            // Asumiendo Cloudinary:
            this.currentImageUrl = data.paquete.imagenUrl;
          }
          // -----------------------
          
          this.isPageLoading = false;
        },
        error: (err) => {
          this.errorMessage = 'No se pudo cargar el paquete para editar.';
          this.isPageLoading = false;
        }
      });
    }
  }

    // =======================================================
    // --- NUEVOS MÉTODOS PARA EL MANEJO DE ARCHIVOS ---
    // =======================================================
    
    /**
     * Captura el archivo seleccionado por el usuario y genera una vista previa.
     */
    onFileSelected(event: any): void {
        const file = event.target.files[0];
        
        // Reiniciamos los estados de la imagen
        this.selectedFile = null;
        this.imagePreviewUrl = null;

        if (file) {
            this.selectedFile = file;
            
            // Generar la vista previa para el usuario
            const reader = new FileReader();
            reader.onload = () => {
                this.imagePreviewUrl = reader.result;
            };
            reader.readAsDataURL(file);
        }
    }

    /**
     * Construye el objeto FormData requerido por el backend (multipart/form-data).
     */
    private buildFormData(): FormData {
        const formData = new FormData();
        
        // 1. Obtener el DTO del paquete
        const paqueteData = this.paqueteForm.value;
        
        // 2. Añadir el JSON del paquete como una parte de texto (clave: 'paquete')
        // El nombre de la clave debe coincidir con @RequestPart("paquete") en Spring
        formData.append('paquete', new Blob([JSON.stringify(paqueteData)], {
            type: "application/json"
        }));
        
        // 3. Añadir el archivo de imagen (clave: 'imagen')
        // El nombre de la clave debe coincidir con @RequestPart("imagen") en Spring
        if (this.selectedFile) {
            formData.append('imagen', this.selectedFile, this.selectedFile.name);
        }
        
        return formData;
    }


    /**
     * (RF-12) Se llama al guardar el formulario (ACTUALIZADO)
     */
    onSubmit(): void {
        if (this.paqueteForm.invalid) {
            this.paqueteForm.markAllAsTouched();
            this.errorMessage = 'Por favor, completa todos los campos obligatorios.';
            return;
        }

        // En modo "Crear", exigimos una imagen
        if (!this.isEditMode && !this.selectedFile) {
            this.errorMessage = 'Debe seleccionar una imagen para crear un nuevo paquete.';
            return;
        }
        
        // En modo "Editar", si no hay imagen nueva ni antigua, también exigimos una
        if (this.isEditMode && !this.selectedFile && !this.currentImageUrl) {
            this.errorMessage = 'Debe seleccionar una imagen.';
            return;
        }

        this.isSubmitting = true; 
        this.errorMessage = '';
        
        // 1. Construir el FormData con el archivo y el JSON
        const formData = this.buildFormData();
        
        if (this.isEditMode && this.paqueteId) {
            // --- MODO EDITAR ---
            // Enviamos el FormData
            this.paqueteService.actualizarPaquete(this.paqueteId, formData).subscribe({
                next: () => this.handleSuccess('Paquete actualizado exitosamente.'),
                error: (err) => this.handleError(err)
            });
        } else {
            // --- MODO CREAR ---
            // Enviamos el FormData
            this.paqueteService.crearPaquete(formData).subscribe({
                next: () => this.handleSuccess('Paquete creado exitosamente.'),
                error: (err) => this.handleError(err)
            });
        }
    }


    // =======================================================
    // --- MÉTODOS AUXILIARES (Se mantienen igual) ---
    // =======================================================

  mostrarNotificacion(titulo: string, mensaje: string, tipo: 'success' | 'error') {
    // ... (cuerpo se mantiene igual) ...
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

  private handleSuccess(message: string): void {
      this.isSubmitting = false; 
      // Al crear, limpiamos el formulario para que no se envíe dos veces
      if (!this.isEditMode) {
          this.paqueteForm.reset({ disponibilidad: true }); // Limpia formulario
          this.selectedFile = null; // Limpia el archivo
          this.imagePreviewUrl = null; // Limpia la vista previa
      }
      this.mostrarNotificacion('¡Éxito!', message, 'success');
  }

  private handleError(err: any): void {
    this.isSubmitting = false; 
    this.errorMessage = err.error.message || 'Ocurrió un error al guardar el paquete.'; // Ajustamos el acceso al error
  }
}