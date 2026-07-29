package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DetalleCompraDTO {

    private Long idDetalleCompra;

    //Producto
    private Long idProducto;
    private String nombreProducto;
    private String descripcionProducto;

    //Compra
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
