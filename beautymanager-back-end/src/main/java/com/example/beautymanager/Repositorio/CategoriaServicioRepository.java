package com.example.beautymanager.Repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.CategoriaServicioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoCategoriaServicioEnums;

public interface CategoriaServicioRepository extends JpaRepository<CategoriaServicioEntity, Long>{

    Optional<CategoriaServicioEntity> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    List<CategoriaServicioEntity> findByEstado(EstadoCategoriaServicioEnums estado);

    List<CategoriaServicioEntity> findByNombreContainingIgnoreCase(String nombre);

    List<CategoriaServicioEntity> findByDescripcionContainingIgnoreCase(String descripcion);
}
