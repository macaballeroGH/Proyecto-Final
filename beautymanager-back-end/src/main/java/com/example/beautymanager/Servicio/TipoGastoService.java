package com.example.beautymanager.Servicio;

import java.util.List;

import com.example.beautymanager.Modelo.DTO.ActualizarTipoGastoDTO;
import com.example.beautymanager.Modelo.DTO.CrearTipoGastoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerTipoGastoDTO;
import com.example.beautymanager.Modelo.Entidad.TipoGastoEntity;

public interface TipoGastoService {

    ObtenerTipoGastoDTO toMap(TipoGastoEntity tipoGasto);

    ObtenerTipoGastoDTO crearTipoGasto(CrearTipoGastoDTO tipoGasto);

    ObtenerTipoGastoDTO actualizarTipoGasto(Long id, ActualizarTipoGastoDTO tipoGasto);

    void eliminarTipoGasto(Long id);

    ObtenerTipoGastoDTO buscarPorId(Long id);

    List<ObtenerTipoGastoDTO> listarTodos();

    List<ObtenerTipoGastoDTO> listarOrdenados();

    List<ObtenerTipoGastoDTO> buscarPorNombre(String nombre);

    List<ObtenerTipoGastoDTO> buscarPorDescripcion(String descripcion);

    boolean existePorNombre(String nombre);
}