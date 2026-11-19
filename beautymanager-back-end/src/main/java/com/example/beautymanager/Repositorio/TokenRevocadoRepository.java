package com.example.beautymanager.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.TokenRevocadoEntity;

public interface TokenRevocadoRepository extends JpaRepository<TokenRevocadoEntity, Long> {

    boolean existsByJwtId(String jwtId);
}
