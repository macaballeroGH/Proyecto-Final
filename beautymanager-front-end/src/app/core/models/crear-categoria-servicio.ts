import { EstadoCategoriaServicio } from "../enums/estadoCategoriaServicio";

export interface CrearCategoriaServicio {
    nombreCategoria: string;
    descripcion: string;
    estado: EstadoCategoriaServicio;
}