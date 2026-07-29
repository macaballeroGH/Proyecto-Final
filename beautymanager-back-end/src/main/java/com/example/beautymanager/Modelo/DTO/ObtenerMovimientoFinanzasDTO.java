package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.beautymanager.Modelo.Enums.EstadoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.TipoMovimientoEnums;

import lombok.Data;

@Data
public class ObtenerMovimientoFinanzasDTO {

    private Long idMovimiento;
    private LocalDateTime fecha;
    private BigDecimal monto;
    private TipoMovimientoEnums tipoMovimiento;
    private String descripcion;
    private EstadoMovimientoEnums estadoMovimiento;

    //Usuario
    private Long idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;

    //Tipo gasto
    private Long idTipoGasto;
    private String nombreTipoGasto;

    //Compra
    private Long idCompra;
}
