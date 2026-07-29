package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ErrorDTO {

    private String mensaje;
    private Integer codigo;
    private LocalDateTime fecha;
}
