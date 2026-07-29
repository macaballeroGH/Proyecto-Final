package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;

import com.example.beautymanager.Modelo.Enums.EstadoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.TipoMovimientoEnums;

import lombok.Data;

@Data
public class CrearMovimientoFinanzasDTO {

    private BigDecimal monto;
    private TipoMovimientoEnums tipoMovimiento;
    private String descripcion;
    private EstadoMovimientoEnums estado;
    private Long idUsuario;
    private Long idTipoGasto;
    private Long idCompra;
}
