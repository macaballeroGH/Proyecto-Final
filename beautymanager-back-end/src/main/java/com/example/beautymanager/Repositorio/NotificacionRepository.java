package com.example.beautymanager.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.NotificacionEntity;

public interface NotificacionRepository extends JpaRepository<NotificacionEntity, Long> {

    List<NotificacionEntity> findByUsuarioId(Long idUsuario);

    List<NotificacionEntity> findByUsuarioIdAndLeidaFalse(Long idUsuario);
}
