package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.example.beautymanager.Modelo.DTO.CrearReservaDTO;
import com.example.beautymanager.Modelo.DTO.CrearReservaTurnoDTO;
import com.example.beautymanager.Modelo.Entidad.ClienteEntity;
import com.example.beautymanager.Modelo.Entidad.EmpleadoEntity;
import com.example.beautymanager.Modelo.Entidad.HorarioEmpleadoEntity;
import com.example.beautymanager.Modelo.Entidad.ReservaEntity;
import com.example.beautymanager.Modelo.Entidad.ServicioEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoServicioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoEmpleadoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoServicioEnums;
import com.example.beautymanager.Modelo.Enums.EstadoTurnoEnums;
import com.example.beautymanager.Repositorio.BloqueoAgendaRepository;
import com.example.beautymanager.Repositorio.ClienteRepository;
import com.example.beautymanager.Repositorio.EmpleadoRepository;
import com.example.beautymanager.Repositorio.HorarioEmpleadoRepository;
import com.example.beautymanager.Repositorio.ReservaRepository;
import com.example.beautymanager.Repositorio.ServicioRepository;
import com.example.beautymanager.Repositorio.TurnoRepository;
import com.example.beautymanager.Servicio.RecordatorioService;
import com.example.beautymanager.Servicio.ReservaService;
import com.example.beautymanager.exception.BusinessException;
import com.example.beautymanager.utils.CompatibilidadServicio;
import com.example.beautymanager.utils.HorarioAtencion;

@Service 
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ServicioRepository servicioRepository;
    private final TurnoRepository turnoRepository;
    private final HorarioEmpleadoRepository horarioEmpleadoRepository;
    private final BloqueoAgendaRepository bloqueoAgendaRepository;
    private final RecordatorioService recordatorioService;

    public ReservaServiceImpl(
            ReservaRepository reservaRepository,
            ClienteRepository clienteRepository,
            EmpleadoRepository empleadoRepository,
            ServicioRepository servicioRepository,
            TurnoRepository turnoRepository,
            HorarioEmpleadoRepository horarioEmpleadoRepository,
            BloqueoAgendaRepository bloqueoAgendaRepository, RecordatorioService recordatorioService) {

        this.reservaRepository = reservaRepository;
        this.clienteRepository = clienteRepository;
        this.empleadoRepository = empleadoRepository;
        this.servicioRepository = servicioRepository;
        this.turnoRepository = turnoRepository;
        this.horarioEmpleadoRepository = horarioEmpleadoRepository;
        this.bloqueoAgendaRepository = bloqueoAgendaRepository;
        this.recordatorioService = recordatorioService;
    }

    //=========================
    //Crear reserva
    //=========================
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Long crearReserva(CrearReservaDTO crearReserva) {

        if(crearReserva == null ||
           crearReserva.getIdCliente() == null ||
           crearReserva.getTurnos() == null ||
           crearReserva.getTurnos().isEmpty()) {

            throw new BusinessException("Los datos de la reserva son obligatorios");
        }

        
        //=========================
        //Bloquear cliente
        //=========================
        ClienteEntity cliente = clienteRepository
            .bloquearParaReserva(crearReserva.getIdCliente())
            .orElseThrow(() ->
                new BusinessException("Cliente no encontrado")
            );

        //=========================
        //Validar solicitudes y
        //reunir empleados
        //=========================
        Set<Long> idsEmpleados = new TreeSet<>();

        for(CrearReservaTurnoDTO solicitud : crearReserva.getTurnos()) {

            if(solicitud == null ||
               solicitud.getIdEmpleado() == null ||
               solicitud.getFechaHoraInicio() == null ||
               solicitud.getServiciosIds() == null ||
               solicitud.getServiciosIds().isEmpty()) {

                throw new BusinessException(
                    "Los datos de uno de los turnos son invalidos"
                );
            }

            idsEmpleados.add(solicitud.getIdEmpleado());
        }

        //=========================
        //Bloquear empleados
        //en orden ascendente
        //=========================
        Map<Long, EmpleadoEntity> empleadosBloqueados =
            new HashMap<>();

        for(Long idEmpleado : idsEmpleados) {

            EmpleadoEntity empleado = empleadoRepository
                .bloquearParaReserva(idEmpleado)
                .orElseThrow(() ->
                    new BusinessException(
                        "Empleado no encontrado: " + idEmpleado
                    )
                );

            empleadosBloqueados.put(idEmpleado, empleado);
        }

        //=========================
        //Preparar reserva
        //=========================
        
        ReservaEntity reserva = new ReservaEntity();

        reserva.setCliente(cliente);
        reserva.setClaveIdempotencia(crearReserva.getClaveIdempotencia());

        List<TurnoEntity> turnosPreparados = new ArrayList<>();


        for(CrearReservaTurnoDTO solicitud : crearReserva.getTurnos()) {

            if(solicitud == null ||
               solicitud.getIdEmpleado() == null ||
               solicitud.getFechaHoraInicio() == null ||
               solicitud.getServiciosIds() == null ||
               solicitud.getServiciosIds().isEmpty()) {

                throw new BusinessException("Los datos de uno de los turnos son invalidos");
            }

            LocalDateTime inicio = solicitud.getFechaHoraInicio();

            if(inicio.isBefore(LocalDateTime.now())) {
                throw new BusinessException("No se pueden reservar horarios pasados");
            }

            EmpleadoEntity empleado = empleadosBloqueados.get(solicitud.getIdEmpleado());

            if(empleado.getEstadoEmpleado() != EstadoEmpleadoEnums.ACTIVO ||
               empleado.getFechaBaja() != null) {

                throw new BusinessException("El empleado no esta disponible para recibir turnos");
            }

            int duracionTotal = 0;
            BigDecimal precioTotal = BigDecimal.ZERO;

            List<ServicioEntity> servicios = new ArrayList<>();

            for(Long idServicio : solicitud.getServiciosIds()) {

                if(idServicio == null) {
                    throw new BusinessException("El identificador del servicio es obligatorio");
                }

                ServicioEntity servicio = servicioRepository.findById(idServicio)
                    .orElseThrow(() -> new BusinessException("Servicio no encontrado: " + idServicio));

                if(servicio.getEstado() != EstadoServicioEnums.ACTIVO) {
                    throw new BusinessException("El servicio no esta activo: " + servicio.getNombre());
                }

                if(servicio.getDuracionMinutos() == null ||
                   servicio.getDuracionMinutos() <= 0) {

                    throw new BusinessException("Duracion invalida: " + servicio.getNombre());
                }

                if(servicio.getPrecio() == null) {
                    throw new BusinessException("El servicio no tiene un precio asignado");
                }

                if(!CompatibilidadServicio.puedeRealizar(empleado, servicio)) {
                    throw new BusinessException(
                        "El empleado no puede realizar el servicio: " + servicio.getNombre()
                    );
                }

                duracionTotal += servicio.getDuracionMinutos();
                precioTotal = precioTotal.add(servicio.getPrecio());

                servicios.add(servicio);
            }

            LocalDateTime fin = inicio.plusMinutes(duracionTotal);

            if(!inicio.toLocalDate().equals(fin.toLocalDate()) ||
               inicio.toLocalTime().isBefore(HorarioAtencion.APERTURA) ||
               fin.toLocalTime().isAfter(HorarioAtencion.CIERRE)) {

                throw new BusinessException("El turno esta fuera del horario de atencion");
            }

            validarHorarioLaboral(empleado.getId(), inicio, fin);
            validarOcupacionEmpleado(empleado.getId(), inicio, fin);
            validarBloqueos(empleado.getId(), inicio, fin);
            validarOcupacionCliente(cliente.getId(), inicio, fin);

            for(TurnoEntity anterior : turnosPreparados) {

                if(seSuperponen(
                    inicio, fin,
                    anterior.getFechaHoraInicio(),
                    anterior.getFechaHoraFin())) {

                    throw new BusinessException(
                        "Los servicios de la reserva tienen horarios superpuestos"
                    );
                }
            }

            TurnoEntity turno = new TurnoEntity();

            turno.setCliente(cliente);
            turno.setEmpleado(empleado);
            turno.setReserva(reserva);
            turno.setFechaHoraInicio(inicio);
            turno.setFechaHoraFin(fin);
            turno.setPrecioTotal(precioTotal);
            turno.setEstadoTurno(EstadoTurnoEnums.PENDIENTE);

            List<TurnoServicioEntity> relaciones = new ArrayList<>();

            for(ServicioEntity servicio : servicios) {

                TurnoServicioEntity relacion = new TurnoServicioEntity();

                relacion.setTurno(turno);
                relacion.setServicio(servicio);

                relaciones.add(relacion);
            }

            turno.setServicios(relaciones);

            turnosPreparados.add(turno);
        }

        ReservaEntity reservaGuardada = reservaRepository.save(reserva);

        List<TurnoEntity> turnosGuardados = turnoRepository.saveAll(turnosPreparados);

        for (TurnoEntity turno : turnosGuardados) {

            recordatorioService.crearRecordatorios(turno);
        }

        return reservaGuardada.getId();
    }

    //============================
    //Validar horario laboral
    //============================
    private void validarHorarioLaboral(
            Long idEmpleado,
            LocalDateTime inicio,
            LocalDateTime fin) {

        List<HorarioEmpleadoEntity> horarios =
            horarioEmpleadoRepository.findByEmpleadoIdAndDiaSemana(
                idEmpleado,
                inicio.getDayOfWeek().getValue()
            );

        boolean dentroHorario = horarios.stream().anyMatch(horario ->
            !inicio.toLocalTime().isBefore(horario.getHoraInicio()) &&
            !fin.toLocalTime().isAfter(horario.getHoraFin())
        );

        if(!dentroHorario) {
            throw new BusinessException(
                "El turno no coincide con el horario laboral del empleado"
            );
        }
    }

    //============================
    //Validar ocupacion empleado
    //============================
    private void validarOcupacionEmpleado(
            Long idEmpleado,
            LocalDateTime inicio,
            LocalDateTime fin) {

        boolean ocupado = turnoRepository
            .findByEmpleadoIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                idEmpleado, fin, inicio
            )
            .stream()
            .anyMatch(this::ocupaAgenda);

        if(ocupado) {
            throw new BusinessException(
                "El empleado ya tiene un turno en ese horario"
            );
        }
    }

    //=========================
    //Validar bloqueos
    //=========================
    private void validarBloqueos(
            Long idEmpleado,
            LocalDateTime inicio,
            LocalDateTime fin) {

        boolean bloqueado = !bloqueoAgendaRepository
            .findByEmpleadoIdAndInicioLessThanAndFinGreaterThan(
                idEmpleado, fin, inicio
            )
            .isEmpty();

        if(bloqueado) {
            throw new BusinessException(
                "El empleado tiene un bloqueo de agenda en ese horario"
            );
        }
    }

    //==========================
    //Validar ocupacion cliente
    //==========================
    private void validarOcupacionCliente(
            Long idCliente,
            LocalDateTime inicio,
            LocalDateTime fin) {

        boolean ocupado = turnoRepository
            .findByClienteIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                idCliente, fin, inicio
            )
            .stream()
            .anyMatch(this::ocupaAgenda);

        if(ocupado) {
            throw new BusinessException(
                "El cliente ya tiene un turno en ese horario"
            );
        }
    }

    //========================
    //Estados que ocupan agenda
    //========================
    private boolean ocupaAgenda(TurnoEntity turno) {

        return switch(turno.getEstadoTurno()) {

            case PENDIENTE, CONFIRMADO, EN_CURSO, FINALIZADO -> true;

            case CANCELADO, RECHAZADO, REPROGRAMADO, AUSENTE -> false;
        };
    }

    //=========================
    //Superposicion de horarios
    //=========================
    private boolean seSuperponen(
            LocalDateTime inicioA,
            LocalDateTime finA,
            LocalDateTime inicioB,
            LocalDateTime finB) {

        return inicioA.isBefore(finB) && finA.isAfter(inicioB);
    }
}
