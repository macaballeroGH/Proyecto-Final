package com.example.beautymanager.Modelo.DTO;

import lombok.Data;

@Data
public class RegistrarTratamientoServicioDTO {
    
    private Long idServicioRealizado;

    private Long idTurnoServicioReservado;

    private String motivoCambio;
}
