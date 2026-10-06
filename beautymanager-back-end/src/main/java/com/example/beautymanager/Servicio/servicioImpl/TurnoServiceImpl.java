package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.CrearTurnoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerServicioDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerTurnoDTO;
import com.example.beautymanager.Modelo.Entidad.ClienteEntity;
import com.example.beautymanager.Modelo.Entidad.EmpleadoEntity;
import com.example.beautymanager.Modelo.Entidad.ServicioEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoServicioEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoServicioEnums;
import com.example.beautymanager.Modelo.Enums.EstadoTurnoEnums;
import com.example.beautymanager.Repositorio.ClienteRepository;
import com.example.beautymanager.Repositorio.EmpleadoRepository;
import com.example.beautymanager.Repositorio.ServicioRepository;
import com.example.beautymanager.Repositorio.TurnoRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.TurnoService;
import com.example.beautymanager.exception.BusinessException;
import com.example.beautymanager.utils.HorarioAtencion;

import jakarta.transaction.Transactional;

@Service
public class TurnoServiceImpl implements TurnoService {

   @Autowired
   private TurnoRepository turnoRepository;

   @Autowired
   private ClienteRepository clienteRepository;

   @Autowired
   private EmpleadoRepository empleadoRepository;

   @Autowired
   private ServicioRepository servicioRepository;

   @Autowired
   private UsuarioRepository usuarioRepository;

   //=====================
   //Crear turno
   //=====================
   @Override
   @Transactional
   public ObtenerTurnoDTO crearTurno(CrearTurnoDTO crearTurno, Long idCliente) {

    if(crearTurno == null){
        throw new BusinessException("Los datos del turno son obligatorios");
    }

    if(crearTurno.getFechaHoraInicio() == null || crearTurno.getServiciosIds() == null || crearTurno.getServiciosIds().isEmpty()){
        throw new BusinessException("Los datos son invalidos para crear el turno");
    }

    LocalDateTime inicio = crearTurno.getFechaHoraInicio();

    if(inicio.isBefore(LocalDateTime.now())){
        throw new BusinessException("No se pueden crear turno en fechas pasadas");
    }

    LocalTime hora = inicio.toLocalTime();

    if(hora.isBefore(HorarioAtencion.APERTURA) || hora.isAfter(HorarioAtencion.CIERRE)){
        throw new BusinessException("Horario fuera del rango de atencion");
    }

    ClienteEntity cliente = clienteRepository.findById(idCliente)
            .orElseThrow(() -> new BusinessException("Cliente no encontrado"));

    EmpleadoEntity empleado = empleadoRepository.findById(crearTurno.getIdEmpleado())
            .orElseThrow(() -> new BusinessException("Empleado no encontrado"));

    int duracionTotal = 0;

    BigDecimal precioTotal = BigDecimal.ZERO;

    List<ServicioEntity> servicios = new ArrayList<>();

    for(Long idServicio : crearTurno.getServiciosIds()){

        ServicioEntity servicio = servicioRepository.findById(idServicio)
                .orElseThrow(() -> new BusinessException("Servicio no encontrado: " + idServicio));

        if(servicio.getEstado() != EstadoServicioEnums.ACTIVO){
            throw new BusinessException("El servicio no esta activo: " + servicio.getNombre());
        }

        if(servicio.getDuracionMinutos() == null || servicio.getDuracionMinutos() <= 0){
            throw new BusinessException("El servicio tiene una duracion invalida: " + servicio.getNombre());
        }

        if(servicio.getPrecio() == null){
            throw new BusinessException("El servicio no tiene un precio asignado: " + servicio.getNombre());
        }

        duracionTotal += servicio.getDuracionMinutos();

        precioTotal = precioTotal.add(servicio.getPrecio());

        servicios.add(servicio);
    }

    LocalDateTime fin = inicio.plusMinutes(duracionTotal);

    if(fin.toLocalTime().isAfter(HorarioAtencion.CIERRE)){
        throw new BusinessException("El turno no puede finalizar despues de las 20:00");
    }

    if(!turnoRepository.findByEmpleadoIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(crearTurno.getIdEmpleado(), fin, inicio).isEmpty()){
        throw new BusinessException("El empleado ya tiene un turno en ese horario");
    }

    if(!turnoRepository.findByClienteIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(cliente.getId(), fin, inicio).isEmpty()){
        throw new BusinessException("El cliente ya tiene un turno en ese horario");
    }

    TurnoEntity turno = new TurnoEntity();
    turno.setCliente(cliente);
    turno.setEmpleado(empleado);
    turno.setFechaHoraInicio(inicio);
    turno.setFechaHoraFin(fin);
    turno.setEstadoTurno(EstadoTurnoEnums.PENDIENTE);
    turno.setPrecioTotal(precioTotal);

    List<TurnoServicioEntity> lista = new ArrayList<>();

    for(ServicioEntity servicio : servicios){
        TurnoServicioEntity ts = new TurnoServicioEntity();
        ts.setTurno(turno);
        ts.setServicio(servicio);
        lista.add(ts);
    }

    turno.setServicios(lista);

    return toMap(turnoRepository.save(turno));
   }

   //========================
   //Crear turno admin
   //========================
   @Override 
   @Transactional 
   public ObtenerTurnoDTO crearTurnoAdmin(CrearTurnoDTO crearTurno) {

    if(crearTurno == null || crearTurno.getIdCliente() == null){
        throw new BusinessException("El cliente es obligatorio");
    }

    return crearTurno(crearTurno, crearTurno.getIdCliente());
   }

   //========================
   //Cancelar turno
   //========================
   @Override
   @Transactional
   public void cancelarTurno(Long idTurno, Long idCliente) {

        TurnoEntity turno = turnoRepository.findById(idTurno)
            .orElseThrow(() -> new BusinessException("Turno no encontrado"));
        
        if(!turno.getCliente().getId().equals(idCliente)){
            throw new BusinessException("No puedes cancelar un turno que no te pertenece");
        }

        if(turno.getEstadoTurno() == EstadoTurnoEnums.CANCELADO){
            throw new BusinessException("El turno ya esta cancelado");
        }

        if(turno.getEstadoTurno() == EstadoTurnoEnums.FINALIZADO){
            throw new BusinessException("No se puede cancelar un turno finalizado");
        }

        if(turno.getEstadoTurno() == EstadoTurnoEnums.EN_CURSO){
            throw new BusinessException("No se puede cancelar un turno que ya comenzó");
        }

        turno.setEstadoTurno(EstadoTurnoEnums.CANCELADO);

        turnoRepository.save(turno);
   }

   //===============================
   //Aceptar turno (por empleado)
   //===============================
   @Override
   @Transactional
   public void aceptarTurno(Long idTurno, Long idUsuario){

        TurnoEntity turno = turnoRepository.findById(idTurno)
            .orElseThrow(() -> new BusinessException("Turno no encontrado"));

        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        if(!turno.getEmpleado().getId().equals(usuario.getEmpleado().getId())){
            throw new BusinessException("No tiene permiso para aceptar este turno");
        }

        if(turno.getEstadoTurno() != EstadoTurnoEnums.PENDIENTE){
            throw new BusinessException("El turno no esta en estado pendiente");
        }

        turno.setEstadoTurno(EstadoTurnoEnums.CONFIRMADO);

        turnoRepository.save(turno);
   }

   //===============================
   //Rechazar turno (por empleado)
   //===============================
   @Override
   @Transactional
   public void rechazarTurno(Long idTurno, Long idUsuario){

        TurnoEntity turno = turnoRepository.findById(idTurno)
            .orElseThrow(() -> new BusinessException("Turno no encontrado"));

        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        if(!turno.getEmpleado().getId().equals(usuario.getEmpleado().getId())){
            throw new BusinessException("No tienes permiso para rechazar este turno");
        }

        if(turno.getEstadoTurno() != EstadoTurnoEnums.PENDIENTE){
            throw new BusinessException("El turno no esta en estado pendiente");
        }

        if(turno.getEstadoTurno() == EstadoTurnoEnums.EN_CURSO){
            throw new BusinessException("No se puede rechazar un turno que ya comenzó");
        }

        turno.setEstadoTurno(EstadoTurnoEnums.RECHAZADO);

        turnoRepository.save(turno);
   }

   //=============================
   //Marcar ausencia del cliente
   //=============================
   @Override
   @Transactional
   public void marcarAusente(Long idTurno, Long idUsuario){

    TurnoEntity turno = turnoRepository.findById(idTurno)
            .orElseThrow(() -> new BusinessException("Turno no encontrado"));

    UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
        .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

    if(turno.getEmpleado() == null || usuario.getEmpleado() == null){
        throw new BusinessException("Usuaio invalido");
    }

    if(!turno.getEmpleado().getId().equals(usuario.getEmpleado().getId())){
        throw new BusinessException("No tienes permiso para marcar este turno como ausente");
    }

    if(turno.getEstadoTurno() == EstadoTurnoEnums.CANCELADO){
        throw new BusinessException("El turno esta cancelado");
    }

    if(turno.getEstadoTurno() == EstadoTurnoEnums.FINALIZADO){
        throw new BusinessException("El turno ya fue finalizado");
    }

    if(turno.getEstadoTurno() == EstadoTurnoEnums.AUSENTE){
        throw new BusinessException("El turno ya esta marcado como ausente");
    }

    if(turno.getEstadoTurno() == EstadoTurnoEnums.EN_CURSO){
        throw new BusinessException("No se puede marcar como ausente un turno que ya comenzó");
    }

    turno.setEstadoTurno(EstadoTurnoEnums.AUSENTE);

    turnoRepository.save(turno);
   }

   //========================
   //Iniciar turno
   //========================
   @Override
   @Transactional
   public void iniciarTurno(Long idTurno, Long idUsuario){

        TurnoEntity turno = turnoRepository.findById(idTurno)
            .orElseThrow(() -> new BusinessException("Turno no encontrado"));

        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        if(turno.getEmpleado() == null || usuario.getEmpleado() == null){
            throw new BusinessException("Usuario invalido");
        }

        if(!turno.getEmpleado().getId().equals(usuario.getEmpleado().getId())){
            throw new BusinessException("No tienes permiso para iniciar este turno");
        }

        if(turno.getEstadoTurno() != EstadoTurnoEnums.CONFIRMADO){
            throw new BusinessException("Solo se pueden iniciar turnos confirmados");
        }

        LocalDateTime ahora = LocalDateTime.now();

        if(ahora.isBefore(turno.getFechaHoraInicio())){
            throw new BusinessException("El turno todavía no puede iniciarse");
        }

        if(ahora.isAfter(turno.getFechaHoraFin())){
            throw new BusinessException("El horario del turno ya finalizó");
        }

        turno.setEstadoTurno(EstadoTurnoEnums.EN_CURSO);

        turnoRepository.save(turno);
   }

   //========================
   //Finalizar turno
   //========================
   @Override
   @Transactional
   public void finalizarTurno(Long idTurno, Long idUsuario){

    TurnoEntity turno = turnoRepository.findById(idTurno)
        .orElseThrow(() -> new BusinessException("Turno no encontrado"));

    UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
        .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

    if(turno.getEmpleado() == null || usuario.getEmpleado() == null){
        throw new BusinessException("Usuario invalido");
    }

    if(!turno.getEmpleado().getId().equals(usuario.getEmpleado().getId())){
        throw new BusinessException("No tienes permiso para finalizar este turno");
    }

    if(turno.getEstadoTurno() != EstadoTurnoEnums.EN_CURSO){
        throw new BusinessException("Solo se pueden finalizar turnos en curno");
    }

    LocalDateTime ahora = LocalDateTime.now();

    if(ahora.isBefore(turno.getFechaHoraInicio())){
        throw new BusinessException("El turno todavía no comenzó");
    }

    turno.setEstadoTurno(EstadoTurnoEnums.FINALIZADO);

    turnoRepository.save(turno);
   }

   //========================
   //Turnos por cliente
   //========================
   @Override
   public List<ObtenerTurnoDTO> obtenerTurnosPorCliente(Long idUsuario) {

        return turnoRepository.findByClienteId(idUsuario).stream().map(this::toMap).toList();
   }

   //========================
   //Turnos por empleado
   //========================
   @Override
   public List<ObtenerTurnoDTO> obtenerTurnosPorEmpleado(Long idEmpleado) {

        return turnoRepository.findByEmpleadoId(idEmpleado).stream().map(this::toMap).toList();
   }

   //==============================
   //Todos los turnos para admin
   //==============================
   @Override
   public List<ObtenerTurnoDTO> obtenerTodosLosTurnos(){

    return turnoRepository.findAll().stream().map(this::toMap).toList();
   }

   @Override
   public ObtenerTurnoDTO toMap(TurnoEntity turno) {

        ObtenerTurnoDTO obtenerTurnoDTO = new ObtenerTurnoDTO();

        obtenerTurnoDTO.setId(turno.getId());
        obtenerTurnoDTO.setFechaHoraInicio(turno.getFechaHoraInicio());
        obtenerTurnoDTO.setFechaHoraFin(turno.getFechaHoraFin());
        obtenerTurnoDTO.setEstadoTurno(turno.getEstadoTurno());
        obtenerTurnoDTO.setPrecioTotal(turno.getPrecioTotal());

        //Cliente
        obtenerTurnoDTO.setIdCliente(turno.getCliente().getId());
        obtenerTurnoDTO.setNombreCliente(turno.getCliente().getUsuario().getNombre());
        obtenerTurnoDTO.setApellidoCliente(turno.getCliente().getUsuario().getApellido());
        obtenerTurnoDTO.setFotoCliente(turno.getCliente().getUsuario().getFotoPerfil());

        //Empleado
        obtenerTurnoDTO.setIdEmpleado(turno.getEmpleado().getId());
        obtenerTurnoDTO.setNombreEmpleado(turno.getEmpleado().getUsuario().getNombre());
        obtenerTurnoDTO.setApellidoEmpleado(turno.getEmpleado().getUsuario().getApellido());
        obtenerTurnoDTO.setFotoEmpleado(turno.getEmpleado().getUsuario().getFotoPerfil());
        obtenerTurnoDTO.setEspecialidadEmpleado(turno.getEmpleado().getEspecialidad());

        //Servicios
        List<ObtenerServicioDTO> servicios = new ArrayList<>();
        for(TurnoServicioEntity ts : turno.getServicios()) {
                ObtenerServicioDTO servicioDTO = new ObtenerServicioDTO();
                servicioDTO.setId(ts.getServicio().getId());
                servicioDTO.setNombre(ts.getServicio().getNombre());
                servicioDTO.setDescripcion(ts.getServicio().getDescripcion());
                servicioDTO.setDuracionMinutos(ts.getServicio().getDuracionMinutos());
                servicioDTO.setPrecio(ts.getServicio().getPrecio());
                servicioDTO.setEstado(ts.getServicio().getEstado());
                servicios.add(servicioDTO);
        }
        obtenerTurnoDTO.setServicios(servicios);

        return obtenerTurnoDTO;
   }
}
