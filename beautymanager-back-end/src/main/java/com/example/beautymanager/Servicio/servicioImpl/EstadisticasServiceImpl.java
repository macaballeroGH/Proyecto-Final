package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.Entidad.MovimientoFinanzasEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoEntity;
import com.example.beautymanager.Repositorio.CompraRepository;
import com.example.beautymanager.Repositorio.DetalleCompraRepository;
import com.example.beautymanager.Repositorio.MovimientoFinanzasRepository;
import com.example.beautymanager.Repositorio.TurnoRepository;
import com.example.beautymanager.Repositorio.TurnoServicioRepository;
import com.example.beautymanager.Servicio.EstadisticasService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class EstadisticasServiceImpl implements EstadisticasService {

    @Autowired
    private MovimientoFinanzasRepository movimientoFinanzasRepository;

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private TurnoServicioRepository turnoServicioRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private DetalleCompraRepository detalleCompraRepository;

    //=======================
    //Ingresos totales
    //=======================
    @Override
    public BigDecimal obtenerIngresosTotales() {

        List<MovimientoFinanzasEntity> movimientos = movimientoFinanzasRepository.findAll();

        BigDecimal total = BigDecimal.ZERO;

        for(MovimientoFinanzasEntity movimiento : movimientos) {

            if(movimiento.getMonto() != null) {
                total = total.add(movimiento.getMonto());
            }
        }

        return total;
    }

    //=======================
    //Ingresos por periodo
    //=======================
    @Override
    public BigDecimal obtenerIngresosPorPeriodo(LocalDate inicio, LocalDate fin) {

        LocalDateTime fechaInicio = inicio.atStartOfDay();
        LocalDateTime fechaFin = fin.atTime(LocalTime.MAX);

        List<MovimientoFinanzasEntity> movimientos = movimientoFinanzasRepository.findByFechaBetween(fechaInicio, fechaFin);

        BigDecimal total = BigDecimal.ZERO;

        for(MovimientoFinanzasEntity movimiento : movimientos) {

            if(movimiento.getMonto() != null) {
                total = total.add(movimiento.getMonto());
            }
        }
        return total;
    }

    //=======================
    //Cantidad turnos
    //=======================
    @Override
    public Long obtenerCantidadTurnos(LocalDate inicio, LocalDate fin) {

        LocalDateTime fechaInicio = inicio.atStartOfDay();
        LocalDateTime fechaFin = fin.atTime(LocalTime.MAX);

        List<TurnoEntity> turnos = turnoRepository.findByFechaHoraInicioBetween(fechaInicio, fechaFin);

        return (long) turnos.size();
    }

    //=======================
    //Cantidad compras
    //=======================
    @Override
    public Long obtenerCantidadCompras(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) {
            throw new BusinessException("Fechas de inicio y fin son obligatorias");
        }

        if (inicio.isAfter(fin)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        LocalDateTime fechaInicio = inicio.atStartOfDay();
        LocalDateTime fechaFin = fin.atTime(23, 59, 59);

        return compraRepository.countByFechaBetween(fechaInicio, fechaFin);
    }

    //===============================
    //Servicios mas solicitados
    //===============================
    @Override
    public Map<String, Long> obtenerServiciosMasSolicitados() {

        Map<String, Long> resultado = new HashMap<>();

        for (Object[] fila : turnoServicioRepository.contarServiciosPorNombre()) {
            String nombreServicio = (String) fila[0];
            Long cantidad = (Long) fila[1];
            resultado.put(nombreServicio, cantidad != null ? cantidad : 0L);
        }

        return resultado;
    }

    //==========================
    //Productos mas vendidos
    //==========================
    @Override
    public Map<String, Long> obtenerProductosMasVendidos() {
        
        Map<String, Long> resultado = new HashMap<>();

        for (Object[] fila : detalleCompraRepository.contarProductosVendidos()) {
            String nombreProducto = (String) fila[0];
            Long cantidad = (Long) fila[1];
            resultado.put(nombreProducto, cantidad != null ? cantidad : 0L);
        }
        return resultado;
    }

}
