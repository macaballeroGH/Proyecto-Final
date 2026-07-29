package com.example.beautymanager.Servicio;

import com.example.beautymanager.Modelo.DTO.ActualizarCantidadCarritoDTO;
import com.example.beautymanager.Modelo.DTO.AgregarProductoCarritoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerCarritoDTO;
import com.example.beautymanager.Modelo.Entidad.CarritoEntity;

public interface CarritoService {

    ObtenerCarritoDTO toMap(CarritoEntity carrito);

    ObtenerCarritoDTO obtenerCarrito(String email);

    ObtenerCarritoDTO agregarProducto(String email, AgregarProductoCarritoDTO agregarProductoCarrito);

    ObtenerCarritoDTO eliminarProducto(String email, Long idProducto);

    ObtenerCarritoDTO actualizarCantidad(String email, ActualizarCantidadCarritoDTO actualizarCantidadCarrito);
}
