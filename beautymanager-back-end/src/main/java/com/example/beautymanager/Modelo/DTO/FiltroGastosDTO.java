package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.beautymanager.Modelo.Enums.TipoGastoEnums;

import lombok.Data;

@Data 
public class FiltroGastosDTO {
    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;
    private TipoGastoEnums nombreGasto;
    private BigDecimal montoMinimo;
    private BigDecimal montoMaximo;
    private String ordenarPor;
    private String sentido;
}
