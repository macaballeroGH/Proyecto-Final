package com.example.beautymanager.Modelo.DTO;

import lombok.Data;

@Data
public class ResponseDTO {

    private int numOfErrors;
    private String mensaje;
    private boolean success;
}
