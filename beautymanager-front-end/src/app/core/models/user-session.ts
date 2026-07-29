import { Rol } from "../enums/rol";

export interface UserSession{
    idUsuario: number;
    nombre: string;
    apellido: string;
    email: string;
    rol: Rol;
    token: string;
}