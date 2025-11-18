// En: src/app/core/interfaces/admin.interface.ts

export interface DashboardMetricsDTO {
    totalVentas: number;
    paquetesActivos: number;
    nuevasReservas: number;
    totalUsuarios: number;
}

// ¡AÑADIR ESTA NUEVA INTERFAZ!
export interface MonthlySaleDTO {
    year: number;
    month: number;
    total: number;
}

// NOTA: Puedes añadir cualquier otra interfaz relacionada con la administración (ej: Reportes de Ingresos) aquí.