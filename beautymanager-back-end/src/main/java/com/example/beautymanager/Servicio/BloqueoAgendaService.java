package com.example.beautymanager.Servicio;

import java.time.LocalDateTime;
import java.util.List;

import com.example.beautymanager.Modelo.DTO.ActualizarBloqueoAgendaDTO;
import com.example.beautymanager.Modelo.DTO.CrearBloqueoAgendaDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerBloqueoAgendaDTO;
import com.example.beautymanager.Modelo.Entidad.BloqueoAgendaEntity;

public interface BloqueoAgendaService {

    ObtenerBloqueoAgendaDTO toMap(BloqueoAgendaEntity bloqueo);

    ObtenerBloqueoAgendaDTO crearBloqueo(CrearBloqueoAgendaDTO crearBloqueo);

    ObtenerBloqueoAgendaDTO actualizarBloqueo(Long idBloqueo, ActualizarBloqueoAgendaDTO actualizarBloqueo);

    ObtenerBloqueoAgendaDTO obtenerPorId(Long idBloqueo);

    List<ObtenerBloqueoAgendaDTO> obtenerTodos();

    List<ObtenerBloqueoAgendaDTO> obtenerPorEmpleado(Long idEmpleado);

    List<ObtenerBloqueoAgendaDTO> obtenerBloqueosSuperpuestos(Long idEmpleado, LocalDateTime inicio, LocalDateTime fin);

    void eliminar(Long idBloqueo);
}
