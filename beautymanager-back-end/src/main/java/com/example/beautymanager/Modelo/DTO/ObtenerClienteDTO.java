package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ObtenerClienteDTO {

    private Long idCliente;

    //Usuario
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    //Cliente
    private String direccion;
    private LocalDate fechaNacimiento;

}
