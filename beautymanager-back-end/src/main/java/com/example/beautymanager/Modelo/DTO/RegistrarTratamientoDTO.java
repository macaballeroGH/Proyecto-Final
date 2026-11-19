package com.example.beautymanager.Modelo.DTO;

import java.util.List;

import com.example.beautymanager.Modelo.Enums.ResultadoTratamientoEnums;

import lombok.Data;

@Data
public class RegistrarTratamientoDTO {

    private Long idTurno;

    private String observacion;

    private Integer duracionReal;

    private String productosUtilizados;

    private ResultadoTratamientoEnums resultadoTratamiento;

    private List<RegistrarTratamientoServicioDTO> serviciosRealizados;
}
