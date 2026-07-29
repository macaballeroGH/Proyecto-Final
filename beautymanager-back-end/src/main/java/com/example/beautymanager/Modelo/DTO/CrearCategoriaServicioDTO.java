package com.example.beautymanager.Modelo.DTO;

import com.example.beautymanager.Modelo.Enums.EstadoCategoriaServicioEnums;

import lombok.Data;

@Data
public class CrearCategoriaServicioDTO {

    private String nombreCategoria;
    private String descripcion;
    private EstadoCategoriaServicioEnums estado;
}
