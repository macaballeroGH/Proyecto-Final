package com.example.beautymanager.Servicio;

import java.time.LocalDateTime;
import java.util.List;

import com.example.beautymanager.Modelo.DTO.HistorialClienteDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerHistorialTratamientoDTO;
import com.example.beautymanager.Modelo.DTO.RegistrarTratamientoDTO;
import com.example.beautymanager.Modelo.Entidad.HistorialTratamientoEntity;

public interface HistorialTratamientoService {

    ObtenerHistorialTratamientoDTO toMap(HistorialTratamientoEntity historial);

    ObtenerHistorialTratamientoDTO registrarTratamiento(Long idUsuario, RegistrarTratamientoDTO registrarTratamiento);
    
    ObtenerHistorialTratamientoDTO buscarPorId(Long idHistorial);

    List<ObtenerHistorialTratamientoDTO> listarTodos();

    List<ObtenerHistorialTratamientoDTO> listarPorCliente(Long idUsuario);

    List<ObtenerHistorialTratamientoDTO> listarPorEmpleado(Long idEmpleado);

    List<ObtenerHistorialTratamientoDTO> listarPorFecha(LocalDateTime fecha);

    List<ObtenerHistorialTratamientoDTO> listarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<ObtenerHistorialTratamientoDTO> listarPorTurno(Long idTurno);

    List<ObtenerHistorialTratamientoDTO> buscarPorTexto(String texto);

    List<ObtenerHistorialTratamientoDTO> verHistorialCliente(Long idCliente);

    HistorialClienteDTO obtenerHistorialCompletoCliente(Long idCliente);

    void eliminar(Long idHistorial);
}
