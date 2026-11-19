package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.CrearBilleteraDTO;
import com.example.beautymanager.Modelo.DTO.DebitarSaldoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerBilleteraDTO;
import com.example.beautymanager.Modelo.DTO.RecargarSaldoDTO;
import com.example.beautymanager.Modelo.Entidad.BilleteraEntity;
import com.example.beautymanager.Modelo.Entidad.MovimientoBilleteraEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoUsuarioEnums;
import com.example.beautymanager.Modelo.Enums.MovimientoBilleteraEnums;
import com.example.beautymanager.Repositorio.BilleteraRepository;
import com.example.beautymanager.Repositorio.MovimientoBilleteraRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.BilleteraService;
import com.example.beautymanager.exception.BusinessException;
import com.example.beautymanager.utils.BilleteraConstantes;

import jakarta.transaction.Transactional;

@Service
public class BilleteraServiceImpl implements BilleteraService {

    private final BilleteraRepository billeteraRepository;

    private final UsuarioRepository usuarioRepository;

    private final MovimientoBilleteraRepository movimientoBilleteraRepository;

    public BilleteraServiceImpl(BilleteraRepository billeteraRepository, UsuarioRepository usuarioRepository, MovimientoBilleteraRepository movimientoBilleteraRepository){

        this.billeteraRepository = billeteraRepository;

        this.usuarioRepository = usuarioRepository;

        this.movimientoBilleteraRepository = movimientoBilleteraRepository;
    }

    //==================
    //Crear Billetera
    //==================
    @Override
    public ObtenerBilleteraDTO crearBilletera(CrearBilleteraDTO crearBilletera) {

        if(crearBilletera == null){
            throw new BusinessException("Los datos de la billetera son obligatorios");
        }

        if(crearBilletera.getIdUsuario() == null){
            throw new BusinessException("El ID del usuario es obligatorio");
        }

        UsuarioEntity usuario = usuarioRepository.findById(crearBilletera.getIdUsuario())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        if(usuario.getEstadoUsuario() != EstadoUsuarioEnums.ACTIVO){
            throw new BusinessException("El usuario no esta activo");
        }

        if(billeteraRepository.findByUsuario(usuario).isPresent()){
            throw new BusinessException("El usuario ya tiene una billetera");
        }

        BilleteraEntity billetera = new BilleteraEntity();

        billetera.setUsuario(usuario);
        billetera.setSaldo(BigDecimal.ZERO);

        return toMap(billeteraRepository.save(billetera));
    }

    //==================
    // Obtener Billetera
    //==================
    private BilleteraEntity obtenerBilleteraEntity(Long idUsuario){

        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado con ID: " + idUsuario));
        
        if(usuario.getEstadoUsuario() != EstadoUsuarioEnums.ACTIVO){
            throw new BusinessException("El usuario no esta activo");
        }

        return billeteraRepository.findByUsuario(usuario)
            .orElseThrow(() -> new BusinessException("Billetera no encontrada"));
    }

    //==========================
    //Obtener billetera bloqueada
    //==========================

    private BilleteraEntity obtenerBilleteraBloqueada(Long idUsuario){

        BilleteraEntity billetera = billeteraRepository.buscarPorUsuarioConBloqueo(idUsuario)
            .orElseThrow(() -> new BusinessException("Billetera no encontrada para el usuario con ID: " + idUsuario));

        if(billetera.getUsuario().getEstadoUsuario() !=  EstadoUsuarioEnums.ACTIVO){
            throw new BusinessException("El usuario no esta activo");
        }

        return billetera;
    }

    //=======================
    //Obtener por usuario
    //=======================
    @Override
    public ObtenerBilleteraDTO obtenerPorUsuario(Long idUsuario){
        return toMap(obtenerBilleteraEntity(idUsuario));
    }

    //==================
    //Validar Monto
    //==================
    private void validarMonto(BigDecimal monto){

        if (monto == null){
            throw new BusinessException("El monto no puede ser nulo");
        } 

        if (monto.compareTo(BilleteraConstantes.MONTO_MINIMO) < 0){
            throw new BusinessException("El monto minimo es: " + BilleteraConstantes.MONTO_MINIMO);
        }

        if (monto.compareTo(BilleteraConstantes.MONTO_MAXIMO) > 0){
            throw new BusinessException("El monto maximo es: " + BilleteraConstantes.MONTO_MAXIMO);
        }
    }

    //=======================
    //Registrar Movimiento
    //=======================
    private void registrarMovimiento(BilleteraEntity billetera, BigDecimal monto, MovimientoBilleteraEnums tipoMovimiento, String descripcion){

        MovimientoBilleteraEntity movimiento = new MovimientoBilleteraEntity();

        movimiento.setBilletera(billetera);
        movimiento.setMonto(monto);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setDescripcion(descripcion);

        movimientoBilleteraRepository.save(movimiento);
    }

    //==================
    //Consultar Saldo
    //==================
    @Override
    public BigDecimal consultarSaldo(Long idUsuario){
        return obtenerBilleteraEntity(idUsuario).getSaldo();
    }

    //==========================
    //Recargar Saldo (Credito)
    //==========================
    @Override
    @Transactional
    public ObtenerBilleteraDTO recargarSaldo(RecargarSaldoDTO recargarSaldo){

        if(recargarSaldo == null){
            throw new BusinessException("Los datos son obligatorios");
        }

        if(recargarSaldo.getIdUsuario() == null){
            throw new BusinessException("El ID del usuario es obligatorio");
        }

        validarMonto(recargarSaldo.getMonto());

        BilleteraEntity billetera = obtenerBilleteraBloqueada(recargarSaldo.getIdUsuario());

        billetera.setSaldo(billetera.getSaldo().add(recargarSaldo.getMonto()));

        billeteraRepository.save(billetera);

        registrarMovimiento(billetera, recargarSaldo.getMonto(), MovimientoBilleteraEnums.CREDITO, "Recarga de saldo");

        return toMap(billetera);
    }

    //============================
    //Debitar Saldo (para compras)
    //============================
    @Override
    @Transactional
    public ObtenerBilleteraDTO debitarSaldo(DebitarSaldoDTO debitarSaldo){

        if(debitarSaldo == null){
            throw new BusinessException("Los datos son obligatorios");
        }

        if(debitarSaldo.getIdUsuario() == null){
            throw new BusinessException("El ID del usuario es obligatorio");
        }

        validarMonto(debitarSaldo.getMonto());

        BilleteraEntity billetera = obtenerBilleteraBloqueada(debitarSaldo.getIdUsuario());

        if(billetera.getSaldo().compareTo(debitarSaldo.getMonto()) < 0){
            throw new BusinessException("Saldo insuficiente");
        }

        billetera.setSaldo(billetera.getSaldo().subtract(debitarSaldo.getMonto()));

        billeteraRepository.save(billetera);

        registrarMovimiento(billetera, debitarSaldo.getMonto(), MovimientoBilleteraEnums.DEBITO, debitarSaldo.getDescripcion());

        return toMap(billetera);
    }

    
    //==========================
    //Transferir saldo
    //==========================
    @Override
    @Transactional
    public void transferirSaldo(Long idUsuarioOrigen, Long idUsuarioDestino, BigDecimal monto, String descripcion) {

        //==========================
        //Validar datos
        //==========================
        if(idUsuarioOrigen == null || idUsuarioDestino == null){
            throw new BusinessException("Los usuarios de la transferencia son obligatorios");
        }

        if(idUsuarioOrigen.equals(idUsuarioDestino)){
            throw new BusinessException("No se puede transferir saldo a la misma billetera");
        }

        validarMonto(monto);

        if(descripcion == null || descripcion.isBlank()){
            throw new BusinessException("La descripcion de la transferencia es obligatoria");
        }

        //==========================
        //Bloquear billeteras
        //==========================
        //Se bloquean en orden de ID para
        //reducir el riesgo de interbloqueos.

        Long primerId = Math.min(idUsuarioOrigen, idUsuarioDestino);

        Long segundoId = Math.max(idUsuarioOrigen, idUsuarioDestino);

        BilleteraEntity primeraBilletera = obtenerBilleteraBloqueada(primerId);

        BilleteraEntity segundaBilletera = obtenerBilleteraBloqueada(segundoId);

        BilleteraEntity billeteraOrigen =
            idUsuarioOrigen.equals(primerId)
                ? primeraBilletera
                : segundaBilletera;

        BilleteraEntity billeteraDestino =
            idUsuarioDestino.equals(primerId)
                ? primeraBilletera
                : segundaBilletera;

        //==========================
        //Validar saldo
        //==========================
        if(billeteraOrigen.getSaldo().compareTo(monto) < 0){
            throw new BusinessException("Saldo insuficiente para realizar el pago");
        }

        //==========================
        //Descontar saldo de origen
        //==========================
        billeteraOrigen.setSaldo(
            billeteraOrigen.getSaldo().subtract(monto)
        );

        //==========================
        //Acreditar saldo de destino
        //==========================
        billeteraDestino.setSaldo(
            billeteraDestino.getSaldo().add(monto)
        );

        billeteraRepository.save(billeteraOrigen);

        billeteraRepository.save(billeteraDestino);

        //==========================
        //Registrar movimiento origen
        //==========================
        registrarMovimiento(
            billeteraOrigen,
            monto,
            MovimientoBilleteraEnums.DEBITO,
            "Pago: " + descripcion
        );

        //==========================
        //Registrar movimiento destino
        //==========================
        registrarMovimiento(
            billeteraDestino,
            monto,
            MovimientoBilleteraEnums.CREDITO,
            "Ingreso: " + descripcion
        );
    }


    @Override
    public ObtenerBilleteraDTO toMap(BilleteraEntity billetera) {

        ObtenerBilleteraDTO obtenerBilleteraDTO = new ObtenerBilleteraDTO();

        obtenerBilleteraDTO.setIdBilletera(billetera.getId());
        obtenerBilleteraDTO.setSaldo(billetera.getSaldo());
        obtenerBilleteraDTO.setFechaCreacion(billetera.getFechaCreacion());

        if(billetera.getUsuario() != null){

            obtenerBilleteraDTO.setIdUsuario(billetera.getUsuario().getId());
            obtenerBilleteraDTO.setNombreUsuario(billetera.getUsuario().getNombre());
            obtenerBilleteraDTO.setApellidoUsuario(billetera.getUsuario().getApellido());
            obtenerBilleteraDTO.setEmailUsuario(billetera.getUsuario().getEmail());
        }

        obtenerBilleteraDTO.setCantidadMovimientos(billetera.getMovimientos() != null ? billetera.getMovimientos().size() : 0);

        return obtenerBilleteraDTO;
    }
}
