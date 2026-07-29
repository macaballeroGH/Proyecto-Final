import { EstadoUsuario } from "../enums/estadoUsuario";
import { Rol } from "../enums/rol";

export interface UsuarioPerfil {

    idUsuario: number;
    nombre: string;
    apellido: string;
    telefono: string;
    email: string;
    fotoPerfil: string | null;
    rol: Rol;
    estadoUsuario: EstadoUsuario
}