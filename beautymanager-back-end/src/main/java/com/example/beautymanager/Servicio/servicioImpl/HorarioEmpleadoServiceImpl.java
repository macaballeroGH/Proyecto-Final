package com.example.beautymanager.Servicio.servicioImpl;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ActualizarHorarioEmpleadoDTO;
import com.example.beautymanager.Modelo.DTO.CrearHorarioEmpleadoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerHorarioEmpleadoDTO;
import com.example.beautymanager.Modelo.Entidad.EmpleadoEntity;
import com.example.beautymanager.Modelo.Entidad.HorarioEmpleadoEntity;
import com.example.beautymanager.Repositorio.EmpleadoRepository;
import com.example.beautymanager.Repositorio.HorarioEmpleadoRepository;
import com.example.beautymanager.Servicio.HorarioEmpleadoService;
import com.example.beautymanager.exception.BusinessException;
import com.example.beautymanager.utils.HorarioAtencion;

import jakarta.transaction.Transactional;

@Service
public class HorarioEmpleadoServiceImpl implements HorarioEmpleadoService{

    @Autowired
    private HorarioEmpleadoRepository horarioEmpleadoRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    //=======================
    //Crear Horario
    //=======================
    @Override
    @Transactional
    public ObtenerHorarioEmpleadoDTO crearHorario(CrearHorarioEmpleadoDTO crearHorario){

        validarHorario(crearHorario.getDiaSemana(), crearHorario.getHoraInicio(), crearHorario.getHoraFin());

        EmpleadoEntity empleado = empleadoRepository.findById(crearHorario.getIdEmpleado())
            .orElseThrow(() -> new BusinessException("Empleado no encontrado"));

        boolean existeSuperposicion = horarioEmpleadoRepository.existsByEmpleadoIdAndDiaSemanaAndHoraInicioLessThanAndHoraFinGreaterThan(crearHorario.getIdEmpleado(), crearHorario.getDiaSemana(), crearHorario.getHoraFin(), crearHorario.getHoraInicio());

        if(existeSuperposicion){
            throw new BusinessException("El empleado ya tiene un horario superpuesto ese dia");
        }

        HorarioEmpleadoEntity horario = new HorarioEmpleadoEntity();

        horario.setEmpleado(empleado);
        horario.setDiaSemana(crearHorario.getDiaSemana());
        horario.setHoraInicio(crearHorario.getHoraInicio());
        horario.setHoraFin(crearHorario.getHoraFin());

        return toMap(horarioEmpleadoRepository.save(horario));
    }

    //=======================
    //Actualizar Horario
    //=======================
    @Override
    @Transactional
    public ObtenerHorarioEmpleadoDTO actualizarHorario(Long idHorario, ActualizarHorarioEmpleadoDTO actualizarHorario){

        HorarioEmpleadoEntity horario = horarioEmpleadoRepository.findById(idHorario)
            .orElseThrow(() -> new BusinessException("Horario no encontrado"));

        validarHorario(actualizarHorario.getDiaSemana(), actualizarHorario.getHoraInicio(), actualizarHorario.getHoraFin());

        boolean existeSuperposicion = horarioEmpleadoRepository.existsByEmpleadoIdAndDiaSemanaAndHoraInicioLessThanAndHoraFinGreaterThanAndIdNot(horario.getEmpleado().getId(), actualizarHorario.getDiaSemana(), actualizarHorario.getHoraFin(), actualizarHorario.getHoraInicio(), idHorario);

        if(existeSuperposicion){
            throw new BusinessException("El empleado ya tiene un horario superpuesto ese dia");
        }

        horario.setDiaSemana(actualizarHorario.getDiaSemana());
        horario.setHoraInicio(actualizarHorario.getHoraInicio());
        horario.setHoraFin(actualizarHorario.getHoraFin());

        return toMap(horarioEmpleadoRepository.save(horario));
    }

    //=======================
    //Obtener por ID
    //=======================
    @Override
    public ObtenerHorarioEmpleadoDTO obtenerPorId(Long idHorario){
        
        HorarioEmpleadoEntity horario = horarioEmpleadoRepository.findById(idHorario)
                .orElseThrow(() -> new BusinessException("Horario no encontrado"));

        return toMap(horario);
    }

    //=======================
    //Obtener todos
    //=======================
    @Override
    public List<ObtenerHorarioEmpleadoDTO> obtenerTodos(){
        return horarioEmpleadoRepository.findAllByOrderByDiaSemanaAsc().stream().map(this::toMap).toList();
    }

    //=======================
    //Obtener por empleado
    //=======================
    @Override
    public List<ObtenerHorarioEmpleadoDTO> obtenerPorEmpleado(Long idEmpleado){
        return horarioEmpleadoRepository.findByEmpleadoId(idEmpleado).stream().map(this::toMap).toList();
    }

    //===============================
    //Obtener por empleado y dia
    //===============================
    @Override
    public List<ObtenerHorarioEmpleadoDTO> obtenerPorEmpleadoYDia(Long idEmpleado, Integer diaSemana){
        return horarioEmpleadoRepository.findByEmpleadoIdAndDiaSemana(idEmpleado, diaSemana).stream().map(this::toMap).toList();
    }

    //=======================
    //Eliminar
    //=======================
    @Override
    @Transactional
    public void eliminar(Long idHorario){

        HorarioEmpleadoEntity horario = horarioEmpleadoRepository.findById(idHorario)
                .orElseThrow(() -> new BusinessException("Horario no encontrado"));

        horarioEmpleadoRepository.delete(horario);
    }

    //=======================
    //Validaciones
    //=======================
    private void validarHorario(Integer diaSemana, LocalTime horaInicio, LocalTime horaFin){

        if(diaSemana == null){
            throw new BusinessException("El dia es obligatorio");
        }

        if(diaSemana < 1 || diaSemana > 6){
            throw new BusinessException("El horario debe ser de lunes a sabado");
        }

        if(horaInicio == null){
            throw new BusinessException("La hora de inicio es obligatoria");
        }

        if(horaFin == null){
            throw new BusinessException("La hora de fin es obligatoria");
        }

        if(!horaInicio.isBefore(horaFin)){
            throw new BusinessException("La hora de inicio debe ser menor a la hora de fin");
        }

        if(horaInicio.isBefore(HorarioAtencion.APERTURA)){
            throw new BusinessException("La hora de inicio no puede ser anterior a las 08:00");
        }

        if(horaFin.isAfter(HorarioAtencion.CIERRE)){
            throw new BusinessException("La hora de fin no puede ser posterior a las 20:00");
        }
    }

    //=======================
    //Nombre del dia
    //=======================
    private String obtenerNombreDia(Integer diaSemana){
        return switch(diaSemana){
            case 1 -> "LUNES";
            case 2 -> "MARTES";
            case 3 -> "MIERCOLES";
            case 4 -> "JUEVES";
            case 5 -> "VIERNES";
            case 6 -> "SABADO";
            case 7 -> "DOMINGO";
            default -> "DESCONOCIDO";
        };
    }

    @Override
    public ObtenerHorarioEmpleadoDTO toMap(HorarioEmpleadoEntity horario) {

        ObtenerHorarioEmpleadoDTO obtenerHorarioEmpleadoDTO = new ObtenerHorarioEmpleadoDTO();

        obtenerHorarioEmpleadoDTO.setIdHorario(horario.getId());
        obtenerHorarioEmpleadoDTO.setDiaSemana(horario.getDiaSemana());
        obtenerHorarioEmpleadoDTO.setNombreDia(obtenerNombreDia(horario.getDiaSemana()));
        obtenerHorarioEmpleadoDTO.setHoraInicio(horario.getHoraInicio());
        obtenerHorarioEmpleadoDTO.setHoraFin(horario.getHoraFin());

        if(horario.getEmpleado() != null){

            obtenerHorarioEmpleadoDTO.setIdEmpleado(horario.getEmpleado().getId());

            if(horario.getEmpleado().getUsuario() != null){
                obtenerHorarioEmpleadoDTO.setNombreEmpleado(horario.getEmpleado().getUsuario().getNombre());
                obtenerHorarioEmpleadoDTO.setApellidoEmpleado(horario.getEmpleado().getUsuario().getApellido());
            }

            if(horario.getEmpleado().getEspecialidad() != null){
                obtenerHorarioEmpleadoDTO.setEspecialidadEmpleado(horario.getEmpleado().getEspecialidad());
            }
        }
        return obtenerHorarioEmpleadoDTO;
    }
}
