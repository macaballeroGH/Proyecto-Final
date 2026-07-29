package com.example.beautymanager.Repositorio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.HistorialTratamientoEntity;

public interface HistorialTratamientoRepository extends JpaRepository<HistorialTratamientoEntity, Long> {
    List<HistorialTratamientoEntity> findByTurnoId(Long idTurno);

    List<HistorialTratamientoEntity> findByFecha(LocalDateTime fecha);

    List<HistorialTratamientoEntity> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<HistorialTratamientoEntity> findByTurnoClienteId(Long idCliente);

    List<HistorialTratamientoEntity> findByTurnoEmpleadoId(Long idEmpleado);

    List<HistorialTratamientoEntity> findByObservacionContainingIgnoreCase(String texto);

    List<HistorialTratamientoEntity> findByTurnoClienteIdAndFecha(Long idCliente, LocalDateTime fecha);

    List<HistorialTratamientoEntity> findByTurnoEmpleadoIdAndFecha(Long idEmpleado, LocalDateTime fecha);

    List<HistorialTratamientoEntity> findByTurnoClienteIdOrderByFechaDesc(Long idCliente);

    List<HistorialTratamientoEntity> findByTurnoEmpleadoIdOrderByFechaDesc(Long idEmpleado);
}
