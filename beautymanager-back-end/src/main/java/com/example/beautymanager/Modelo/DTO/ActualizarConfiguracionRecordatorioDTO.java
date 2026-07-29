package com.example.beautymanager.Modelo.DTO;

import com.example.beautymanager.Modelo.Enums.EstadoRecordatorioEnums;

import lombok.Data;

@Data
public class ActualizarConfiguracionRecordatorioDTO {

    private EstadoRecordatorioEnums estadoEmail;
    private EstadoRecordatorioEnums estadoWhatsapp;
    private EstadoRecordatorioEnums estadoNotificacionInterna;
    private Integer primerRecordatorioHoras;
    private Integer segundoRecordatorioHoras;
}
