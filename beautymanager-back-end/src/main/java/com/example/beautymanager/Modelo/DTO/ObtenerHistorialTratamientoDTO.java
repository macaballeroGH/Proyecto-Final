package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.beautymanager.Modelo.Enums.EspecialidadEmpleadoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoTurnoEnums;

import lombok.Data;

@Data
public class ObtenerHistorialTratamientoDTO {

    private Long idHistorial;
    private LocalDateTime fecha;
    private String observacion;
    private Integer duracionReal;
    private String productosUtilizados;

    //Turno
    private Long idTurno;
    private LocalDateTime fechaHoraTurno;
    private EstadoTurnoEnums estadoTurno;
    private BigDecimal precioTotalTurno;

    //Cliente
    private Long idCliente;
    private String nombreCliente;
    private String apellidoCliente;

    //Empleado
    private Long idEmpleado;
    private String nombreEmpleado;
    private String apellidoEmpleado;
    private EspecialidadEmpleadoEnums especialidadEmpleado;

    //Servicios realizados
    private List<String> servicios;
}
