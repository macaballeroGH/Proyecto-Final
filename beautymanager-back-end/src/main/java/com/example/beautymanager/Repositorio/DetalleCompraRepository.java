package com.example.beautymanager.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.beautymanager.Modelo.Entidad.CompraEntity;
import com.example.beautymanager.Modelo.Entidad.DetalleCompraEntity;
import com.example.beautymanager.Modelo.Entidad.ProductoEntity;

public interface DetalleCompraRepository extends JpaRepository<DetalleCompraEntity, Long> {
    List<DetalleCompraEntity> findByCompra(CompraEntity compra);

    List<DetalleCompraEntity> findByProducto(ProductoEntity producto);

    boolean existsByProducto(ProductoEntity producto);

    @Query("SELECT d.producto.nombre, SUM(d.cantidad) FROM DetalleCompraEntity d GROUP BY d.producto.nombre")
    List<Object[]> contarProductosVendidos();
}
