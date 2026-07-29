package com.example.beautymanager.Modelo.DTO.Response;

import lombok.Data;

@Data
public class InvitacionResponseDTO {

    private Long id;
    private String emailDestino;
    private String token;
    private String estado;

}
