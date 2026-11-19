package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDateTime;

import com.example.beautymanager.Modelo.Enums.EstadoReprogramacionEnums;
import com.example.beautymanager.Modelo.Enums.EstadoTurnoEnums;

import lombok.Data;

@Data 
public class ObtenerReprogramacionTurnoDTO {

    private Long idReprogramacion;

    private Long idTurno;

    private LocalDateTime inicioAnterior;

    private LocalDateTime finAnterior;

    private LocalDateTime inicioNuevo;

    private LocalDateTime finNuevo;

    private String motivo;

    private EstadoReprogramacionEnums estadoPropuesta;

    private EstadoTurnoEnums estadoTurno;

    private LocalDateTime fechaCambio;
}
