export interface ObtenerCliente {
    idCliente: number;
    idUsuario: number;
    nombre: string;
    apellido: string;
    email: string;
    telefono: string;
    fotoPerfil: string | null;
    direccion: string;
    fechaNacimiento: string;
}