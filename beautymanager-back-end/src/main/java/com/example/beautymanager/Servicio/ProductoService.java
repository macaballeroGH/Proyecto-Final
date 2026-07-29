package com.example.beautymanager.Servicio;

import java.util.List;

import com.example.beautymanager.Modelo.DTO.ActualizarProductoDTO;
import com.example.beautymanager.Modelo.DTO.CrearProductoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerProductoDTO;
import com.example.beautymanager.Modelo.Entidad.ProductoEntity;

public interface ProductoService {

    ObtenerProductoDTO toMap(ProductoEntity producto);

    ObtenerProductoDTO crearProducto(CrearProductoDTO crearProducto);

    ObtenerProductoDTO actualizarProducto(Long id, ActualizarProductoDTO actualizarProducto);

    void eliminarProducto(Long id);

    ObtenerProductoDTO obtenerPorId(Long id);

    List<ObtenerProductoDTO> listarProducto();

    void descontarStock(Long idProducto, Integer cantidad);

    void aumentarStock(Long idProducto, Integer cantidad);
}
