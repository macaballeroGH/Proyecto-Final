package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;

import com.example.beautymanager.Modelo.Enums.EstadoProductoEnums;

import lombok.Data;

@Data
public class ActualizarProductoDTO {

    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private EstadoProductoEnums estado;
}
