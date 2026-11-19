package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ActualizarMovimientoFinanzasDTO;
import com.example.beautymanager.Modelo.DTO.CrearGastoDTO;
import com.example.beautymanager.Modelo.DTO.CrearMovimientoFinanzasDTO;
import com.example.beautymanager.Modelo.DTO.DebitarSaldoDTO;
import com.example.beautymanager.Modelo.DTO.FiltroGastosDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerMovimientoFinanzasDTO;
import com.example.beautymanager.Modelo.Entidad.CompraEntity;
import com.example.beautymanager.Modelo.Entidad.MovimientoFinanzasEntity;
import com.example.beautymanager.Modelo.Entidad.TipoGastoEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.MetodoPagoEnums;
import com.example.beautymanager.Modelo.Enums.TipoGastoEnums;
import com.example.beautymanager.Modelo.Enums.TipoMovimientoEnums;
import com.example.beautymanager.Repositorio.CompraRepository;
import com.example.beautymanager.Repositorio.MovimientoFinanzasRepository;
import com.example.beautymanager.Repositorio.TipoGastoRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.BilleteraService;
import com.example.beautymanager.Servicio.MovimientoFinanzasService;
import com.example.beautymanager.exception.BusinessException;

import jakarta.transaction.Transactional;

@Service
public class MovimientoFinanzasServiceImpl implements MovimientoFinanzasService {

    private final MovimientoFinanzasRepository movimientoFinanzasRepository;
    private final UsuarioRepository usuarioRepository;
    private final CompraRepository compraRepository;
    private final TipoGastoRepository tipoGastoRepository;
    private final BilleteraService billeteraService;

    public MovimientoFinanzasServiceImpl(MovimientoFinanzasRepository movimientoFinanzasRepository, UsuarioRepository usuarioRepository, CompraRepository compraRepository, TipoGastoRepository tipoGastoRepository, BilleteraService billeteraService) {

        this.movimientoFinanzasRepository = movimientoFinanzasRepository;

        this.usuarioRepository = usuarioRepository;

        this.compraRepository = compraRepository;

        this.tipoGastoRepository = tipoGastoRepository;

        this.billeteraService = billeteraService;
    }

    //====================
    //Crear movimiento
    //====================
    @Override
    @Transactional
    public ObtenerMovimientoFinanzasDTO crearMovimiento(CrearMovimientoFinanzasDTO crearMovimiento) {

        if (crearMovimiento == null) {
            throw new BusinessException("Los datos son obligatorios");
        }

        if (crearMovimiento.getIdUsuario() == null) {
            throw new BusinessException("El usuario es obligatorio");
        }

        UsuarioEntity usuario = usuarioRepository.findById(crearMovimiento.getIdUsuario())
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        MovimientoFinanzasEntity movimiento = new MovimientoFinanzasEntity();

        movimiento.setFechaGasto(crearMovimiento.getFechaGasto());
        movimiento.setMonto(crearMovimiento.getMonto());
        movimiento.setTipoMovimiento(crearMovimiento.getTipoMovimiento());
        movimiento.setDescripcion(crearMovimiento.getDescripcion());
        movimiento.setEstado(crearMovimiento.getEstado());
        movimiento.setMetodoPago(crearMovimiento.getMetodoPago());
        movimiento.setUsuario(usuario);

        if (crearMovimiento.getNombreGasto() != null) {
            movimiento.setTipoGasto(buscarTipoGasto(crearMovimiento.getNombreGasto()));
        }

        if (crearMovimiento.getIdCompra() != null) {
            CompraEntity compra = compraRepository.findById(crearMovimiento.getIdCompra())
                .orElseThrow(() -> new BusinessException("Compra no encontrada"));

            movimiento.setCompra(compra);
        }

        if (movimiento.getEstado() == null) {
            movimiento.setEstado(EstadoMovimientoEnums.PENDIENTE);
        }

        validarMovimiento(movimiento);

        return toMap(movimientoFinanzasRepository.save(movimiento));
    }

    //====================
    //Crear gasto
    //====================
    @Override
    @Transactional
    public ObtenerMovimientoFinanzasDTO crearGasto(CrearGastoDTO crearGasto, Long idUsuario) {

        if (crearGasto == null) {
            throw new BusinessException("Los datos del gasto son obligatorios");
        }

        if (idUsuario == null) {
            throw new BusinessException("El usuario es obligatorio");
        }

        if (crearGasto.getNombreGasto() == null) {
            throw new BusinessException("La categoría del gasto es obligatoria");
        }

        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        TipoGastoEntity tipoGasto = buscarTipoGasto(crearGasto.getNombreGasto());

        MovimientoFinanzasEntity movimiento = new MovimientoFinanzasEntity();

        movimiento.setFechaGasto(crearGasto.getFechaGasto());
        movimiento.setMonto(crearGasto.getMonto());
        movimiento.setTipoMovimiento(TipoMovimientoEnums.EGRESO);
        movimiento.setDescripcion(crearGasto.getDescripcion());
        movimiento.setMetodoPago(crearGasto.getMetodoPago());
        movimiento.setUsuario(usuario);
        movimiento.setTipoGasto(tipoGasto);
        movimiento.setEstado(EstadoMovimientoEnums.CONFIRMADO);

        validarMovimiento(movimiento);

        if (crearGasto.getMetodoPago() == MetodoPagoEnums.BILLETERA) {

            DebitarSaldoDTO debitarSaldo = new DebitarSaldoDTO();

            debitarSaldo.setIdUsuario(idUsuario);
            debitarSaldo.setMonto(crearGasto.getMonto());
            debitarSaldo.setDescripcion(crearGasto.getDescripcion() != null && !crearGasto.getDescripcion().isBlank() ? crearGasto.getDescripcion(): "Pago de gasto: " + crearGasto.getNombreGasto());

            billeteraService.debitarSaldo(debitarSaldo);
        }

        return toMap(movimientoFinanzasRepository.save(movimiento));
    }

    //==========================
    //Actualizar movimiento
    //==========================
    @Override
    @Transactional
    public ObtenerMovimientoFinanzasDTO actualizarMovimiento(Long id, ActualizarMovimientoFinanzasDTO actualizarMovimiento) {

        if (id == null || id <= 0) {
            throw new BusinessException("ID de movimiento inválido");
        }

        if (actualizarMovimiento == null) {
            throw new BusinessException("Los datos son obligatorios");
        }

        MovimientoFinanzasEntity existente = movimientoFinanzasRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Movimiento no encontrado"));

        existente.setFechaGasto(actualizarMovimiento.getFechaGasto());
        existente.setMonto(actualizarMovimiento.getMonto());
        existente.setTipoMovimiento(actualizarMovimiento.getTipoMovimiento());
        existente.setDescripcion(actualizarMovimiento.getDescripcion());
        existente.setEstado(actualizarMovimiento.getEstado());
        existente.setMetodoPago(actualizarMovimiento.getMetodoPago());

        if (actualizarMovimiento.getNombreGasto() != null) {
            existente.setTipoGasto(buscarTipoGasto(actualizarMovimiento.getNombreGasto()));
        }

        if (actualizarMovimiento.getIdCompra() != null) {

            CompraEntity compra = compraRepository.findById(actualizarMovimiento.getIdCompra())
                .orElseThrow(() -> new BusinessException("Compra no encontrada"));

            existente.setCompra(compra);
        } else {
            existente.setCompra(null);
        }

        validarMovimiento(existente);

        return toMap(movimientoFinanzasRepository.save(existente));
    }

    //=======================
    //Buscar por ID
    //=======================
    @Override
    public ObtenerMovimientoFinanzasDTO buscarPorId(Long id) {

        if (id == null || id <= 0) {
            throw new BusinessException("ID de movimiento inválido");
        }

        return toMap(movimientoFinanzasRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Movimiento no encontrado")));
    }

    //=======================
    //Listar todos
    //=======================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> listarTodos() {

        return movimientoFinanzasRepository.findAll().stream().sorted(Comparator.comparing(MovimientoFinanzasEntity::getFechaGasto, Comparator.nullsLast(Comparator.reverseOrder()))).map(this::toMap).toList();
    }

    //==========================
    //Listar gastos
    //==========================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> listarGastos(FiltroGastosDTO filtros) {

        if (filtros == null) {
            filtros = new FiltroGastosDTO();
        }

        if (filtros.getFechaDesde() != null && filtros.getFechaHasta() != null && filtros.getFechaDesde().isAfter(filtros.getFechaHasta())) {
            throw new BusinessException("La fecha desde no puede ser posterior a la fecha hasta");
        }

        if (filtros.getFechaHasta() != null){
            filtros.setFechaHasta(filtros.getFechaHasta().toLocalDate().atTime(LocalTime.MAX));
        }

        if (filtros.getMontoMinimo() != null && filtros.getMontoMinimo().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("El monto mínimo no puede ser negativo");
        }

        if (filtros.getMontoMaximo() != null && filtros.getMontoMaximo().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("El monto máximo no puede ser negativo");
        }

        if (filtros.getMontoMinimo() != null && filtros.getMontoMaximo() != null && filtros.getMontoMinimo().compareTo(filtros.getMontoMaximo()) > 0) {
            throw new BusinessException("El monto mínimo no puede superar al monto máximo");
        }

        String ordenarPor = filtros.getOrdenarPor() == null || filtros.getOrdenarPor().isBlank() ? "fecha" : filtros.getOrdenarPor().trim().toLowerCase();

        String sentido = filtros.getSentido() == null || filtros.getSentido().isBlank() ? "desc" : filtros.getSentido().trim().toLowerCase();

        if (!ordenarPor.equals("fecha") && !ordenarPor.equals("monto")) {
            throw new BusinessException("El orden debe ser por fecha o por monto");
        }

        if (!sentido.equals("asc") && !sentido.equals("desc")) {
            throw new BusinessException("El sentido del orden debe ser asc o desc");
        }

        Comparator<MovimientoFinanzasEntity> comparador;

        if (ordenarPor.equals("monto")) {
            comparador = Comparator.comparing(MovimientoFinanzasEntity::getMonto, Comparator.nullsLast(sentido.equals("desc") ? Comparator.<BigDecimal>reverseOrder() : Comparator.<BigDecimal>naturalOrder()));
        } else {
            comparador = Comparator.comparing(MovimientoFinanzasEntity::getFechaGasto, Comparator.nullsLast(sentido.equals("desc") ? Comparator.<LocalDateTime>reverseOrder(): Comparator.<LocalDateTime>naturalOrder()));
        }

        final FiltroGastosDTO criterios = filtros;

        return movimientoFinanzasRepository.findByTipoMovimiento(TipoMovimientoEnums.EGRESO).stream().filter(m -> criterios.getFechaDesde() == null || (m.getFechaGasto() != null && !m.getFechaGasto().isBefore(criterios.getFechaDesde()))).filter(m -> criterios.getFechaHasta() == null || (m.getFechaGasto() != null && !m.getFechaGasto().isAfter(criterios.getFechaHasta()))).filter(m -> criterios.getNombreGasto() == null || (m.getTipoGasto() != null && criterios.getNombreGasto().equals(m.getTipoGasto().getNombreGasto()))).filter(m -> criterios.getMontoMinimo() == null || (m.getMonto() != null && m.getMonto().compareTo(criterios.getMontoMinimo()) >= 0)).filter(m -> criterios.getMontoMaximo() == null || (m.getMonto() != null && m.getMonto().compareTo(criterios.getMontoMaximo()) <= 0)).sorted(comparador).map(this::toMap).toList();
    }

    //=======================
    //Eliminar movimiento
    //=======================
    @Override
    @Transactional
    public void eliminarMovimiento(Long id) {

        if(id == null || id <= 0){
            throw new BusinessException("ID de movimiento invalido");
        }

        MovimientoFinanzasEntity movimiento = movimientoFinanzasRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Movimiento no encontrado"));

        movimientoFinanzasRepository.delete(movimiento);
    }

    //=======================
    //Buscar por fecha
    //=======================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> buscarPorFecha(LocalDateTime fecha) {

        if (fecha == null) {
            throw new BusinessException("La fecha es obligatoria");
        }

        LocalDateTime inicio = fecha.toLocalDate().atStartOfDay();

        LocalDateTime fin = fecha.toLocalDate().atTime(LocalTime.MAX);

        return movimientoFinanzasRepository.findByFechaGastoBetween(inicio, fin).stream().map(this::toMap).toList();
    }

    //===========================
    //Buscar por rango de fechas
    //===========================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {

        validarRangoFechas(inicio, fin);

        return movimientoFinanzasRepository.findByFechaGastoBetween(inicio, fin).stream().map(this::toMap).toList();
    }

    //===============================
    //Buscar por tipo de movimiento
    //===============================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> buscarPorTipoMovimiento(TipoMovimientoEnums tipoMovimiento) {

        if (tipoMovimiento == null) {
            throw new BusinessException("El tipo de movimiento es obligatorio");
        }

        return movimientoFinanzasRepository.findByTipoMovimiento(tipoMovimiento).stream().map(this::toMap).toList();
    }

    //======================
    //Buscar por usuario
    //======================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> buscarPorUsuario(Long idUsuario) {

        if (idUsuario == null || idUsuario <= 0) {
            throw new BusinessException("ID de usuario inválido");
        }

        return movimientoFinanzasRepository.findByUsuarioId(idUsuario).stream().map(this::toMap).toList();
    }

    //=============================
    //Buscar por tipo de gasto
    //=============================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> buscarPorTipoGasto(TipoGastoEnums tipoGasto) {

        if (tipoGasto == null) {
            throw new BusinessException("La categoría del gasto es obligatoria");
        }

        TipoGastoEntity categoria = buscarTipoGasto(tipoGasto);

        return movimientoFinanzasRepository.findByTipoGastoId(categoria.getId()).stream().map(this::toMap).toList();
    }

    //=======================
    //Buscar por compra
    //=======================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> buscarPorCompra(Long idCompra) {

        if (idCompra == null || idCompra <= 0) {
            throw new BusinessException("ID de compra inválido");
        }

        return movimientoFinanzasRepository.findByCompraId(idCompra).stream().map(this::toMap).toList();
    }

    //=======================
    //Buscar por estado
    //=======================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> buscarPorEstado(EstadoMovimientoEnums estado) {

        if (estado == null) {
            throw new BusinessException("El estado es obligatorio");
        }

        return movimientoFinanzasRepository.findByEstado(estado).stream().map(this::toMap).toList();
    }

    //============================
    //Buscar por descripción
    //============================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> buscarPorDescripcion(String descripcion) {

        if (descripcion == null || descripcion.isBlank()) {
            throw new BusinessException("La descripción es obligatoria");
        }

        return movimientoFinanzasRepository.findByDescripcionContainingIgnoreCase(descripcion.trim()).stream().map(this::toMap).toList();
    }

    //=======================
    //Total ingresos
    //=======================
    @Override
    public BigDecimal totalIngresos(LocalDateTime inicio, LocalDateTime fin) {

        validarRangoFechas(inicio, fin);

        return movimientoFinanzasRepository.findByTipoMovimientoAndEstadoAndFechaGastoBetween(TipoMovimientoEnums.INGRESO, EstadoMovimientoEnums.CONFIRMADO, inicio, fin).stream().map(MovimientoFinanzasEntity::getMonto).filter(monto -> monto != null).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    //=======================
    //Total egresos
    //=======================
    @Override
    public BigDecimal totalEgresos(LocalDateTime inicio, LocalDateTime fin) {

        validarRangoFechas(inicio, fin);

        return movimientoFinanzasRepository.findByTipoMovimientoAndEstadoAndFechaGastoBetween(TipoMovimientoEnums.EGRESO, EstadoMovimientoEnums.CONFIRMADO, inicio, fin).stream().map(MovimientoFinanzasEntity::getMonto).filter(monto -> monto != null).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    //=======================
    //Balance
    //=======================
    @Override
    public BigDecimal balance(LocalDateTime inicio, LocalDateTime fin) {
        return totalIngresos(inicio, fin).subtract(totalEgresos(inicio, fin));
    }

    //============================
    //Buscar categoría por enum
    //============================
    private TipoGastoEntity buscarTipoGasto(TipoGastoEnums nombreGasto) {

        return tipoGastoRepository.findByNombreGasto(nombreGasto)
            .orElseThrow(() -> new BusinessException("No existe la categoría de gasto: " + nombreGasto));
    }

    //============================
    //Validar rango de fechas
    //============================
    private void validarRangoFechas(LocalDateTime inicio, LocalDateTime fin) {

        if (inicio == null || fin == null) {
            throw new BusinessException("Las fechas de inicio y fin son obligatorias");
        }

        if (inicio.isAfter(fin)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
    }

    //=======================
    //Validar movimiento
    //=======================
    private void validarMovimiento(MovimientoFinanzasEntity movimiento) {

        if (movimiento == null) {
            throw new BusinessException("El movimiento no puede ser nulo");
        }

        if (movimiento.getFechaGasto() == null) {
            throw new BusinessException("La fecha del gasto es obligatoria");
        }

        if (movimiento.getMonto() == null || movimiento.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto debe ser mayor a cero");
        }

        if (movimiento.getTipoMovimiento() == null) {
            throw new BusinessException("El tipo de movimiento es obligatorio");
        }

        if (movimiento.getMetodoPago() == null) {
            throw new BusinessException("El método de pago es obligatorio");
        }

        if (movimiento.getUsuario() == null) {
            throw new BusinessException("El usuario es obligatorio");
        }

        if (movimiento.getTipoMovimiento() == TipoMovimientoEnums.EGRESO && movimiento.getTipoGasto() == null) {
            throw new BusinessException("La categoría es obligatoria para los egresos");
        }
    }

    //=======================
    //Mapear DTO
    //=======================
    @Override
    public ObtenerMovimientoFinanzasDTO toMap(MovimientoFinanzasEntity movimiento) {

        ObtenerMovimientoFinanzasDTO obtenerMovimientoFinanzasDTO = new ObtenerMovimientoFinanzasDTO();

        obtenerMovimientoFinanzasDTO.setIdMovimiento(movimiento.getId());
        obtenerMovimientoFinanzasDTO.setFechaGasto(movimiento.getFechaGasto());
        obtenerMovimientoFinanzasDTO.setFechaRegistro(movimiento.getFechaRegistro());
        obtenerMovimientoFinanzasDTO.setMonto(movimiento.getMonto());
        obtenerMovimientoFinanzasDTO.setTipoMovimiento(movimiento.getTipoMovimiento());
        obtenerMovimientoFinanzasDTO.setDescripcion(movimiento.getDescripcion());
        obtenerMovimientoFinanzasDTO.setEstadoMovimiento(movimiento.getEstado());
        obtenerMovimientoFinanzasDTO.setMetodoPago(movimiento.getMetodoPago());

        if (movimiento.getTipoGasto() != null) {
            obtenerMovimientoFinanzasDTO.setNombreGasto(movimiento.getTipoGasto().getNombreGasto());
        }

        if (movimiento.getUsuario() != null) {
            obtenerMovimientoFinanzasDTO.setIdUsuario(movimiento.getUsuario().getId());
            obtenerMovimientoFinanzasDTO.setNombreUsuario(movimiento.getUsuario().getNombre());
            obtenerMovimientoFinanzasDTO.setApellidoUsuario(movimiento.getUsuario().getApellido());
        }

        if (movimiento.getCompra() != null) {
            obtenerMovimientoFinanzasDTO.setIdCompra(movimiento.getCompra().getId());
        }

        return obtenerMovimientoFinanzasDTO;
    }
}
