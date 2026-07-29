package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ActualizarPagoDTO;
import com.example.beautymanager.Modelo.DTO.CrearPagoDTO;
import com.example.beautymanager.Modelo.DTO.DebitarSaldoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerPagoDTO;
import com.example.beautymanager.Modelo.Entidad.ClienteEntity;
import com.example.beautymanager.Modelo.Entidad.CompraEntity;
import com.example.beautymanager.Modelo.Entidad.MovimientoFinanzasEntity;
import com.example.beautymanager.Modelo.Entidad.PagoEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoPagoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoTurnoEnums;
import com.example.beautymanager.Modelo.Enums.MetodoPagoEnums;
import com.example.beautymanager.Modelo.Enums.TipoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.TipoPagoEnums;
import com.example.beautymanager.Repositorio.ClienteRepository;
import com.example.beautymanager.Repositorio.CompraRepository;
import com.example.beautymanager.Repositorio.MovimientoFinanzasRepository;
import com.example.beautymanager.Repositorio.PagoRepository;
import com.example.beautymanager.Repositorio.TurnoRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.BilleteraService;
import com.example.beautymanager.Servicio.PagoService;
import com.example.beautymanager.exception.BusinessException;

import jakarta.transaction.Transactional;

@Service
public class PagoServiceImpl implements PagoService {


    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private BilleteraService billeteraService;

    @Autowired
    private MovimientoFinanzasRepository movimientoFinanzasRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    //=======================
    //Crear pago turno
    //=======================
    @Override
    @Transactional
    public ObtenerPagoDTO crearPago(CrearPagoDTO crearPago) {

        if(crearPago == null){
            throw new BusinessException("Los datos del pago son obligatorios");
        }

        if(crearPago.getMetodoPago() == null){
            throw new BusinessException("Debe seleccionar un metodo de pago");
        }

        if(crearPago.getIdTurno() == null){
            throw new BusinessException("Debe indicar un turno");
        }

        TurnoEntity turno = turnoRepository.findById(crearPago.getIdTurno())
            .orElseThrow(() -> new BusinessException("Turno no encontrado"));

        if(turno.getEstadoTurno() == EstadoTurnoEnums.CANCELADO){
            throw new BusinessException("No se puede pagar un turno cancelado");
        }

        if(pagoRepository.findByTurnoId(turno.getId()).isPresent()){
            throw new BusinessException("El turno ya tiene un pago");
        }

        if(turno.getServicios() == null || turno.getServicios().isEmpty()){
            throw new BusinessException("El turno no tiene servicios");
        }

        BigDecimal total = turno.getServicios().stream().map(ts -> ts.getServicio().getPrecio()).reduce(BigDecimal.ZERO, BigDecimal::add);

        if(total.compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException("Monto invalido");
        }

        PagoEntity pago = new PagoEntity();

        pago.setTurno(turno);
        pago.setMetodoPago(crearPago.getMetodoPago());
        pago.setMonto(total);
        pago.setEstado(EstadoPagoEnums.PENDIENTE);

        return toMap(pagoRepository.save(pago));
    }

    //=======================
    //Crear pago compra
    //=======================
    @Override
    public ObtenerPagoDTO crearPagoCompra(CrearPagoDTO crearPago){

        if(crearPago == null){
            throw new BusinessException("Los datos del pago son obligatorios");
        }

        if(crearPago.getMetodoPago() == null){
            throw new BusinessException("Debe seleccionar un metodo de pago");
        }

        if(crearPago.getIdCompra() == null){
            throw new BusinessException("Debe indicar una compra");
        }

        CompraEntity compra = compraRepository.findById(crearPago.getIdCompra())
            .orElseThrow(() -> new BusinessException("Compra no encontrada"));

        if(pagoRepository.findByCompraId(compra.getId()).isPresent()){
            throw new BusinessException("La compra ya tiene un pago");
        }

        if(compra.getTotal() == null || compra.getTotal().compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException("Total de compra invalido");
        }

        PagoEntity pago = new PagoEntity();

        pago.setCompra(compra);
        pago.setMetodoPago(crearPago.getMetodoPago());
        pago.setMonto(compra.getTotal());
        pago.setEstado(EstadoPagoEnums.PENDIENTE);

        return toMap(pagoRepository.save(pago));
    }

    //=======================
    //Pagar
    //=======================
    @Override
    @Transactional
    public ObtenerPagoDTO pagar(Long idUsuario, Long idPago) {

        if(idUsuario == null){
            throw new BusinessException("El usuario es obligatorio");
        }

        if(idPago == null){
            throw new BusinessException("El pago es obligatorio");
        }

        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        ClienteEntity cliente = clienteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new BusinessException("El usuario no es cliente"));

        PagoEntity pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new BusinessException("Pago no encontrado"));

        //======================
        //Validar pertenencia
        //======================
        if(pago.getTurno() != null && !pago.getTurno().getCliente().getId().equals(cliente.getId())){
            throw new BusinessException("No puede pagar un turno que no le pertenece");
        }

        if(pago.getCompra() != null && !pago.getCompra().getCliente().getId().equals(cliente.getId())){
            throw new BusinessException("No puede pagar una compra que no le pertenece");
        }

        if(pago.getEstado() == EstadoPagoEnums.PAGADO){
            throw new BusinessException("El pago ya fue realizado");
        }

        if(pago.getEstado() == EstadoPagoEnums.CANCELADO){
            throw new BusinessException("No se puede pagar un pago cancelado");
        }

        if(pago.getMonto() == null || pago.getMonto().compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException("Monto invalido");
        }

        //===================
        //Pago Turno
        //===================
        if(pago.getTurno() != null){
            
            if(pago.getTurno().getEstadoTurno() == EstadoTurnoEnums.CANCELADO){
                throw new BusinessException("No se puede pagar un turno cancelado");
            }

            if(pago.getMetodoPago() == MetodoPagoEnums.BILLETERA){

                DebitarSaldoDTO debitarSaldoDTO = new DebitarSaldoDTO();
                debitarSaldoDTO.setIdUsuario(usuario.getId());
                debitarSaldoDTO.setMonto(pago.getMonto());
                debitarSaldoDTO.setDescripcion("Pago de turno ID: " + pago.getTurno().getId());

                billeteraService.debitarSaldo(debitarSaldoDTO);
            }
        }

        //===================
        //Pago compra
        //===================
        else if(pago.getCompra() != null){

            if(pago.getMetodoPago() == MetodoPagoEnums.BILLETERA){

                DebitarSaldoDTO debitarSaldoDTO = new DebitarSaldoDTO();
                debitarSaldoDTO.setIdUsuario(usuario.getId());
                debitarSaldoDTO.setMonto(pago.getMonto());
                debitarSaldoDTO.setDescripcion("Pago de compra ID: " + pago.getCompra().getId());

                billeteraService.debitarSaldo(debitarSaldoDTO);
            }
        }

        else{
            throw new BusinessException("El pago no tiene relacion");
        }

        pago.setEstado(EstadoPagoEnums.PAGADO);

        //=======================
        //Movimiento Finanzas
        //=======================
        MovimientoFinanzasEntity movimiento = new MovimientoFinanzasEntity();

        movimiento.setMonto(pago.getMonto());
        movimiento.setTipoMovimiento(TipoMovimientoEnums.INGRESO);
        movimiento.setEstado(EstadoMovimientoEnums.CONFIRMADO);
        movimiento.setUsuario(usuario);

        if(pago.getTurno() != null){
            movimiento.setDescripcion("Pago turno ID: " + pago.getTurno().getId());
        }

        if(pago.getCompra() != null){
            movimiento.setDescripcion("Pago compra ID: " + pago.getCompra().getId());
        }

        movimientoFinanzasRepository.save(movimiento);

        return toMap(pagoRepository.save(pago));
    }

    //=======================
    //Actualizar pago
    //=======================
    @Override
    public ObtenerPagoDTO actualizarPago(Long idPago, ActualizarPagoDTO actualizarPago){

        PagoEntity pago = pagoRepository.findById(idPago)
            .orElseThrow(() -> new BusinessException("Pago no encontrado"));

        if(actualizarPago.getMetodoPago() != null){
            pago.setMetodoPago(actualizarPago.getMetodoPago());
        }

        if(actualizarPago.getEstadoPago() != null){
            pago.setEstado(actualizarPago.getEstadoPago());
        }

        return toMap(pagoRepository.save(pago));
    }

    //=======================
    //Obtener por turno
    //=======================
    @Override
    public ObtenerPagoDTO obtenerPorTurno(Long idTurno){
        return toMap(pagoRepository.findByTurnoId(idTurno)
                .orElseThrow(() -> new BusinessException("Pago no encontrado")));
    }

    //=======================
    //Obtener por id
    //=======================
    @Override
    public ObtenerPagoDTO obtenerPorId(Long idPago){
        return toMap(pagoRepository.findById(idPago)
                .orElseThrow(() -> new BusinessException("Pago no encontrado")));
    }

    //=======================
    //Listar por estado
    //=======================
    @Override
    public List<ObtenerPagoDTO> listarPorEstado(EstadoPagoEnums estado){
        return pagoRepository.findByEstado(estado).stream().map(this::toMap).toList();
    }

    //=======================
    //Obtener mis pagos
    //=======================
    @Override
    public List<ObtenerPagoDTO> obtenerMisPagos(Long idUsuario){

        if(idUsuario == null){
            throw new BusinessException("El usuario es obligatorio");
        }

        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        ClienteEntity cliente = clienteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new BusinessException("El usuario no es cliente"));

        List<PagoEntity> pagos = pagoRepository.findAll().stream()
                .filter(p -> {
                    
                    if(p.getTurno() != null && p.getTurno().getCliente().getId().equals(cliente.getId())){
                        return true;
                    }

                    if(p.getCompra() != null && p.getCompra().getCliente().getId().equals(cliente.getId())){
                        return true;
                    }
                    return false;
                })
                .toList();

        return pagos.stream().map(this::toMap).toList();
    }

    //============================
    //Obtener pagos por cliente
    //============================
    @Override
    public List<ObtenerPagoDTO> obtenerPagosPorCliente(Long idCliente){

        if(idCliente == null){
            throw new BusinessException("El cliente es obligatorio");
        }

        ClienteEntity cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new BusinessException("Cliente no encontrado"));

        List<PagoEntity> pagos = pagoRepository.findAll().stream()
                .filter(p -> {

                    if(p.getTurno() != null && p.getTurno().getCliente().getId().equals(cliente.getId())){
                        return true;
                    }

                    if(p.getCompra() != null && p.getCompra().getCliente().getId().equals(cliente.getId())){
                        return true;
                    }

                    return false;
                })
                .toList();

        return pagos.stream().map(this::toMap).toList();
    }

    @Override
    public ObtenerPagoDTO toMap(PagoEntity pago) {

        ObtenerPagoDTO obtenerPagoDTO = new ObtenerPagoDTO();

        obtenerPagoDTO.setIdPago(pago.getId());
        obtenerPagoDTO.setMonto(pago.getMonto());
        obtenerPagoDTO.setFecha(pago.getFecha());
        obtenerPagoDTO.setMetodoPago(pago.getMetodoPago());
        obtenerPagoDTO.setEstadoPago(pago.getEstado());

        if(pago.getTurno() != null){

            obtenerPagoDTO.setTipoPago(TipoPagoEnums.TURNO);

            obtenerPagoDTO.setIdTurno(pago.getTurno().getId());

            if(pago.getTurno().getCliente() != null && pago.getTurno().getCliente().getUsuario() != null){

                obtenerPagoDTO.setIdCliente(pago.getTurno().getCliente().getId());
                obtenerPagoDTO.setNombreCliente(pago.getTurno().getCliente().getUsuario().getNombre());
                obtenerPagoDTO.setApellidoCliente(pago.getTurno().getCliente().getUsuario().getApellido());
            }
        }

        if(pago.getCompra() != null){

            obtenerPagoDTO.setTipoPago(TipoPagoEnums.COMPRA_PRODUCTO);
            obtenerPagoDTO.setIdCompra(pago.getCompra().getId());

            if(pago.getCompra().getCliente() != null && pago.getCompra().getCliente().getUsuario() != null){

                obtenerPagoDTO.setIdCliente(pago.getCompra().getCliente().getId());
                obtenerPagoDTO.setNombreCliente(pago.getCompra().getCliente().getUsuario().getNombre());
                obtenerPagoDTO.setApellidoCliente(pago.getCompra().getCliente().getUsuario().getApellido());
            }
        }

        return obtenerPagoDTO;
    }
}