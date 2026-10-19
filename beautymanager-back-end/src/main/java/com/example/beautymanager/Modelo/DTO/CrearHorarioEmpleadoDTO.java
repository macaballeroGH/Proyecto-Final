package com.example.beautymanager.Modelo.DTO;

import java.time.LocalTime;

import lombok.Data;

@Data
public class CrearHorarioEmpleadoDTO {

    private Integer diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
