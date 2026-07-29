package com.example.beautymanager.Repositorio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.beautymanager.Modelo.Entidad.TurnoEntity;
import com.example.beautymanager.Modelo.Enums.EstadoTurnoEnums;

public interface TurnoRepository extends JpaRepository<TurnoEntity, Long> {
    List<TurnoEntity> findByFechaHoraInicioBetween(LocalDateTime inicio, LocalDateTime fin);

    List<TurnoEntity> findByClienteId(Long idCliente);

    List<TurnoEntity> findByEmpleadoId(Long idEmpleado);

    @Query("SELECT DISTINCT t FROM TurnoEntity t JOIN t.servicios ts WHERE ts.servicio.id = :idServicio")
    List<TurnoEntity> findByServicioId(@Param("idServicio") Long idServicio);

    List<TurnoEntity> findByEstadoTurno(EstadoTurnoEnums estadoTurno);

    List<TurnoEntity> findByClienteIdAndFechaHoraInicioBetween(Long idCliente, LocalDateTime inicio, LocalDateTime fin);

    List<TurnoEntity> findByEmpleadoIdAndFechaHoraInicioBetween(Long idEmpleado, LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT DISTINCT t FROM TurnoEntity t JOIN t.servicios ts WHERE ts.servicio.id = :idServicio AND t.fechaHoraInicio BETWEEN :inicio AND :fin")
    List<TurnoEntity> findByServicioIdAndFechaHoraInicioBetween(@Param("idServicio") Long idServicio, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    List<TurnoEntity> findByEstadoTurnoAndFechaHoraInicioBetween(EstadoTurnoEnums estadoTurno, LocalDateTime inicio, LocalDateTime fin);

    List<TurnoEntity> findByEmpleadoIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(Long idEmpleado, LocalDateTime inicio, LocalDateTime fin);

    List<TurnoEntity> findByClienteIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(Long idCliente, LocalDateTime inicio, LocalDateTime fin);
}
