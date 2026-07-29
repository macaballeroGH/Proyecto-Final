package com.example.beautymanager.Modelo.DTO;

import java.util.List;

import lombok.Data;

@Data
public class HistorialClienteDTO {

    private Long idCliente;
    private String nombreCliente;
    private String apellidoCliente;

    //Obtener el historial de tratamientos del cliente
    private List<ObtenerHistorialTratamientoDTO> tratamientos;

    //Obtener el historial de pagos del cliente
    private List<ObtenerPagoDTO> pagos;
    
}
