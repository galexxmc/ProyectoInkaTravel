import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsuarioService } from '../../services/usuario.service';
import { UsuarioResponseDTO } from '../../../../core/interfaces/usuario.interface';

import { PagoService } from '../../../pagos/services/pago.service';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './perfil.component.html',
  styleUrls: ['./perfil.component.scss']
})
export class PerfilComponent implements OnInit {

  private usuarioService = inject(UsuarioService);
  private pagoService = inject(PagoService);

  usuario: UsuarioResponseDTO | null = null;
  isLoading: boolean = true;
  errorMessage: string = '';

  ngOnInit(): void {
    this.cargarPerfil();
  }

  cargarPerfil(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.usuarioService.getMiPerfil().subscribe({
      next: (data) => {
        this.usuario = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar tu perfil.';
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  /**
   * (NUEVO - RF-06) Llama al servicio para crear el link de suscripción
   */
  irAPremium(): void {
    this.errorMessage = '';
    console.log('Iniciando suscripción premium...');

    this.pagoService.crearLinkSuscripcion().subscribe({
      next: (linkResponse) => {
        // Redirigimos al usuario al checkout de Mercado Pago
        window.location.href = linkResponse.url;
      },
      error: (err) => {
        this.errorMessage = 'Error al procesar la suscripción. Intente más tarde.';
        console.error(err);
      }
    });
  }
}