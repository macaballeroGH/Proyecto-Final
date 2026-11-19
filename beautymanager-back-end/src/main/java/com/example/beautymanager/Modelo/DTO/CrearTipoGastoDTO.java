package com.example.beautymanager.Modelo.DTO;

import com.example.beautymanager.Modelo.Enums.TipoGastoEnums;

import lombok.Data;

@Data
public class CrearTipoGastoDTO {

    private TipoGastoEnums nombreGasto;
    private String descripcion;
}
