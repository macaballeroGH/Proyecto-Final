package com.example.beautymanager.Repositorio;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.beautymanager.Modelo.Entidad.ProductoEntity;
import com.example.beautymanager.Modelo.Enums.EstadoProductoEnums;

import jakarta.persistence.LockModeType;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    List<ProductoEntity> findByNombre(String nombre);

    List<ProductoEntity> findByNombreContainingIgnoreCase(String nombre);

    List<ProductoEntity> findByDescripcionContainingIgnoreCase(String descripcion);

    List<ProductoEntity> findByPrecioBetween(BigDecimal min, BigDecimal max);

    List<ProductoEntity> findByStockGreaterThan(Integer stock);

    List<ProductoEntity> findByEstadoProducto(EstadoProductoEnums estadoProducto);

    List<ProductoEntity> findByEstadoProductoAndStockGreaterThan(EstadoProductoEnums estadoProducto, Integer stock);

    boolean existsByNombreIgnoreCase(String nombre);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductoEntity p WHERE p.id = :idProducto")
    Optional<ProductoEntity> buscarPorIdConBloqueo(
        @Param("idProducto") Long idProducto
    );
}
