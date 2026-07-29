package com.example.beautymanager.Modelo.DTO;

import com.example.beautymanager.Modelo.Enums.RolesEnums;

import lombok.Data;

@Data
public class UsuarioAutenticadoDTO {

    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private RolesEnums rol;
}
