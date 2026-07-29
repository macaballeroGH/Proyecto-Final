package com.example.beautymanager.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.RolEntity;
import com.example.beautymanager.Modelo.Enums.RolesEnums;

public interface RolRepository extends JpaRepository<RolEntity, Long>{

    boolean existsByNombre(RolesEnums nombre);

    Optional<RolEntity> findByNombre(RolesEnums nombre);

}
