package com.example.beautymanager.Repositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.EmpleadoEntity;
import com.example.beautymanager.Modelo.Enums.EspecialidadEmpleadoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoEmpleadoEnums;

public interface EmpleadoRepository extends JpaRepository<EmpleadoEntity, Long> {
    List<EmpleadoEntity> findByEspecialidad(EspecialidadEmpleadoEnums especialidad);

    List<EmpleadoEntity> findByEstadoEmpleado(EstadoEmpleadoEnums estadoEmpleado);

    List<EmpleadoEntity> findByEspecialidadAndEstadoEmpleado(EspecialidadEmpleadoEnums especialidad, EstadoEmpleadoEnums estadoEmpleado);

    List<EmpleadoEntity> findByEstadoEmpleadoOrderByFechaAltaDesc(EstadoEmpleadoEnums estadoEmpleado);

    List<EmpleadoEntity> findByFechaBajaIsNull();

    List<EmpleadoEntity> findByFechaBajaIsNotNull();

    List<EmpleadoEntity> findByFechaAltaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<EmpleadoEntity> findByFechaBajaBetween(LocalDateTime inicio, LocalDateTime fin);

    boolean existsByUsuario_Id(Long idUsuario);

    Optional<EmpleadoEntity> findByUsuario_Id(Long idUsuario);

    Optional<EmpleadoEntity> findByUsuario_Email(String email);

    List<EmpleadoEntity> findAllByOrderByFechaAltaDesc();

    List<EmpleadoEntity> findAllByOrderByIdAsc();
}
