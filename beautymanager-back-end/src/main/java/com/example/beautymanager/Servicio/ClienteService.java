package com.example.beautymanager.Servicio;

import java.util.List;

import com.example.beautymanager.Modelo.DTO.ActualizarClienteDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerClienteDTO;
import com.example.beautymanager.Modelo.Entidad.ClienteEntity;

public interface ClienteService {

    ObtenerClienteDTO toMap(ClienteEntity cliente);

    ObtenerClienteDTO actualizarCliente(String email, ActualizarClienteDTO actualizarCliente);

    void eliminarCliente(Long idCliente);

    ObtenerClienteDTO buscarPorId(Long idCliente);

    ObtenerClienteDTO buscarPorUsuarioId(Long idUsuario);

    ObtenerClienteDTO buscarPorEmail(String email);

    List<ObtenerClienteDTO> listarTodos();

    boolean existePorUsuario(Long idUsuario);
}
