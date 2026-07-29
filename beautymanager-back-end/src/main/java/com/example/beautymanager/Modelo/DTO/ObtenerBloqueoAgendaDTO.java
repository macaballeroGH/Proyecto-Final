package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDateTime;

import com.example.beautymanager.Modelo.Enums.EspecialidadEmpleadoEnums;

import lombok.Data;

@Data
public class ObtenerBloqueoAgendaDTO {

    private Long idBloqueo;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private String motivo;

    //Empleado
    private Long idEmpleado;
    private String nombreEmpleado;
    private String apellidoEmpleado;
    private EspecialidadEmpleadoEnums especialidadEmpleado;
}
