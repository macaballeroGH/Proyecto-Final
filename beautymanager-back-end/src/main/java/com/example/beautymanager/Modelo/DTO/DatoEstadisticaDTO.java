package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data 
public class DatoEstadisticaDTO {

    private String periodo;

    private BigDecimal valor;
}
