package com.example.beautymanager.Modelo.DTO;

import lombok.Data;

@Data
public class ObtenerTipoGastoDTO {

    private Long idTipoGasto;
    private String nombreGasto;
    private String descripcion;
    private Integer cantidadMovimientos;
}
