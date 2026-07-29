package com.example.beautymanager.Modelo.DTO;

import lombok.Data;

@Data
public class CambiarPasswordDTO {

    private String passwordActual;
    private String passwordNueva;
    private String confirmarPasswordNueva;
}
