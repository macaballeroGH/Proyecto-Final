package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDateTime;

import lombok.Data;

@Data 
public class ReprogramarTurnoDTO {

    private LocalDateTime nuevaFechaHoraInicio;

    private String motivo;
}