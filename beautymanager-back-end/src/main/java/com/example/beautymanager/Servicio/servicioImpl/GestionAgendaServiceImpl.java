package com.example.beautymanager.Servicio.servicioImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ActualizarTurnoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerTurnoDTO;
import com.example.beautymanager.Modelo.Entidad.EmpleadoEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoEntity;
import com.example.beautymanager.Modelo.Enums.EstadoTurnoEnums;
import com.example.beautymanager.Repositorio.EmpleadoRepository;
import com.example.beautymanager.Repositorio.TurnoRepository;
import com.example.beautymanager.Servicio.GestionAgendaService;
import com.example.beautymanager.Servicio.TurnoService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class GestionAgendaServiceImpl implements GestionAgendaService{

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private TurnoService turnoService;

    //=======================
    //Listar todos
    //=======================
    @Override
    public List<ObtenerTurnoDTO> listarTodos(){
        return turnoRepository.findAll().stream().map(turnoService::toMap).toList();
    }

    //=======================
    //Obtener por id
    //=======================
    @Override
    public ObtenerTurnoDTO obtenerPorId(Long idTurno){

        TurnoEntity turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new BusinessException("Turno no encontrado"));

        return turnoService.toMap(turno);
    }

    //=======================
    //Listar por cliente
    //=======================
    @Override
    public List<ObtenerTurnoDTO> listarPorCliente(Long idCliente){

        if(idCliente == null){
            throw new BusinessException("Cliente invalido");
        }

        return turnoRepository.findByClienteId(idCliente).stream().map(turnoService::toMap).toList();
    }

    //=======================
    //Listar por empleado
    //=======================
    @Override
    public List<ObtenerTurnoDTO> listarPorEmpleado(Long idEmpleado){

        if(idEmpleado == null){
            throw new BusinessException("Empleado invalido");
        }

        return turnoRepository.findByEmpleadoId(idEmpleado).stream().map(turnoService::toMap).toList();
    }

    //=======================
    //Aceptar turno
    //=======================
    @Override
    public ObtenerTurnoDTO aceptarTurno(Long idTurno){

        TurnoEntity turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new BusinessException("Turno no encontrado"));

        if(turno.getEstadoTurno() == EstadoTurnoEnums.CANCELADO){
            throw new BusinessException("No se puede aceptar un turno cancelado");
        }

        turno.setEstadoTurno(EstadoTurnoEnums.CONFIRMADO);

        return turnoService.toMap(turnoRepository.save(turno));
    }

    //=======================
    //Rechazar turno
    //=======================
    @Override
    public ObtenerTurnoDTO rechazarTurno(Long idTurno){

        TurnoEntity turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new BusinessException("Turno no encontrado"));

        if(turno.getEstadoTurno() == EstadoTurnoEnums.CANCELADO){
            throw new BusinessException("No se puede rechazar un turno cancelado");
        }

        turno.setEstadoTurno(EstadoTurnoEnums.RECHAZADO);

        return turnoService.toMap(turnoRepository.save(turno));
    }

    //=======================
    //Cancelar turno
    //=======================
    @Override
    public ObtenerTurnoDTO cancelarTurno(Long idTurno){

        TurnoEntity turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new BusinessException("Turno no encontrado"));

        if(turno.getEstadoTurno() == EstadoTurnoEnums.CANCELADO){
            throw new BusinessException("El turno ya se encuentra cancelado");
        }

        turno.setEstadoTurno(EstadoTurnoEnums.CANCELADO);

        return turnoService.toMap(turnoRepository.save(turno));
    }

    //=======================
    //Reprogramar turno
    //=======================
    @Override
    public ObtenerTurnoDTO reprogramarTurno(Long idTurno, ActualizarTurnoDTO actualizarTurno){

        TurnoEntity turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new BusinessException("Turno no encontrado"));

        if(turno.getEstadoTurno() == EstadoTurnoEnums.CANCELADO){
            throw new BusinessException("No se puede reprogramar un turno cancelado");
        }

        if(actualizarTurno.getFechaHoraInicio() != null){
            turno.setFechaHoraInicio(actualizarTurno.getFechaHoraInicio());
        }

        if(actualizarTurno.getObservaciones() != null){
            turno.setObservaciones(actualizarTurno.getObservaciones());
        }

        return turnoService.toMap(turnoRepository.save(turno));
    }

    //=======================
    //Cambiar empleado
    //=======================
    @Override
    public ObtenerTurnoDTO cambiarEmpleado(Long idTurno, Long idEmpleado){

        TurnoEntity turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new BusinessException("Turno no encontrado"));

        if(idEmpleado == null){
            throw new BusinessException("Empleado invalido");
        }

        EmpleadoEntity empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));

        if(turno.getEstadoTurno() == EstadoTurnoEnums.CANCELADO){
            throw new BusinessException("No se puede cambiar el empleado de un turno cancelado");
        }

        turno.setEmpleado(empleado);

        return turnoService.toMap(turnoRepository.save(turno));
    }

}
