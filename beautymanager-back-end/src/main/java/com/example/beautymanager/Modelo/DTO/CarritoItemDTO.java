package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CarritoItemDTO {

    private Long idCarritoItem;

    //Producto
    private Long idProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private BigDecimal precioProducto;

    //Carrito
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
