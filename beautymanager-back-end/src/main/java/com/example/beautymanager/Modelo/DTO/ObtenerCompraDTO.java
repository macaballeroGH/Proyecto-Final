package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.beautymanager.Modelo.Enums.EstadoCompraEnums;

import lombok.Data;

@Data
public class ObtenerCompraDTO {

    private Long idCompra;
    private LocalDateTime fecha;
    private BigDecimal total;

    //Cliente
    private Long idCliente;
    private String nombreCliente;
    private String apellidoCliente;

    //Estado
    private EstadoCompraEnums estadoCompra;

    //Detalle de los productos
    private List<DetalleCompraDTO> detalles;
}
