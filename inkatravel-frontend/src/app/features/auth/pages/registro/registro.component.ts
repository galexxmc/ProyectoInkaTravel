import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../core/services/auth.service';
import { Router } from '@angular/router'; 
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink], 
  templateUrl: './registro.component.html',
  styleUrls: ['./registro.component.scss']
})
export class RegistroComponent implements OnInit {

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  registroForm!: FormGroup;
  errorMessage: string = '';

  constructor() { }

  ngOnInit(): void {
    // Inicializar el formulario con todos los campos de tu entidad Usuario
    this.registroForm = this.fb.group({
      nombre: ['', Validators.required],
      correo: ['', [Validators.required, Validators.email]],
      contrasena: ['', [Validators.required, Validators.minLength(6)]],
      telefono: ['', [Validators.required, Validators.pattern('^[0-9]{9}$')]], // Asume 9 dígitos
    });
  }

  /**
   * (RF-01) Lógica que se ejecuta al enviar el formulario.
   */
  onSubmit(): void {
    this.errorMessage = '';
    
    if (this.registroForm.valid) {
      
      // Llamamos al servicio de Registro
      this.authService.registro(this.registroForm.value).subscribe({
        
        next: (response) => {
          // Éxito: El usuario se creó en la base de datos
          console.log('Registro Exitoso:', response);
          
          // Opcional: Podrías loguear al usuario aquí o simplemente mandarlo al Login
          alert('¡Registro exitoso! Por favor, inicia sesión.');
          this.router.navigateByUrl('/login');

        },
        error: (err) => {
          // Error: El backend devolvió un 400 (ej: Correo ya existe)
          console.error('Error de Registro:', err.error);
          
          // El backend devuelve el mensaje de error directamente (ej: "El correo electrónico ya está registrado.")
          this.errorMessage = err.error || 'Ocurrió un error inesperado durante el registro.';
        }
      });
    } else {
      this.errorMessage = 'Por favor, completa todos los campos correctamente.';
    }
  }

}