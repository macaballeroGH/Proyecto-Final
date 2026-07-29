package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ObtenerNotificacionDTO {

    private Long idNotificacion;
    private String mensaje;
    private Boolean leida;
    private LocalDateTime fechaCreacion;

    //Usuario
    private Long idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String nombreCompletoUsuario;
    private String emailUsuario;
}
