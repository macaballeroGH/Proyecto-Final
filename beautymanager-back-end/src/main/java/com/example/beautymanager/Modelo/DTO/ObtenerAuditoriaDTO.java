package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ObtenerAuditoriaDTO {

    private Long idAuditoria;
    private String accion;
    private String tablaAfectada;
    private Long idRegistro;
    private String detalle;
    private LocalDateTime fechaHora;

    //Usuario
    private Long idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String emailUsuario;
}
