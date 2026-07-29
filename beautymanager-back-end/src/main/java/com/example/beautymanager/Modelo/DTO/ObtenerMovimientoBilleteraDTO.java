package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.beautymanager.Modelo.Enums.MovimientoBilleteraEnums;

import lombok.Data;

@Data
public class ObtenerMovimientoBilleteraDTO {

    private Long idMovimiento;
    private LocalDateTime fecha;
    private BigDecimal monto;
    private String descripcion;
    private MovimientoBilleteraEnums tipoMovimiento;

    //Billetera
    private Long idBilletera;

    //Usuario
    private Long idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String emailUsuario;

    //Compra
    private Long idCompra;
}
