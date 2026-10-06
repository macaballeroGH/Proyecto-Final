package com.example.beautymanager.Servicio;

import java.util.List;

import com.example.beautymanager.Modelo.DTO.CrearTurnoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerTurnoDTO;
import com.example.beautymanager.Modelo.Entidad.TurnoEntity;

public interface TurnoService {

    ObtenerTurnoDTO toMap(TurnoEntity turno);
    
    ObtenerTurnoDTO crearTurno(CrearTurnoDTO crearTurno, Long idCliente);

    ObtenerTurnoDTO crearTurnoAdmin(CrearTurnoDTO crearTurno);

    void cancelarTurno(Long idTurno, Long idCliente);

    void aceptarTurno(Long idTurno, Long idUsuario);

    void rechazarTurno(Long idTurno, Long idUsuario);

    void marcarAusente(Long idTurno, Long idUsuario);

    void iniciarTurno(Long idTurno, Long idUsuario);

    void finalizarTurno(Long idTurno, Long idUsuario);

    List<ObtenerTurnoDTO> obtenerTurnosPorCliente(Long idUsuario);

    List<ObtenerTurnoDTO> obtenerTurnosPorEmpleado(Long idEmpleado);

    List<ObtenerTurnoDTO> obtenerTodosLosTurnos();
}
