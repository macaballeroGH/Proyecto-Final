package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDate;

import com.example.beautymanager.Modelo.Enums.TipoPeriodoEstadisticaEnums;

import lombok.Data;

@Data 
public class FiltroEstadisticasDTO {

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private TipoPeriodoEstadisticaEnums tipoPeriodo;

    private Boolean comparar;
}
