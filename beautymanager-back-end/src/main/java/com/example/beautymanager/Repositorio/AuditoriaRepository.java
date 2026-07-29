package com.example.beautymanager.Repositorio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.AuditoriaEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;

public interface AuditoriaRepository extends JpaRepository<AuditoriaEntity, Long> {
    List<AuditoriaEntity> findByUsuario(UsuarioEntity usuario);

    List<AuditoriaEntity> findByUsuarioId(Long idUsuario);

    List<AuditoriaEntity> findByAccion(String accion);

    List<AuditoriaEntity> findByTablaAfectada(String tablaAfectada);

    List<AuditoriaEntity> findByIdRegistro(Long idRegistro);

    List<AuditoriaEntity> findByFechaHora(LocalDateTime fechaHora);

    List<AuditoriaEntity> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    List<AuditoriaEntity> findTop20ByOrderByFechaHoraDesc();
}
