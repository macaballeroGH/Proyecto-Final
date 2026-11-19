package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.example.beautymanager.Modelo.Enums.EspecialidadEmpleadoEnums;

import lombok.Data;

@Data 
public class ObtenerDisponibilidadReservaDTO {
    private Long idEmpleado;
    private String nombre;
    private String apellido;
    private EspecialidadEmpleadoEnums especialidad;
    private List<LocalDateTime> horariosDisponibles;
}