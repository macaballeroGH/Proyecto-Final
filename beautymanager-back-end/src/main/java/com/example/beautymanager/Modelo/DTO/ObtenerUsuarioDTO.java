package com.example.beautymanager.Modelo.DTO;

import com.example.beautymanager.Modelo.Enums.EstadoUsuarioEnums;
import com.example.beautymanager.Modelo.Enums.RolesEnums;

import lombok.Data;

@Data
public class ObtenerUsuarioDTO {

    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String fotoPerfil;

    private RolesEnums rol;
    private EstadoUsuarioEnums estadoUsuario;
}
