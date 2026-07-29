package com.example.beautymanager.Repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.TipoGastoEntity;

public interface TipoGastoRepository extends JpaRepository<TipoGastoEntity, Long> {
    Optional<TipoGastoEntity> findByNombreGasto(String nombreGasto);

    boolean existsByNombreGasto(String nombreGasto);

    boolean existsByNombreGastoIgnoreCase(String nombreGasto);

    List<TipoGastoEntity> findByNombreGastoContainingIgnoreCase(String nombreGasto);

    List<TipoGastoEntity> findByDescripcionContainingIgnoreCase(String descripcion);

    List<TipoGastoEntity> findAllByOrderByNombreGastoAsc();
}
