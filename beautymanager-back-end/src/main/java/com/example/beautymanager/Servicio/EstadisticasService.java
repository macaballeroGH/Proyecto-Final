package com.example.beautymanager.Servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import com.example.beautymanager.Modelo.DTO.FiltroEstadisticasDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerEstadisticasDTO;

public interface EstadisticasService {

    BigDecimal obtenerIngresosTotales();

    BigDecimal obtenerIngresosPorPeriodo(LocalDate inicio, LocalDate fin);

    Long obtenerCantidadTurnos(LocalDate inicio, LocalDate fin);

    Long obtenerCantidadCompras(LocalDate inicio, LocalDate fin);

    Map<String, Long> obtenerServiciosMasSolicitados();

    Map<String, Long> obtenerProductosMasVendidos();

    ObtenerEstadisticasDTO obtenerEstadisticas(FiltroEstadisticasDTO filtro);
}
