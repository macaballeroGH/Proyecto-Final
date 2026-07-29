package com.example.beautymanager.Servicio.servicioImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.InicializarClienteDTO;
import com.example.beautymanager.Modelo.Entidad.BilleteraEntity;
import com.example.beautymanager.Modelo.Entidad.ClienteEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Repositorio.BilleteraRepository;
import com.example.beautymanager.Repositorio.ClienteRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.ClienteInicializacionService;
import com.example.beautymanager.exception.BusinessException;

import jakarta.transaction.Transactional;

@Service
public class ClienteInicializacionServiceImpl implements ClienteInicializacionService{

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private BilleteraRepository billeteraRepository;

    //=======================
    //Inicializar cliente
    //=======================
    @Override
    @Transactional
    public void inicializarCliente(InicializarClienteDTO inicializarCliente){

        if(inicializarCliente == null){
            throw new BusinessException("Los datos del cliente son obligatorios");
        }

        if(inicializarCliente.getIdUsuario() == null || inicializarCliente.getIdUsuario() <= 0){
            throw new BusinessException("El ID del usuario es obligatorio");
        }

        UsuarioEntity usuario = usuarioRepository.findById(inicializarCliente.getIdUsuario())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        if(clienteRepository.existsByUsuario(usuario)){
            throw new BusinessException("El usuario ya posee un perfil de cliente");
        }

        ClienteEntity cliente = new ClienteEntity();

        cliente.setUsuario(usuario);
        cliente.setDireccion(inicializarCliente.getDireccion() != null ? inicializarCliente.getDireccion().trim() : null);
        cliente.setFechaNacimiento(inicializarCliente.getFechaNacimiento());

        clienteRepository.save(cliente);

        if(billeteraRepository.existsByUsuario(usuario)){
            throw new BusinessException("El usuario ya tiene una billetera");
        }

        BilleteraEntity billetera = new BilleteraEntity();
        billetera.setUsuario(usuario);

        billeteraRepository.save(billetera);
    }
}
