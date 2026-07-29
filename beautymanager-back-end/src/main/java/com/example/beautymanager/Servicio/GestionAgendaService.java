package com.example.beautymanager.Servicio;

import java.util.List;

import com.example.beautymanager.Modelo.DTO.ActualizarTurnoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerTurnoDTO;

public interface GestionAgendaService {

    List<ObtenerTurnoDTO> listarTodos();

    ObtenerTurnoDTO obtenerPorId(Long idTurno);

    List<ObtenerTurnoDTO> listarPorCliente(Long idCliente);

    List<ObtenerTurnoDTO> listarPorEmpleado(Long idEmpleado);

    ObtenerTurnoDTO aceptarTurno(Long idTurno);

    ObtenerTurnoDTO rechazarTurno(Long idTurno);

    ObtenerTurnoDTO cancelarTurno(Long idTurno);

    ObtenerTurnoDTO reprogramarTurno(Long idTurno, ActualizarTurnoDTO actualizarTurno);

    ObtenerTurnoDTO cambiarEmpleado(Long idTurno, Long idEmpleado);
}
