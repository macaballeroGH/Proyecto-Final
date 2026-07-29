package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.beautymanager.Modelo.Enums.EstadoPagoEnums;
import com.example.beautymanager.Modelo.Enums.MetodoPagoEnums;
import com.example.beautymanager.Modelo.Enums.TipoPagoEnums;

import lombok.Data;

@Data
public class ObtenerPagoDTO {

    private Long idPago;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private MetodoPagoEnums metodoPago;
    private EstadoPagoEnums estadoPago;
    private TipoPagoEnums tipoPago;

    //Datos del turno
    private Long idTurno;
    private LocalDateTime fechaTurno;
    private BigDecimal precioTurno;

    //Datos de la compra
    private Long idCompra;
    private BigDecimal totalCompra;
    private LocalDateTime fechaCompra;

    //Datos del cliente
    private Long idCliente;
    private String nombreCliente;
    private String apellidoCliente;
}
