package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDate;

import lombok.Data;

@Data
public class InicializarClienteDTO {

    private Long idUsuario;
    private String direccion;
    private LocalDate fechaNacimiento;
}
