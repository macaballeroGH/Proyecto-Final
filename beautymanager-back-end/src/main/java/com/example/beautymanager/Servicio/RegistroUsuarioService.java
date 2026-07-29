package com.example.beautymanager.Servicio;

import com.example.beautymanager.Modelo.DTO.RegistroDTO;
import com.example.beautymanager.Modelo.DTO.ResponseDTO;

public interface RegistroUsuarioService {
    public ResponseDTO registrarUsuario(RegistroDTO usuarioDTO) throws Exception;
}
