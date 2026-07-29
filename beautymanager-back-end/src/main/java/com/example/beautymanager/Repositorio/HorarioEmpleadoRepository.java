package com.example.beautymanager.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.HorarioEmpleadoEntity;

public interface HorarioEmpleadoRepository extends JpaRepository<HorarioEmpleadoEntity, Long> {

    List<HorarioEmpleadoEntity> findByEmpleadoId(Long idEmpleado);

    List<HorarioEmpleadoEntity> findByEmpleadoIdAndDiaSemana(Long idEmpleado, Integer diaSemana);

    List<HorarioEmpleadoEntity> findAllByOrderByDiaSemanaAsc();

}
