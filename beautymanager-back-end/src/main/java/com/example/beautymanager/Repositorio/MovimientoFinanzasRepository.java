package com.example.beautymanager.Repositorio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.MovimientoFinanzasEntity;
import com.example.beautymanager.Modelo.Enums.EstadoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.TipoMovimientoEnums;

public interface MovimientoFinanzasRepository extends JpaRepository<MovimientoFinanzasEntity, Long> {
    List<MovimientoFinanzasEntity> findByFecha(LocalDateTime fecha);

    List<MovimientoFinanzasEntity> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<MovimientoFinanzasEntity> findByTipoMovimiento(TipoMovimientoEnums tipoMovimiento);

    List<MovimientoFinanzasEntity> findByUsuarioId(Long idUsuario);

    List<MovimientoFinanzasEntity> findByTipoGastoId(Long idTipoGasto);

    List<MovimientoFinanzasEntity> findByCompraId(Long idCompra);

    List<MovimientoFinanzasEntity> findByEstado(EstadoMovimientoEnums estado);

    List<MovimientoFinanzasEntity> findByDescripcionContainingIgnoreCase(String descripcion);

    List<MovimientoFinanzasEntity> findByTipoMovimientoAndFechaBetween(TipoMovimientoEnums tipoMovimiento, LocalDateTime inicio, LocalDateTime fin);

    List<MovimientoFinanzasEntity> findByTipoMovimientoAndEstadoAndFechaBetween(TipoMovimientoEnums tipoMovimiento, EstadoMovimientoEnums estado, LocalDateTime inicio, LocalDateTime fin);

    List<MovimientoFinanzasEntity> findByUsuarioIdAndFechaBetween(Long idUsuario, LocalDateTime inicio, LocalDateTime fin);

    List<MovimientoFinanzasEntity> findByTipoGastoIdAndFechaBetween(Long idTipoGasto, LocalDateTime inicio, LocalDateTime fin);
}
