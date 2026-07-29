package com.example.beautymanager.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.BilleteraEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;

public interface BilleteraRepository extends JpaRepository<BilleteraEntity, Long> {
    Optional<BilleteraEntity> findByUsuario(UsuarioEntity usuario);

    boolean existsByUsuario(UsuarioEntity usuario);
}
