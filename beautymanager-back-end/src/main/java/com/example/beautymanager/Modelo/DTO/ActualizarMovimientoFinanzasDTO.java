package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.beautymanager.Modelo.Enums.EstadoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.MetodoPagoEnums;
import com.example.beautymanager.Modelo.Enums.TipoGastoEnums;
import com.example.beautymanager.Modelo.Enums.TipoMovimientoEnums;

import lombok.Data;

@Data
public class ActualizarMovimientoFinanzasDTO {

    private LocalDateTime fechaGasto;
    private BigDecimal monto;
    private TipoMovimientoEnums tipoMovimiento;
    private String descripcion;
    private EstadoMovimientoEnums estado;
    private TipoGastoEnums nombreGasto;
    private Long idCompra;
    private MetodoPagoEnums metodoPago;
}
