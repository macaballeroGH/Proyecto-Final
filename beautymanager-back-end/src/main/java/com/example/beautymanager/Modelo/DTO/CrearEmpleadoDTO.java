package com.example.beautymanager.Modelo.DTO;

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
}
