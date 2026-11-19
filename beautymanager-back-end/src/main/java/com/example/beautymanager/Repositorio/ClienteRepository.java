package com.example.beautymanager.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.beautymanager.Modelo.Entidad.ClienteEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;

import jakarta.persistence.LockModeType;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
    Optional<ClienteEntity> findByUsuario(UsuarioEntity usuario);

    Optional<ClienteEntity> findByUsuarioId(Long usuarioId);

    Optional<ClienteEntity> findByUsuarioEmail(String email);

    boolean existsByUsuario(UsuarioEntity usuario);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT c
        FROM ClienteEntity c
        WHERE c.id = :idCliente
    """)
    Optional<ClienteEntity> bloquearParaReserva(@Param("idCliente") Long idCliente);
}