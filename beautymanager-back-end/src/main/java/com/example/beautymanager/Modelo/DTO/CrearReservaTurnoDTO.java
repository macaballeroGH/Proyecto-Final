package com.example.beautymanager.Modelo.DTO;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data 
public class CrearReservaTurnoDTO {

    private Long idEmpleado;

    private List<Long> serviciosIds;

    private LocalDateTime fechaHoraInicio;
}
