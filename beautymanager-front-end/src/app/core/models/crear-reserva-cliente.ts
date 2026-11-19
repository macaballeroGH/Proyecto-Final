import { CrearReservaTurno } from "./crear-reserva-turno";

export interface CrearReservaCliente {
    
    turnos: CrearReservaTurno[];

    claveIdempotencia: string;
}