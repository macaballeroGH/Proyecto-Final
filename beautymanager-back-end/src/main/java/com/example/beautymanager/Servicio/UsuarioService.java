package com.example.beautymanager.Servicio;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.beautymanager.Modelo.DTO.ActualizarUsuarioDTO;
import com.example.beautymanager.Modelo.DTO.CambiarPasswordDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerUsuarioDTO;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoUsuarioEnums;

public interface UsuarioService {

    ObtenerUsuarioDTO toMap(UsuarioEntity usuario);

    List<ObtenerUsuarioDTO> findAllUsuario();

    ObtenerUsuarioDTO obtenerPorId(Long id);

    ObtenerUsuarioDTO obtenerPorEmail(String email);

    List<ObtenerUsuarioDTO> buscarPorNombre(String nombre);

    List<ObtenerUsuarioDTO> buscarPorApellido(String apellido);

    List<ObtenerUsuarioDTO> buscarPorRol(Long idRol);

    List<ObtenerUsuarioDTO> buscarPorEstado(EstadoUsuarioEnums estado);

    ObtenerUsuarioDTO actualizarUsuario(Long id, ActualizarUsuarioDTO actualizarUsuario);

    boolean existeEmail(String email);

    void cambiarPassword(Long idUsuario, CambiarPasswordDTO cambiarPassword);

    void actualizarFotoPerfil(Long idUsuario, MultipartFile foto);

    void eliminarFotoPerfil(Long idUsuario);

}
