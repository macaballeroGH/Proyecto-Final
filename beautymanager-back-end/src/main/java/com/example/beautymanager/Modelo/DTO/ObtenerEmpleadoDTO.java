package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDateTime;

import com.example.beautymanager.Modelo.Enums.EspecialidadEmpleadoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoEmpleadoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoUsuarioEnums;

import lombok.Data;

@Data
public class ObtenerEmpleadoDTO {

    private Long idEmpleado;

    //Usuario
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private EstadoUsuarioEnums estadoUsuario;

    //Empleado
    private EspecialidadEmpleadoEnums especialidad;
    private LocalDateTime fechaAlta;
    private LocalDateTime fechaBaja;
    private EstadoEmpleadoEnums estadoEmpleado;
}
