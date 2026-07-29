package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;

import com.example.beautymanager.Modelo.Enums.EstadoServicioEnums;

import lombok.Data;

@Data
public class CrearServicioDTO {

    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer duracionMinutos;
    private Long idCategoria;
    private EstadoServicioEnums estado;
}
