package com.example.beautymanager.Modelo.DTO;

import java.util.List;

import com.example.beautymanager.Modelo.Enums.EspecialidadEmpleadoEnums;

import lombok.Data;

@Data
public class CrearEmpleadoDTO {

    //Datos del usuario
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String telefono;

    //Datos del empleado
    private EspecialidadEmpleadoEnums especialidad;
    private List<CrearHorarioEmpleadoDTO> horarios;
}
