package com.example.beautymanager.Servicio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.beautymanager.Modelo.DTO.ObtenerTurnoDTO;


public interface AgendaService {

    List<LocalDateTime> obtenerDisponibilidad(Long idEmpleado, List<Long> serviciosIds, LocalDate fecha);

    List<LocalDateTime> obtenerDisponibilidadRango(Long idEmpleado, List<Long> serviciosIds, LocalDate inicio, LocalDate fin);

    List<LocalDateTime> obtenerDisponibilidadPorEmpleado(Long idUsuario, List<Long> serviciosIds, LocalDate fecha);

    List <LocalDateTime> obtenerDisponibilidadGeneral(LocalDate fecha);

    List<ObtenerTurnoDTO> obtenerAgenda(LocalDate inicio, LocalDate fin);

    boolean validarDisponibilidad(Long idEmpleado, LocalDateTime inicio, LocalDateTime fin);

    Integer calcularDuracionTotal(List<Long> serviciosIds);
}
