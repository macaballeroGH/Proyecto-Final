package com.example.beautymanager.Servicio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.beautymanager.Modelo.DTO.ActualizarMovimientoFinanzasDTO;
import com.example.beautymanager.Modelo.DTO.CrearMovimientoFinanzasDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerMovimientoFinanzasDTO;
import com.example.beautymanager.Modelo.Entidad.MovimientoFinanzasEntity;
import com.example.beautymanager.Modelo.Enums.EstadoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.TipoMovimientoEnums;

public interface MovimientoFinanzasService {

    ObtenerMovimientoFinanzasDTO toMap(MovimientoFinanzasEntity movimiento);

    ObtenerMovimientoFinanzasDTO crearMovimiento(CrearMovimientoFinanzasDTO crearMovimiento);

    ObtenerMovimientoFinanzasDTO actualizarMovimiento(Long id, ActualizarMovimientoFinanzasDTO actualizarMovimiento);

    ObtenerMovimientoFinanzasDTO buscarPorId(Long id);

    List<ObtenerMovimientoFinanzasDTO> listarTodos();

    void eliminarMovimiento(Long id);

    List<ObtenerMovimientoFinanzasDTO> buscarPorFecha(LocalDateTime fecha);

    List<ObtenerMovimientoFinanzasDTO> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);

    List<ObtenerMovimientoFinanzasDTO> buscarPorTipoMovimiento(TipoMovimientoEnums tipoMovimiento);

    List<ObtenerMovimientoFinanzasDTO> buscarPorUsuario(Long idUsuario);

    List<ObtenerMovimientoFinanzasDTO> buscarPorTipoGasto(Long idTipoGasto);

    List<ObtenerMovimientoFinanzasDTO> buscarPorCompra(Long idCompra);

    List<ObtenerMovimientoFinanzasDTO> buscarPorEstado(EstadoMovimientoEnums estado);

    List<ObtenerMovimientoFinanzasDTO> buscarPorDescripcion(String descripcion);

    BigDecimal totalIngresos(LocalDateTime inicio, LocalDateTime fin);

    BigDecimal totalEgresos(LocalDateTime inicio, LocalDateTime fin);

    BigDecimal balance(LocalDateTime inicio, LocalDateTime fin);
}
