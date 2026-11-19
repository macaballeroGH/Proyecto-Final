package com.example.beautymanager.Modelo.DTO;

import com.example.beautymanager.Modelo.Enums.TipoGastoEnums;

import lombok.Data;

@Data
public class ObtenerTipoGastoDTO {

    private Long idTipoGasto;
    private TipoGastoEnums nombreGasto;
    private String descripcion;
    private Integer cantidadMovimientos;
}
