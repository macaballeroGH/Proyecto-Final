package com.example.beautymanager.Repositorio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.MovimientoFinanzasEntity;
import com.example.beautymanager.Modelo.Enums.EstadoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.TipoMovimientoEnums;

public interface MovimientoFinanzasRepository extends JpaRepository<MovimientoFinanzasEntity, Long> {

    List<MovimientoFinanzasEntity> findByFechaGasto(LocalDateTime fechaGasto);

    List<MovimientoFinanzasEntity> findByFechaGastoBetween(LocalDateTime inicio, LocalDateTime fin);

    List<MovimientoFinanzasEntity> findByFechaRegistroBetween(LocalDateTime inicio, LocalDateTime fin);

    List<MovimientoFinanzasEntity> findByTipoMovimiento(TipoMovimientoEnums tipoMovimiento);

    List<MovimientoFinanzasEntity> findByUsuarioId(Long idUsuario);

    List<MovimientoFinanzasEntity> findByUsuarioIdAndFechaGastoBetween(Long idUsuario, LocalDateTime inicio, LocalDateTime fin);

    List<MovimientoFinanzasEntity> findByTipoGastoId(Long idTipoGasto);

    List<MovimientoFinanzasEntity> findByTipoGastoIdAndFechaGastoBetween(Long idTipoGasto, LocalDateTime inicio, LocalDateTime fin);

    List<MovimientoFinanzasEntity> findByCompraId(Long idCompra);

    List<MovimientoFinanzasEntity> findByEstado(EstadoMovimientoEnums estado);

    List<MovimientoFinanzasEntity> findByDescripcionContainingIgnoreCase(String descripcion);

    List<MovimientoFinanzasEntity> findByTipoMovimientoAndFechaGastoBetween(TipoMovimientoEnums tipoMovimiento, LocalDateTime inicio, LocalDateTime fin);

    List<MovimientoFinanzasEntity> findByTipoMovimientoAndEstadoAndFechaGastoBetween(TipoMovimientoEnums tipoMovimiento, EstadoMovimientoEnums estado, LocalDateTime inicio, LocalDateTime fin);

    List<MovimientoFinanzasEntity> findByTipoMovimientoAndFechaGastoGreaterThanEqual(TipoMovimientoEnums tipoMovimiento, LocalDateTime fechaDesde);

    List<MovimientoFinanzasEntity> findByTipoMovimientoAndFechaGastoLessThanEqual(TipoMovimientoEnums tipoMovimiento, LocalDateTime fechaHasta);
}