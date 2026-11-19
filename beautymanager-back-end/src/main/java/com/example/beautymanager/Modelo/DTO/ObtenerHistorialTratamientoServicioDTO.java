package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ObtenerHistorialTratamientoServicioDTO {

    private Long idHistorialServicio;

    private Long idServicioRealizado;

    private String nombreServicioRealizado;

    //========================================
    //SERVICIO ORIGINALMENTE RESERVADO
    //========================================

    private Long idTurnoServicioReservado;

    private Long idServicioReservado;

    private String nombreServicioReservado;

    //========================================
    //CAMBIOS DEL TRATAMIENTO
    //========================================

    private String motivoCambio;

    private LocalDateTime fechaCambio;
}
