package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDateTime;

import lombok.Data;

@Data 
public class ArticuloBellezaDTO {

    private String titulo;
    private String descripcion;
    private String enlace;
    private LocalDateTime fechaPublicacion;
    private String fuente;
    private String imagenUrl;
    private String categoria;
}
