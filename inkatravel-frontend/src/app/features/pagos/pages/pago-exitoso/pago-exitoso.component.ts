import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pago-exitoso',
  standalone: true,
  imports: [CommonModule], // Dejamos RouterLink por si quieres volver a usarlo
  templateUrl: './pago-exitoso.component.html',
  styleUrls: ['./pago-exitoso.component.scss']
})
export class PagoExitosoComponent {

  // Ya no necesitamos el 'router' ni 'ngOnInit'
  
  constructor() { }

  // El contador y el 'setInterval' han sido eliminados.
}