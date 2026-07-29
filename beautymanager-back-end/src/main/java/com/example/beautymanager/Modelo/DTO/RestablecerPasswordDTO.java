package com.example.beautymanager.Modelo.DTO;

import lombok.Data;

@Data
public class RestablecerPasswordDTO {

    private String token;
    private String passwordNueva;
    private String confirmarPasswordNueva;
}
