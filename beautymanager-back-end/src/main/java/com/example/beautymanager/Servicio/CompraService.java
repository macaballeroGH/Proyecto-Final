package com.example.beautymanager.Servicio;

import java.util.List;

import com.example.beautymanager.Modelo.DTO.ObtenerCompraDTO;
import com.example.beautymanager.Modelo.Entidad.CompraEntity;

public interface CompraService {

    ObtenerCompraDTO toMap(CompraEntity compra);

    ObtenerCompraDTO realizarCompra(Long idCliente, String claveIdempotencia);

    List<ObtenerCompraDTO> obtenerMisCompras(Long idCliente);

    ObtenerCompraDTO obtenerMiCompra(Long idCliente, Long idCompra);
}