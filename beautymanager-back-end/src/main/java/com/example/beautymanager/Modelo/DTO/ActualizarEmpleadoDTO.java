package com.example.beautymanager.Modelo.DTO;

import com.example.beautymanager.Modelo.Enums.EspecialidadEmpleadoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoEmpleadoEnums;

import lombok.Data;

@Data
public class ActualizarEmpleadoDTO {

    private EspecialidadEmpleadoEnums especialidad;
    private EstadoEmpleadoEnums estadoEmpleado;
}
