import { EstadoCategoriaServicio } from "../enums/estadoCategoriaServicio";

export interface ActualizarCategoriaServicio {
    nombreCategoria: string;
    descripcion: string;
    estado: EstadoCategoriaServicio
}