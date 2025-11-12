import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-modal-confirmacion',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal-confirmacion.component.html',
  styleUrls: ['./modal-confirmacion.component.scss']
})
export class ModalConfirmacionComponent {

  @Input() titulo: string = 'Confirmar Acción';
  @Input() mensaje: string = '¿Estás seguro de que quieres continuar?';
  @Input() visible: boolean = false; 

  // --- ¡NUEVOS INPUTS! ---
  @Input() confirmButtonText: string = 'Confirmar'; // Texto del botón de confirmar
  @Input() confirmButtonColor: string = 'red';     // 'red' o 'blue'
  @Input() showCancelButton: boolean = true;     // Para ocultar "Cancelar"

  @Output() onConfirm = new EventEmitter<void>();
  @Output() onCancel = new EventEmitter<void>();

  constructor() { }

  confirmar(): void {
    this.onConfirm.emit();
  }

  cancelar(): void {
    this.onCancel.emit();
  }
}