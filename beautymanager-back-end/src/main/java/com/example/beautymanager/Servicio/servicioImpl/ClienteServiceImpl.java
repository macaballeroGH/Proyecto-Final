package com.example.beautymanager.Servicio.servicioImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ActualizarClienteDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerClienteDTO;
import com.example.beautymanager.Modelo.Entidad.ClienteEntity;
import com.example.beautymanager.Repositorio.ClienteRepository;
import com.example.beautymanager.Servicio.ClienteService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    //=======================
    // Actualizar cliente
    //=======================
    @Override
    public ObtenerClienteDTO actualizarCliente(String email, ActualizarClienteDTO actualizarCliente) {

        ClienteEntity existente = clienteRepository.findByUsuarioEmail(email)
            .orElseThrow(() -> new BusinessException("Cliente no encontrado"));

        if(actualizarCliente == null){
            throw new BusinessException("Datos invalidos");
        }

        if(actualizarCliente.getDireccion() != null && !actualizarCliente.getDireccion().isBlank()){
            existente.setDireccion(actualizarCliente.getDireccion().trim());
        }

        if(actualizarCliente.getEmail() != null && !actualizarCliente.getEmail().isBlank()){
            existente.getUsuario().setEmail(actualizarCliente.getEmail().trim());
        }

        if(actualizarCliente.getTelefono() != null && !actualizarCliente.getTelefono().isBlank()){
            existente.getUsuario().setTelefono(actualizarCliente.getTelefono().trim());
        }

        return toMap(clienteRepository.save(existente));
    }

    //=======================
    //Eliminar cliente
    //=======================
    @Override
    public void eliminarCliente(Long idCliente) {
        
        ClienteEntity cliente = clienteRepository.findById(idCliente)
            .orElseThrow(() -> new BusinessException("Cliente no encontrado"));

        clienteRepository.delete(cliente);
    }

    //=======================
    //Buscar cliente por id
    //=======================
    @Override
    public ObtenerClienteDTO buscarPorId(Long idCliente) {
        return toMap(clienteRepository.findById(idCliente)
            .orElseThrow(() -> new BusinessException("Cliente no encontrado")));
    }

    //=======================
    //Buscar por usuario id
    //=======================
    @Override
    public ObtenerClienteDTO buscarPorUsuarioId(Long idUsuario) {
        return toMap(clienteRepository.findByUsuarioId(idUsuario)
            .orElseThrow(() -> new BusinessException("Cliente no encontrado")));
    }

    //=======================
    //Buscar por email
    //=======================
    @Override
    public ObtenerClienteDTO buscarPorEmail(String email) {
        return toMap(clienteRepository.findByUsuarioEmail(email)
            .orElseThrow(() -> new BusinessException("Cliente no encontrado")));
    }

    //=======================
    //Listar todos
    //=======================
    @Override
    public List<ObtenerClienteDTO> listarTodos() {
        return clienteRepository.findAll().stream().map(this::toMap).toList();
    }

    //=======================
    //Existe por usuario
    //=======================
    @Override
    public boolean existePorUsuario(Long idUsuario) {
        return clienteRepository.findByUsuarioId(idUsuario).isPresent();
    }

    @Override
    public ObtenerClienteDTO toMap(ClienteEntity cliente) {

        ObtenerClienteDTO obtenerClienteDTO = new ObtenerClienteDTO();

        obtenerClienteDTO.setIdCliente(cliente.getId());

        //===================
        //Usuario
        //===================
        obtenerClienteDTO.setIdUsuario(cliente.getUsuario().getId());
        obtenerClienteDTO.setNombre(cliente.getUsuario().getNombre());
        obtenerClienteDTO.setApellido(cliente.getUsuario().getApellido());
        obtenerClienteDTO.setEmail(cliente.getUsuario().getEmail());
        obtenerClienteDTO.setTelefono(cliente.getUsuario().getTelefono());

        //===================
        //Cliente
        //===================
        obtenerClienteDTO.setDireccion(cliente.getDireccion());
        obtenerClienteDTO.setFechaNacimiento(cliente.getFechaNacimiento());

        return obtenerClienteDTO;
    }
}
