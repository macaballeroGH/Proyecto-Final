package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private BilleteraRepository billeteraRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MovimientoBilleteraRepository movimientoBilleteraRepository;

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

        if (monto.compareTo(BilleteraConstantes.Monto_Minimo) < 0){
            throw new BusinessException("El monto minimo es: " + BilleteraConstantes.Monto_Minimo);
        }

        if (monto.compareTo(BilleteraConstantes.Monto_Maximo) > 0){
            throw new BusinessException("El monto maximo es: " + BilleteraConstantes.Monto_Maximo);
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

        BilleteraEntity billetera = obtenerBilleteraEntity(recargarSaldo.getIdUsuario());

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

        BilleteraEntity billetera = obtenerBilleteraEntity(debitarSaldo.getIdUsuario());

        if(billetera.getSaldo().compareTo(debitarSaldo.getMonto()) < 0){
            throw new BusinessException("Saldo insuficiente");
        }

        billetera.setSaldo(billetera.getSaldo().subtract(debitarSaldo.getMonto()));

        billeteraRepository.save(billetera);

        registrarMovimiento(billetera, debitarSaldo.getMonto(), MovimientoBilleteraEnums.DEBITO, debitarSaldo.getDescripcion());

        return toMap(billetera);
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
