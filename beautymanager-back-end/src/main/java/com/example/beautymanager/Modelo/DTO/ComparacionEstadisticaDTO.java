package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data 
public class ComparacionEstadisticaDTO {

    private BigDecimal valorActual;

    private BigDecimal valorAnterior;

    private BigDecimal variacion;
}
