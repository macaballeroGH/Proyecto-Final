package com.example.beautymanager.Repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.TipoGastoEntity;
import com.example.beautymanager.Modelo.Enums.TipoGastoEnums;

public interface TipoGastoRepository extends JpaRepository<TipoGastoEntity, Long> {
    Optional<TipoGastoEntity> findByNombreGasto(TipoGastoEnums nombreGasto);

    boolean existsByNombreGasto(TipoGastoEnums nombreGasto);

    List<TipoGastoEntity> findByDescripcionContainingIgnoreCase(String descripcion);

    List<TipoGastoEntity> findAllByOrderByNombreGastoAsc();
}
