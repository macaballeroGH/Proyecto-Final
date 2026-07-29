package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RegistroDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String telefono;

    //Para el cliente
    private String direccion;
    private LocalDate fechaNacimiento;
}