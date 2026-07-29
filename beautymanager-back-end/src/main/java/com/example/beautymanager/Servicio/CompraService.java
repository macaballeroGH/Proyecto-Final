package com.example.beautymanager.Servicio;

import com.example.beautymanager.Modelo.DTO.ObtenerCompraDTO;
import com.example.beautymanager.Modelo.Entidad.CompraEntity;

public interface CompraService {

    ObtenerCompraDTO toMap(CompraEntity compra);

    ObtenerCompraDTO realizarCompra(Long idCliente);
}