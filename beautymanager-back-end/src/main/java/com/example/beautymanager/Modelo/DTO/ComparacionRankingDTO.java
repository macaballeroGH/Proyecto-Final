package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data 
public class ComparacionRankingDTO {

    private String nombre;

    private Long cantidadActual;

    private Long cantidadAnterior;

    private BigDecimal variacion;
}
