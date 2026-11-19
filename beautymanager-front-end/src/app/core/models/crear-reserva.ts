import { CrearReservaTurno } from "./crear-reserva-turno";

export interface CrearReserva {
    idCliente: number;
    turnos: CrearReservaTurno[];
}