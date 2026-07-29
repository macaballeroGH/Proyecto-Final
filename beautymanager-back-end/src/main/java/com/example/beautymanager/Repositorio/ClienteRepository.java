package com.example.beautymanager.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.ClienteEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
    Optional<ClienteEntity> findByUsuario(UsuarioEntity usuario);

    Optional<ClienteEntity> findByUsuarioId(Long usuarioId);

    Optional<ClienteEntity> findByUsuarioEmail(String email);

    boolean existsByUsuario(UsuarioEntity usuario);
}
