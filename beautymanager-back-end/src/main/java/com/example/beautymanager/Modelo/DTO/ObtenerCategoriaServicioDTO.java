package com.example.beautymanager.Modelo.DTO;

import com.example.beautymanager.Modelo.Enums.EstadoCategoriaServicioEnums;

import lombok.Data;

@Data
public class ObtenerCategoriaServicioDTO {

    private Long idCategoria;
    private String nombreCategoria;
    private String descripcion;
    private EstadoCategoriaServicioEnums estado;
}
