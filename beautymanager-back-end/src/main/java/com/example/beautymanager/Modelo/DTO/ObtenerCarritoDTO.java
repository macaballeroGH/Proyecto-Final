package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.beautymanager.Modelo.Enums.EstadoCarritoEnums;

import lombok.Data;

@Data
public class ObtenerCarritoDTO {

    private Long idCarrito;
    private LocalDateTime fechaCreacion;

    //Usuario
    private Long idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;

    //Estado
    private EstadoCarritoEnums estadoCarrito;

    //Productos
    private List<CarritoItemDTO> items;

    //Totales
    private BigDecimal total;
}
