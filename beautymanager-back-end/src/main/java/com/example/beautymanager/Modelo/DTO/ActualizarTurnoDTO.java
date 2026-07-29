package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDateTime;

import com.example.beautymanager.Modelo.Enums.EstadoTurnoEnums;

import lombok.Data;

@Data
public class ActualizarTurnoDTO {

    private LocalDateTime fechaHoraInicio;
    private Long idEmpleado;
    private EstadoTurnoEnums estadoTurno;
    private String observaciones;
}
