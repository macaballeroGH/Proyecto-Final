package com.example.beautymanager.Servicio.servicioImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ActualizarBloqueoAgendaDTO;
import com.example.beautymanager.Modelo.DTO.CrearBloqueoAgendaDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerBloqueoAgendaDTO;
import com.example.beautymanager.Modelo.Entidad.BloqueoAgendaEntity;
import com.example.beautymanager.Modelo.Entidad.EmpleadoEntity;
import com.example.beautymanager.Repositorio.BloqueoAgendaRepository;
import com.example.beautymanager.Repositorio.EmpleadoRepository;
import com.example.beautymanager.Servicio.BloqueoAgendaService;
import com.example.beautymanager.exception.BusinessException;

import jakarta.transaction.Transactional;

@Service
public class BloqueoAgendaServiceImpl implements BloqueoAgendaService{

    @Autowired
    private BloqueoAgendaRepository bloqueoAgendaRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    //=======================
    //Crear Bloqueo
    //=======================
    @Override
    @Transactional
    public ObtenerBloqueoAgendaDTO crearBloqueo(CrearBloqueoAgendaDTO crearBloqueo){

        validarFechas(crearBloqueo.getInicio(), crearBloqueo.getFin());

        EmpleadoEntity empleado = empleadoRepository.findById(crearBloqueo.getIdEmpleado())
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));

        validarSolapamiento(empleado.getId(), crearBloqueo.getInicio(), crearBloqueo.getFin());

        BloqueoAgendaEntity bloqueo = new BloqueoAgendaEntity();

        bloqueo.setEmpleado(empleado);
        bloqueo.setInicio(crearBloqueo.getInicio());
        bloqueo.setFin(crearBloqueo.getFin());
        bloqueo.setMotivo(crearBloqueo.getMotivo());

        return toMap(bloqueoAgendaRepository.save(bloqueo));
    }

    //=======================
    //Actualizar bloqueo
    //=======================
    @Override
    @Transactional
    public ObtenerBloqueoAgendaDTO actualizarBloqueo(Long idBloqueo, ActualizarBloqueoAgendaDTO actualizarBloqueo){

        BloqueoAgendaEntity bloqueo = bloqueoAgendaRepository.findById(idBloqueo)
                .orElseThrow(() -> new BusinessException("Bloqueo no encontrado"));

        validarFechas(actualizarBloqueo.getInicio(), actualizarBloqueo.getFin());

        List<BloqueoAgendaEntity> bloqueos = bloqueoAgendaRepository.findByEmpleadoIdAndInicioLessThanAndFinGreaterThan(bloqueo.getEmpleado().getId(), actualizarBloqueo.getFin(), actualizarBloqueo.getInicio());

        boolean existeSolapamiento = bloqueos.stream().anyMatch(b -> !b.getId().equals(idBloqueo));

        if(existeSolapamiento){
            throw new BusinessException("Ya existe un bloqueo en ese rango horario");
        }

        bloqueo.setInicio(actualizarBloqueo.getInicio());
        bloqueo.setFin(actualizarBloqueo.getFin());
        bloqueo.setMotivo(actualizarBloqueo.getMotivo());

        return toMap(bloqueoAgendaRepository.save(bloqueo));
    }

    //=======================
    //Obtener por ID
    //=======================
    @Override
    public ObtenerBloqueoAgendaDTO obtenerPorId(Long idBloqueo){

        BloqueoAgendaEntity bloqueo = bloqueoAgendaRepository.findById(idBloqueo)
                .orElseThrow(() -> new BusinessException("Bloqueo no encontrado"));

        return toMap(bloqueo);
    }

    //=======================
    //Obtener todos
    //=======================
    @Override
    public List<ObtenerBloqueoAgendaDTO> obtenerTodos(){
        return bloqueoAgendaRepository.findAllByOrderByInicioDesc().stream().map(this::toMap).toList();
    }

    //=======================
    //Obtener por empleado
    //=======================
    @Override
    public List<ObtenerBloqueoAgendaDTO> obtenerPorEmpleado(Long iEmpleado){
        return bloqueoAgendaRepository.findByEmpleadoId(iEmpleado).stream().map(this::toMap).toList();
    }

    //=======================
    //Bloqueos superpuestos
    //=======================
    @Override
    public List<ObtenerBloqueoAgendaDTO> obtenerBloqueosSuperpuestos(Long idEmpleado, LocalDateTime inicio, LocalDateTime fin){
        return bloqueoAgendaRepository.findByEmpleadoIdAndInicioLessThanAndFinGreaterThan(idEmpleado, fin, inicio).stream().map(this::toMap).toList();
    }

    //=======================
    //Eliminar
    //=======================
    @Override
    @Transactional
    public void eliminar(Long idBloqueo){

        BloqueoAgendaEntity bloqueo = bloqueoAgendaRepository.findById(idBloqueo)
                .orElseThrow(() -> new BusinessException("Bloqueo no encontrado"));

        bloqueoAgendaRepository.delete(bloqueo);
    }

    //=======================
    //Validar fecha
    //=======================
    private void validarFechas(LocalDateTime inicio, LocalDateTime fin){

        if(inicio == null){
            throw new BusinessException("La fecha de inicio es obligatoria");
        }

        if(fin == null){
            throw new BusinessException("La fecha de fin es obligatoria");
        }

        if(!inicio.isBefore(fin)){
            throw new BusinessException("La fecha de inicio debe ser anterior a la fecha de fin");
        }
    }

    //=======================
    //Validar solapamiento
    //=======================
    private void validarSolapamiento(Long idEmpleado, LocalDateTime inicio, LocalDateTime fin){

        List<BloqueoAgendaEntity> bloqueos = bloqueoAgendaRepository.findByEmpleadoIdAndInicioLessThanAndFinGreaterThan(idEmpleado, fin, inicio);

        if(!bloqueos.isEmpty()){
            throw new BusinessException("Ya existe un bloqueo en ese rango horario");
        }
    }

    @Override
    public ObtenerBloqueoAgendaDTO toMap(BloqueoAgendaEntity bloqueo) {

        ObtenerBloqueoAgendaDTO obtenerBloqueoAgendaDTO = new ObtenerBloqueoAgendaDTO();

        obtenerBloqueoAgendaDTO.setIdBloqueo(bloqueo.getId());
        obtenerBloqueoAgendaDTO.setInicio(bloqueo.getInicio());
        obtenerBloqueoAgendaDTO.setFin(bloqueo.getFin());
        obtenerBloqueoAgendaDTO.setMotivo(bloqueo.getMotivo());

        if(bloqueo.getEmpleado() != null){

            obtenerBloqueoAgendaDTO.setIdEmpleado(bloqueo.getEmpleado().getId());

            if(bloqueo.getEmpleado().getUsuario() != null){

                obtenerBloqueoAgendaDTO.setNombreEmpleado(bloqueo.getEmpleado().getUsuario().getNombre());
                obtenerBloqueoAgendaDTO.setApellidoEmpleado(bloqueo.getEmpleado().getUsuario().getApellido());
            }

            obtenerBloqueoAgendaDTO.setEspecialidadEmpleado(bloqueo.getEmpleado().getEspecialidad());
        }

        return obtenerBloqueoAgendaDTO;
    }
}
