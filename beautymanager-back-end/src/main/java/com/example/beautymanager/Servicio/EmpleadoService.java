package com.example.beautymanager.Servicio;

import java.time.LocalDateTime;
import java.util.List;

import com.example.beautymanager.Modelo.DTO.ActualizarEmpleadoDTO;
import com.example.beautymanager.Modelo.DTO.CrearEmpleadoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerEmpleadoDTO;
import com.example.beautymanager.Modelo.Entidad.EmpleadoEntity;
import com.example.beautymanager.Modelo.Enums.EspecialidadEmpleadoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoEmpleadoEnums;

public interface EmpleadoService {

    ObtenerEmpleadoDTO toMap(EmpleadoEntity empleado);

    ObtenerEmpleadoDTO crearEmpleado(CrearEmpleadoDTO crearEmpleado);
    
    ObtenerEmpleadoDTO actualizarEmpleado(Long id, ActualizarEmpleadoDTO actualizarEmpleado);

    void eliminarEmpleado(Long id);

    ObtenerEmpleadoDTO buscarPorId(Long id);

    List<ObtenerEmpleadoDTO> listarTodos();

    List<ObtenerEmpleadoDTO> buscarPorEspecialidad(EspecialidadEmpleadoEnums especialidad);

    List<ObtenerEmpleadoDTO> buscarPorEstado(EstadoEmpleadoEnums estadoEmpleado);

    List<ObtenerEmpleadoDTO> buscarPorEspecialidadAndEstado(EspecialidadEmpleadoEnums especialidad, EstadoEmpleadoEnums estadoEmpleado);

    ObtenerEmpleadoDTO buscarPorEmail(String email);

    ObtenerEmpleadoDTO buscarPorUsuarioId(Long idUsuario);

    boolean existePorUsuario(Long idUsuario);

    ObtenerEmpleadoDTO cambiarEstado(Long idEmpleado, EstadoEmpleadoEnums nuevoEstado);

    ObtenerEmpleadoDTO darDeBaja(Long idEmpleado);

    ObtenerEmpleadoDTO reactivarEmpleado(Long idEmpleado);

    List<ObtenerEmpleadoDTO> empleadosActivos();

    List<ObtenerEmpleadoDTO> empleadosInactivos();

    List<ObtenerEmpleadoDTO> altasEntreFechas(LocalDateTime inicio, LocalDateTime fin);

    List<ObtenerEmpleadoDTO> bajasEntreFechas(LocalDateTime inicio, LocalDateTime fin);
}
