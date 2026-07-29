import { Rol } from "../enums/rol";

export interface LoginResponse {
    apellido: string;
    email: string;
    idUsuario: number;
    message: string;
    nombre: string;
    rol: Rol;
    success: boolean;
    token: string;
}