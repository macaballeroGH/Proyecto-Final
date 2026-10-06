package com.example.beautymanager.Repositorio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.RecordatorioEntity;

public interface RecordatorioRepository extends JpaRepository<RecordatorioEntity, Long> {
    List<RecordatorioEntity> findByEnviadoFalse();

    List<RecordatorioEntity> findByEnviadoFalseAndFechaEnvioBefore(LocalDateTime fecha);

    List<RecordatorioEntity> findByTurnoId(Long idTurno);

    List<RecordatorioEntity> findByTurnoIdAndEnviadoFalse(Long idTurno);
}
