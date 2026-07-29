package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ActualizarMovimientoFinanzasDTO;
import com.example.beautymanager.Modelo.DTO.CrearMovimientoFinanzasDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerMovimientoFinanzasDTO;
import com.example.beautymanager.Modelo.Entidad.CompraEntity;
import com.example.beautymanager.Modelo.Entidad.MovimientoFinanzasEntity;
import com.example.beautymanager.Modelo.Entidad.TipoGastoEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.TipoMovimientoEnums;
import com.example.beautymanager.Repositorio.CompraRepository;
import com.example.beautymanager.Repositorio.MovimientoFinanzasRepository;
import com.example.beautymanager.Repositorio.TipoGastoRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.MovimientoFinanzasService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class MovimientoFinanzasServiceImpl implements MovimientoFinanzasService {

    @Autowired
    private MovimientoFinanzasRepository movimientoFinanzasRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoGastoRepository tipoGastoRepository;

    @Autowired
    private CompraRepository compraRepository;

    //====================
    //Crear movimiento
    //====================
    @Override
    public ObtenerMovimientoFinanzasDTO crearMovimiento(CrearMovimientoFinanzasDTO crearMovimiento) {

        if(crearMovimiento == null){
            throw new BusinessException("Los datos son obligatorios");
        }

        UsuarioEntity usuario = usuarioRepository.findById(crearMovimiento.getIdUsuario())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        MovimientoFinanzasEntity movimiento = new MovimientoFinanzasEntity();

        movimiento.setMonto(crearMovimiento.getMonto());
        movimiento.setTipoMovimiento(crearMovimiento.getTipoMovimiento());
        movimiento.setDescripcion(crearMovimiento.getDescripcion());
        movimiento.setEstado(crearMovimiento.getEstado());
        movimiento.setUsuario(usuario);

        if(crearMovimiento.getIdTipoGasto() != null){
            TipoGastoEntity tipoGasto = tipoGastoRepository.findById(crearMovimiento.getIdTipoGasto())
                .orElseThrow(() -> new BusinessException("Tipo de gasto no encontrado"));

            movimiento.setTipoGasto(tipoGasto);
        }

        if(crearMovimiento.getIdCompra() != null){
            CompraEntity compra = compraRepository.findById(crearMovimiento.getIdCompra())
                .orElseThrow(() -> new BusinessException("Compra no encontrada"));

            movimiento.setCompra(compra);
        }

        validarMovimiento(movimiento);

        if(movimiento.getEstado() == null){
            movimiento.setEstado(EstadoMovimientoEnums.PENDIENTE);
        }

        return toMap(movimientoFinanzasRepository.save(movimiento));
    }

    //==========================
    //Actualizar movimiento
    //==========================
    @Override
    public ObtenerMovimientoFinanzasDTO actualizarMovimiento(Long id, ActualizarMovimientoFinanzasDTO actualizarMovimiento) {

        MovimientoFinanzasEntity existente = movimientoFinanzasRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Movimiento no encontrado"));

        existente.setMonto(actualizarMovimiento.getMonto());
        existente.setTipoMovimiento(actualizarMovimiento.getTipoMovimiento());
        existente.setDescripcion(actualizarMovimiento.getDescripcion());
        existente.setEstado(actualizarMovimiento.getEstado());

        if(actualizarMovimiento.getIdTipoGasto() != null){
            TipoGastoEntity tipoGasto = tipoGastoRepository.findById(actualizarMovimiento.getIdTipoGasto())
                .orElseThrow(() -> new BusinessException("Tipo de gasto no encontrado"));

            existente.setTipoGasto(tipoGasto);
        } else {
            existente.setTipoGasto(null);
        }

        if(actualizarMovimiento.getIdCompra() != null){
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
    //Buscar por id
    //=======================
    @Override
    public ObtenerMovimientoFinanzasDTO buscarPorId(Long id){

        return toMap(movimientoFinanzasRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Movimiento no encontrado")));
    }

    //=======================
    //Listar todos
    //=======================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> listarTodos() {
        return movimientoFinanzasRepository.findAll().stream().map(this::toMap).toList();
    }

    //=======================
    //Eliminar
    //=======================
    @Override
    public void eliminarMovimiento(Long id) {

        MovimientoFinanzasEntity movimiento = movimientoFinanzasRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Movimiento no encontrado"));

        if(movimiento.getEstado() == EstadoMovimientoEnums.CONFIRMADO) {
            throw new BusinessException("No se puede eliminar un movimiento ya confirmado");
        }

        movimientoFinanzasRepository.delete(movimiento);
    }

    //=======================
    //Buscar por fecha
    //=======================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> buscarPorFecha(LocalDateTime fecha) {
        return movimientoFinanzasRepository.findByFechaBetween(fecha.toLocalDate().atStartOfDay(), fecha.toLocalDate().atTime(LocalTime.MAX)).stream().map(this::toMap).toList();
    }

    //===========================
    //Buscar por rango fecha
    //===========================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return movimientoFinanzasRepository.findByFechaBetween(inicio, fin).stream().map(this::toMap).toList();
    }

    //===============================
    //Buscar por tipo movimiento
    //===============================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> buscarPorTipoMovimiento(TipoMovimientoEnums tipoMovimiento) {
        return movimientoFinanzasRepository.findByTipoMovimiento(tipoMovimiento).stream().map(this::toMap).toList();
    }

    //======================
    //Buscar por usuario
    //======================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> buscarPorUsuario(Long idUsuario) {
        return movimientoFinanzasRepository.findByUsuarioId(idUsuario).stream().map(this::toMap).toList();
    }

    //=============================
    //Buscar por tipo de gasto
    //=============================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> buscarPorTipoGasto(Long idTipoGasto) {
        return movimientoFinanzasRepository.findByTipoGastoId(idTipoGasto).stream().map(this::toMap).toList();
    }

    //=======================
    //Buscar por compra
    //=======================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> buscarPorCompra(Long idCompra) {
        return movimientoFinanzasRepository.findByCompraId(idCompra).stream().map(this::toMap).toList();
    }

    //=======================
    //Buscar por estado
    //=======================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> buscarPorEstado(EstadoMovimientoEnums estado) {
        return movimientoFinanzasRepository.findByEstado(estado).stream().map(this::toMap).toList();
    }

    //============================
    //Buscar por descripcion
    //============================
    @Override
    public List<ObtenerMovimientoFinanzasDTO> buscarPorDescripcion(String descripcion) {

        if(descripcion == null || descripcion.isBlank()){
            throw new BusinessException("La descripcion es obligatoria");
        }
        
        return movimientoFinanzasRepository.findByDescripcionContainingIgnoreCase(descripcion).stream().map(this::toMap).toList();
    }

    //=======================
    //Total ingresos
    //=======================
    @Override
    public BigDecimal totalIngresos(LocalDateTime inicio, LocalDateTime fin) {
        return movimientoFinanzasRepository.findByTipoMovimientoAndFechaBetween(TipoMovimientoEnums.INGRESO, inicio, fin).stream().map(MovimientoFinanzasEntity::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    //=======================
    //Total egresos
    //=======================
    @Override
    public BigDecimal totalEgresos(LocalDateTime inicio, LocalDateTime fin) {
        return movimientoFinanzasRepository.findByTipoMovimientoAndFechaBetween(TipoMovimientoEnums.EGRESO, inicio, fin).stream().map(MovimientoFinanzasEntity::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    //=======================
    //Balance
    //=======================
    @Override
    public BigDecimal balance(LocalDateTime inicio, LocalDateTime fin) {
        return totalIngresos(inicio, fin).subtract(totalEgresos(inicio, fin));
    }

    //=======================
    //Validar movimiento
    //=======================
    private void validarMovimiento(MovimientoFinanzasEntity movimiento) {
        
        if (movimiento == null) {
            throw new BusinessException("El movimiento no puede ser nulo");
        }

        if (movimiento.getMonto() == null || movimiento.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto debe ser mayor a cero");
        }

        if (movimiento.getTipoMovimiento() == null) {
            throw new BusinessException("El tipo de movimiento es obligatorio");
        }

        if (movimiento.getUsuario() == null) {
            throw new BusinessException("El usuario es obligatorio");
        }
    }

    @Override
    public ObtenerMovimientoFinanzasDTO toMap(MovimientoFinanzasEntity movimiento) {

        ObtenerMovimientoFinanzasDTO obtenerMovimientoFinanzasDTO = new ObtenerMovimientoFinanzasDTO();

        obtenerMovimientoFinanzasDTO.setIdMovimiento(movimiento.getId());
        obtenerMovimientoFinanzasDTO.setFecha(movimiento.getFecha());
        obtenerMovimientoFinanzasDTO.setMonto(movimiento.getMonto());
        obtenerMovimientoFinanzasDTO.setTipoMovimiento(movimiento.getTipoMovimiento());
        obtenerMovimientoFinanzasDTO.setDescripcion(movimiento.getDescripcion());
        obtenerMovimientoFinanzasDTO.setEstadoMovimiento(movimiento.getEstado());

        if(movimiento.getUsuario() != null){
            obtenerMovimientoFinanzasDTO.setIdUsuario(movimiento.getUsuario().getId());
            obtenerMovimientoFinanzasDTO.setNombreUsuario(movimiento.getUsuario().getNombre());
            obtenerMovimientoFinanzasDTO.setApellidoUsuario(movimiento.getUsuario().getApellido());
        }

        if(movimiento.getTipoGasto() != null){
            obtenerMovimientoFinanzasDTO.setIdTipoGasto(movimiento.getTipoGasto().getId());
            obtenerMovimientoFinanzasDTO.setNombreTipoGasto(movimiento.getTipoGasto().getNombreGasto());
        }

        if(movimiento.getCompra() != null) {
            obtenerMovimientoFinanzasDTO.setIdCompra(movimiento.getCompra().getId());
        }

        return obtenerMovimientoFinanzasDTO;
    }
}
