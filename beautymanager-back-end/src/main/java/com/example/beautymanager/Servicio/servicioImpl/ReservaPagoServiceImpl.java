package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.example.beautymanager.Modelo.DTO.CrearReservaDTO;
import com.example.beautymanager.Modelo.DTO.CrearReservaTurnoDTO;
import com.example.beautymanager.Modelo.Entidad.ClienteEntity;
import com.example.beautymanager.Modelo.Entidad.MovimientoFinanzasEntity;
import com.example.beautymanager.Modelo.Entidad.PagoEntity;
import com.example.beautymanager.Modelo.Entidad.ReservaEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoPagoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoUsuarioEnums;
import com.example.beautymanager.Modelo.Enums.MetodoPagoEnums;
import com.example.beautymanager.Modelo.Enums.TipoMovimientoEnums;
import com.example.beautymanager.Repositorio.ClienteRepository;
import com.example.beautymanager.Repositorio.MovimientoFinanzasRepository;
import com.example.beautymanager.Repositorio.PagoRepository;
import com.example.beautymanager.Repositorio.ReservaRepository;
import com.example.beautymanager.Repositorio.TurnoRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.BilleteraService;
import com.example.beautymanager.Servicio.ReservaPagoService;
import com.example.beautymanager.Servicio.ReservaService;
import com.example.beautymanager.exception.BusinessException;

@Service 
public class ReservaPagoServiceImpl implements ReservaPagoService {

    private final UsuarioRepository usuarioRepository;

    private final ClienteRepository clienteRepository;

    private final TurnoRepository turnoRepository;

    private final PagoRepository pagoRepository;

    private final MovimientoFinanzasRepository movimientoFinanzasRepository;

    private final ReservaService reservaService;

    private final BilleteraService billeteraService;

    private final Long idUsuarioAdministrador;

    private final ReservaRepository reservaRepository;

    public ReservaPagoServiceImpl(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            TurnoRepository turnoRepository,
            PagoRepository pagoRepository,
            ReservaRepository reservaRepository,
            MovimientoFinanzasRepository movimientoFinanzasRepository,
            ReservaService reservaService,
            BilleteraService billeteraService,
            @Value("${beautymanager.cobros.admin-usuario-id}")
            Long idUsuarioAdministrador) {

        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.turnoRepository = turnoRepository;
        this.pagoRepository = pagoRepository;
        this.movimientoFinanzasRepository = movimientoFinanzasRepository;
        this.reservaService = reservaService;
        this.billeteraService = billeteraService;
        this.idUsuarioAdministrador = idUsuarioAdministrador;
        this.reservaRepository = reservaRepository;
    }

    //==========================
    //Confirmar y pagar reserva
    //==========================
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Long confirmarYPagar(
            Long idUsuario,
            CrearReservaDTO crearReserva) {

        //==========================
        //Validar datos
        //==========================
        if(idUsuario == null){
            throw new BusinessException("El usuario es obligatorio");
        }

        if(crearReserva == null ||
           crearReserva.getTurnos() == null ||
           crearReserva.getTurnos().isEmpty()){

            throw new BusinessException(
                "La reserva debe contener al menos un turno"
            );
        }


        //==========================
        //Validar clave de idempotencia
        //==========================

        String claveIdempotencia =
            crearReserva.getClaveIdempotencia();

        if(claveIdempotencia == null ||
           claveIdempotencia.isBlank()) {

            throw new BusinessException(
                "La clave de idempotencia es obligatoria"
            );
        }

        try {

            UUID uuid = UUID.fromString(claveIdempotencia);

            if(claveIdempotencia.length() != 36 ||
               !uuid.toString().equalsIgnoreCase(claveIdempotencia)) {

                throw new IllegalArgumentException();
            }

        } catch(IllegalArgumentException e) {

            throw new BusinessException(
                "La clave de idempotencia no tiene un formato valido"
            );
        }

        //Validar los datos necesarios para
        //comparar solicitudes repetidas.

        for(CrearReservaTurnoDTO turno : crearReserva.getTurnos()) {

            if(turno == null ||
               turno.getIdEmpleado() == null ||
               turno.getFechaHoraInicio() == null ||
               turno.getServiciosIds() == null ||
               turno.getServiciosIds().isEmpty() ||
               turno.getServiciosIds().contains(null)) {

                throw new BusinessException(
                    "Los datos de uno de los turnos son invalidos"
                );
            }
        }

        //==========================
        //Obtener usuario cliente
        //==========================
        UsuarioEntity usuarioCliente = usuarioRepository
            .findById(idUsuario)
            .orElseThrow(() ->
                new BusinessException("Usuario no encontrado")
            );

        if(usuarioCliente.getEstadoUsuario() != EstadoUsuarioEnums.ACTIVO){
            throw new BusinessException("El usuario no esta activo");
        }

        if(usuarioCliente.getRol() == null ||
           !"CLIENTE".equals(String.valueOf(usuarioCliente.getRol().getNombre()))){

            throw new BusinessException(
                "El usuario no tiene rol de cliente"
            );
        }

        //==========================
        //Obtener perfil cliente
        //==========================
        ClienteEntity cliente = clienteRepository
            .findByUsuario(usuarioCliente)
            .orElseThrow(() ->
                new BusinessException("Perfil de cliente no encontrado")
            );

        
        //==========================
        //Bloquear cliente durante
        //la operacion de pago
        //==========================

        cliente = clienteRepository
            .bloquearParaReserva(cliente.getId())
            .orElseThrow(() ->
                new BusinessException(
                    "Perfil de cliente no encontrado"
                )
            );

        //==========================
        //Consultar operacion anterior
        //==========================

        ReservaEntity reservaExistente = reservaRepository
            .findByClaveIdempotencia(claveIdempotencia)
            .orElse(null);

        if(reservaExistente != null) {

            if(!reservaExistente.getCliente().getId()
                    .equals(cliente.getId())) {

                throw new BusinessException(
                    "La clave ya pertenece a otra reserva"
                );
            }

            List<TurnoEntity> turnosExistentes =
                turnoRepository.findByReservaId(
                    reservaExistente.getId()
                );

            if(!mismaSolicitud(
                crearReserva.getTurnos(),
                turnosExistentes
            )) {

                throw new BusinessException(
                    "La clave ya fue utilizada con otros datos"
                );
            }

            //Comprobar que los pagos de la
            //operacion anterior estén completos.

            for(TurnoEntity turno : turnosExistentes) {

                boolean pagado = pagoRepository
                    .findByTurnoId(turno.getId())
                    .filter(pago ->
                        pago.getEstado() == EstadoPagoEnums.PAGADO &&
                        pago.getMetodoPago() == MetodoPagoEnums.BILLETERA
                    )
                    .isPresent();

                if(!pagado) {

                    throw new BusinessException(
                        "La reserva existente no tiene todos los pagos confirmados"
                    );
                }
            }

            //La operacion ya se realizo.
            //No transferir dinero nuevamente.

            return reservaExistente.getId();
        }


        //==========================
        //Obtener administrador
        //==========================
        UsuarioEntity administrador = usuarioRepository
            .findById(idUsuarioAdministrador)
            .orElseThrow(() ->
                new BusinessException(
                    "Usuario administrador de cobros no encontrado"
                )
            );

        if(administrador.getEstadoUsuario() != EstadoUsuarioEnums.ACTIVO){
            throw new BusinessException(
                "El usuario administrador no esta activo"
            );
        }

        if(administrador.getRol() == null ||
           !"ADMIN".equals(String.valueOf(administrador.getRol().getNombre()))){

            throw new BusinessException(
                "El usuario receptor no tiene rol de administrador"
            );
        }

        //==========================
        //Validar anticipacion
        //==========================
        LocalDateTime ahora = LocalDateTime.now();

        for(CrearReservaTurnoDTO turno : crearReserva.getTurnos()){

            if(turno == null || turno.getFechaHoraInicio() == null){
                throw new BusinessException(
                    "Todos los turnos deben tener fecha y hora"
                );
            }

            if(turno.getFechaHoraInicio().isBefore(ahora.plusHours(2))){
                throw new BusinessException(
                    "Los turnos deben reservarse con al menos dos horas de anticipacion"
                );
            }

            if(turno.getFechaHoraInicio().isAfter(ahora.plusDays(3))){
                throw new BusinessException(
                    "No se permiten reservas con mas de tres dias de anticipacion"
                );
            }
        }

        //==========================
        //Preparar reserva
        //==========================
        //El cliente se obtiene desde el
        //usuario autenticado, no desde Angular.

        CrearReservaDTO reservaSegura = new CrearReservaDTO();

        reservaSegura.setIdCliente(cliente.getId());
        reservaSegura.setTurnos(crearReserva.getTurnos());
        reservaSegura.setClaveIdempotencia(claveIdempotencia);

        //==========================
        //Crear reserva y turnos
        //==========================
        Long idReserva = reservaService.crearReserva(reservaSegura);

        //==========================
        //Obtener turnos creados
        //==========================
        List<TurnoEntity> turnos = turnoRepository
            .findByReservaId(idReserva);

        if(turnos.isEmpty() ||
           turnos.size() != crearReserva.getTurnos().size()){

            throw new BusinessException(
                "No se pudieron recuperar los turnos de la reserva"
            );
        }

        //==========================
        //Calcular precio total
        //==========================
        BigDecimal totalReserva = BigDecimal.ZERO;

        for(TurnoEntity turno : turnos){

            if(turno.getPrecioTotal() == null ||
               turno.getPrecioTotal().compareTo(BigDecimal.ZERO) <= 0){

                throw new BusinessException(
                    "Uno de los turnos tiene un precio invalido"
                );
            }

            totalReserva = totalReserva.add(turno.getPrecioTotal());
        }

        //==========================
        //Transferir saldo
        //==========================
        //Se descuenta una sola vez el
        //total de la reserva.

        billeteraService.transferirSaldo(
            usuarioCliente.getId(),
            administrador.getId(),
            totalReserva,
            "Reserva ID: " + idReserva
        );

        //==========================
        //Registrar pagos
        //==========================
        for(TurnoEntity turno : turnos){

            PagoEntity pago = new PagoEntity();

            pago.setTurno(turno);
            pago.setMonto(turno.getPrecioTotal());
            pago.setMetodoPago(MetodoPagoEnums.BILLETERA);
            pago.setEstado(EstadoPagoEnums.PAGADO);

            PagoEntity pagoGuardado = pagoRepository.save(pago);

            //==========================
            //Registrar ingreso financiero
            //==========================
            MovimientoFinanzasEntity movimiento =
                new MovimientoFinanzasEntity();

            movimiento.setFechaGasto(pagoGuardado.getFecha());
            movimiento.setMonto(pagoGuardado.getMonto());
            movimiento.setTipoMovimiento(TipoMovimientoEnums.INGRESO);
            movimiento.setEstado(EstadoMovimientoEnums.CONFIRMADO);
            movimiento.setMetodoPago(MetodoPagoEnums.BILLETERA);
            movimiento.setUsuario(usuarioCliente);

            movimiento.setDescripcion(
                "Pago de turno ID: " + turno.getId() +
                " - Reserva ID: " + idReserva
            );

            movimientoFinanzasRepository.save(movimiento);
        }

        //==========================
        //Reserva pagada
        //==========================
        return idReserva;
    }


    //==========================
    //Comparar solicitudes de reserva
    //==========================

    private boolean mismaSolicitud(
            List<CrearReservaTurnoDTO> solicitados,
            List<TurnoEntity> existentes) {

        if(solicitados.size() != existentes.size()) {
            return false;
        }

        List<String> firmasSolicitadas = solicitados.stream()
            .map(turno -> firmaTurno(
                turno.getIdEmpleado(),
                turno.getFechaHoraInicio(),
                turno.getServiciosIds()
            ))
            .sorted()
            .toList();

        List<String> firmasExistentes = existentes.stream()
            .map(turno -> firmaTurno(
                turno.getEmpleado().getId(),
                turno.getFechaHoraInicio(),
                turno.getServicios().stream()
                    .map(relacion -> relacion.getServicio().getId())
                    .toList()
            ))
            .sorted()
            .toList();

        return firmasSolicitadas.equals(firmasExistentes);
    }

    //==========================
    //Identificar datos de un turno
    //==========================

    private String firmaTurno(
            Long idEmpleado,
            LocalDateTime fechaHoraInicio,
            List<Long> serviciosIds) {

        List<Long> serviciosOrdenados =
            new ArrayList<>(serviciosIds);

        serviciosOrdenados.sort(Long::compareTo);

        return idEmpleado + "|" +
               fechaHoraInicio + "|" +
               serviciosOrdenados;
    }

}
