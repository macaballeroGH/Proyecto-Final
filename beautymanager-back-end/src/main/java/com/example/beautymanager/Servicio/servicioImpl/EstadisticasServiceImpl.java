package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ComparacionEstadisticaDTO;
import com.example.beautymanager.Modelo.DTO.ComparacionRankingDTO;
import com.example.beautymanager.Modelo.DTO.DatoEstadisticaDTO;
import com.example.beautymanager.Modelo.DTO.FiltroEstadisticasDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerEstadisticasDTO;
import com.example.beautymanager.Modelo.DTO.RankingEstadisticasDTO;
import com.example.beautymanager.Modelo.Entidad.MovimientoFinanzasEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoEntity;
import com.example.beautymanager.Modelo.Enums.EstadoCompraEnums;
import com.example.beautymanager.Modelo.Enums.EstadoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoTurnoEnums;
import com.example.beautymanager.Modelo.Enums.TipoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.TipoPeriodoEstadisticaEnums;
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
    // Ingresos totales
    //=======================
    @Override
    public BigDecimal obtenerIngresosTotales() {

        List<MovimientoFinanzasEntity> movimientos = movimientoFinanzasRepository.findAll();

        BigDecimal total = BigDecimal.ZERO;

        for (MovimientoFinanzasEntity movimiento : movimientos) {

            if (movimiento.getMonto() != null) {
                total = total.add(movimiento.getMonto());
            }
        }

        return total;
    }

    //=======================
    // Ingresos por periodo
    //=======================
    @Override
    public BigDecimal obtenerIngresosPorPeriodo(LocalDate inicio, LocalDate fin) {

        LocalDateTime fechaInicio = inicio.atStartOfDay();

        LocalDateTime fechaFin = fin.atTime(LocalTime.MAX);

        List<MovimientoFinanzasEntity> movimientos = movimientoFinanzasRepository.findByFechaBetween(fechaInicio, fechaFin);

        BigDecimal total = BigDecimal.ZERO;

        for (MovimientoFinanzasEntity movimiento : movimientos) {

            if (movimiento.getMonto() != null) {
                total = total.add(movimiento.getMonto());
            }
        }

        return total;
    }

    //=======================
    // Cantidad turnos
    //=======================
    @Override
    public Long obtenerCantidadTurnos(LocalDate inicio, LocalDate fin) {

        LocalDateTime fechaInicio = inicio.atStartOfDay();

        LocalDateTime fechaFin = fin.atTime(LocalTime.MAX);

        List<TurnoEntity> turnos = turnoRepository.findByFechaHoraInicioBetween(fechaInicio, fechaFin);

        return (long) turnos.size();
    }

    //=======================
    // Cantidad compras
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
    // Servicios mas solicitados
    //===============================
    @Override
    public Map<String, Long> obtenerServiciosMasSolicitados() {

        Map<String, Long> resultado = new HashMap<>();

        for (Object[] fila : turnoServicioRepository.contarServiciosPorNombre()) {

            String nombreServicio = (String) fila[0];

            Long cantidad = fila[1] != null ? ((Number) fila[1]).longValue() : 0L;

            resultado.put(nombreServicio, cantidad);
        }

        return resultado;
    }

    //==========================
    // Productos mas vendidos
    //==========================
    @Override
    public Map<String, Long> obtenerProductosMasVendidos() {

        Map<String, Long> resultado = new HashMap<>();

        for (Object[] fila : detalleCompraRepository.contarProductosVendidos()) {

            String nombreProducto = (String) fila[0];

            Long cantidad = fila[1] != null ? ((Number) fila[1]).longValue() : 0L;

            resultado.put(nombreProducto, cantidad);
        }

        return resultado;
    }

    //==========================
    // Obtener estadísticas
    //==========================
    @Override
    public ObtenerEstadisticasDTO obtenerEstadisticas(FiltroEstadisticasDTO filtro) {

        validarFiltro(filtro);

        LocalDate inicio = obtenerInicioPeriodo(filtro);

        LocalDate fin = obtenerFinPeriodo(filtro);

        ObtenerEstadisticasDTO resultado = new ObtenerEstadisticasDTO();

        boolean agruparPorMes = debeAgruparPorMes(inicio, fin, filtro.getTipoPeriodo());

        resultado.setIngresos(obtenerIngresosAgrupados(inicio, fin, agruparPorMes));

        resultado.setTurnos(obtenerTurnosAgrupados(inicio, fin, agruparPorMes));

        resultado.setServiciosMasSolicitados(obtenerServiciosPorPeriodo(inicio, fin));

        resultado.setProductosMasVendidos(obtenerProductosPorPeriodo(inicio, fin));

        if (Boolean.TRUE.equals(filtro.getComparar())) {

            LocalDate inicioAnterior = obtenerInicioPeriodoAnterior(inicio, fin, filtro.getTipoPeriodo());

            LocalDate finAnterior = obtenerFinPeriodoAnterior(inicio, fin, filtro.getTipoPeriodo());

            resultado.setComparacionIngresos(compararIngresos(inicio, fin, inicioAnterior, finAnterior));

            resultado.setComparacionTurno(compararTurnos(inicio, fin, inicioAnterior, finAnterior));

            resultado.setComparacionServicios(compararServicios(inicio, fin, inicioAnterior, finAnterior));

            resultado.setComparacionProductos(compararProductos(inicio, fin, inicioAnterior, finAnterior));
        }

        return resultado;
    }

    //==========================
    // Validar filtro
    //==========================

    private void validarFiltro(
            FiltroEstadisticasDTO filtro) {

        if (filtro == null) {
            throw new BusinessException("El filtro de estadísticas es obligatorio");
        }

        if (filtro.getFechaInicio() == null || filtro.getFechaFin() == null) {
            throw new BusinessException("Las fechas de inicio y fin son obligatorias");
        }

        if (filtro.getFechaInicio().isAfter(filtro.getFechaFin())) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        if (filtro.getTipoPeriodo() == null) {
            throw new BusinessException("El tipo de periodo es obligatorio");
        }

        if (filtro.getComparar() == null) {
            filtro.setComparar(false);
        }
    }


    //==========================
    // Inicio del periodo
    //==========================

    private LocalDate obtenerInicioPeriodo(FiltroEstadisticasDTO filtro) {

        LocalDate fecha = filtro.getFechaInicio();

        switch (filtro.getTipoPeriodo()) {

            case SEMANA:

                return fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

            case MES:

                return fecha.withDayOfMonth(1);

            case ANIO:

                return fecha.withDayOfYear(1);

            case PERSONALIZADO:

                return fecha;

            default:

                return fecha;
        }
    }

    //==========================
    // Fin del periodo
    //==========================

    private LocalDate obtenerFinPeriodo(FiltroEstadisticasDTO filtro) {

        LocalDate fecha = filtro.getFechaInicio();

        switch (filtro.getTipoPeriodo()) {

            case SEMANA:

                return fecha.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            case MES:

                return fecha.withDayOfMonth(fecha.lengthOfMonth());

            case ANIO:

                return fecha.withDayOfYear(fecha.lengthOfYear());

            case PERSONALIZADO:

                return filtro.getFechaFin();

            default:

                return filtro.getFechaFin();
        }
    }

    //==========================
    // Determinar agrupación
    //==========================

    private boolean debeAgruparPorMes(LocalDate inicio, LocalDate fin, TipoPeriodoEstadisticaEnums tipoPeriodo) {

        if (tipoPeriodo == TipoPeriodoEstadisticaEnums.ANIO) {
            return true;
        }

        if (tipoPeriodo == TipoPeriodoEstadisticaEnums.PERSONALIZADO) {

            long cantidadDias = ChronoUnit.DAYS.between(inicio, fin) + 1;

            return cantidadDias > 31;
        }

        return false;
    }

    //==========================
    // Ingresos agrupados
    //==========================

    private List<DatoEstadisticaDTO> obtenerIngresosAgrupados(LocalDate inicio, LocalDate fin, boolean agruparPorMes) {

        LocalDateTime fechaInicio = inicio.atStartOfDay();

        LocalDateTime fechaFin = fin.atTime(LocalTime.MAX);

        List<MovimientoFinanzasEntity> movimientos = movimientoFinanzasRepository.findByTipoMovimientoAndEstadoAndFechaBetween(TipoMovimientoEnums.INGRESO, EstadoMovimientoEnums.CONFIRMADO, fechaInicio, fechaFin);

        Map<String, BigDecimal> agrupados = inicializarPeriodos(inicio, fin, agruparPorMes);

        for (MovimientoFinanzasEntity movimiento : movimientos) {

            if (movimiento.getFecha() == null || movimiento.getMonto() == null) {
                continue;
            }

            String clave = obtenerClavePeriodo(movimiento.getFecha().toLocalDate(), agruparPorMes);

            if (agrupados.containsKey(clave)) {

                BigDecimal valorActual = agrupados.get(clave);

                agrupados.put(clave, valorActual.add(movimiento.getMonto()));
            }
        }

        List<DatoEstadisticaDTO> resultado = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entrada : agrupados.entrySet()) {

            DatoEstadisticaDTO dato = new DatoEstadisticaDTO();

            dato.setPeriodo(entrada.getKey());

            dato.setValor(entrada.getValue());

            resultado.add(dato);
        }

        return resultado;
    }

    //==========================
    // Turnos agrupados
    //==========================

    private List<DatoEstadisticaDTO> obtenerTurnosAgrupados(LocalDate inicio, LocalDate fin, boolean agruparPorMes) {

        LocalDateTime fechaInicio = inicio.atStartOfDay();

        LocalDateTime fechaFin = fin.atTime(LocalTime.MAX);

        List<TurnoEntity> turnos = turnoRepository.findByEstadoTurnoAndFechaHoraInicioBetween(EstadoTurnoEnums.FINALIZADO, fechaInicio, fechaFin);

        Map<String, BigDecimal> agrupados = inicializarPeriodos(inicio, fin, agruparPorMes);

        for (TurnoEntity turno : turnos) {

            if (turno.getFechaHoraInicio() == null) {
                continue;
            }

            String clave = obtenerClavePeriodo(turno.getFechaHoraInicio().toLocalDate(), agruparPorMes);

            if (agrupados.containsKey(clave)) {

                BigDecimal valorActual = agrupados.get(clave);

                agrupados.put(clave, valorActual.add(BigDecimal.ONE));
            }
        }

        List<DatoEstadisticaDTO> resultado = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entrada : agrupados.entrySet()) {

            DatoEstadisticaDTO dato = new DatoEstadisticaDTO();

            dato.setPeriodo(entrada.getKey());

            dato.setValor(entrada.getValue());

            resultado.add(dato);
        }

        return resultado;
    }

    //==========================
    // Inicializar periodos
    //==========================

    private Map<String, BigDecimal> inicializarPeriodos(LocalDate inicio, LocalDate fin, boolean agruparPorMes) {

        Map<String, BigDecimal> resultado = new LinkedHashMap<>();

        if (agruparPorMes) {

            LocalDate periodo = inicio.withDayOfMonth(1);

            LocalDate ultimoMes = fin.withDayOfMonth(1);

            while (!periodo.isAfter(ultimoMes)) {

                resultado.put(obtenerClavePeriodo(periodo, true), BigDecimal.ZERO);

                periodo = periodo.plusMonths(1);
            }

        } else {

            LocalDate periodo = inicio;

            while (!periodo.isAfter(fin)) {

                resultado.put(obtenerClavePeriodo(periodo, false), BigDecimal.ZERO);

                periodo = periodo.plusDays(1);
            }
        }

        return resultado;
    }

    //==========================
    // Clave del periodo
    //==========================

    private String obtenerClavePeriodo(LocalDate fecha, boolean agruparPorMes) {

        if (agruparPorMes) {

            return fecha.withDayOfMonth(1).toString();
        }

        return fecha.toString();
    }

    //==========================
    // Servicios por periodo
    //==========================

    private List<RankingEstadisticasDTO> obtenerServiciosPorPeriodo(LocalDate inicio, LocalDate fin) {

        LocalDateTime fechaInicio = inicio.atStartOfDay();

        LocalDateTime fechaFin = fin.atTime(LocalTime.MAX);

        List<RankingEstadisticasDTO> resultado = new ArrayList<>();

        for (Object[] fila : turnoServicioRepository.contarServiciosPorNombreYPeriodo(EstadoTurnoEnums.FINALIZADO, fechaInicio, fechaFin)) {

            RankingEstadisticasDTO ranking = new RankingEstadisticasDTO();

            ranking.setNombre((String) fila[0]);

            ranking.setCantidad(fila[1] != null ? ((Number) fila[1]).longValue() : 0L);

            resultado.add(ranking);
        }

        return resultado;
    }

    //==========================
    // Productos por periodo
    //==========================

    private List<RankingEstadisticasDTO> obtenerProductosPorPeriodo(LocalDate inicio, LocalDate fin) {

        LocalDateTime fechaInicio = inicio.atStartOfDay();

        LocalDateTime fechaFin = fin.atTime(LocalTime.MAX);

        List<RankingEstadisticasDTO> resultado = new ArrayList<>();

        for (Object[] fila : detalleCompraRepository.contarProductosVendidosYPeriodo(EstadoCompraEnums.PAGADA, fechaInicio, fechaFin)) {

            RankingEstadisticasDTO ranking = new RankingEstadisticasDTO();

            ranking.setNombre((String) fila[0]);

            ranking.setCantidad(fila[1] != null ? ((Number) fila[1]).longValue() : 0L);

            resultado.add(ranking);
        }

        return resultado;
    }

    //==========================
    // Inicio periodo anterior
    //==========================

    private LocalDate obtenerInicioPeriodoAnterior(LocalDate inicio, LocalDate fin, TipoPeriodoEstadisticaEnums tipoPeriodo) {

        switch (tipoPeriodo) {

            case SEMANA:

                return inicio.minusWeeks(1);

            case MES:

                return inicio.minusMonths(1);

            case ANIO:

                return inicio.minusYears(1);

            case PERSONALIZADO:

                long cantidadDias = ChronoUnit.DAYS.between(inicio, fin) + 1;

                return inicio.minusDays(cantidadDias);

            default:

                return inicio;
        }
    }

    //==========================
    // Fin periodo anterior
    //==========================

    private LocalDate obtenerFinPeriodoAnterior(
            LocalDate inicio,
            LocalDate fin,
            TipoPeriodoEstadisticaEnums tipoPeriodo) {

        switch (tipoPeriodo) {

            case SEMANA:

                return fin.minusWeeks(1);

            case MES:

                return inicio.minusDays(1);

            case ANIO:

                return inicio.minusDays(1);

            case PERSONALIZADO:

                return inicio.minusDays(1);

            default:

                return fin;
        }
    }

    //==========================
    // Comparar ingresos
    //==========================

    private ComparacionEstadisticaDTO compararIngresos(LocalDate inicio, LocalDate fin, LocalDate inicioAnterior, LocalDate finAnterior) {

        BigDecimal actual = obtenerIngresosConfirmados(inicio, fin);

        BigDecimal anterior = obtenerIngresosConfirmados(inicioAnterior, finAnterior);

        return crearComparacion(actual, anterior);
    }

    //==========================
    // Comparar turnos
    //==========================

    private ComparacionEstadisticaDTO compararTurnos(LocalDate inicio, LocalDate fin, LocalDate inicioAnterior, LocalDate finAnterior) {

        Long actual = obtenerTurnosFinalizados(inicio, fin);

        Long anterior = obtenerTurnosFinalizados(inicioAnterior, finAnterior);

        return crearComparacion(BigDecimal.valueOf(actual), BigDecimal.valueOf(anterior));
    }

    //==========================
    // Ingresos confirmados
    //==========================

    private BigDecimal obtenerIngresosConfirmados(LocalDate inicio, LocalDate fin) {

        List<MovimientoFinanzasEntity> movimientos = movimientoFinanzasRepository.findByTipoMovimientoAndEstadoAndFechaBetween(TipoMovimientoEnums.INGRESO, EstadoMovimientoEnums.CONFIRMADO, inicio.atStartOfDay(), fin.atTime(LocalTime.MAX));

        BigDecimal total = BigDecimal.ZERO;

        for (MovimientoFinanzasEntity movimiento : movimientos) {

            if (movimiento.getMonto() != null) {

                total = total.add(movimiento.getMonto());
            }
        }

        return total;
    }

    //==========================
    // Turnos finalizados
    //==========================

    private Long obtenerTurnosFinalizados(LocalDate inicio, LocalDate fin) {

        List<TurnoEntity> turnos = turnoRepository.findByEstadoTurnoAndFechaHoraInicioBetween(EstadoTurnoEnums.FINALIZADO, inicio.atStartOfDay(), fin.atTime(LocalTime.MAX));

        return (long) turnos.size();
    }

    //==========================
    // Crear comparación
    //==========================

    private ComparacionEstadisticaDTO crearComparacion(BigDecimal actual, BigDecimal anterior) {

        ComparacionEstadisticaDTO resultado = new ComparacionEstadisticaDTO();

        resultado.setValorActual(actual);

        resultado.setValorAnterior(anterior);

        resultado.setVariacion(calcularVariacion(actual, anterior));

        return resultado;
    }

    //==========================
    // Calcular variación
    //==========================

    private BigDecimal calcularVariacion(BigDecimal actual, BigDecimal anterior) {

        if (anterior.compareTo(BigDecimal.ZERO) == 0 && actual.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        if (anterior.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return actual.subtract(anterior).multiply(BigDecimal.valueOf(100)).divide(anterior, 2, RoundingMode.HALF_UP);
    }

    //==========================
    // Comparar servicios
    //==========================

    private List<ComparacionRankingDTO> compararServicios(LocalDate inicio, LocalDate fin, LocalDate inicioAnterior, LocalDate finAnterior) {

        List<RankingEstadisticasDTO> actuales = obtenerServiciosPorPeriodo(inicio, fin);

        List<RankingEstadisticasDTO> anteriores = obtenerServiciosPorPeriodo(inicioAnterior, finAnterior);

        return crearComparacionRanking(actuales, anteriores);
    }

    //==========================
    // Comparar productos
    //==========================

    private List<ComparacionRankingDTO> compararProductos(LocalDate inicio, LocalDate fin, LocalDate inicioAnterior, LocalDate finAnterior) {

        List<RankingEstadisticasDTO> actuales = obtenerProductosPorPeriodo(inicio, fin);

        List<RankingEstadisticasDTO> anteriores = obtenerProductosPorPeriodo(inicioAnterior, finAnterior);

        return crearComparacionRanking(actuales, anteriores);
    }

    //==========================
    // Comparación de rankings
    //==========================

    private List<ComparacionRankingDTO> crearComparacionRanking(List<RankingEstadisticasDTO> actuales, List<RankingEstadisticasDTO> anteriores) {

        Map<String, Long> mapaActual = new LinkedHashMap<>();

        Map<String, Long> mapaAnterior = new LinkedHashMap<>();

        for (RankingEstadisticasDTO dato : actuales) {

            mapaActual.put(dato.getNombre(), dato.getCantidad());
        }

        for (RankingEstadisticasDTO dato : anteriores) {

            mapaAnterior.put(dato.getNombre(), dato.getCantidad());
        }

        Map<String, Long> nombres = new LinkedHashMap<>();

        for (String nombre : mapaActual.keySet()) {

            nombres.put(nombre, 0L);
        }

        for (String nombre : mapaAnterior.keySet()) {

            nombres.putIfAbsent(nombre, 0L);
        }

        List<ComparacionRankingDTO> resultado = new ArrayList<>();

        for (String nombre : nombres.keySet()) {

            Long cantidadActual = mapaActual.getOrDefault(nombre, 0L);

            Long cantidadAnterior = mapaAnterior.getOrDefault(nombre, 0L);

            ComparacionRankingDTO comparacion = new ComparacionRankingDTO();

            comparacion.setNombre(nombre);

            comparacion.setCantidadActual(cantidadActual);

            comparacion.setCantidadAnterior(cantidadAnterior);

            comparacion.setVariacion(calcularVariacion(BigDecimal.valueOf(cantidadActual), BigDecimal.valueOf(cantidadAnterior)));

            resultado.add(comparacion);
        }
        return resultado;
    }
}