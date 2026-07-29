package com.example.beautymanager.Modelo.DTO;

import java.time.LocalTime;

import com.example.beautymanager.Modelo.Enums.EspecialidadEmpleadoEnums;

import lombok.Data;

@Data
public class ObtenerHorarioEmpleadoDTO {

    private Long idHorario;
    private Integer diaSemana;
    private String nombreDia;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    //Datos del empleado
    private Long idEmpleado;
    private String nombreEmpleado;
    private String apellidoEmpleado;
    private EspecialidadEmpleadoEnums especialidadEmpleado;
}
