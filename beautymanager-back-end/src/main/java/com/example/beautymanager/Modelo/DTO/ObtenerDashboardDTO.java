package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ObtenerDashboardDTO {

    private Long totalClientes;
    private Long totalEmpleados;
    private Long totalProductos;
    private Long totalCompras;
    private Long totalTurnos;
    private BigDecimal ingresosTotales;
    private Long productosSinStock;
    private Long turnosHoy;

}
