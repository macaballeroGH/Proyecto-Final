package com.example.beautymanager.Servicio;

import com.example.beautymanager.Modelo.DTO.LoginDTO;
import com.example.beautymanager.Modelo.DTO.LoginResponseDTO;
import com.example.beautymanager.Modelo.DTO.ResponseDTO;
import com.example.beautymanager.Modelo.DTO.UsuarioAutenticadoDTO;

public interface AuthService {

    LoginResponseDTO login(LoginDTO login);

    ResponseDTO logout();

    UsuarioAutenticadoDTO obtenerUsuarioPorId(Long idUsuario);

    ResponseDTO mensajeSoloAdmin();

}
