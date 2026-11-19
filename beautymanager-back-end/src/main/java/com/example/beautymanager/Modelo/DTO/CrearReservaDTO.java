package com.example.beautymanager.Modelo.DTO;

import java.util.List;

import lombok.Data;

@Data
public class CrearReservaDTO {

    private Long idCliente;

    private List<CrearReservaTurnoDTO> turnos;

    private String claveIdempotencia;
}
