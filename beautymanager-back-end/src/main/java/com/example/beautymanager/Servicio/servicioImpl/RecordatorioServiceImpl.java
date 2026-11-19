package com.example.beautymanager.Servicio.servicioImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.CrearNotificacionDTO;
import com.example.beautymanager.Modelo.Entidad.ConfiguracionRecordatorioEntity;
import com.example.beautymanager.Modelo.Entidad.RecordatorioEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoEntity;
import com.example.beautymanager.Modelo.Enums.EstadoRecordatorioEnums;
import com.example.beautymanager.Modelo.Enums.EstadoTurnoEnums;
import com.example.beautymanager.Modelo.Enums.TipoNotificacionEnums;
import com.example.beautymanager.Repositorio.ConfiguracionRecordatorioRepository;
import com.example.beautymanager.Repositorio.RecordatorioRepository;
import com.example.beautymanager.Repositorio.TurnoRepository;
import com.example.beautymanager.Servicio.EmailService;
import com.example.beautymanager.Servicio.NotificacionService;
import com.example.beautymanager.Servicio.RecordatorioService;
import com.example.beautymanager.exception.BusinessException;

import jakarta.transaction.Transactional;

@Service
public class RecordatorioServiceImpl implements RecordatorioService {

    private static final Logger logger = LoggerFactory.getLogger(RecordatorioServiceImpl.class);

    private static final int MAX_INTENTOS = 3;

    private final RecordatorioRepository recordatorioRepository;

    private final EmailService emailService;

    private final NotificacionService notificacionService;

    private final ConfiguracionRecordatorioRepository configuracionRepository;
    
    private final TurnoRepository turnoRepository;

    public RecordatorioServiceImpl(RecordatorioRepository recordatorioRepository, EmailService emailService, NotificacionService notificacionService, ConfiguracionRecordatorioRepository configuracionRepository, TurnoRepository turnoRepository) {

    this.recordatorioRepository = recordatorioRepository;
    this.emailService = emailService;
    this.notificacionService = notificacionService;
    this.configuracionRepository = configuracionRepository;
    this.turnoRepository = turnoRepository;
}

    //=========================
    //Crear recordatorios
    //=========================
    @Override
    public void crearRecordatorios(TurnoEntity turno) {

        if(turno == null){
            throw new BusinessException("El turno no debe ser nulo");
        }

        LocalDateTime inicio = turno.getFechaHoraInicio();

        if(inicio == null){
            throw new BusinessException("La fecha del turno es obligatoria");
        }

        if(turno.getEstadoTurno() != null){
            
            EstadoTurnoEnums estadoTurno = turno.getEstadoTurno();

            if(estadoTurno == EstadoTurnoEnums.CANCELADO){
                throw new BusinessException("No se pueden crear recordatorios para turnos cancelados");
            }
        }

        ConfiguracionRecordatorioEntity configuracion = configuracionRepository.findFirstByOrderByIdConfiguracionAsc()
            .orElseThrow(() -> new BusinessException("No existe una configuracion de recordatorios"));

        List<RecordatorioEntity> lista = new ArrayList<>();

        LocalDateTime primerRecordatorio = inicio.minusHours(configuracion.getPrimerRecordatorioHoras());

        if(primerRecordatorio.isAfter(LocalDateTime.now())){
            crearPorConfiguracion(lista, turno,primerRecordatorio, configuracion);
        }

        LocalDateTime segundoRecordatorio = inicio.minusHours(configuracion.getSegundoRecordatorioHoras());

        if(segundoRecordatorio.isAfter(LocalDateTime.now())){
            crearPorConfiguracion(lista, turno, segundoRecordatorio, configuracion);
        }

        if(!lista.isEmpty()){
            recordatorioRepository.saveAll(lista);
        }
    }


    //========================================
    //Reprogramar recordatorios de un turno
    //========================================
    @Override
    @Transactional
    public void reprogramarRecordatoriosDeTurno(TurnoEntity turno) {

        //====================================
        //Validar turno
        //====================================

        if (turno == null || turno.getId() == null) {
            throw new BusinessException(
                "El turno es obligatorio"
            );
        }

        if (turno.getFechaHoraInicio() == null) {
            throw new BusinessException(
                "El turno no tiene una fecha válida"
            );
        }

        //====================================
        //Validar estado
        //====================================

        EstadoTurnoEnums estado = turno.getEstadoTurno();

        if (
            estado != EstadoTurnoEnums.PENDIENTE &&
            estado != EstadoTurnoEnums.CONFIRMADO
        ) {
            throw new BusinessException(
                "No se pueden reprogramar recordatorios para este estado"
            );
        }

        //====================================
        //Validar nueva fecha
        //====================================

        if (!turno.getFechaHoraInicio().isAfter(LocalDateTime.now())) {
            throw new BusinessException(
                "La nueva fecha del turno debe ser futura"
            );
        }

        //====================================
        //Obtener recordatorios no enviados
        //====================================

        List<RecordatorioEntity> pendientes =
            recordatorioRepository.findByTurnoIdAndEnviadoFalse(
                turno.getId()
            );

        //====================================
        //Eliminar los recordatorios anteriores
        //====================================

        if (!pendientes.isEmpty()) {

            recordatorioRepository.deleteAll(pendientes);

            logger.info(
                "Se eliminaron {} recordatorios pendientes del turno {}",
                pendientes.size(),
                turno.getId()
            );
        }

        //====================================
        //Crear recordatorios con la nueva fecha
        //====================================

        crearRecordatorios(turno);

        logger.info(
            "Recordatorios actualizados para el turno {} con fecha {}",
            turno.getId(),
            turno.getFechaHoraInicio()
        );
    }


    private void crearPorConfiguracion(List<RecordatorioEntity> lista, TurnoEntity turno, LocalDateTime fecha, ConfiguracionRecordatorioEntity configuracion){

        if(configuracion.getEstadoEmail() == EstadoRecordatorioEnums.ACTIVO){
            lista.add(crear(turno, fecha, TipoNotificacionEnums.EMAIL));
        }

        if(configuracion.getEstadoWhatsapp() == EstadoRecordatorioEnums.ACTIVO){
            lista.add(crear(turno, fecha, TipoNotificacionEnums.WHATSAPP));
        }

        if(configuracion.getEstadoNotificacionInterna() == EstadoRecordatorioEnums.ACTIVO){
            lista.add(crear(turno, fecha, TipoNotificacionEnums.INTERNA));
        }
    }

    private RecordatorioEntity crear(TurnoEntity turno, LocalDateTime fecha, TipoNotificacionEnums tipo) {

        RecordatorioEntity r = new RecordatorioEntity();
        r.setTurno(turno);
        r.setFechaEnvio(fecha);
        r.setEnviado(false);
        r.setTipo(tipo);
        r.setMensaje("Recordatorio: tienes un turno programado para el  " + turno.getFechaHoraInicio());

        r.setIntentos(0);
        r.setErrorEnvio(null);

        return r;
    }

    //=============================
    //Procesar automaticamente
    //=============================
    @Override
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void procesarRecordatorios(){
        List<RecordatorioEntity> pendientes = recordatorioRepository.findByEnviadoFalseAndFechaEnvioBefore(LocalDateTime.now());

        for (RecordatorioEntity r : pendientes) {
            
            try {

                if(r.getIntentos() != null && r.getIntentos() >= MAX_INTENTOS) {
                    
                    logger.warn("Recordatorio ID {} supero el maximo de intentos", r.getId());

                    continue;
                }

                
                //====================================
                //Verificar que el turno siga vigente
                //====================================

                if (r.getTurno() == null) {
                    logger.warn(
                        "Recordatorio ID {} sin turno asociado",
                        r.getId()
                    );

                    r.setEnviado(true);
                    r.setErrorEnvio(
                        "Turno asociado no encontrado"
                    );

                    continue;
                }

                EstadoTurnoEnums estadoTurno =
                    r.getTurno().getEstadoTurno();

                if (
                    estadoTurno != EstadoTurnoEnums.PENDIENTE &&
                    estadoTurno != EstadoTurnoEnums.CONFIRMADO
                ) {

                    logger.info(
                        "Recordatorio ID {} omitido por estado {}",
                        r.getId(),
                        estadoTurno
                    );

                    r.setEnviado(true);
                    r.setErrorEnvio(null);

                    continue;
                }

                
                switch (r.getTipo()) {

                    case EMAIL -> enviarEmail(r);

                    case WHATSAPP -> enviarWhatsapp(r);

                    case INTERNA -> enviarInterna(r);
                }

                r.setEnviado(true);

                r.setErrorEnvio(null);

            } catch (Exception e) {
                
                logger.error("Error enviando recordatorio ID {}: {}", r.getId(), e.getMessage());

                r.setErrorEnvio(e.getMessage());

                Integer intentosRegistrados = r.getIntentos();

                int intentosActuales = 0;

                if(intentosRegistrados != null){
                    intentosActuales = intentosRegistrados;
                }

                r.setIntentos(intentosActuales + 1);
            }
        }

        recordatorioRepository.saveAll(pendientes);
    }

    //==================================
    //Reprogramar recordatorios futuros
    //==================================
    @Override
    @Transactional
    public void reprogramarRecordatoriosFuturos() {

        ConfiguracionRecordatorioEntity configuracion = configuracionRepository.findFirstByOrderByIdConfiguracionAsc()
            .orElseThrow(() -> new BusinessException("No existe una configuracion de recordatorios"));

        LocalDateTime ahora = LocalDateTime.now();

        List<TurnoEntity> turnosFuturos = turnoRepository.findByFechaHoraInicioAfter(ahora);

        for(TurnoEntity turno : turnosFuturos){

            if (turno.getEstadoTurno() != EstadoTurnoEnums.PENDIENTE && turno.getEstadoTurno() != EstadoTurnoEnums.CONFIRMADO) {
                continue;
            }

            List<RecordatorioEntity> recordatoriosPendientes = recordatorioRepository.findByTurnoIdAndEnviadoFalse(turno.getId());

            if(!recordatoriosPendientes.isEmpty()){
                recordatorioRepository.deleteAll(recordatoriosPendientes);
            }

            List<RecordatorioEntity> nuevosRecordatorios = new ArrayList<>();

            LocalDateTime primerRecordatorio = turno.getFechaHoraInicio().minusHours(configuracion.getPrimerRecordatorioHoras());

            if(primerRecordatorio.isAfter(ahora)){
                crearPorConfiguracion(nuevosRecordatorios, turno, primerRecordatorio, configuracion);
            }

            LocalDateTime segundoRecordatorio = turno.getFechaHoraInicio().minusHours(configuracion.getSegundoRecordatorioHoras());

            if(segundoRecordatorio.isAfter(ahora)){
                crearPorConfiguracion(nuevosRecordatorios, turno, segundoRecordatorio, configuracion);
            }

            if(!nuevosRecordatorios.isEmpty()){
                recordatorioRepository.saveAll(nuevosRecordatorios);
            }
        }
    }

    //=================
    //Email
    //=================
    private void enviarEmail(RecordatorioEntity r) {

        String email = r.getTurno()
                .getCliente()
                .getUsuario()
                .getEmail();

        if(email == null || email.isBlank()) {
            throw new BusinessException("El usuario no tiene email registrado");
        }

        logger.info("Enviando email a {}", email);

        emailService.enviarEmail(r);
    }

    //==================
    //Whatsapp
    //==================
    private void enviarWhatsapp(RecordatorioEntity r) {

        String telefono = r.getTurno()
                .getCliente()
                .getUsuario()
                .getTelefono();

        if(telefono == null || telefono.isBlank()) {
            throw new BusinessException("El usuario no tiene telefono registrado");
        }

        logger.info("Whatsapp enviado a {}", telefono);

        logger.info("Mensaje: {}", r.getMensaje());
    }

    //=========================
    //Notificacion interna
    //=========================
    private void enviarInterna(RecordatorioEntity r) {

        Long idUsuario = r.getTurno()
                .getCliente()
                .getUsuario()
                .getId();

        logger.info("Notificacion interna creada para usuario ID {}", idUsuario);

        CrearNotificacionDTO crearNotificacionDTO = new CrearNotificacionDTO();

        crearNotificacionDTO.setIdUsuario(idUsuario);
        crearNotificacionDTO.setMensaje(r.getMensaje());

        notificacionService.crearNotificacion(crearNotificacionDTO);
    }
}