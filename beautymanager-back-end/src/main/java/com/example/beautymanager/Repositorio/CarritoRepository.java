package com.example.beautymanager.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.CarritoEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoCarritoEnums;

public interface CarritoRepository extends JpaRepository<CarritoEntity, Long> {
    Optional<CarritoEntity> findByUsuarioAndEstado(UsuarioEntity usuario, EstadoCarritoEnums estado);
    boolean existsByUsuarioAndEstado(UsuarioEntity usuario, EstadoCarritoEnums estado);
}
