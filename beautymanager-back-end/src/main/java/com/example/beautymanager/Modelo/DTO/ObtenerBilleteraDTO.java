package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ObtenerBilleteraDTO {

    private Long idBilletera;
    private BigDecimal saldo;
    private LocalDateTime fechaCreacion;

    //Usuario
    private Long idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String emailUsuario;

    //Movimientos
    private Integer cantidadMovimientos;
}
