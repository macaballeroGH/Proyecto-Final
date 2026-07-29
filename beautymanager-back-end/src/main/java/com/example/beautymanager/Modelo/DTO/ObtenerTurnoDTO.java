package com.example.beautymanager.Modelo.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.beautymanager.Modelo.Enums.EspecialidadEmpleadoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoTurnoEnums;

import lombok.Data;

@Data
public class ObtenerTurnoDTO {

    private Long id;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private EstadoTurnoEnums estadoTurno;
    private BigDecimal precioTotal;

    //Cliente
    private Long idCliente;
    private String nombreCliente;
    private String apellidoCliente;
    private String fotoCliente;

    //Empleado
    private Long idEmpleado;
    private String nombreEmpleado;
    private String apellidoEmpleado;
    private String fotoEmpleado;
    private EspecialidadEmpleadoEnums especialidadEmpleado;

    //Servicio
    private List<ObtenerServicioDTO> servicios;
}
