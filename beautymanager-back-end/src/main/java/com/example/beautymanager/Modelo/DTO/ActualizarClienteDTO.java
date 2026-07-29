package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ActualizarClienteDTO {

    private String direccion;
    private LocalDate fechaNacimiento;
}
