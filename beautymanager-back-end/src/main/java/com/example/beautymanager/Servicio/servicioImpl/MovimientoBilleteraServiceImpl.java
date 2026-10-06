package com.example.beautymanager.Servicio.servicioImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ObtenerMovimientoBilleteraDTO;
import com.example.beautymanager.Modelo.Entidad.BilleteraEntity;
import com.example.beautymanager.Modelo.Entidad.MovimientoBilleteraEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoUsuarioEnums;
import com.example.beautymanager.Repositorio.BilleteraRepository;
import com.example.beautymanager.Repositorio.MovimientoBilleteraRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.MovimientoBilleteraService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class MovimientoBilleteraServiceImpl implements MovimientoBilleteraService{

    @Autowired
    private MovimientoBilleteraRepository movimientoBilleteraRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BilleteraRepository billeteraRepository;

    //=======================
    //Obtener billetera
    //=======================
    private BilleteraEntity obtenerBilletera(Long idUsuario){
        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado con ID: " + idUsuario));

        if(usuario.getEstadoUsuario() != EstadoUsuarioEnums.ACTIVO){
            throw new BusinessException("El usuario no esta activo");
        }

        return billeteraRepository.findByUsuario(usuario)
            .orElseThrow(() -> new BusinessException("Billetera no encontrada para el usuario con ID: " + idUsuario));
    }

    //===========================
    //Movimiento por usuario
    //===========================
    @Override
    public List<ObtenerMovimientoBilleteraDTO> obtenerMovimientosPorUsuario(Long idUsuario){

        BilleteraEntity billetera =  obtenerBilletera(idUsuario);

        return movimientoBilleteraRepository.findByBilleteraOrderByFechaDesc(billetera).stream().map(this::toMap).toList();
    }

    @Override
    public ObtenerMovimientoBilleteraDTO toMap(MovimientoBilleteraEntity movimiento) {

        ObtenerMovimientoBilleteraDTO obtenerMovimientoBilleteraDTO = new ObtenerMovimientoBilleteraDTO();

        obtenerMovimientoBilleteraDTO.setIdMovimiento(movimiento.getId());
        obtenerMovimientoBilleteraDTO.setFecha(movimiento.getFecha());
        obtenerMovimientoBilleteraDTO.setMonto(movimiento.getMonto());
        obtenerMovimientoBilleteraDTO.setDescripcion(movimiento.getDescripcion());
        obtenerMovimientoBilleteraDTO.setTipoMovimiento(movimiento.getTipoMovimiento());

        //===================
        //Billetera
        //===================
        if(movimiento.getBilletera() != null){

            BilleteraEntity billetera = movimiento.getBilletera();

            obtenerMovimientoBilleteraDTO.setIdBilletera(billetera.getId());

            //===============
            //Usuario
            //===============
            if(billetera.getUsuario() != null){

                UsuarioEntity usuario = billetera.getUsuario();

                obtenerMovimientoBilleteraDTO.setIdUsuario(usuario.getId());
                obtenerMovimientoBilleteraDTO.setNombreUsuario(usuario.getNombre());
                obtenerMovimientoBilleteraDTO.setApellidoUsuario(usuario.getApellido());
                obtenerMovimientoBilleteraDTO.setEmailUsuario(usuario.getEmail());
            }
        }

        //===================
        //Compra
        //===================
        if(movimiento.getCompra() != null){
            obtenerMovimientoBilleteraDTO.setIdCompra(movimiento.getCompra().getId());
        }

        return obtenerMovimientoBilleteraDTO;
    }
}
