export interface ObtenerNotificacion {
    idNotificacion: number;
    mensaje: string;
    leida: boolean;
    fechaCreacion: string;

    idUsuario: number;
    nombreUsuario: string;
    apellidoUsuario: string;
    nombreCompletoUsuario: string;
    emailUsuario: string;
}