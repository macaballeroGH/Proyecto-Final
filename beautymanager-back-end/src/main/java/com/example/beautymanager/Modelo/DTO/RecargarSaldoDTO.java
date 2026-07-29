package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class RecargarSaldoDTO {

    private Long idUsuario;
    private BigDecimal monto;
}
