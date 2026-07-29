package com.example.beautymanager.Servicio;

import java.math.BigDecimal;
import java.util.List;

import com.example.beautymanager.Modelo.DTO.ActualizarServicioDTO;
import com.example.beautymanager.Modelo.DTO.CrearServicioDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerServicioDTO;
import com.example.beautymanager.Modelo.Entidad.ServicioEntity;

public interface ServicioService {

    ObtenerServicioDTO toMap(ServicioEntity servicio);

    ObtenerServicioDTO crearServicio(CrearServicioDTO crearServicio);

    ObtenerServicioDTO actualizarServicio(Long id, ActualizarServicioDTO actualizarServicio);

    void eliminarServicio(Long id);

    ObtenerServicioDTO obtenerPorId(Long id);

    List<ObtenerServicioDTO> listarTodos();

    List<ObtenerServicioDTO> listarActivos();

    List<ObtenerServicioDTO> listarInactivos();
    
    List<ObtenerServicioDTO> buscarPorNombre(String nombre);

    List<ObtenerServicioDTO> buscarPorDescripcion(String descripcion);

    List<ObtenerServicioDTO> buscarPorCategoria(String categoria);

    List<ObtenerServicioDTO> buscarActivosPorCategoria(String categoria);

    List<ObtenerServicioDTO> buscarPorRangoPrecio(BigDecimal min, BigDecimal max);

    List<ObtenerServicioDTO> buscarPrecioMaximo(BigDecimal precio);

    List<ObtenerServicioDTO> buscarPrecioMinimo(BigDecimal precio);

    List<ObtenerServicioDTO> buscarPorDuracion(Integer minutos);

    List<ObtenerServicioDTO> buscarDuracionMaxima(Integer minutos);

    List<ObtenerServicioDTO> buscarDuracionMinima(Integer minutos);

    List<ObtenerServicioDTO> buscarPorRangoDuracion(Integer min, Integer max);

}
