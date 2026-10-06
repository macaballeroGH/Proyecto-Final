import { ObtenerHistorialTratamiento } from "./obtener-historial-tratamiento";
import { ObtenerPago } from "./obtener-pago";

export interface HistorialCliente {
    idCliente: number;
    nombreCliente: string;
    apellidoCliente: string;
    tratamientos: ObtenerHistorialTratamiento[];
    pagos: ObtenerPago[];
}