import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router'; // Sigue importando RouterLink si usas el href

@Component({
  selector: 'app-pago-exitoso',
  standalone: true,
  imports: [CommonModule, RouterLink], // Dejamos RouterLink por si quieres volver a usarlo
  templateUrl: './pago-exitoso.component.html',
  styleUrls: ['./pago-exitoso.component.scss']
})
export class PagoExitosoComponent {

  // Ya no necesitamos el 'router' ni 'ngOnInit'
  
  constructor() { }

  // El contador y el 'setInterval' han sido eliminados.
}