package com.example.beautymanager.Servicio;

import java.util.List;

import com.example.beautymanager.Modelo.DTO.ActualizarHorarioEmpleadoDTO;
import com.example.beautymanager.Modelo.DTO.CrearHorarioEmpleadoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerHorarioEmpleadoDTO;
import com.example.beautymanager.Modelo.Entidad.HorarioEmpleadoEntity;

public interface HorarioEmpleadoService {

    ObtenerHorarioEmpleadoDTO toMap(HorarioEmpleadoEntity horario);

    ObtenerHorarioEmpleadoDTO crearHorario(CrearHorarioEmpleadoDTO crearHorario);

    ObtenerHorarioEmpleadoDTO actualizarHorario(Long idHorario, ActualizarHorarioEmpleadoDTO actualizarHorario);

    ObtenerHorarioEmpleadoDTO obtenerPorId(Long idHorario);

    List<ObtenerHorarioEmpleadoDTO> obtenerTodos();

    List<ObtenerHorarioEmpleadoDTO> obtenerPorEmpleado(Long idEmpleado);

    List<ObtenerHorarioEmpleadoDTO> obtenerPorEmpleadoYDia(Long idEmpleado, Integer diaSemana);

    void eliminar(Long idHorario);
}
