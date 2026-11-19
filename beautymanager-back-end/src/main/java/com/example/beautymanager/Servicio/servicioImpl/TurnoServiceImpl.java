package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.CrearNotificacionDTO;
import com.example.beautymanager.Modelo.DTO.CrearTurnoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerReprogramacionTurnoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerServicioDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerTurnoDTO;
import com.example.beautymanager.Modelo.DTO.ReprogramarTurnoDTO;
import com.example.beautymanager.Modelo.Entidad.ClienteEntity;
import com.example.beautymanager.Modelo.Entidad.EmpleadoEntity;
import com.example.beautymanager.Modelo.Entidad.HorarioEmpleadoEntity;
import com.example.beautymanager.Modelo.Entidad.ReprogramacionTurnoEntity;
import com.example.beautymanager.Modelo.Entidad.ServicioEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoServicioEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoEmpleadoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoReprogramacionEnums;
import com.example.beautymanager.Modelo.Enums.EstadoServicioEnums;
import com.example.beautymanager.Modelo.Enums.EstadoTurnoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoUsuarioEnums;
import com.example.beautymanager.Modelo.Enums.RolesEnums;
import com.example.beautymanager.Repositorio.BloqueoAgendaRepository;
import com.example.beautymanager.Repositorio.ClienteRepository;
import com.example.beautymanager.Repositorio.EmpleadoRepository;
import com.example.beautymanager.Repositorio.HistorialTratamientoRepository;
import com.example.beautymanager.Repositorio.HorarioEmpleadoRepository;
import com.example.beautymanager.Repositorio.PagoRepository;
import com.example.beautymanager.Repositorio.ReprogramacionTurnoRepository;
import com.example.beautymanager.Repositorio.ServicioRepository;
import com.example.beautymanager.Repositorio.TurnoRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.NotificacionService;
import com.example.beautymanager.Servicio.RecordatorioService;
import com.example.beautymanager.Servicio.TurnoService;
import com.example.beautymanager.exception.BusinessException;
import com.example.beautymanager.utils.HorarioAtencion;

import jakarta.transaction.Transactional;

@Service
public class TurnoServiceImpl implements TurnoService {

   private final TurnoRepository turnoRepository;

   private final ClienteRepository clienteRepository;

   private final EmpleadoRepository empleadoRepository;

   private final ServicioRepository servicioRepository;

   private final PagoRepository pagoRepository;

   private final UsuarioRepository usuarioRepository;

    private final HorarioEmpleadoRepository horarioEmpleadoRepository;

    private final BloqueoAgendaRepository bloqueoAgendaRepository;

    private final ReprogramacionTurnoRepository reprogramacionTurnoRepository;

    private final RecordatorioService recordatorioService;

    private final NotificacionService notificacionService;

    private final HistorialTratamientoRepository historialTratamientoRepository;


   public TurnoServiceImpl(TurnoRepository turnoRepository, ClienteRepository clienteRepository, EmpleadoRepository empleadoRepository, ServicioRepository servicioRepository, UsuarioRepository usuarioRepository, PagoRepository pagoRepository, HorarioEmpleadoRepository horarioEmpleadoRepository, BloqueoAgendaRepository bloqueoAgendaRepository, ReprogramacionTurnoRepository reprogramacionTurnoRepository, RecordatorioService recordatorioService, NotificacionService notificacionService, HistorialTratamientoRepository historialTratamientoRepository){

    this.turnoRepository = turnoRepository;

    this.clienteRepository = clienteRepository;

    this.empleadoRepository = empleadoRepository;

    this.servicioRepository = servicioRepository;

    this.usuarioRepository = usuarioRepository;

    this.pagoRepository = pagoRepository;

    this.horarioEmpleadoRepository = horarioEmpleadoRepository;

    this.bloqueoAgendaRepository = bloqueoAgendaRepository;

    this.reprogramacionTurnoRepository = reprogramacionTurnoRepository;

    this.recordatorioService = recordatorioService;

    this.notificacionService = notificacionService;

    this.historialTratamientoRepository = historialTratamientoRepository;
   }

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

    //==========================
    //Validar identificadores
    //==========================
    if(idCliente == null){
        throw new BusinessException("El ID de la clienta es obligatorio");
    }

    if(crearTurno.getIdEmpleado() == null){
        throw new BusinessException("El ID de la empleada es obligatorio");
    }

    //==========================
    //Bloquear clienta
    //==========================
    ClienteEntity cliente = clienteRepository.bloquearParaReserva(idCliente)
        .orElseThrow(() -> new BusinessException("Clienta no encontrada"));

    //==========================
    //Bloquear empleada
    //==========================
    EmpleadoEntity empleado = empleadoRepository.bloquearParaReserva(crearTurno.getIdEmpleado())
        .orElseThrow(() -> new BusinessException("Empleada no encontrada"));

    if(empleado.getEstadoEmpleado() != EstadoEmpleadoEnums.ACTIVO){
        throw new BusinessException("El empleado no está disponible para recibir turnos");
    }

    if(empleado.getFechaBaja() != null){
        throw new BusinessException("El empleado fue dado de baja y no puede recibir turnos");
    }

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

    //========================================
    //Validar horario general de atención
    //========================================

    if (
        !inicio.toLocalDate().equals(fin.toLocalDate()) ||
        fin.toLocalTime().isAfter(HorarioAtencion.CIERRE)
    ) {
        throw new BusinessException(
            "El turno debe finalizar dentro del horario de atención"
        );
    }

    //========================================
    //Validar jornada laboral de la empleada
    //========================================

    int diaSemana = inicio.getDayOfWeek().getValue();

    List<HorarioEmpleadoEntity> horarios =
        horarioEmpleadoRepository.findByEmpleadoIdAndDiaSemana(
            empleado.getId(),
            diaSemana
        );

    boolean dentroDeJornada = horarios.stream().anyMatch(horario ->
        !inicio.toLocalTime().isBefore(horario.getHoraInicio()) &&
        !fin.toLocalTime().isAfter(horario.getHoraFin())
    );

    if (!dentroDeJornada) {
        throw new BusinessException(
            "El horario solicitado está fuera de la jornada laboral de la empleada"
        );
    }

    //========================================
    //Validar bloqueos de agenda
    //========================================

    boolean existeBloqueo = !bloqueoAgendaRepository
        .findByEmpleadoIdAndInicioLessThanAndFinGreaterThan(
            empleado.getId(),
            fin,
            inicio
        )
        .isEmpty();

    if (existeBloqueo) {
        throw new BusinessException(
            "La empleada tiene su agenda bloqueada en el horario solicitado"
        );
    }


    //==========================
//Validar ocupacion empleada
//==========================
boolean empleadaOcupada = turnoRepository
    .findByEmpleadoIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
        empleado.getId(),
        fin,
        inicio
    )
    .stream()
    .anyMatch(this::ocupaAgenda);

if(empleadaOcupada){
    throw new BusinessException(
        "La empleada ya tiene un turno en ese horario"
    );
}


    //========================================
    //Validar propuestas de reprogramación
    //pendientes en el horario solicitado
    //========================================

    boolean horarioReservadoPorPropuesta =
        reprogramacionTurnoRepository
            .existeConflictoConPropuestaPendiente(
                empleado.getId(),
                cliente.getId(),
                inicio,
                fin,
                EstadoReprogramacionEnums.PENDIENTE
            );

    if (horarioReservadoPorPropuesta) {
        throw new BusinessException(
            "El horario está reservado para una reprogramación pendiente"
        );
    }


//==========================
//Validar ocupacion clienta
//==========================
boolean clientaOcupada = turnoRepository
    .findByClienteIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
        cliente.getId(),
        fin,
        inicio
    )
    .stream()
    .anyMatch(this::ocupaAgenda);

if(clientaOcupada){
    throw new BusinessException(
        "La clienta ya tiene un turno en ese horario"
    );
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

    TurnoEntity turnoGuardado = turnoRepository.save(turno);

    recordatorioService.crearRecordatorios(turnoGuardado);

    return toMap(turnoGuardado);
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

    //==========================
    //Estados que ocupan agenda
    //==========================
private boolean ocupaAgenda(TurnoEntity turno){

    return switch(turno.getEstadoTurno()){

        case PENDIENTE, CONFIRMADO, EN_CURSO, FINALIZADO -> true;

        case CANCELADO, RECHAZADO, REPROGRAMADO, AUSENTE -> false;
    };
}

   //========================
   //Cancelar turno
   //========================
   @Override
   @Transactional
   public void cancelarTurno(Long idTurno, Long idUsuario) {

        if(idTurno == null){
            throw new BusinessException("El ID del turno es obligatorio");
        }

        if(idUsuario == null){
            throw new BusinessException("El ID del usuario es obligatorio");
        }

        //========================
        //Obtener usuario
        //========================
        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() ->
                new BusinessException("Usuario no encontrado")
            );

        //========================
        //Obtener perfil de clienta
        //========================
        ClienteEntity cliente = clienteRepository.findByUsuario(usuario)
            .orElseThrow(() ->
                new BusinessException("Perfil de clienta no encontrado")
            );

        //========================
        //Obtener turno
        //========================
        TurnoEntity turno = turnoRepository.findById(idTurno)
            .orElseThrow(() ->
                new BusinessException("Turno no encontrado")
            );

        //========================
        //Validar pertenencia
        //========================
        if(turno.getCliente() == null ||
           !turno.getCliente().getId().equals(cliente.getId())){

            throw new BusinessException(
                "No puedes cancelar un turno que no te pertenece"
            );
        }

        //========================
        //Validar estado del turno
        //========================
        if(turno.getEstadoTurno() == EstadoTurnoEnums.CANCELADO){
            throw new BusinessException("El turno ya esta cancelado");
        }

        if(turno.getEstadoTurno() == EstadoTurnoEnums.FINALIZADO){
            throw new BusinessException(
                "No se puede cancelar un turno finalizado"
            );
        }

        if(turno.getEstadoTurno() == EstadoTurnoEnums.EN_CURSO){
            throw new BusinessException(
                "No se puede cancelar un turno que ya comenzo"
            );
        }


        //========================================
        //No permitir cancelar ausencias
        //========================================

        if (turno.getEstadoTurno() == EstadoTurnoEnums.AUSENTE) {
            throw new BusinessException(
                "No se puede cancelar un turno marcado como ausente"
            );
        }


        //========================
        //Validar pagos asociados
        //========================
        //Hasta implementar los reintegros,
        //no permitimos cancelar turnos que
        //tengan un pago registrado.

        boolean tienePago = pagoRepository
            .findByTurnoId(turno.getId())
            .isPresent();

        if(tienePago){

            throw new BusinessException(
                "Este turno tiene un pago asociado. " +
                "No se puede cancelar hasta que se implemente " +
                "el proceso de reintegro."
            );
        }

        //========================
        //Cancelar turno
        //========================
        turno.setEstadoTurno(EstadoTurnoEnums.CANCELADO);

        turnoRepository.save(turno);

        //====================================
        //Anular propuesta de reprogramación
        //====================================

        anularPropuestaPendiente(idTurno);
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

        notificarUsuario(
    turno.getCliente().getUsuario().getId(),
    "Tu turno del " +
    formatearFechaTurno(turno.getFechaHoraInicio()) +
    " fue confirmado por la empleada."
);
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

        anularPropuestaPendiente(idTurno);

        notificarUsuario(
    turno.getCliente().getUsuario().getId(),
    "Tu turno del " +
    formatearFechaTurno(turno.getFechaHoraInicio()) +
    " fue rechazado por la empleada."
);
   }

   //=============================
   //Marcar ausencia del cliente
   //=============================
   @Override
   @Transactional
   public void marcarAusente(Long idTurno, Long idUsuario){

    if (idTurno == null || idUsuario == null) {
            throw new BusinessException(
                "Los identificadores son obligatorios"
            );
        }

        //========================================
        //Obtener turno
        //========================================

        TurnoEntity turno = turnoRepository.findById(idTurno)
            .orElseThrow(() ->
                new BusinessException("Turno no encontrado")
            );

        //========================================
        //Obtener usuario autenticado
        //========================================

        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() ->
                new BusinessException("Usuario no encontrado")
            );

        //========================================
        //Validar perfil de empleado
        //========================================

        EmpleadoEntity empleado = usuario.getEmpleado();

        if (empleado == null || turno.getEmpleado() == null) {
            throw new BusinessException(
                "Perfil de empleado inválido"
            );
        }

        //========================================
        //Validar empleado habilitado
        //========================================

        if (
            empleado.getEstadoEmpleado() != EstadoEmpleadoEnums.ACTIVO ||
            empleado.getFechaBaja() != null
        ) {
            throw new BusinessException(
                "El empleado no está habilitado para gestionar turnos"
            );
        }

        //========================================
        //Validar pertenencia del turno
        //========================================

        if (!turno.getEmpleado().getId().equals(empleado.getId())) {
            throw new BusinessException(
                "No tenés permiso para modificar este turno"
            );
        }

        //========================================
        //Validar estado
        //========================================

        if (turno.getEstadoTurno() != EstadoTurnoEnums.CONFIRMADO) {
            throw new BusinessException(
                "Solo se pueden marcar como ausentes los turnos confirmados"
            );
        }

        //========================================
        //Validar fecha y hora
        //========================================

        LocalDateTime ahora = LocalDateTime.now();

        if (ahora.isBefore(turno.getFechaHoraInicio())) {
            throw new BusinessException(
                "No se puede registrar una ausencia antes del inicio del turno"
            );
        }

        //========================================
        //Registrar ausencia
        //========================================

        turno.setEstadoTurno(EstadoTurnoEnums.AUSENTE);

        turnoRepository.save(turno);

        anularPropuestaPendiente(idTurno);
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

        anularPropuestaPendiente(idTurno);
   }

   //========================
   //Finalizar turno
   //========================
   @Override
   @Transactional
   public void finalizarTurno(Long idTurno, Long idUsuario){

    if (idTurno == null || idUsuario == null) {
            throw new BusinessException(
                "Los identificadores son obligatorios"
            );
        }

        //====================================
        //Obtener turno
        //====================================

        TurnoEntity turno = turnoRepository.findById(idTurno)
            .orElseThrow(() ->
                new BusinessException("Turno no encontrado")
            );

        //====================================
        //Obtener usuario autenticado
        //====================================

        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() ->
                new BusinessException("Usuario no encontrado")
            );

        //====================================
        //Validar empleada
        //====================================

        if (turno.getEmpleado() == null ||
                usuario.getEmpleado() == null) {
            throw new BusinessException(
                "Perfil de empleada inválido"
            );
        }

        //====================================
        //Validar pertenencia del turno
        //====================================

        if (!turno.getEmpleado().getId()
                .equals(usuario.getEmpleado().getId())) {

            throw new BusinessException(
                "No tenés permiso para finalizar este turno"
            );
        }

        //====================================
        //Validar estado del turno
        //====================================

        if (turno.getEstadoTurno() != EstadoTurnoEnums.EN_CURSO) {
            throw new BusinessException(
                "Solo se pueden finalizar turnos en curso"
            );
        }

        //====================================
        //Validar fecha de inicio
        //====================================

        if (turno.getFechaHoraInicio() == null ||
                LocalDateTime.now().isBefore(turno.getFechaHoraInicio())) {

            throw new BusinessException(
                "El turno todavía no comenzó"
            );
        }

        //====================================
        //Exigir tratamiento registrado
        //====================================

        boolean tieneTratamiento = historialTratamientoRepository
                .existsByTurnoId(turno.getId());

        if (!tieneTratamiento) {
            throw new BusinessException(
                "Para finalizar el turno primero debés registrar el tratamiento realizado"
            );
        }

        //====================================
        //Finalizar turno
        //====================================

        turno.setEstadoTurno(EstadoTurnoEnums.FINALIZADO);

        turnoRepository.save(turno);
   }


    //========================================
    //Obtener propuesta de la clienta
    //========================================
    private ReprogramacionTurnoEntity obtenerPropuestaPendienteDeCliente(
            Long idReprogramacion,
            Long idUsuario) {

        if (
            idReprogramacion == null ||
            idReprogramacion <= 0 ||
            idUsuario == null
        ) {
            throw new BusinessException(
                "Los identificadores son inválidos"
            );
        }

        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() ->
                new BusinessException("Usuario no encontrado")
            );

        ClienteEntity cliente = clienteRepository.findByUsuario(usuario)
            .orElseThrow(() ->
                new BusinessException("Perfil de clienta no encontrado")
            );

        //Serializar operaciones de esta clienta.
        clienteRepository.bloquearParaReserva(cliente.getId())
            .orElseThrow(() ->
                new BusinessException("Clienta no encontrada")
            );

        ReprogramacionTurnoEntity propuesta =
            reprogramacionTurnoRepository.findById(idReprogramacion)
                .orElseThrow(() ->
                    new BusinessException(
                        "Propuesta de reprogramación no encontrada"
                    )
                );

        TurnoEntity turno = propuesta.getTurno();

        if (
            turno == null ||
            turno.getCliente() == null ||
            !turno.getCliente().getId().equals(cliente.getId())
        ) {
            throw new BusinessException(
                "No tenés permiso para responder esta propuesta"
            );
        }

        if (
            propuesta.getEstadoPropuesta() !=
                EstadoReprogramacionEnums.PENDIENTE
        ) {
            throw new BusinessException(
                "La propuesta ya fue respondida o anulada"
            );
        }

        if (
            turno.getEstadoTurno() != EstadoTurnoEnums.PENDIENTE &&
            turno.getEstadoTurno() != EstadoTurnoEnums.CONFIRMADO
        ) {
            throw new BusinessException(
                "El turno ya no admite reprogramaciones"
            );
        }

        return propuesta;
    }


    //========================================
    //Reprogramar turno por empleado
    //========================================
    @Override
    @Transactional
    public ObtenerTurnoDTO reprogramarTurno(
            Long idTurno,
            Long idUsuario,
            ReprogramarTurnoDTO datos) {

        //====================================
        //Validar datos
        //====================================

        if (idTurno == null || idUsuario == null || datos == null) {
            throw new BusinessException(
                "Los datos de la reprogramación son obligatorios"
            );
        }

        LocalDateTime nuevoInicio = datos.getNuevaFechaHoraInicio();

        if (nuevoInicio == null) {
            throw new BusinessException(
                "La nueva fecha y hora son obligatorias"
            );
        }

        String motivo = datos.getMotivo() == null
            ? ""
            : datos.getMotivo().trim();

        if (motivo.isBlank() || motivo.length() > 500) {
            throw new BusinessException(
                "El motivo debe tener entre 1 y 500 caracteres"
            );
        }

        LocalDateTime ahora = LocalDateTime.now();

        if (!nuevoInicio.isAfter(ahora)) {
            throw new BusinessException(
                "El nuevo horario debe ser futuro"
            );
        }

        
        //====================================
        //Obtener usuario autenticado
        //====================================

        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() ->
                new BusinessException("Usuario no encontrado")
            );

        //====================================
        //Comprobar rol
        //====================================

        RolesEnums rol = usuario.getRol() == null
            ? null
            : usuario.getRol().getNombre();

        boolean esAdmin = rol == RolesEnums.ADMIN;

        if (!esAdmin && rol != RolesEnums.EMPLEADO) {
            throw new BusinessException(
                "No tenés permiso para proponer reprogramaciones"
            );
        }

        //====================================
        //Obtener turno
        //====================================

        TurnoEntity turno = turnoRepository.findById(idTurno)
            .orElseThrow(() ->
                new BusinessException("Turno no encontrado")
            );

        //====================================
        //Obtener empleada asignada
        //====================================

        EmpleadoEntity empleado = turno.getEmpleado();

        if (empleado == null) {
            throw new BusinessException(
                "El turno no tiene una empleada asignada"
            );
        }

        //====================================
        //Validar empleada activa
        //====================================

        if (
            empleado.getEstadoEmpleado() != EstadoEmpleadoEnums.ACTIVO ||
            empleado.getFechaBaja() != null
        ) {
            throw new BusinessException(
                "La empleada asignada no está habilitada"
            );
        }

        //====================================
        //Validar permisos de EMPLEADO
        //ADMIN puede gestionar cualquier turno
        //====================================

        if (!esAdmin) {

            EmpleadoEntity empleadaAutenticada =
                usuario.getEmpleado();

            if (
                empleadaAutenticada == null ||
                !empleadaAutenticada.getId().equals(empleado.getId())
            ) {
                throw new BusinessException(
                    "No podés reprogramar turnos de otra empleada"
                );
            }
        }


        //====================================
        //Bloquear clienta y empleada
        //para coordinar reservas simultáneas
        //====================================

        Long idCliente = turno.getCliente().getId();
        Long idEmpleado = empleado.getId();

        clienteRepository.bloquearParaReserva(idCliente)
            .orElseThrow(() ->
                new BusinessException("Clienta no encontrada")
            );

        empleadoRepository.bloquearParaReserva(idEmpleado)
            .orElseThrow(() ->
                new BusinessException("Empleada no encontrada")
            );

        //====================================
        //Validar estado del turno
        //====================================

        if (
            turno.getEstadoTurno() != EstadoTurnoEnums.PENDIENTE &&
            turno.getEstadoTurno() != EstadoTurnoEnums.CONFIRMADO
        ) {
            throw new BusinessException(
                "Solo se pueden reprogramar turnos pendientes o confirmados"
            );
        }

        //====================================
        //Validar horario original
        //====================================

        LocalDateTime inicioAnterior = turno.getFechaHoraInicio();
        LocalDateTime finAnterior = turno.getFechaHoraFin();

        if (inicioAnterior == null || finAnterior == null) {
            throw new BusinessException(
                "El turno no tiene un horario válido"
            );
        }

        if (!inicioAnterior.isAfter(ahora)) {
            throw new BusinessException(
                "No se puede reprogramar un turno que ya comenzó"
            );
        }

        if (nuevoInicio.equals(inicioAnterior)) {
            throw new BusinessException(
                "La nueva fecha es igual a la actual"
            );
        }

        //====================================
        //Comprobar propuestas pendientes
        //====================================

        boolean tienePropuestaPendiente =
            reprogramacionTurnoRepository
                .existsByTurnoIdAndEstadoPropuesta(
                    idTurno,
                    EstadoReprogramacionEnums.PENDIENTE
                );

        if (tienePropuestaPendiente) {
            throw new BusinessException(
                "Este turno ya tiene una propuesta de reprogramación pendiente"
            );
        }

        //====================================
        //Conservar la duración
        //====================================

        long duracion = Duration.between(
            inicioAnterior,
            finAnterior
        ).toMinutes();

        if (duracion <= 0) {
            throw new BusinessException(
                "La duración del turno no es válida"
            );
        }

        LocalDateTime nuevoFin = nuevoInicio.plusMinutes(duracion);

        //====================================
        //Validar horario general
        //====================================

        if (
            !nuevoInicio.toLocalDate().equals(nuevoFin.toLocalDate()) ||
            nuevoInicio.toLocalTime().isBefore(HorarioAtencion.APERTURA) ||
            nuevoFin.toLocalTime().isAfter(HorarioAtencion.CIERRE)
        ) {
            throw new BusinessException(
                "El nuevo horario está fuera del horario de atención"
            );
        }

        //====================================
        //Validar jornada laboral
        //====================================

        int diaSemana = nuevoInicio.getDayOfWeek().getValue();

        List<HorarioEmpleadoEntity> horarios =
            horarioEmpleadoRepository.findByEmpleadoIdAndDiaSemana(
                idEmpleado,
                diaSemana
            );

        boolean dentroDeJornada = horarios.stream().anyMatch(horario ->
            !nuevoInicio.toLocalTime().isBefore(horario.getHoraInicio()) &&
            !nuevoFin.toLocalTime().isAfter(horario.getHoraFin())
        );

        if (!dentroDeJornada) {
            throw new BusinessException(
                "El horario propuesto está fuera de la jornada laboral"
            );
        }

        //====================================
        //Validar bloqueos
        //====================================

        boolean existeBloqueo = !bloqueoAgendaRepository
            .findByEmpleadoIdAndInicioLessThanAndFinGreaterThan(
                idEmpleado,
                nuevoFin,
                nuevoInicio
            ).isEmpty();

        if (existeBloqueo) {
            throw new BusinessException(
                "El horario propuesto se encuentra bloqueado"
            );
        }

        //====================================
        //Validar otros turnos de la empleada
        //====================================

        boolean empleadoOcupado = turnoRepository
            .findByEmpleadoIdAndIdNotAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                idEmpleado,
                idTurno,
                nuevoFin,
                nuevoInicio
            )
            .stream()
            .anyMatch(this::ocupaAgenda);

        if (empleadoOcupado) {
            throw new BusinessException(
                "La empleada tiene otro turno en el horario propuesto"
            );
        }

        //====================================
        //Validar otros turnos de la clienta
        //====================================

        boolean clienteOcupado = turnoRepository
            .findByClienteIdAndIdNotAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                idCliente,
                idTurno,
                nuevoFin,
                nuevoInicio
            )
            .stream()
            .anyMatch(this::ocupaAgenda);

        if (clienteOcupado) {
            throw new BusinessException(
                "La clienta tiene otro turno en el horario propuesto"
            );
        }

        //====================================
        //Validar otras propuestas pendientes
        //====================================

        boolean propuestaOcupada = reprogramacionTurnoRepository
            .existeConflictoConPropuestaPendiente(
                idEmpleado,
                idCliente,
                nuevoInicio,
                nuevoFin,
                EstadoReprogramacionEnums.PENDIENTE
            );

        if (propuestaOcupada) {
            throw new BusinessException(
                "El horario propuesto ya está reservado para otra reprogramación"
            );
        }

        //====================================
        //Crear propuesta de reprogramación
        //====================================

        ReprogramacionTurnoEntity propuesta =
            new ReprogramacionTurnoEntity();

        propuesta.setTurno(turno);
        propuesta.setUsuarioResponsable(usuario);

        propuesta.setInicioAnterior(inicioAnterior);
        propuesta.setFinAnterior(finAnterior);

        propuesta.setInicioNuevo(nuevoInicio);
        propuesta.setFinNuevo(nuevoFin);

        propuesta.setMotivo(motivo);

        propuesta.setEstadoPropuesta(
            EstadoReprogramacionEnums.PENDIENTE
        );

        propuesta.setFechaRespuesta(null);

        reprogramacionTurnoRepository.save(propuesta);


        //====================================
        //Notificar propuesta a la clienta
        //====================================

        String mensaje =
            "Se propuso un cambio de horario para tu turno. " +
            "Horario actual: " + formatearFechaTurno(inicioAnterior) +
            ". Nuevo horario: " + formatearFechaTurno(nuevoInicio) +
            ". Motivo: " + motivo +
            ". Ingresá a tu agenda para aceptar o rechazar el cambio.";

        notificarUsuario(
            turno.getCliente().getUsuario().getId(),
            mensaje
        );


        //====================================
        //El turno original NO cambia todavía
        //Los recordatorios tampoco se modifican
        //====================================

        return toMap(turno);
    }


    //========================================
    //Aceptar reprogramación (CLIENTE)
    //========================================
    @Override
    @Transactional
    public ObtenerTurnoDTO aceptarReprogramacion(
            Long idReprogramacion,
            Long idUsuario) {

        ReprogramacionTurnoEntity propuesta =
            obtenerPropuestaPendienteDeCliente(
                idReprogramacion,
                idUsuario
            );

        TurnoEntity turno = propuesta.getTurno();

        LocalDateTime ahora = LocalDateTime.now();

        LocalDateTime nuevoInicio = propuesta.getInicioNuevo();
        LocalDateTime nuevoFin = propuesta.getFinNuevo();

        //====================================
        //Validar vigencia
        //====================================

        if (
            nuevoInicio == null ||
            nuevoFin == null ||
            !nuevoInicio.isAfter(ahora) ||
            !nuevoFin.isAfter(nuevoInicio)
        ) {
            throw new BusinessException(
                "El horario propuesto ya no es válido"
            );
        }

        if (
            turno.getFechaHoraInicio() == null ||
            turno.getFechaHoraFin() == null ||
            !turno.getFechaHoraInicio().isAfter(ahora)
        ) {
            throw new BusinessException(
                "El turno original ya comenzó o no es válido"
            );
        }

        //Comprobar que el turno original
        //no haya sido modificado por otra operación.

        if (
            !turno.getFechaHoraInicio().equals(
                propuesta.getInicioAnterior()
            ) ||
            !turno.getFechaHoraFin().equals(
                propuesta.getFinAnterior()
            )
        ) {
            throw new BusinessException(
                "El horario original cambió y la propuesta ya no es válida"
            );
        }

        //====================================
        //Bloquear empleada
        //====================================

        Long idEmpleado = turno.getEmpleado().getId();
        Long idCliente = turno.getCliente().getId();

        EmpleadoEntity empleado = empleadoRepository
            .bloquearParaReserva(idEmpleado)
            .orElseThrow(() ->
                new BusinessException("Empleada no encontrada")
            );

        if (
            empleado.getEstadoEmpleado() != EstadoEmpleadoEnums.ACTIVO ||
            empleado.getFechaBaja() != null
        ) {
            throw new BusinessException(
                "La empleada ya no está disponible"
            );
        }

        //====================================
        //Revalidar jornada laboral
        //====================================

        int diaSemana = nuevoInicio.getDayOfWeek().getValue();

        List<HorarioEmpleadoEntity> horarios =
            horarioEmpleadoRepository.findByEmpleadoIdAndDiaSemana(
                idEmpleado,
                diaSemana
            );

        boolean dentroDeJornada = horarios.stream().anyMatch(horario ->
            !nuevoInicio.toLocalTime().isBefore(horario.getHoraInicio()) &&
            !nuevoFin.toLocalTime().isAfter(horario.getHoraFin())
        );

        if (!dentroDeJornada) {
            throw new BusinessException(
                "El horario propuesto ya no pertenece a la jornada laboral"
            );
        }

        //====================================
        //Revalidar bloqueos
        //====================================

        boolean existeBloqueo = !bloqueoAgendaRepository
            .findByEmpleadoIdAndInicioLessThanAndFinGreaterThan(
                idEmpleado,
                nuevoFin,
                nuevoInicio
            ).isEmpty();

        if (existeBloqueo) {
            throw new BusinessException(
                "El horario propuesto se encuentra bloqueado"
            );
        }

        //====================================
        //Revalidar turnos de la empleada
        //====================================

        boolean empleadoOcupado = turnoRepository
            .findByEmpleadoIdAndIdNotAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                idEmpleado,
                turno.getId(),
                nuevoFin,
                nuevoInicio
            )
            .stream()
            .anyMatch(this::ocupaAgenda);

        if (empleadoOcupado) {
            throw new BusinessException(
                "La empleada tiene otro turno en ese horario"
            );
        }

        //====================================
        //Revalidar turnos de la clienta
        //====================================

        boolean clienteOcupada = turnoRepository
            .findByClienteIdAndIdNotAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                idCliente,
                turno.getId(),
                nuevoFin,
                nuevoInicio
            )
            .stream()
            .anyMatch(this::ocupaAgenda);

        if (clienteOcupada) {
            throw new BusinessException(
                "La clienta tiene otro turno en ese horario"
            );
        }

        //====================================
        //Revalidar otras propuestas
        //====================================

        boolean otraPropuestaOcupada =
            reprogramacionTurnoRepository
                .existeOtraPropuestaEnHorario(
                    propuesta.getId(),
                    idEmpleado,
                    idCliente,
                    nuevoInicio,
                    nuevoFin,
                    EstadoReprogramacionEnums.PENDIENTE
                );

        if (otraPropuestaOcupada) {
            throw new BusinessException(
                "El horario fue reservado para otra reprogramación"
            );
        }

        //====================================
        //Aplicar el nuevo horario
        //====================================

        turno.setFechaHoraInicio(nuevoInicio);
        turno.setFechaHoraFin(nuevoFin);

        //Se conserva el estado PENDIENTE o CONFIRMADO.

        turnoRepository.save(turno);

        //====================================
        //Registrar aceptación
        //====================================

        propuesta.setEstadoPropuesta(
            EstadoReprogramacionEnums.ACEPTADA
        );

        propuesta.setFechaRespuesta(ahora);

        reprogramacionTurnoRepository.save(propuesta);

        //====================================
        //Actualizar recordatorios
        //====================================

        recordatorioService.reprogramarRecordatoriosDeTurno(turno);

        notificarUsuario(
            propuesta.getUsuarioResponsable().getId(),
            "La clienta aceptó la reprogramación del turno " +
            turno.getId() +
            ". Nuevo horario: " +
            formatearFechaTurno(nuevoInicio) + "."
        );

        return toMap(turno);
    }


    //========================================
    //Rechazar reprogramación (CLIENTE)
    //========================================
    @Override
    @Transactional
    public void rechazarReprogramacion(
            Long idReprogramacion,
            Long idUsuario) {

        ReprogramacionTurnoEntity propuesta =
            obtenerPropuestaPendienteDeCliente(
                idReprogramacion,
                idUsuario
            );

        propuesta.setEstadoPropuesta(
            EstadoReprogramacionEnums.RECHAZADA
        );

        propuesta.setFechaRespuesta(LocalDateTime.now());

        reprogramacionTurnoRepository.save(propuesta);

        notificarUsuario(
            propuesta.getUsuarioResponsable().getId(),
            "La clienta rechazó la propuesta de reprogramación " +
            "del turno " + propuesta.getTurno().getId() +
            ". Se conserva el horario original: " +
            formatearFechaTurno(propuesta.getInicioAnterior()) + "."
        );

    }


    //========================================
    //Obtener reprogramaciones pendientes
    //de la clienta autenticada
    //========================================
    @Override
    @Transactional
    public List<ObtenerReprogramacionTurnoDTO>
        obtenerReprogramacionesPendientesCliente(Long idUsuario) {

        //====================================
        //Validar identificador
        //====================================

        if (idUsuario == null || idUsuario <= 0) {
            throw new BusinessException(
                "El identificador del usuario es inválido"
            );
        }

        //====================================
        //Obtener usuario autenticado
        //====================================

        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() ->
                new BusinessException("Usuario no encontrado")
            );

        //====================================
        //Obtener perfil de clienta
        //====================================

        ClienteEntity cliente = clienteRepository.findByUsuario(usuario)
            .orElseThrow(() ->
                new BusinessException(
                    "Perfil de clienta no encontrado"
                )
            );

        //====================================
        //Consultar propuestas pendientes
        //====================================

        List<ReprogramacionTurnoEntity> propuestas =
            reprogramacionTurnoRepository
                .findByTurnoClienteIdAndEstadoPropuesta(
                    cliente.getId(),
                    EstadoReprogramacionEnums.PENDIENTE
                );

        //====================================
        //Ordenar de más reciente a más antigua
        //====================================

        return propuestas.stream()
            .sorted((a, b) ->
                b.getFechaCambio().compareTo(a.getFechaCambio())
            )
            .map(this::toMapReprogramacion)
            .toList();
    }


    //========================================
    //Convertir reprogramación a DTO
    //========================================
    private ObtenerReprogramacionTurnoDTO toMapReprogramacion(
            ReprogramacionTurnoEntity propuesta) {

        ObtenerReprogramacionTurnoDTO dto =
            new ObtenerReprogramacionTurnoDTO();

        dto.setIdReprogramacion(propuesta.getId());

        dto.setIdTurno(propuesta.getTurno().getId());

        //====================================
        //Horario anterior
        //====================================

        dto.setInicioAnterior(
            propuesta.getInicioAnterior()
        );

        dto.setFinAnterior(
            propuesta.getFinAnterior()
        );

        //====================================
        //Horario nuevo
        //====================================

        dto.setInicioNuevo(
            propuesta.getInicioNuevo()
        );

        dto.setFinNuevo(
            propuesta.getFinNuevo()
        );

        //====================================
        //Información de la propuesta
        //====================================

        dto.setMotivo(propuesta.getMotivo());

        dto.setEstadoPropuesta(
            propuesta.getEstadoPropuesta()
        );

        dto.setEstadoTurno(
            propuesta.getTurno().getEstadoTurno()
        );

        dto.setFechaCambio(
            propuesta.getFechaCambio()
        );

        return dto;
    }


   //========================
   //Turnos por cliente
   //========================
   @Override
   public List<ObtenerTurnoDTO> obtenerTurnosPorCliente(Long idUsuario) {

        if (idUsuario == null) {
            throw new BusinessException(
                "El identificador del usuario es obligatorio"
            );
        }

        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() ->
                new BusinessException("Usuario no encontrado")
            );

        ClienteEntity cliente = clienteRepository.findByUsuario(usuario)
            .orElseThrow(() ->
                new BusinessException("Perfil de clienta no encontrado")
            );

        return turnoRepository.findByClienteId(cliente.getId())
            .stream()
            .map(this::toMap)
            .toList();
   }

   //========================
   //Turnos por empleado
   //========================
   @Override
   public List<ObtenerTurnoDTO> obtenerTurnosPorEmpleado(Long idUsuario) {

        System.out.println("===== DIAGNOSTICO AGENDA =====");
    System.out.println("Usuario autenticado: " + idUsuario);

    EmpleadoEntity empleado = empleadoRepository
        .findByUsuario_Id(idUsuario)
        .orElseThrow(() ->
            new BusinessException(
                "Perfil de empleada no encontrado"
            )
        );

    System.out.println("Empleado encontrado: " + empleado.getId());

    List<TurnoEntity> turnos =
        turnoRepository.findByEmpleadoId(empleado.getId());

    System.out.println("Turnos encontrados: " + turnos.size());
    System.out.println("==============================");

    return turnos.stream()
        .map(this::toMap)
        .toList();
   }

   //==============================
   //Todos los turnos para admin
   //==============================
   @Override
   public List<ObtenerTurnoDTO> obtenerTodosLosTurnos(){

    return turnoRepository.findAll().stream().map(this::toMap).toList();
   }


    //========================================
    //Anular propuesta pendiente de un turno
    //========================================
    private void anularPropuestaPendiente(Long idTurno) {

        reprogramacionTurnoRepository
            .findByTurnoIdAndEstadoPropuesta(
                idTurno,
                EstadoReprogramacionEnums.PENDIENTE
            )
            .ifPresent(propuesta -> {

                propuesta.setEstadoPropuesta(
                    EstadoReprogramacionEnums.ANULADA
                );

                propuesta.setFechaRespuesta(LocalDateTime.now());

                reprogramacionTurnoRepository.save(propuesta);
            });
    }


    //========================================
    //Crear notificación interna
    //========================================
    private void notificarUsuario(
            Long idUsuario,
            String mensaje) {

        if (idUsuario == null) {
            throw new BusinessException(
                "No se puede notificar a una usuaria inexistente"
            );
        }

        CrearNotificacionDTO datos = new CrearNotificacionDTO();

        datos.setIdUsuario(idUsuario);
        datos.setMensaje(mensaje);

        notificacionService.crearNotificacion(datos);
    }


    //========================================
    //Formatear fecha para notificaciones
    //========================================
    private String formatearFechaTurno(LocalDateTime fecha) {

        DateTimeFormatter formato =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm");

        return fecha.format(formato);
    }



    //========================================
    //Confirmar turno (ADMIN)
    //========================================
    @Override
    @Transactional
    public void confirmarTurnoAdmin(Long idTurno) {

        //====================================
        //Validar identificador
        //====================================

        if (idTurno == null || idTurno <= 0) {
            throw new BusinessException(
                "El identificador del turno es inválido"
            );
        }

        //====================================
        //Obtener turno
        //====================================

        TurnoEntity turno = turnoRepository.findById(idTurno)
            .orElseThrow(() ->
                new BusinessException("Turno no encontrado")
            );

        //====================================
        //Validar estado
        //====================================

        if (turno.getEstadoTurno() != EstadoTurnoEnums.PENDIENTE) {
            throw new BusinessException(
                "Solo se pueden confirmar turnos pendientes"
            );
        }

        //====================================
        //Validar fecha
        //====================================

        if (
            turno.getFechaHoraInicio() == null ||
            !turno.getFechaHoraInicio().isAfter(LocalDateTime.now())
        ) {
            throw new BusinessException(
                "No se puede confirmar un turno cuyo horario ya comenzó"
            );
        }

        //====================================
        //Confirmar turno
        //====================================

        turno.setEstadoTurno(EstadoTurnoEnums.CONFIRMADO);

        turnoRepository.save(turno);

        notificarUsuario(
    turno.getCliente().getUsuario().getId(),
    "Tu turno del " +
    formatearFechaTurno(turno.getFechaHoraInicio()) +
    " fue confirmado por la administración."
);
    }


    //========================================
    //Rechazar turno (ADMIN)
    //========================================
    @Override
    @Transactional
    public void rechazarTurnoAdmin(Long idTurno) {

        //====================================
        //Validar identificador
        //====================================

        if (idTurno == null || idTurno <= 0) {
            throw new BusinessException(
                "El identificador del turno es inválido"
            );
        }

        //====================================
        //Obtener turno
        //====================================

        TurnoEntity turno = turnoRepository.findById(idTurno)
            .orElseThrow(() ->
                new BusinessException("Turno no encontrado")
            );

        //====================================
        //Validar estado
        //====================================

        if (turno.getEstadoTurno() != EstadoTurnoEnums.PENDIENTE) {
            throw new BusinessException(
                "Solo se pueden rechazar turnos pendientes"
            );
        }

        //====================================
        //Validar pagos
        //====================================

        boolean tienePago = pagoRepository
            .findByTurnoId(idTurno)
            .isPresent();

        if (tienePago) {
            throw new BusinessException(
                "No se puede rechazar un turno con un pago asociado " +
                "hasta gestionar su reintegro"
            );
        }

        //====================================
        //Rechazar turno
        //====================================

        turno.setEstadoTurno(EstadoTurnoEnums.RECHAZADO);

        turnoRepository.save(turno);

        //====================================
        //Anular reprogramación pendiente
        //====================================

        anularPropuestaPendiente(idTurno);

        notificarUsuario(
    turno.getCliente().getUsuario().getId(),
    "Tu turno del " +
    formatearFechaTurno(turno.getFechaHoraInicio()) +
    " fue rechazado por la administración."
);
    }


    //========================================
    //Cancelar turno (ADMIN)
    //========================================
    @Override
    @Transactional
    public void cancelarTurnoAdmin(Long idTurno) {

        //====================================
        //Validar identificador
        //====================================

        if (idTurno == null || idTurno <= 0) {
            throw new BusinessException(
                "El identificador del turno es inválido"
            );
        }

        //====================================
        //Obtener turno
        //====================================

        TurnoEntity turno = turnoRepository.findById(idTurno)
            .orElseThrow(() ->
                new BusinessException("Turno no encontrado")
            );

        //====================================
        //Validar estado
        //====================================

        EstadoTurnoEnums estado = turno.getEstadoTurno();

        if (
            estado != EstadoTurnoEnums.PENDIENTE &&
            estado != EstadoTurnoEnums.CONFIRMADO
        ) {
            throw new BusinessException(
                "Solo se pueden cancelar turnos pendientes o confirmados"
            );
        }

        //====================================
        //Validar fecha del turno
        //====================================

        LocalDateTime inicio = turno.getFechaHoraInicio();

        if (
            inicio == null ||
            !inicio.isAfter(LocalDateTime.now())
        ) {
            throw new BusinessException(
                "No se puede cancelar un turno que ya comenzó"
            );
        }

        //====================================
        //Validar pagos asociados
        //====================================

        boolean tienePago = pagoRepository
            .findByTurnoId(idTurno)
            .isPresent();

        if (tienePago) {
            throw new BusinessException(
                "El turno tiene un pago asociado. " +
                "No se puede cancelar hasta implementar " +
                "el proceso de reintegro"
            );
        }

        //====================================
        //Cancelar turno
        //====================================

        turno.setEstadoTurno(EstadoTurnoEnums.CANCELADO);

        turnoRepository.save(turno);

        //====================================
        //Anular reprogramación pendiente
        //====================================

        anularPropuestaPendiente(idTurno);

        //====================================
        //Notificar a la clienta
        //====================================

        Long idUsuarioCliente =
            turno.getCliente().getUsuario().getId();

        String mensaje =
            "Tu turno programado para el " +
            formatearFechaTurno(inicio) +
            " fue cancelado por la administración.";

        notificarUsuario(
            idUsuarioCliente,
            mensaje
        );
    }


    //========================================
    //Cambiar empleada asignada (ADMIN)
    //========================================
    @Override
    @Transactional
    public void cambiarEmpleadoAdmin(
            Long idTurno,
            Long idEmpleado) {

        //====================================
        //Validar identificadores
        //====================================

        if (
            idTurno == null || idTurno <= 0 ||
            idEmpleado == null || idEmpleado <= 0
        ) {
            throw new BusinessException(
                "Los identificadores del turno y la empleada son inválidos"
            );
        }

        //====================================
        //Obtener turno
        //====================================

        TurnoEntity turno = turnoRepository.findById(idTurno)
            .orElseThrow(() ->
                new BusinessException("Turno no encontrado")
            );

        //====================================
        //Validar estado del turno
        //====================================

        if (
            turno.getEstadoTurno() != EstadoTurnoEnums.PENDIENTE &&
            turno.getEstadoTurno() != EstadoTurnoEnums.CONFIRMADO
        ) {
            throw new BusinessException(
                "Solo se puede cambiar la empleada de turnos pendientes o confirmados"
            );
        }

        //====================================
        //Validar horario original
        //====================================

        LocalDateTime inicio = turno.getFechaHoraInicio();
        LocalDateTime fin = turno.getFechaHoraFin();

        if (
            inicio == null ||
            fin == null ||
            !fin.isAfter(inicio) ||
            !inicio.isAfter(LocalDateTime.now())
        ) {
            throw new BusinessException(
                "El turno no tiene un horario futuro válido"
            );
        }

        if (!inicio.toLocalDate().equals(fin.toLocalDate())) {
            throw new BusinessException(
                "El turno no puede abarcar varios días"
            );
        }

        //====================================
        //Validar empleada anterior
        //====================================

        EmpleadoEntity empleadaAnterior = turno.getEmpleado();

        if (empleadaAnterior == null) {
            throw new BusinessException(
                "El turno no tiene una empleada asignada"
            );
        }

        if (empleadaAnterior.getId().equals(idEmpleado)) {
            throw new BusinessException(
                "La nueva empleada ya está asignada a este turno"
            );
        }

        //====================================
        //Bloquear clienta
        //====================================

        if (turno.getCliente() == null) {
            throw new BusinessException(
                "El turno no tiene una clienta asociada"
            );
        }

        clienteRepository.bloquearParaReserva(
            turno.getCliente().getId()
        ).orElseThrow(() ->
            new BusinessException("Clienta no encontrada")
        );

        //====================================
        //Bloquear y obtener nueva empleada
        //====================================

        EmpleadoEntity nuevaEmpleada =
            empleadoRepository.bloquearParaReserva(idEmpleado)
                .orElseThrow(() ->
                    new BusinessException("Empleada no encontrada")
                );

        //====================================
        //Validar empleada activa
        //====================================

        if (
            nuevaEmpleada.getEstadoEmpleado() != EstadoEmpleadoEnums.ACTIVO ||
            nuevaEmpleada.getFechaBaja() != null ||
            nuevaEmpleada.getUsuario() == null ||
            nuevaEmpleada.getUsuario().getEstadoUsuario()
                != EstadoUsuarioEnums.ACTIVO
        ) {
            throw new BusinessException(
                "La nueva empleada no está habilitada para recibir turnos"
            );
        }

        //====================================
        //Validar jornada laboral
        //====================================

        int diaSemana = inicio.getDayOfWeek().getValue();

        List<HorarioEmpleadoEntity> horarios =
            horarioEmpleadoRepository.findByEmpleadoIdAndDiaSemana(
                idEmpleado,
                diaSemana
            );

        boolean dentroDeJornada = horarios.stream().anyMatch(horario ->
            !inicio.toLocalTime().isBefore(horario.getHoraInicio()) &&
            !fin.toLocalTime().isAfter(horario.getHoraFin())
        );

        if (!dentroDeJornada) {
            throw new BusinessException(
                "La nueva empleada no trabaja en el horario del turno"
            );
        }

        //====================================
        //Validar bloqueos de agenda
        //====================================

        boolean tieneBloqueos = !bloqueoAgendaRepository
            .findByEmpleadoIdAndInicioLessThanAndFinGreaterThan(
                idEmpleado,
                fin,
                inicio
            )
            .isEmpty();

        if (tieneBloqueos) {
            throw new BusinessException(
                "La nueva empleada tiene su agenda bloqueada en ese horario"
            );
        }

        //====================================
        //Validar otros turnos de la empleada
        //====================================

        boolean tieneOtroTurno = turnoRepository
            .findByEmpleadoIdAndIdNotAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                idEmpleado,
                idTurno,
                fin,
                inicio
            )
            .stream()
            .anyMatch(this::ocupaAgenda);

        if (tieneOtroTurno) {
            throw new BusinessException(
                "La nueva empleada ya tiene otro turno en ese horario"
            );
        }

        //====================================
        //Validar propuestas pendientes
        //de la nueva empleada
        //====================================

        boolean tienePropuestaPendiente =
            reprogramacionTurnoRepository
                .existePropuestaPendienteParaEmpleado(
                    idEmpleado,
                    inicio,
                    fin,
                    EstadoReprogramacionEnums.PENDIENTE
                );

        if (tienePropuestaPendiente) {
            throw new BusinessException(
                "El horario está reservado para una reprogramación pendiente de la nueva empleada"
            );
        }

        //====================================
        //Verificar propuesta pendiente
        //del turno original
        //====================================

        boolean teniaReprogramacion =
            reprogramacionTurnoRepository
                .existsByTurnoIdAndEstadoPropuesta(
                    idTurno,
                    EstadoReprogramacionEnums.PENDIENTE
                );

        //====================================
        //Actualizar empleada asignada
        //====================================

        turno.setEmpleado(nuevaEmpleada);

        turnoRepository.save(turno);

        //====================================
        //Anular la propuesta anterior
        //si existía una pendiente
        //====================================

        if (teniaReprogramacion) {
            anularPropuestaPendiente(idTurno);
        }

        //====================================
        //Notificar a la clienta
        //====================================

        String mensajeCliente =
            "Se modificó la empleada asignada a tu turno del " +
            formatearFechaTurno(inicio) +
            ". La fecha, los servicios y el precio no cambiaron.";

        if (teniaReprogramacion) {
            mensajeCliente +=
                " La propuesta de reprogramación anterior fue anulada.";
        }

        notificarUsuario(
            turno.getCliente().getUsuario().getId(),
            mensajeCliente
        );

        //====================================
        //Notificar a la nueva empleada
        //====================================

        notificarUsuario(
            nuevaEmpleada.getUsuario().getId(),
            "Se te asignó un turno para el " +
            formatearFechaTurno(inicio) + "."
        );

        //====================================
        //Notificar a la empleada anterior
        //====================================

        if (empleadaAnterior.getUsuario() != null) {

            notificarUsuario(
                empleadaAnterior.getUsuario().getId(),
                "Se te retiró el turno programado para el " +
                formatearFechaTurno(inicio) +
                " porque fue reasignado por la administración."
            );
        }
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
                servicioDTO.setIdTurnoServicio(ts.getId());
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
