import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router'; 
import { RouterLink } from '@angular/router'; 
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink], // Importar módulos necesarios
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  // Dependencias inyectadas
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  // Variable para el formulario
  loginForm!: FormGroup;
  
  // Mensaje de error para el usuario
  errorMessage: string = '';

  constructor() { }

  ngOnInit(): void {
    // 1. Inicializar el formulario reactivo
    this.loginForm = this.fb.group({
      correo: ['', [Validators.required, Validators.email]], // Correo es obligatorio y debe ser email
      contrasena: ['', [Validators.required, Validators.minLength(6)]] // Contraseña es obligatoria (mínimo 6)
    });
  }

  /**
   * (RF-02) Lógica que se ejecuta al enviar el formulario.
   */
  onSubmit(): void {
    this.errorMessage = '';
    
    // 1. Verifica si el formulario es válido (si se cumplen los Validators)
    if (this.loginForm.valid) {
      
      // 2. Llama al servicio de Login (habla con tu backend Spring Boot)
      this.authService.login(this.loginForm.value).subscribe({
        
        next: (response) => {
          // 3. Éxito: El backend devolvió un 200 OK con el token
          console.log('Login Exitoso!', response);
          
          // Guardar el Token (por ahora lo haremos simple)
          localStorage.setItem('jwt_token', response.token);
          localStorage.setItem('user_info', JSON.stringify(response.usuario));

          // 4. Redirigir al usuario al Home o al catálogo
          this.router.navigateByUrl('/home'); // Creamos la ruta /home en el siguiente paso

        },
        error: (err) => {
          // 5. Error: El backend devolvió un 401 (Credenciales incorrectas)
          console.error('Error de Login:', err);
          
          // Mostrar mensaje de error al usuario
          this.errorMessage = 'Credenciales incorrectas. Verifica tu correo y contraseña.';
        }
      });
    } else {
      this.errorMessage = 'Por favor, completa los campos requeridos.';
    }
  }

}