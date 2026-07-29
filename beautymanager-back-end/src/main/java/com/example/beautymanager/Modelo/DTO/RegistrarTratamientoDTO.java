package com.example.beautymanager.Modelo.DTO;

import lombok.Data;

@Data
public class RegistrarTratamientoDTO {

    private Long idTurno;
    private String observacion;
    private Integer duracionReal;
    private String productosUtilizados;
}
