package com.example.beautymanager.Servicio;

import com.example.beautymanager.Modelo.DTO.CrearReservaDTO;

public interface ReservaPagoService {
    Long confirmarYPagar(Long idUsuario, CrearReservaDTO crearReserva);
}
