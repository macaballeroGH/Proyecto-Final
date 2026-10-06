package com.example.beautymanager.Servicio;

import java.util.List;

import com.example.beautymanager.Modelo.DTO.ObtenerMovimientoBilleteraDTO;
import com.example.beautymanager.Modelo.Entidad.MovimientoBilleteraEntity;

public interface MovimientoBilleteraService {

    ObtenerMovimientoBilleteraDTO toMap(MovimientoBilleteraEntity movimiento);

    List<ObtenerMovimientoBilleteraDTO> obtenerMovimientosPorUsuario(Long idUsuario);
}
