package com.example.beautymanager.Repositorio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.BloqueoAgendaEntity;

public interface BloqueoAgendaRepository extends JpaRepository<BloqueoAgendaEntity, Long> {

    List<BloqueoAgendaEntity> findByEmpleadoId(Long idEmpleado);

    List<BloqueoAgendaEntity> findAllByOrderByInicioDesc();

    List<BloqueoAgendaEntity> findByEmpleadoIdAndInicioLessThanAndFinGreaterThan(Long idEmpleado, LocalDateTime inicio, LocalDateTime fin);
}
