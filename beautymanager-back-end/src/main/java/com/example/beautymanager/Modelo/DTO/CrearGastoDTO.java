package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.beautymanager.Modelo.Enums.MetodoPagoEnums;
import com.example.beautymanager.Modelo.Enums.TipoGastoEnums;

import lombok.Data;

@Data 
public class CrearGastoDTO {
    
    private LocalDateTime fechaGasto;
    private BigDecimal monto;
    private String descripcion;
    private MetodoPagoEnums metodoPago;
    private TipoGastoEnums nombreGasto;
}
