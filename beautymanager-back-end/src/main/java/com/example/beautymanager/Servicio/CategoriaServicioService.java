package com.example.beautymanager.Servicio;

import java.util.List;

import com.example.beautymanager.Modelo.DTO.ActualizarCategoriaServicioDTO;
import com.example.beautymanager.Modelo.DTO.CrearCategoriaServicioDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerCategoriaServicioDTO;
import com.example.beautymanager.Modelo.Entidad.CategoriaServicioEntity;

public interface CategoriaServicioService {

    ObtenerCategoriaServicioDTO toMap(CategoriaServicioEntity categoria);

    ObtenerCategoriaServicioDTO crearCategoria(CrearCategoriaServicioDTO crearCategoria);

    ObtenerCategoriaServicioDTO actualizarCategoria(Long id, ActualizarCategoriaServicioDTO actualizarCategoria);

    void eliminarCategoria(Long id);

    ObtenerCategoriaServicioDTO obtenerPorId(Long id);

    List<ObtenerCategoriaServicioDTO> listarTodas();

    List<ObtenerCategoriaServicioDTO> listarActivas();

    List<ObtenerCategoriaServicioDTO> listarInactivas();

    List<ObtenerCategoriaServicioDTO> buscarPorNombre(String nombre);

    List<ObtenerCategoriaServicioDTO> buscarPorDescripcion(String descripcion);
}
