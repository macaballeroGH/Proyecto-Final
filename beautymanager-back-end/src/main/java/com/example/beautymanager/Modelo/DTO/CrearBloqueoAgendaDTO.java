package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CrearBloqueoAgendaDTO {

    private Long idEmpleado;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private String motivo;
}
