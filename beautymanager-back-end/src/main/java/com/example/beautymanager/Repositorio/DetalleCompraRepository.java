package com.example.beautymanager.Repositorio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.beautymanager.Modelo.Entidad.CompraEntity;
import com.example.beautymanager.Modelo.Entidad.DetalleCompraEntity;
import com.example.beautymanager.Modelo.Entidad.ProductoEntity;
import com.example.beautymanager.Modelo.Enums.EstadoCompraEnums;

public interface DetalleCompraRepository extends JpaRepository<DetalleCompraEntity, Long> {
    List<DetalleCompraEntity> findByCompra(CompraEntity compra);

    List<DetalleCompraEntity> findByProducto(ProductoEntity producto);

    boolean existsByProducto(ProductoEntity producto);

    @Query("SELECT d.producto.nombre, SUM(d.cantidad) FROM DetalleCompraEntity d GROUP BY d.producto.nombre")
    List<Object[]> contarProductosVendidos();

    @Query("""
            SELECT d.producto.nombre, SUM(d.cantidad) FROM DetalleCompraEntity d WHERE d.compra.estadoCompra = :estado AND d.compra.fecha BETWEEN :inicio AND :fin GROUP BY d.producto.nombre ORDER BY SUM(d.cantidad) DESC
            """)
    List<Object[]> contarProductosVendidosYPeriodo(@Param("estado") EstadoCompraEnums estado, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
