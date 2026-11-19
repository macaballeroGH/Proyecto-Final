package com.example.beautymanager.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.beautymanager.Modelo.Entidad.BilleteraEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;

import jakarta.persistence.LockModeType;

public interface BilleteraRepository extends JpaRepository<BilleteraEntity, Long> {
    Optional<BilleteraEntity> findByUsuario(UsuarioEntity usuario);

    boolean existsByUsuario(UsuarioEntity usuario);

    //==========================
    //Obtener billetera bloqueada
    //==========================

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT b
        FROM BilleteraEntity b
        WHERE b.usuario.id = :idUsuario
    """)
    Optional<BilleteraEntity> buscarPorUsuarioConBloqueo(@Param("idUsuario") Long idUsuario);
}
