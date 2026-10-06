package com.example.beautymanager.Repositorio;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.HorarioEmpleadoEntity;

public interface HorarioEmpleadoRepository extends JpaRepository<HorarioEmpleadoEntity, Long> {

    List<HorarioEmpleadoEntity> findByEmpleadoId(Long idEmpleado);

    List<HorarioEmpleadoEntity> findByEmpleadoIdAndDiaSemana(Long idEmpleado, Integer diaSemana);

    boolean existsByEmpleadoIdAndDiaSemanaAndHoraInicioLessThanAndHoraFinGreaterThan(Long idEmpleado, Integer diaSemana, LocalTime horaFin, LocalTime horaInicio);

    boolean existsByEmpleadoIdAndDiaSemanaAndHoraInicioLessThanAndHoraFinGreaterThanAndIdNot(Long idEmpleado, Integer diaSemana, LocalTime horaFin, LocalTime horaInicio, Long idHorario);

    List<HorarioEmpleadoEntity> findAllByOrderByDiaSemanaAsc();

}
