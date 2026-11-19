package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.beautymanager.Modelo.Enums.EstadoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.MetodoPagoEnums;
import com.example.beautymanager.Modelo.Enums.TipoGastoEnums;
import com.example.beautymanager.Modelo.Enums.TipoMovimientoEnums;

import lombok.Data;

@Data
public class ObtenerMovimientoFinanzasDTO {

    private Long idMovimiento;
    private LocalDateTime fechaGasto;
    private LocalDateTime fechaRegistro;
    private BigDecimal monto;
    private TipoMovimientoEnums tipoMovimiento;
    private String descripcion;
    private EstadoMovimientoEnums estadoMovimiento;
    private MetodoPagoEnums metodoPago;

    //Usuario
    private Long idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;

    //Tipo gasto
    private TipoGastoEnums nombreGasto;

    //Compra
    private Long idCompra;
}
