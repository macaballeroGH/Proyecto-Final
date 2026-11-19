package com.example.beautymanager.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.ReservaEntity;

public interface ReservaRepository extends JpaRepository<ReservaEntity, Long>{

    Optional<ReservaEntity> findByClaveIdempotencia(String claveIdempotencia);
}
