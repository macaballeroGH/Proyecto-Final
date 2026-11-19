package com.example.beautymanager.Servicio.servicioImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.HistorialClienteDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerHistorialTratamientoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerHistorialTratamientoServicioDTO;
import com.example.beautymanager.Modelo.DTO.RegistrarTratamientoDTO;
import com.example.beautymanager.Modelo.DTO.RegistrarTratamientoServicioDTO;
import com.example.beautymanager.Modelo.Entidad.ClienteEntity;
import com.example.beautymanager.Modelo.Entidad.EmpleadoEntity;
import com.example.beautymanager.Modelo.Entidad.HistorialTratamientoEntity;
import com.example.beautymanager.Modelo.Entidad.HistorialTratamientoServicioEntity;
import com.example.beautymanager.Modelo.Entidad.ServicioEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoServicioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoTurnoEnums;
import com.example.beautymanager.Repositorio.ClienteRepository;
import com.example.beautymanager.Repositorio.EmpleadoRepository;
import com.example.beautymanager.Repositorio.HistorialTratamientoRepository;
import com.example.beautymanager.Repositorio.ServicioRepository;
import com.example.beautymanager.Repositorio.TurnoRepository;
import com.example.beautymanager.Servicio.HistorialTratamientoService;
import com.example.beautymanager.Servicio.PagoService;
import com.example.beautymanager.exception.BusinessException;

import jakarta.transaction.Transactional;

@Service
public class HistorialTratamientoServiceImpl implements HistorialTratamientoService {

    private final HistorialTratamientoRepository historialTratamientoRepository;

    private final TurnoRepository turnoRepository;

    private final ClienteRepository clienteRepository;

    private final EmpleadoRepository empleadoRepository;

    private final PagoService pagoService;

    private final ServicioRepository servicioRepository;

    public HistorialTratamientoServiceImpl(HistorialTratamientoRepository historialTratamientoRepository, TurnoRepository turnoRepository, ClienteRepository clienteRepository, EmpleadoRepository empleadoRepository, PagoService pagoService, ServicioRepository servicioRepository){

        this.historialTratamientoRepository = historialTratamientoRepository;

        this.turnoRepository = turnoRepository;

        this.clienteRepository = clienteRepository;

        this.empleadoRepository = empleadoRepository;

        this.pagoService = pagoService;

        this.servicioRepository = servicioRepository;
    }

    //=========================
    //Registrar tratamiento
    //=========================
    @Override
    @Transactional
    public ObtenerHistorialTratamientoDTO registrarTratamiento(Long idUsuario, RegistrarTratamientoDTO registrarTratamiento) {

        if (idUsuario == null) {
            throw new BusinessException(
                "El usuario es obligatorio"
            );
        }

        if (registrarTratamiento == null) {
            throw new BusinessException(
                "Los datos del tratamiento son obligatorios"
            );
        }

        if (registrarTratamiento.getIdTurno() == null) {
            throw new BusinessException(
                "Debe indicar un turno"
            );
        }

        if (registrarTratamiento.getDuracionReal() == null ||
                registrarTratamiento.getDuracionReal() <= 0) {
            throw new BusinessException(
                "La duración real debe ser mayor a cero"
            );
        }

        if (registrarTratamiento.getResultadoTratamiento() == null) {
            throw new BusinessException(
                "Debe indicar el resultado del tratamiento"
            );
        }

        if (registrarTratamiento.getObservacion() != null &&
                registrarTratamiento.getObservacion().length() > 3500) {
            throw new BusinessException(
                "La observación supera los 3500 caracteres"
            );
        }

        if (registrarTratamiento.getProductosUtilizados() != null &&
                registrarTratamiento.getProductosUtilizados().length() > 2500) {
            throw new BusinessException(
                "Los productos utilizados superan los 2500 caracteres"
            );
        }

        //====================================
        //Validar lista de servicios
        //====================================

        List<RegistrarTratamientoServicioDTO> serviciosDTO =
                registrarTratamiento.getServiciosRealizados();

        if (serviciosDTO == null || serviciosDTO.isEmpty()) {
            throw new BusinessException(
                "Debe registrar al menos un servicio realizado"
            );
        }

        //====================================
        //Obtener empleada autenticada
        //====================================

        EmpleadoEntity empleado = empleadoRepository
                .findByUsuario_Id(idUsuario)
                .orElseThrow(() -> new BusinessException(
                    "El usuario no pertenece a una empleada"
                ));

        //====================================
        //Obtener turno
        //====================================

        TurnoEntity turno = turnoRepository
                .findById(registrarTratamiento.getIdTurno())
                .orElseThrow(() -> new BusinessException(
                    "Turno no encontrado"
                ));

        //====================================
        //Validar empleada asignada
        //====================================

        if (turno.getEmpleado() == null ||
                !turno.getEmpleado().getId().equals(empleado.getId())) {
            throw new BusinessException(
                "No puede registrar tratamientos de turnos asignados a otra empleada"
            );
        }

        //====================================
        //Validar estado del turno
        //====================================

        if (turno.getEstadoTurno() != EstadoTurnoEnums.EN_CURSO) {
            throw new BusinessException(
                "Solo se pueden registrar tratamientos de turnos en curso"
            );
        }

        LocalDateTime ahora = LocalDateTime.now();

        if (turno.getFechaHoraInicio() == null ||
                ahora.isBefore(turno.getFechaHoraInicio())) {
            throw new BusinessException(
                "El turno todavía no comenzó"
            );
        }

        //====================================
        //Evitar tratamientos duplicados
        //====================================

        if (historialTratamientoRepository.existsByTurnoId(turno.getId())) {
            throw new BusinessException(
                "Este turno ya tiene un tratamiento registrado"
            );
        }

        //====================================
        //Crear historial
        //====================================

        HistorialTratamientoEntity historial =
                new HistorialTratamientoEntity();

        historial.setTurno(turno);
        historial.setRegistradoPor(empleado);
        historial.setFecha(ahora);

        historial.setObservacion(
            registrarTratamiento.getObservacion()
        );

        historial.setDuracionReal(
            registrarTratamiento.getDuracionReal()
        );

        historial.setProductosUtilizados(
            registrarTratamiento.getProductosUtilizados()
        );

        historial.setResultadoTratamiento(
            registrarTratamiento.getResultadoTratamiento()
        );

        //====================================
        //Registrar servicios realizados
        //====================================

        List<HistorialTratamientoServicioEntity> servicios =
                new ArrayList<>();

        Set<Long> reservasProcesadas = new HashSet<>();

        for (RegistrarTratamientoServicioDTO datos : serviciosDTO) {

            if (datos == null ||
                    datos.getIdServicioRealizado() == null) {
                throw new BusinessException(
                    "Debe indicar cada servicio realizado"
                );
            }

            //================================
            //Obtener servicio realizado
            //================================

            ServicioEntity servicioRealizado =
                    servicioRepository
                        .findById(datos.getIdServicioRealizado())
                        .orElseThrow(() -> new BusinessException(
                            "Servicio realizado no encontrado"
                        ));

            //================================
            //Buscar servicio reservado
            //================================

            TurnoServicioEntity reservado = null;

            Long idReservado =
                    datos.getIdTurnoServicioReservado();

            if (idReservado != null) {

                reservado = turno.getServicios()
                        .stream()
                        .filter(ts -> ts.getId().equals(idReservado))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException(
                            "El servicio reservado no pertenece a este turno"
                        ));

                if (!reservasProcesadas.add(idReservado)) {
                    throw new BusinessException(
                        "No se puede registrar dos veces el mismo servicio reservado"
                    );
                }
            }

            //================================
            //Comprobar si hubo cambio
            //================================

            boolean huboCambio =
                    reservado == null ||
                    !reservado.getServicio().getId()
                            .equals(servicioRealizado.getId());

            String motivo = datos.getMotivoCambio() == null
                    ? ""
                    : datos.getMotivoCambio().trim();

            if (huboCambio && motivo.isBlank()) {
                throw new BusinessException(
                    "Debe indicar el motivo del cambio o incorporación del servicio"
                );
            }

            if (motivo.length() > 500) {
                throw new BusinessException(
                    "El motivo del cambio supera los 500 caracteres"
                );
            }

            //================================
            //Crear detalle del servicio
            //================================

            HistorialTratamientoServicioEntity detalle =
                    new HistorialTratamientoServicioEntity();

            detalle.setHistorial(historial);
            detalle.setServicioRealizado(servicioRealizado);
            detalle.setServicioReservado(reservado);

            detalle.setMotivoCambio(
                huboCambio ? motivo : null
            );

            detalle.setFechaCambio(
                huboCambio ? ahora : null
            );

            servicios.add(detalle);
        }

        //====================================
        //Asociar servicios al historial
        //====================================

        historial.setServiciosRealizados(servicios);

        //====================================
        //Guardar historial y servicios
        //====================================

        HistorialTratamientoEntity guardado =
                historialTratamientoRepository.save(historial);

        //====================================
        //Finalizar turno
        //====================================

        turno.setEstadoTurno(EstadoTurnoEnums.FINALIZADO);

        turnoRepository.save(turno);

        //====================================
        //Devolver tratamiento registrado
        //====================================

        return toMap(guardado);
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

    //=====================================
//Mis tratamientos como empleada
//=====================================
@Override
public List<ObtenerHistorialTratamientoDTO> listarMisTratamientosEmpleado(Long idUsuario) {

    if (idUsuario == null) {
        throw new BusinessException(
            "El usuario es obligatorio"
        );
    }

    //====================================
    //Obtener empleada autenticada
    //====================================

    EmpleadoEntity empleado = empleadoRepository
            .findByUsuario_Id(idUsuario)
            .orElseThrow(() -> new BusinessException(
                "El usuario no pertenece a una empleada"
            ));

    //====================================
    //Consultar sus tratamientos
    //====================================

    return listarPorEmpleado(empleado.getId());
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
    public List<ObtenerHistorialTratamientoDTO> verHistorialCliente(Long idUsuario, Long idCliente){

        if (idUsuario == null) {
            throw new BusinessException(
                "El usuario es obligatorio"
            );
        }

        if (idCliente == null || idCliente <= 0) {
            throw new BusinessException(
                "La clienta es obligatoria"
            );
        }

        //====================================
        //OBTENER EMPLEADA AUTENTICADA
        //====================================

        EmpleadoEntity empleado = empleadoRepository
                .findByUsuario_Id(idUsuario)
                .orElseThrow(() -> new AccessDeniedException(
                    "El usuario no tiene autorización para consultar historiales de clientas"
                ));

        //====================================
        //ESTADOS QUE PERMITEN LA CONSULTA
        //====================================

        List<EstadoTurnoEnums> estadosPermitidos = List.of(
            EstadoTurnoEnums.CONFIRMADO,
            EstadoTurnoEnums.EN_CURSO
        );

        //====================================
        //VERIFICAR RELACIÓN CON LA CLIENTA
        //====================================

        boolean tieneAcceso =
                turnoRepository.existeTurnoVigenteEntreEmpleadoYCliente(
                    empleado.getId(),
                    idCliente,
                    estadosPermitidos,
                    LocalDateTime.now()
                );

        //====================================
        //DENEGAR ACCESO NO AUTORIZADO
        //====================================

        if (!tieneAcceso) {
            throw new AccessDeniedException(
                "No tenés una atención vigente asignada con esta clienta"
            );
        }

        //====================================
        //OBTENER CLIENTA
        //====================================

        ClienteEntity cliente = clienteRepository
                .findById(idCliente)
                .orElseThrow(() -> new BusinessException(
                    "Clienta no encontrada"
                ));

        //====================================
        //CONSULTAR HISTORIAL DE TRATAMIENTOS
        //====================================

        return historialTratamientoRepository
                .findByTurnoClienteIdOrderByFechaDesc(
                    cliente.getId()
                )
                .stream()
                .map(this::toMap)
                .toList();
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
        obtenerHistorialTratamientoDTO.setResultadoTratamiento(historial.getResultadoTratamiento());

        if(historial.getTurno() != null){

            TurnoEntity turno = historial.getTurno();

            obtenerHistorialTratamientoDTO.setIdTurno(turno.getId());
            obtenerHistorialTratamientoDTO.setFechaHoraTurno(turno.getFechaHoraInicio());
            obtenerHistorialTratamientoDTO.setEstadoTurno(turno.getEstadoTurno());
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

            obtenerHistorialTratamientoDTO.setCategorias(turno.getServicios().stream().map(turnoServicio -> turnoServicio.getServicio().getCategoria().getNombre()).distinct().toList());

            List<ObtenerHistorialTratamientoServicioDTO> serviciosRealizados = new ArrayList<>();

            if (historial.getServiciosRealizados() != null) {

        for (HistorialTratamientoServicioEntity detalle :
                historial.getServiciosRealizados()) {

            ObtenerHistorialTratamientoServicioDTO servicioDTO =
                    new ObtenerHistorialTratamientoServicioDTO();

            //================================
            //IDENTIFICADOR DEL REGISTRO
            //================================

            servicioDTO.setIdHistorialServicio(
                detalle.getId()
            );

            //================================
            //SERVICIO REALIZADO
            //================================

            if (detalle.getServicioRealizado() != null) {

                ServicioEntity realizado =
                        detalle.getServicioRealizado();

                servicioDTO.setIdServicioRealizado(
                    realizado.getId()
                );

                servicioDTO.setNombreServicioRealizado(
                    realizado.getNombre()
                );
            }

            //================================
            //SERVICIO ORIGINALMENTE RESERVADO
            //================================

            if (detalle.getServicioReservado() != null) {

                TurnoServicioEntity reservado =
                        detalle.getServicioReservado();

                servicioDTO.setIdTurnoServicioReservado(
                    reservado.getId()
                );

                if (reservado.getServicio() != null) {

                    ServicioEntity servicioOriginal =
                            reservado.getServicio();

                    servicioDTO.setIdServicioReservado(
                        servicioOriginal.getId()
                    );

                    servicioDTO.setNombreServicioReservado(
                        servicioOriginal.getNombre()
                    );
                }
            }

            //================================
            //MOTIVO Y FECHA DEL CAMBIO
            //================================

            servicioDTO.setMotivoCambio(
                detalle.getMotivoCambio()
            );

            servicioDTO.setFechaCambio(
                detalle.getFechaCambio()
            );

            //================================
            //AGREGAR AL RESULTADO
            //================================

            serviciosRealizados.add(servicioDTO);
        }
    }

    //========================================
    //ASOCIAR SERVICIOS AL HISTORIAL
    //========================================

    obtenerHistorialTratamientoDTO.setServiciosRealizados(serviciosRealizados);
        }

        return obtenerHistorialTratamientoDTO;
    }
}
