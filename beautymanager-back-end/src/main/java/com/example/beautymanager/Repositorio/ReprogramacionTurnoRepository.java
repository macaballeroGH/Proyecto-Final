package com.example.beautymanager.Repositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.beautymanager.Modelo.Entidad.ReprogramacionTurnoEntity;
import com.example.beautymanager.Modelo.Enums.EstadoReprogramacionEnums;

public interface ReprogramacionTurnoRepository extends JpaRepository<ReprogramacionTurnoEntity, Long> {

    List<ReprogramacionTurnoEntity> findByTurnoIdOrderByFechaCambioDesc(Long idTurno);

    boolean existsByTurnoIdAndEstadoPropuesta(Long idTurno, EstadoReprogramacionEnums estadoPropuesta);

    Optional<ReprogramacionTurnoEntity> findByTurnoIdAndEstadoPropuesta(Long idTurno, EstadoReprogramacionEnums estadoPropuesta);

    List<ReprogramacionTurnoEntity> findByTurnoClienteIdAndEstadoPropuesta(Long idCliente, EstadoReprogramacionEnums estadoPropuesta);

    List<ReprogramacionTurnoEntity> findByEstadoPropuesta(EstadoReprogramacionEnums estadoPropuesta);

    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
        FROM ReprogramacionTurnoEntity r
        WHERE r.estadoPropuesta = :estado
          AND r.inicioNuevo < :fin
          AND r.finNuevo > :inicio
          AND (
              r.turno.empleado.id = :idEmpleado
              OR r.turno.cliente.id = :idCliente
          )
        """)
    boolean existeConflictoConPropuestaPendiente(
        @Param("idEmpleado") Long idEmpleado,
        @Param("idCliente") Long idCliente,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin,
        @Param("estado") EstadoReprogramacionEnums estado
    );

    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
        FROM ReprogramacionTurnoEntity r
        WHERE r.id <> :idReprogramacion
          AND r.estadoPropuesta = :estado
          AND r.inicioNuevo < :fin
          AND r.finNuevo > :inicio
          AND (
              r.turno.empleado.id = :idEmpleado
              OR r.turno.cliente.id = :idCliente
          )
        """)
    boolean existeOtraPropuestaEnHorario(
        @Param("idReprogramacion") Long idReprogramacion,
        @Param("idEmpleado") Long idEmpleado,
        @Param("idCliente") Long idCliente,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin,
        @Param("estado") EstadoReprogramacionEnums estado
    );


    //========================================
    //Verificar si una empleada tiene una
    //propuesta pendiente en un horario
    //========================================

    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
        FROM ReprogramacionTurnoEntity r
        WHERE r.turno.empleado.id = :idEmpleado
          AND r.estadoPropuesta = :estado
          AND r.inicioNuevo < :fin
          AND r.finNuevo > :inicio
        """)
    boolean existePropuestaPendienteParaEmpleado(
        @Param("idEmpleado") Long idEmpleado,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin,
        @Param("estado") EstadoReprogramacionEnums estado
    );

}
