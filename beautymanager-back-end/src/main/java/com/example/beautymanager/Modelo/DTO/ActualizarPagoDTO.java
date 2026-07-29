package com.example.beautymanager.Modelo.DTO;

import com.example.beautymanager.Modelo.Enums.EstadoPagoEnums;
import com.example.beautymanager.Modelo.Enums.MetodoPagoEnums;

import lombok.Data;

@Data
public class ActualizarPagoDTO {

    private MetodoPagoEnums metodoPago;
    private EstadoPagoEnums estadoPago;
}
