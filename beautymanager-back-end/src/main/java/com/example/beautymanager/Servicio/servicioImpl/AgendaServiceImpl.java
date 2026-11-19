package com.example.beautymanager.Servicio.servicioImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ObtenerDisponibilidadReservaDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerTurnoDTO;
import com.example.beautymanager.Modelo.Entidad.BloqueoAgendaEntity;
import com.example.beautymanager.Modelo.Entidad.EmpleadoEntity;
import com.example.beautymanager.Modelo.Entidad.HorarioEmpleadoEntity;
import com.example.beautymanager.Modelo.Entidad.ServicioEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoEntity;
import com.example.beautymanager.Modelo.Enums.EstadoEmpleadoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoReprogramacionEnums;
import com.example.beautymanager.Modelo.Enums.EstadoServicioEnums;
import com.example.beautymanager.Repositorio.BloqueoAgendaRepository;
import com.example.beautymanager.Repositorio.EmpleadoRepository;
import com.example.beautymanager.Repositorio.HorarioEmpleadoRepository;
import com.example.beautymanager.Repositorio.ReprogramacionTurnoRepository;
import com.example.beautymanager.Repositorio.ServicioRepository;
import com.example.beautymanager.Repositorio.TurnoRepository;
import com.example.beautymanager.Servicio.AgendaService;
import com.example.beautymanager.Servicio.TurnoService;
import com.example.beautymanager.exception.BusinessException;
import com.example.beautymanager.utils.CompatibilidadServicio;

import jakarta.transaction.Transactional;

@Service
public class AgendaServiceImpl implements AgendaService {

   private final TurnoRepository turnoRepository;

   private final ServicioRepository servicioRepository;

   private final HorarioEmpleadoRepository horarioEmpleadoRepository;

   private final BloqueoAgendaRepository bloqueoAgendaRepository;

   private final EmpleadoRepository empleadoRepository;

   private final TurnoService turnoService;

   private final ReprogramacionTurnoRepository reprogramacionTurnoRepository;

   private static final Logger logger = LoggerFactory.getLogger(AgendaServiceImpl.class);

   public AgendaServiceImpl(TurnoRepository turnoRepository, ServicioRepository servicioRepository, HorarioEmpleadoRepository horarioEmpleadoRepository, BloqueoAgendaRepository bloqueoAgendaRepository, EmpleadoRepository empleadoRepository, TurnoService turnoService, ReprogramacionTurnoRepository reprogramacionTurnoRepository){

        this.turnoRepository = turnoRepository;
    
        this.servicioRepository = servicioRepository;

        this.horarioEmpleadoRepository = horarioEmpleadoRepository;

        this.bloqueoAgendaRepository = bloqueoAgendaRepository;

        this.empleadoRepository = empleadoRepository;

        this.turnoService = turnoService;

        this.reprogramacionTurnoRepository = reprogramacionTurnoRepository;
    }

   //===========================
   //Disponibilidad por dia
   //===========================
   @Override
   public List<LocalDateTime> obtenerDisponibilidad(Long idEmpleado, List<Long> serviciosIds, LocalDate fecha) {

        if (idEmpleado == null || serviciosIds == null || serviciosIds.isEmpty() || fecha == null) {
            throw new BusinessException("Datos invalidos para calcular disponibilidad");
        }

        EmpleadoEntity empleado = empleadoRepository.findById(idEmpleado)
            .orElseThrow(() -> new BusinessException("Empleado no encontrado"));

        if(empleado.getEstadoEmpleado() != EstadoEmpleadoEnums.ACTIVO || empleado.getFechaBaja() != null){
            return new ArrayList<>();
        }

        List<LocalDateTime> disponibles = new ArrayList<>();

        int duracion = calcularDuracionTotal(serviciosIds);

        List<HorarioEmpleadoEntity> horarios = horarioEmpleadoRepository.findByEmpleadoIdAndDiaSemana(idEmpleado, fecha.getDayOfWeek().getValue());

        logger.info("EMPLEADO CONSULTADO: {}", idEmpleado);

        logger.info("FECHA CONSULTADA: {}", fecha);

        logger.info("DIA DE LA SEMANA: {}", fecha.getDayOfWeek().getValue());

        logger.info("DURACION TOTAL: {} minutos", duracion);

        logger.info("HORARIOS LABORALES ENCONTRADOS: {}", horarios.size());

        for(HorarioEmpleadoEntity horario : horarios) {

            logger.info("HORARIO LABORAL: {} - {}", horario.getHoraInicio(), horario.getHoraFin());
        }

        if(horarios.isEmpty()) {
            return disponibles;
        }

        for(HorarioEmpleadoEntity h : horarios) {

            LocalDateTime inicio = fecha.atTime(h.getHoraInicio());
            LocalDateTime fin = fecha.atTime(h.getHoraFin());

            while(!inicio.plusMinutes(duracion).isAfter(fin)) {

                LocalDateTime finTurno = inicio.plusMinutes(duracion);

                if(validarDisponibilidad(idEmpleado, inicio, finTurno) && validarBloqueos(idEmpleado, inicio, finTurno)) {
                    disponibles.add(inicio);
                }
                inicio = inicio.plusMinutes(30);
            }
        }

        logger.info("HORARIOS DISPONIBLES GENERADOS: {}", disponibles);

        return disponibles;
    }

    
    //===========================================
    //Empleados disponibles para una reserva
    //===========================================
    @Override
    @Transactional
    public List<ObtenerDisponibilidadReservaDTO> obtenerEmpleadosDisponibles(List<Long> serviciosIds, LocalDate fecha) {

        if(serviciosIds == null || serviciosIds.isEmpty() || fecha == null) {
            throw new BusinessException("Los servicios y la fecha son obligatorios");
        }

        List<ServicioEntity> servicios = new ArrayList<>();

        for(Long idServicio : serviciosIds) {

            if(idServicio == null) {
                throw new BusinessException("El servicio es obligatorio");
            }

            ServicioEntity servicio = servicioRepository.findById(idServicio)
                .orElseThrow(() -> new BusinessException("Servicio no encontrado: " + idServicio));

            if(servicio.getEstado() != EstadoServicioEnums.ACTIVO) {
                throw new BusinessException("El servicio no esta activo: " + servicio.getNombre());
            }

            servicios.add(servicio);
        }

        List<EmpleadoEntity> empleadosActivos = empleadoRepository.findByEstadoEmpleado(EstadoEmpleadoEnums.ACTIVO);

        List<ObtenerDisponibilidadReservaDTO> resultado = new ArrayList<>();

        for(EmpleadoEntity empleado : empleadosActivos) {

            if(empleado.getFechaBaja() != null) {
                continue;
            }

            boolean compatible = servicios.stream().allMatch(servicio -> CompatibilidadServicio.puedeRealizar(empleado, servicio));

            if(!compatible) {
                continue;
            }

            List<LocalDateTime> horarios = obtenerDisponibilidad(empleado.getId(), serviciosIds, fecha).stream().filter(horario -> horario.isAfter(LocalDateTime.now())).toList();

            if(horarios.isEmpty()) {
                continue;
            }

            ObtenerDisponibilidadReservaDTO obtenerDisponibilidadReservaDTO = new ObtenerDisponibilidadReservaDTO();

            obtenerDisponibilidadReservaDTO.setIdEmpleado(empleado.getId());

            obtenerDisponibilidadReservaDTO.setNombre(empleado.getUsuario().getNombre());

            obtenerDisponibilidadReservaDTO.setApellido(empleado.getUsuario().getApellido());

            obtenerDisponibilidadReservaDTO.setEspecialidad(empleado.getEspecialidad());

            obtenerDisponibilidadReservaDTO.setHorariosDisponibles(horarios);

            resultado.add(obtenerDisponibilidadReservaDTO);
        }

        return resultado;
    }


   //==============================
   //Disponibilidad por rango
   //==============================
   @Override
   public List<LocalDateTime> obtenerDisponibilidadRango(Long idEmpleado, List<Long> serviciosIds, LocalDate inicio, LocalDate fin) {

    if (idEmpleado == null || serviciosIds == null || serviciosIds.isEmpty() || inicio == null || fin == null) {
        throw new BusinessException("Datos invalidos para calcular disponibilidad");
    }

    if (inicio.isAfter(fin)) {
        throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
    }

    List<LocalDateTime> resultado = new ArrayList<>();

    while (!inicio.isAfter(fin)) {
        resultado.addAll(obtenerDisponibilidad(idEmpleado, serviciosIds, inicio));
        inicio = inicio.plusDays(1);
    }
    return resultado;
   }

   //=======================================
   //Obtener disponibilidad por empleado
   //=======================================
   @Override
   public List<LocalDateTime> obtenerDisponibilidadPorEmpleado(Long idUsuario, List<Long> serviciosIds, LocalDate fecha){

    EmpleadoEntity empleado = empleadoRepository.findByUsuario_Id(idUsuario)
            .orElseThrow(() -> new BusinessException("El usuario no pertenece a un empleado"));

    return obtenerDisponibilidad(empleado.getId(), serviciosIds, fecha);
   }

   //=================================
   //Obtener disponibilidad general
   //=================================
   @Override 
   public List<LocalDateTime> obtenerDisponibilidadGeneral(LocalDate fecha) {

    System.out.println("ENTRO A OBTENER DISPONIBILIDAD GENERAL");

    System.out.println("FECHA RECIBIDA: " + fecha);

    if(fecha == null) {
        throw new BusinessException("La fecha es obligatoria");
    }

    List<LocalDateTime> disponibles = new ArrayList<>();

    List<EmpleadoEntity> empleadosActivos = empleadoRepository.findByEstadoEmpleado(EstadoEmpleadoEnums.ACTIVO)
        .stream()
        .filter(empleado -> empleado.getFechaBaja() == null)
        .toList();

    System.out.println("FECHA CONSULTADA: " + fecha);

    System.out.println("EMPLEADOS ACTIVOS: " + empleadosActivos.size());

    if(empleadosActivos.isEmpty()){
        return disponibles;
    }

    for(EmpleadoEntity empleado : empleadosActivos){

        List<HorarioEmpleadoEntity> horarios = horarioEmpleadoRepository.findByEmpleadoIdAndDiaSemana(empleado.getId(), fecha.getDayOfWeek().getValue());

        System.out.println("EMPLEADO: " + empleado.getId() + " - HORARIOS: " + horarios.size() + " - DIA SEMANA: " + fecha.getDayOfWeek().getValue());

        for(HorarioEmpleadoEntity horario : horarios){
            System.out.println("HORARIO: " + horario.getHoraInicio() + " - " + horario.getHoraFin());
        }
    }

    for(int hora = 8; hora < 20; hora++){

        for(int minuto = 0; minuto < 60; minuto += 30){

            LocalDateTime inicio = fecha.atTime(hora, minuto);

            LocalDateTime fin = inicio.plusMinutes(30);

            boolean existeEmpleadoDisponible = false;

            for(EmpleadoEntity empleado : empleadosActivos){

                List<HorarioEmpleadoEntity> horarios = horarioEmpleadoRepository.findByEmpleadoIdAndDiaSemana(empleado.getId(), fecha.getDayOfWeek().getValue());

                for(HorarioEmpleadoEntity horario : horarios){

                    LocalDateTime inicioHorario = fecha.atTime(horario.getHoraInicio());

                    LocalDateTime finHorario = fecha.atTime(horario.getHoraFin());

                    if(!inicio.isBefore(inicioHorario) && !fin.isAfter(finHorario) && validarDisponibilidad(empleado.getId(), inicio, fin) && validarBloqueos(empleado.getId(), inicio, fin)){

                        existeEmpleadoDisponible = true;
                        
                        break;
                    }
                }

                if(existeEmpleadoDisponible){
                    break;
                }
            }

            if(existeEmpleadoDisponible){
                disponibles.add(inicio);
            }
        }
    }

    return disponibles;
   }

   //========================
   //Obtener turno
   //========================
   @Override
   public List<ObtenerTurnoDTO> obtenerAgenda(LocalDate inicio, LocalDate fin){

    if(inicio == null || fin == null){
        throw new BusinessException("Las fechas son obligatorias");
    }

    if(inicio.isAfter(fin)){
        throw new BusinessException("La fecha de inicio no puede ser mayor que la fecha de fin");
    }

    LocalDateTime fechaInicio = inicio.atStartOfDay();
    LocalDateTime fechaFin = fin.atTime(20,00,00);

    return turnoRepository.findByFechaHoraInicioBetween(fechaInicio, fechaFin).stream().map(turnoService::toMap).toList();
   }

   //===========================
   //Validar disponibilidad
   //===========================
   @Override
   public boolean validarDisponibilidad(Long idEmpleado, LocalDateTime inicio, LocalDateTime fin){

    if (
            idEmpleado == null ||
            inicio == null ||
            fin == null
        ) {
            throw new BusinessException(
                "Los datos para validar disponibilidad son obligatorios"
            );
        }

        if (!inicio.isBefore(fin)) {
            throw new BusinessException(
                "La fecha de inicio debe ser anterior a la fecha de fin"
            );
        }

        //====================================
        //Verificar turnos existentes
        //====================================

        List<TurnoEntity> turnos = turnoRepository
            .findByEmpleadoIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                idEmpleado,
                fin,
                inicio
            );

        boolean ocupadoPorTurno = turnos.stream().anyMatch(turno -> {

            return switch (turno.getEstadoTurno()) {

                case PENDIENTE, CONFIRMADO, EN_CURSO, FINALIZADO -> true;

                case CANCELADO, RECHAZADO, REPROGRAMADO, AUSENTE -> false;
            };
        });

        if (ocupadoPorTurno) {
            return false;
        }

        //====================================
        //Verificar propuestas pendientes
        //====================================

        boolean ocupadoPorPropuesta =
            reprogramacionTurnoRepository
                .existePropuestaPendienteParaEmpleado(
                    idEmpleado,
                    inicio,
                    fin,
                    EstadoReprogramacionEnums.PENDIENTE
                );

        return !ocupadoPorPropuesta;
   }

   //========================
   //Validad bloqueo
   //========================
   private boolean validarBloqueos(Long idEmpleado, LocalDateTime inicio, LocalDateTime fin) {

    List<BloqueoAgendaEntity> bloqueos = bloqueoAgendaRepository.findByEmpleadoIdAndInicioLessThanAndFinGreaterThan(idEmpleado, fin, inicio);

    return bloqueos.isEmpty();
   }

   //=============================
   //Calcular duracion total
   //=============================
   @Override
   public Integer calcularDuracionTotal(List<Long> serviciosIds) {
    
    if(serviciosIds == null || serviciosIds.isEmpty()) {
        throw new BusinessException("Debe seleccionar al menos un servicio");
    }

    int total = 0;

    for (Long id : serviciosIds) {
        ServicioEntity servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Servicio no encontrado: " + id));

        total += servicio.getDuracionMinutos();
    }
    return total;
   }
}
