package com.example.beautymanager.Repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.CarritoEntity;
import com.example.beautymanager.Modelo.Entidad.CarritoItemEntity;
import com.example.beautymanager.Modelo.Entidad.ProductoEntity;

public interface CarritoItemRepository extends JpaRepository<CarritoItemEntity, Long>{
    Optional<CarritoItemEntity> findByCarritoAndProducto(CarritoEntity carrito, ProductoEntity producto);

    List<CarritoItemEntity> findByCarrito(CarritoEntity carrito);
}
