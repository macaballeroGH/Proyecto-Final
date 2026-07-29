package com.example.beautymanager.Servicio.servicioImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.HistorialClienteDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerHistorialTratamientoDTO;
import com.example.beautymanager.Modelo.DTO.RegistrarTratamientoDTO;
import com.example.beautymanager.Modelo.Entidad.ClienteEntity;
import com.example.beautymanager.Modelo.Entidad.EmpleadoEntity;
import com.example.beautymanager.Modelo.Entidad.HistorialTratamientoEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoEntity;
import com.example.beautymanager.Repositorio.ClienteRepository;
import com.example.beautymanager.Repositorio.EmpleadoRepository;
import com.example.beautymanager.Repositorio.HistorialTratamientoRepository;
import com.example.beautymanager.Repositorio.TurnoRepository;
import com.example.beautymanager.Servicio.HistorialTratamientoService;
import com.example.beautymanager.Servicio.PagoService;
import com.example.beautymanager.exception.BusinessException;

import jakarta.transaction.Transactional;

@Service
public class HistorialTratamientoServiceImpl implements HistorialTratamientoService {

    @Autowired
    private HistorialTratamientoRepository historialTratamientoRepository;

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private PagoService pagoService;

    //=========================
    //Registrar tratamiento
    //=========================
    @Override
    @Transactional
    public ObtenerHistorialTratamientoDTO registrarTratamiento(Long idUsuario, RegistrarTratamientoDTO registrarTratamiento) {

        if(idUsuario == null){
            throw new BusinessException("El usuario es obligatorio");
        }

        if(registrarTratamiento == null){
            throw new BusinessException("Los datos del tratamiento son obligatorios");
        }

        if(registrarTratamiento.getIdTurno() == null){
            throw new BusinessException("Debe indicar un turno");
        }

        if(registrarTratamiento.getDuracionReal() != null && registrarTratamiento.getDuracionReal() <= 0){
            throw new BusinessException("La duracion real debe ser mayor a cero");
        }

        if(registrarTratamiento.getObservacion() != null && registrarTratamiento.getObservacion().length() > 3500){
            throw new BusinessException("La observacion supera el maximo permitido de 3500 caracteres");
        }

        if(registrarTratamiento.getProductosUtilizados() != null && registrarTratamiento.getProductosUtilizados().length() > 2500){
            throw new BusinessException("Los productos utilizados superan el maximo permitido de 2500 caracteres");
        }

        EmpleadoEntity empleado = empleadoRepository.findByUsuario_Id(idUsuario)
                .orElseThrow(() -> new BusinessException("El usuario no pertenece a un empleado"));
        
        TurnoEntity turno = turnoRepository.findById(registrarTratamiento.getIdTurno())
                .orElseThrow(() -> new BusinessException("Turno no encontrado"));

        if(turno.getEmpleado() == null){
            throw new BusinessException("El turno no tiene un empleado asignado");
        }

        if(!turno.getEmpleado().getId().equals(empleado.getId())){
            throw new BusinessException("No puede registrar tratamientos de turnos asignados a otro empleado");
        }

        HistorialTratamientoEntity historial = new HistorialTratamientoEntity();

        historial.setTurno(turno);
        historial.setFecha(LocalDateTime.now());
        historial.setObservacion(registrarTratamiento.getObservacion());
        historial.setDuracionReal(registrarTratamiento.getDuracionReal());
        historial.setProductosUtilizados(registrarTratamiento.getProductosUtilizados());

        return toMap(historialTratamientoRepository.save(historial));
    }

    //===========================
    //Buscar por id
    //===========================
    @Override
    public ObtenerHistorialTratamientoDTO buscarPorId(Long idHistorial) {
        return toMap(historialTratamientoRepository.findById(idHistorial)
                .orElseThrow(() -> new BusinessException("Historial no encontrado")));
    }

    //============================
    //Listar todos
    //============================
    @Override
    public List<ObtenerHistorialTratamientoDTO> listarTodos() {
        return historialTratamientoRepository.findAll().stream().map(this::toMap).toList();
    }

    //============================
    //Historial por cliente
    //============================
    @Override
    public List<ObtenerHistorialTratamientoDTO> listarPorCliente(Long idUsuario) {
        
        ClienteEntity cliente = clienteRepository.findByUsuarioId(idUsuario)
                .orElseThrow(() -> new BusinessException("Cliente no encontrado"));

        return historialTratamientoRepository.findByTurnoClienteIdOrderByFechaDesc(cliente.getId()).stream().map(this::toMap).toList();
    }

    //=============================
    //Historial por empleado
    //=============================
    @Override
    public List<ObtenerHistorialTratamientoDTO> listarPorEmpleado(Long idEmpleado) {
        return historialTratamientoRepository.findByTurnoEmpleadoIdOrderByFechaDesc(idEmpleado).stream().map(this::toMap).toList();
    }

    //==============================
    //Buscar por fecha exacta
    //==============================
    @Override
    public List<ObtenerHistorialTratamientoDTO> listarPorFecha(LocalDateTime fecha) {
        return historialTratamientoRepository.findByFecha(fecha).stream().map(this::toMap).toList();
    }

    //===============================
    //Buscar entre fecha
    //===============================
    @Override
    public List<ObtenerHistorialTratamientoDTO> listarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return historialTratamientoRepository.findByFechaBetween(fechaInicio, fechaFin).stream().map(this::toMap).toList();
    }

    //=======================
    //Buscar por texto
    //=======================
    @Override
    public List<ObtenerHistorialTratamientoDTO> buscarPorTexto(String texto) {
        return historialTratamientoRepository.findByObservacionContainingIgnoreCase(texto).stream().map(this::toMap).toList();
    }

    //=======================
    //Ver historial cliente
    //=======================
    @Override
    public List<ObtenerHistorialTratamientoDTO> verHistorialCliente(Long idCliente){

        if(idCliente == null){
            throw new BusinessException("El cliente es obligatorio");
        }

        ClienteEntity cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new BusinessException("Cliente no encontrado"));

        return historialTratamientoRepository.findByTurnoClienteIdOrderByFechaDesc(cliente.getId()).stream().map(this::toMap).toList();
    }

    //======================================
    //Obtener historial completo cliente
    //======================================
    @Override
    public HistorialClienteDTO obtenerHistorialCompletoCliente(Long idCliente){

        if(idCliente == null){
            throw new BusinessException("El cliente es obligatorio");
        }

        ClienteEntity cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new BusinessException("Cliente no encontrado"));

        HistorialClienteDTO historialClienteDTO = new HistorialClienteDTO();

        historialClienteDTO.setIdCliente(cliente.getId());
        historialClienteDTO.setNombreCliente(cliente.getUsuario().getNombre());
        historialClienteDTO.setApellidoCliente(cliente.getUsuario().getApellido());

        historialClienteDTO.setTratamientos(historialTratamientoRepository.findByTurnoClienteIdOrderByFechaDesc(cliente.getId()).stream().map(this::toMap).toList());

        historialClienteDTO.setPagos(pagoService.obtenerPagosPorCliente(cliente.getId()));

        return historialClienteDTO;
    }

    //=======================
    //Buscar por turno
    //=======================
    @Override
    public List<ObtenerHistorialTratamientoDTO> listarPorTurno(Long idTurno) {
        return historialTratamientoRepository.findByTurnoId(idTurno).stream().map(this::toMap).toList();
    }

    //========================
    //Eliminar historial
    //========================
    @Override
    @Transactional
    public void eliminar(Long idHistorial) {
        HistorialTratamientoEntity historial = historialTratamientoRepository.findById(idHistorial)
            .orElseThrow(() -> new BusinessException("Historial no encontrado"));

        historialTratamientoRepository.delete(historial);
    }

    @Override
    public ObtenerHistorialTratamientoDTO toMap(HistorialTratamientoEntity historial) {

        ObtenerHistorialTratamientoDTO obtenerHistorialTratamientoDTO = new ObtenerHistorialTratamientoDTO();

        obtenerHistorialTratamientoDTO.setIdHistorial(historial.getId());
        obtenerHistorialTratamientoDTO.setFecha(historial.getFecha());
        obtenerHistorialTratamientoDTO.setObservacion(historial.getObservacion());
        obtenerHistorialTratamientoDTO.setDuracionReal(historial.getDuracionReal());
        obtenerHistorialTratamientoDTO.setProductosUtilizados(historial.getProductosUtilizados());

        if(historial.getTurno() != null){

            TurnoEntity turno = historial.getTurno();

            obtenerHistorialTratamientoDTO.setIdTurno(turno.getId());
            obtenerHistorialTratamientoDTO.setFechaHoraTurno(turno.getFechaHoraInicio());
            obtenerHistorialTratamientoDTO.setPrecioTotalTurno(turno.getPrecioTotal());

            if(turno.getCliente() != null){
                obtenerHistorialTratamientoDTO.setIdCliente(turno.getCliente().getId());

                if(turno.getCliente().getUsuario() != null){
                    obtenerHistorialTratamientoDTO.setNombreCliente(turno.getCliente().getUsuario().getNombre());
                    obtenerHistorialTratamientoDTO.setApellidoCliente(turno.getCliente().getUsuario().getApellido());
                }
            }

            if(turno.getEmpleado() != null){
                obtenerHistorialTratamientoDTO.setIdEmpleado(turno.getEmpleado().getId());
                obtenerHistorialTratamientoDTO.setEspecialidadEmpleado(turno.getEmpleado().getEspecialidad());

                if(turno.getEmpleado().getUsuario() != null){
                    obtenerHistorialTratamientoDTO.setNombreEmpleado(turno.getEmpleado().getUsuario().getNombre());
                    obtenerHistorialTratamientoDTO.setApellidoEmpleado(turno.getEmpleado().getUsuario().getApellido());
                }
            }

            obtenerHistorialTratamientoDTO.setServicios(turno.getServicios().stream().map(turnoServicio -> turnoServicio.getServicio().getNombre()).toList());
        }

        return obtenerHistorialTratamientoDTO;
    }
}
