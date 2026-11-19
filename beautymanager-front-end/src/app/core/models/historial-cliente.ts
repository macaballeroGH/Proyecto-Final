import { ObtenerHistorialTratamiento } from "./obtener-historial-tratamiento";
import { ObtenerPago } from "./obtener-pago";

export interface HistorialCliente {
    idCliente: number;
    nombreCliente: string;
    apellidoCliente: string;
    fotoPerfil: string | null;
    tratamientos: ObtenerHistorialTratamiento[];
    pagos: ObtenerPago[];
}