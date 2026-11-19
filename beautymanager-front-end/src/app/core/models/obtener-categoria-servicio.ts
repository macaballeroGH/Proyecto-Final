import { EstadoCategoriaServicio } from "../enums/estadoCategoriaServicio";

export interface ObtenerCategoriaServicio {
    idCategoria: number;
    nombreCategoria: string;
    descripcion: string | null;
    estado: EstadoCategoriaServicio;
}