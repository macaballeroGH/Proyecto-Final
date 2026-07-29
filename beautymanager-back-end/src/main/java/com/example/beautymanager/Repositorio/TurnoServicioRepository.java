package com.example.beautymanager.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.beautymanager.Modelo.Entidad.TurnoServicioEntity;

public interface TurnoServicioRepository extends JpaRepository<TurnoServicioEntity, Long> {

    List<TurnoServicioEntity> findByTurnoId(Long idTurno);

    List<TurnoServicioEntity> findByServicioId(Long idServicio);

    List<TurnoServicioEntity> findByTurnoIdAndServicioId(Long idTurno, Long idServicio);

    boolean existsByTurnoIdAndServicioId(Long idTurno, Long idServicio);

    @Query("SELECT ts.servicio.nombre, COUNT(ts) FROM TurnoServicioEntity ts GROUP BY ts.servicio.nombre")
    List<Object[]> contarServiciosPorNombre();
}
