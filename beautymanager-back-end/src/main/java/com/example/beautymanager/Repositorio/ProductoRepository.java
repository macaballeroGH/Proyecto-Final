package com.example.beautymanager.Repositorio;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.ProductoEntity;
import com.example.beautymanager.Modelo.Enums.EstadoProductoEnums;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    List<ProductoEntity> findByNombre(String nombre);

    List<ProductoEntity> findByNombreContainingIgnoreCase(String nombre);

    List<ProductoEntity> findByDescripcionContainingIgnoreCase(String descripcion);

    List<ProductoEntity> findByPrecioBetween(BigDecimal min, BigDecimal max);

    List<ProductoEntity> findByStockGreaterThan(Integer stock);

    List<ProductoEntity> findByEstadoProducto(EstadoProductoEnums estadoProducto);

    List<ProductoEntity> findByEstadoProductoAndStockGreaterThan(EstadoProductoEnums estadoProducto, Integer stock);

    boolean existsByNombreIgnoreCase(String nombre);
}
