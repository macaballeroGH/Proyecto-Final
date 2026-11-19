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

    private String tipoEvento;
    private String origen;
    private String resultado;

    private Long idUsuario;

    private String nombreUsuario;
    private String apellidoUsuario;
    private String emailUsuario;
    private String rolUsuario;

    private String identificadorExterno;

    private String valoresAnteriores;
    private String valoresNuevos;
}
