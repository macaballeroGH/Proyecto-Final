package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;

import com.example.beautymanager.Modelo.Enums.EstadoServicioEnums;

import lombok.Data;

@Data
public class ObtenerServicioDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer duracionMinutos;
    private String nombreCategoria;
    private EstadoServicioEnums estado;
}
