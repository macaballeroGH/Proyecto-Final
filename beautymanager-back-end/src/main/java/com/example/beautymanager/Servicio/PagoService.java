package com.example.beautymanager.Servicio;

import java.util.List;

import com.example.beautymanager.Modelo.DTO.ActualizarPagoDTO;
import com.example.beautymanager.Modelo.DTO.CrearPagoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerPagoDTO;
import com.example.beautymanager.Modelo.Entidad.PagoEntity;
import com.example.beautymanager.Modelo.Enums.EstadoPagoEnums;

public interface PagoService {

    ObtenerPagoDTO toMap(PagoEntity pago);

    ObtenerPagoDTO crearPago(CrearPagoDTO crearPago);

    ObtenerPagoDTO pagar(Long idUsuario, Long idPago);

    ObtenerPagoDTO actualizarPago(Long idPago, ActualizarPagoDTO actualizarPago);

    ObtenerPagoDTO obtenerPorTurno(Long idTurno);

    ObtenerPagoDTO obtenerPorId(Long idPago);

    List<ObtenerPagoDTO> listarPorEstado(EstadoPagoEnums estado);

    List<ObtenerPagoDTO> obtenerMisPagos(Long idUsuario);

    List<ObtenerPagoDTO> obtenerPagosPorCliente(Long idCliente);

    ObtenerPagoDTO crearPagoCompra(CrearPagoDTO crearPago);
}
