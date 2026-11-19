package com.example.beautymanager.Servicio.servicioImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ObtenerTurnoDTO;
import com.example.beautymanager.Modelo.Entidad.TurnoEntity;
import com.example.beautymanager.Repositorio.TurnoRepository;
import com.example.beautymanager.Servicio.GestionAgendaService;
import com.example.beautymanager.Servicio.TurnoService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class GestionAgendaServiceImpl implements GestionAgendaService{

    private final TurnoRepository turnoRepository;

    private final TurnoService turnoService;

    //========================================
    //Constructor
    //========================================
    public GestionAgendaServiceImpl(
            TurnoRepository turnoRepository,
            TurnoService turnoService) {

        this.turnoRepository = turnoRepository;
        this.turnoService = turnoService;
    }

    //========================================
    //Listar todos los turnos
    //========================================
    @Override
    public List<ObtenerTurnoDTO> listarTodos() {

        return turnoRepository.findAll()
            .stream()
            .map(turnoService::toMap)
            .toList();
    }

    //========================================
    //Obtener turno por ID
    //========================================
    @Override
    public ObtenerTurnoDTO obtenerPorId(Long idTurno) {

        TurnoEntity turno = turnoRepository.findById(idTurno)
            .orElseThrow(() ->
                new BusinessException("Turno no encontrado")
            );

        return turnoService.toMap(turno);
    }

    //========================================
    //Listar turnos por clienta
    //========================================
    @Override
    public List<ObtenerTurnoDTO> listarPorCliente(Long idCliente) {

        if (idCliente == null || idCliente <= 0) {
            throw new BusinessException(
                "El identificador de la clienta es inválido"
            );
        }

        return turnoRepository.findByClienteId(idCliente)
            .stream()
            .map(turnoService::toMap)
            .toList();
    }

    //========================================
    //Listar turnos por empleada
    //========================================
    @Override
    public List<ObtenerTurnoDTO> listarPorEmpleado(Long idEmpleado) {

        if (idEmpleado == null || idEmpleado <= 0) {
            throw new BusinessException(
                "El identificador de la empleada es inválido"
            );
        }

        return turnoRepository.findByEmpleadoId(idEmpleado)
            .stream()
            .map(turnoService::toMap)
            .toList();
    }

    //========================================
    //Confirmar turno (ADMIN)
    //========================================
    @Override
    public ObtenerTurnoDTO aceptarTurno(Long idTurno) {

        turnoService.confirmarTurnoAdmin(idTurno);

        return obtenerPorId(idTurno);
    }

    //========================================
    //Rechazar turno (ADMIN)
    //========================================
    @Override
    public ObtenerTurnoDTO rechazarTurno(Long idTurno) {

        turnoService.rechazarTurnoAdmin(idTurno);

        return obtenerPorId(idTurno);
    }

    //========================================
    //Cancelar turno (ADMIN)
    //Pendiente de implementación segura
    //========================================
    @Override
    public ObtenerTurnoDTO cancelarTurno(Long idTurno) {

        //====================================
        //Ejecutar cancelación segura
        //====================================

        turnoService.cancelarTurnoAdmin(idTurno);

        //====================================
        //Devolver turno actualizado
        //====================================

        return obtenerPorId(idTurno);
    }

    //========================================
    //Cambiar empleada (ADMIN)
    //Pendiente de validaciones completas
    //========================================
    @Override
    public ObtenerTurnoDTO cambiarEmpleado(
            Long idTurno,
            Long idEmpleado) {

        turnoService.cambiarEmpleadoAdmin(
            idTurno,
            idEmpleado
        );

        //====================================
        //Devolver turno actualizado
        //====================================

        return obtenerPorId(idTurno);
    }
}
