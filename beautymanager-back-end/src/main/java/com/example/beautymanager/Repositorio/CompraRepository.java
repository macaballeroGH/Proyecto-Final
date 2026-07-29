package com.example.beautymanager.Repositorio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.ClienteEntity;
import com.example.beautymanager.Modelo.Entidad.CompraEntity;
import com.example.beautymanager.Modelo.Enums.EstadoCompraEnums;

public interface CompraRepository extends JpaRepository<CompraEntity, Long> {
    List<CompraEntity> findByCliente(ClienteEntity cliente);

    List<CompraEntity> findByEstadoCompra(EstadoCompraEnums estadoCompra);

    List<CompraEntity> findByFecha(LocalDateTime fecha);

    List<CompraEntity> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<CompraEntity> findByClienteAndFechaBetween(ClienteEntity cliente, LocalDateTime inicio, LocalDateTime fin);

    Long countByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}
