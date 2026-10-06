package com.example.beautymanager.Repositorio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.beautymanager.Modelo.Entidad.TurnoServicioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoTurnoEnums;

public interface TurnoServicioRepository extends JpaRepository<TurnoServicioEntity, Long> {

    List<TurnoServicioEntity> findByTurnoId(Long idTurno);

    List<TurnoServicioEntity> findByServicioId(Long idServicio);

    List<TurnoServicioEntity> findByTurnoIdAndServicioId(Long idTurno, Long idServicio);

    boolean existsByTurnoIdAndServicioId(Long idTurno, Long idServicio);

    @Query("SELECT ts.servicio.nombre, COUNT(ts) FROM TurnoServicioEntity ts GROUP BY ts.servicio.nombre")
    List<Object[]> contarServiciosPorNombre();

    @Query("""
            SELECT ts.servicio.nombre, COUNT(ts) FROM TurnoServicioEntity ts WHERE ts.turno.fechaHoraInicio BETWEEN :inicio AND :fin GROUP BY ts.servicio.nombre ORDER BY COUNT(ts) DESC
            """)
    List<Object[]> contarServiciosPorNombreYPeriodo(@Param("estado") EstadoTurnoEnums estado, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
