export interface BloqueHorarioFormulario {
    horaInicio: string;
    horaFin: string;
}

export interface HorarioFormulario {
    seleccionado: boolean;
    bloques: BloqueHorarioFormulario[];
}