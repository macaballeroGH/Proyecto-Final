package com.example.beautymanager.Modelo.DTO;

import com.example.beautymanager.Modelo.Enums.RolesEnums;

import lombok.Data;

@Data
public class JWTAuthResponseDTO {

    private boolean success;
    private String token;
    private Long userId;
    private String email;
    private RolesEnums rol;
}
