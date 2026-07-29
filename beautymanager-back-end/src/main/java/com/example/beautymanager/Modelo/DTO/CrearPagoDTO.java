package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;

import com.example.beautymanager.Modelo.Enums.MetodoPagoEnums;

import lombok.Data;

@Data
public class CrearPagoDTO {

    private BigDecimal monto;
    private MetodoPagoEnums metodoPago;
    private Long idTurno;
    private Long idCompra;
}
