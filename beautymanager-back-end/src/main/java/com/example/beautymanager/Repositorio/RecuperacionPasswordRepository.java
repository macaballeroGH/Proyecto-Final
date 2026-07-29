package com.example.beautymanager.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.RecuperacionPasswordEntity;

public interface RecuperacionPasswordRepository extends JpaRepository<RecuperacionPasswordEntity, Long> {

    Optional<RecuperacionPasswordEntity> findByToken(String token);
}
