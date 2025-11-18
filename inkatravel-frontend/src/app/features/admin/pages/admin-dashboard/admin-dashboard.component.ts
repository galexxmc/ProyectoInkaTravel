import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { AdminService } from '../../services/admin.service';
import { DashboardMetricsDTO } from '../../../../core/interfaces/admin.interface';
import { MonthlySaleDTO } from '../../../../core/interfaces/admin.interface';

// --- ¡NUEVOS IMPORTES PARA GRÁFICOS (Chart.js)! ---

// Importamos la directiva 'BaseChartDirective' en lugar del 'NgChartsModule'
import { BaseChartDirective } from 'ng2-charts'; 
import { ChartData, ChartOptions, ChartType } from 'chart.js';
// --- ¡Importaciones manuales de Chart.js para tree-shaking! ---
import { Chart, BarController, BarElement, CategoryScale, LinearScale, Tooltip, Legend } from 'chart.js';
// -------------------------------------------------

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    CommonModule, 
    DecimalPipe,
    BaseChartDirective // <-- ¡CORREGIDO! Usamos la directiva aquí
  ],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit {
  
  private adminService = inject(AdminService);

  // --- Estado del Componente ---
  public isLoading: boolean = true;
  public totalVentas: number = 0; 
  public paquetesActivos: number = 0; 
  public nuevasReservas: number = 0;
  public totalUsuarios: number = 0;

  // --- CONFIGURACIÓN DEL GRÁFICO ---
  public barChartOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: (value) => `S/ ${value}` // Formato de moneda
        }
      }
    },
    plugins: {
      legend: {
        display: false, // Ocultar leyenda (solo 1 dataset)
      },
      tooltip: {
        callbacks: {
          label: (context) => `Ventas: S/ ${context.formattedValue}` // Tooltip personalizado
        }
      }
    }
  };
  
  public barChartLabels: string[] = []; 
  public barChartType: ChartType = 'bar';
  public barChartData: ChartData<'bar'> = {
    labels: this.barChartLabels,
    datasets: [
      { 
        data: [], 
        label: 'Ventas Mensuales',
        backgroundColor: '#1e3a8a', 
        borderColor: '#1e3a8a',
        borderRadius: 4
      }
    ]
  };
  // ---------------------------------

  constructor() {
    // --- ¡REGISTRO MANUAL DE COMPONENTES DE CHART.JS! ---
    // Esto es necesario para la optimización (tree-shaking)
    Chart.register(
      BarController, 
      BarElement, 
      CategoryScale, 
      LinearScale, 
      Tooltip, 
      Legend
    );
  }

  ngOnInit(): void {
    this.cargarMetricas();
    this.cargarDatosDelGrafico(); 
  }

  /**
   * Carga los KPIs (TotalVentas, Reservas, etc.)
   */
  cargarMetricas(): void {
    this.adminService.getDashboardMetrics().subscribe({
        next: (data: DashboardMetricsDTO) => {
            this.totalVentas = data.totalVentas;
            this.paquetesActivos = data.paquetesActivos;
            this.nuevasReservas = data.nuevasReservas;
            this.totalUsuarios = data.totalUsuarios;
            this.isLoading = false;
        },
        error: (err: any) => {
            console.error("Error al cargar métricas", err);
            this.isLoading = false;
        }
    });
  }

  /**
   * Carga los datos históricos para el gráfico de barras.
   */
  cargarDatosDelGrafico(): void {
    const monthNames = ["Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"];
    
    this.adminService.getMonthlySalesData().subscribe({
      next: (data: MonthlySaleDTO[]) => {
        // Mapea los datos del backend al formato del gráfico
        const labels = data.map(sale => monthNames[sale.month - 1] + ` '${sale.year.toString().substring(2)}`); 
        const totals = data.map(sale => sale.total);

        // Actualiza las propiedades del gráfico (esto refresca la vista)
        this.barChartLabels = labels;
        this.barChartData.labels = this.barChartLabels;
        this.barChartData.datasets[0].data = totals;
      },
      error: (err: any) => {
        console.error("Error al cargar datos del gráfico", err);
      }
    });
  }
}