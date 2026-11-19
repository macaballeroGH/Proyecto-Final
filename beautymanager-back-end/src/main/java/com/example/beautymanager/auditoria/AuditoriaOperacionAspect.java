package com.example.beautymanager.auditoria;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.example.beautymanager.Servicio.servicioImpl.AuditoriaOperacionAutomaticaService;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class AuditoriaOperacionAspect {

    private static final Logger logger =
        LoggerFactory.getLogger(AuditoriaOperacionAspect.class);

    private final AuditoriaOperacionAutomaticaService registroService;

    // ======================================================
    // OPERACIONES AUDITADAS POR MODULO
    // ======================================================

    private static final Map<String, Set<String>> OPERACIONES =
        Map.ofEntries(

            Map.entry("AuthServiceImpl", Set.of(
                "login", "logout"
            )),

            Map.entry("RegistroUsuarioServiceImpl", Set.of(
                "registrarUsuario"
            )),

            Map.entry("RecuperacionPasswordServiceImpl", Set.of(
                "solicitarRecuperacionPassword",
                "restablecerPassword"
            )),

            Map.entry("UsuarioServiceImpl", Set.of(
                "actualizarUsuario",
                "cambiarPassword",
                "actualizarFotoPerfil",
                "eliminarFotoPerfil"
            )),

            Map.entry("ClienteInicializacionServiceImpl", Set.of(
                "inicializarCliente"
            )),

            Map.entry("ClienteServiceImpl", Set.of(
                "actualizarCliente",
                "eliminarCliente"
            )),

            Map.entry("EmpleadoServiceImpl", Set.of(
                "crearEmpleado",
                "actualizarEmpleado",
                "eliminarEmpleado",
                "cambiarEstado",
                "darDeBaja",
                "reactivarEmpleado"
            )),

            Map.entry("HorarioEmpleadoServiceImpl", Set.of(
                "crearHorario",
                "actualizarHorario",
                "eliminar"
            )),

            Map.entry("BloqueoAgendaServiceImpl", Set.of(
                "crearBloqueo",
                "actualizarBloqueo",
                "eliminar"
            )),

            Map.entry("ServicioServiceImpl", Set.of(
                "crearServicio",
                "actualizarServicio",
                "eliminarServicio"
            )),

            Map.entry("CategoriaServicioServiceImpl", Set.of(
                "crearCategoria",
                "actualizarCategoria",
                "eliminarCategoria"
            )),

            Map.entry("ProductoServiceImpl", Set.of(
                "crearProducto",
                "actualizarProducto",
                "eliminarProducto",
                "descontarStock",
                "aumentarStock",
                "actualizarFotoProducto",
                "eliminarFotoProducto"
            )),

            Map.entry("CarritoServiceImpl", Set.of(
                "agregarProducto",
                "eliminarProducto",
                "actualizarCantidad"
            )),

            Map.entry("CompraServiceImpl", Set.of(
                "realizarCompra"
            )),

            Map.entry("PagoServiceImpl", Set.of(
                "crearPago",
                "pagar",
                "actualizarPago",
                "crearPagoCompra"
            )),

            Map.entry("BilleteraServiceImpl", Set.of(
                "crearBilletera",
                "recargarSaldo",
                "debitarSaldo",
                "transferirSaldo"
            )),

            Map.entry("MovimientoFinanzasServiceImpl", Set.of(
                "crearMovimiento",
                "crearGasto",
                "actualizarMovimiento",
                "eliminarMovimiento"
            )),

            Map.entry("TipoGastoServiceImpl", Set.of(
                "crearTipoGasto",
                "actualizarTipoGasto",
                "eliminarTipoGasto"
            )),

            Map.entry("ConfiguracionRecordatorioServiceImpl", Set.of(
                "crearConfiguracion",
                "actualizarConfiguracion"
            )),

            Map.entry("NotificacionServiceImpl", Set.of(
                "crearNotificacion",
                "actualizarNotificacion",
                "eliminar"
            )),

            Map.entry("HistorialTratamientoServiceImpl", Set.of(
                "registrarTratamiento",
                "eliminar"
            )),

            Map.entry("TurnoServiceImpl", Set.of(
                "crearTurno",
                "crearTurnoAdmin",
                "cancelarTurno",
                "aceptarTurno",
                "rechazarTurno",
                "marcarAusente",
                "iniciarTurno",
                "finalizarTurno",
                "confirmarTurnoAdmin",
                "rechazarTurnoAdmin",
                "cancelarTurnoAdmin",
                "cambiarEmpleadoAdmin",
                "reprogramarTurno",
                "aceptarReprogramacion",
                "rechazarReprogramacion"
            )),

            Map.entry("ReservaServiceImpl", Set.of(
                "crearReserva"
            )),

            Map.entry("ReservaPagoServiceImpl", Set.of(
                "confirmarYPagar"
            )),

            Map.entry("RecordatorioServiceImpl", Set.of(
                "crearRecordatorios",
                "reprogramarRecordatoriosFuturos",
                "reprogramarRecordatoriosDeTurno"
            )),

            Map.entry("EmailServiceImpl", Set.of(
                "enviarEmail",
                "enviarEmailPrueba"
            )),

            Map.entry("ArchivoServiceImpl", Set.of(
                "guardarArchivo",
                "eliminarArchivo"
            ))

        );

    // ======================================================
    // PROPIEDADES SEGURAS DE LOS DTO DE RESPUESTA
    // ======================================================

    private static final Set<String> PROPIEDADES_SEGURAS =
        Set.of(
            "idUsuario",
            "idCliente",
            "idEmpleado",
            "idServicio",
            "idCategoria",
            "idProducto",
            "idTurno",
            "idCompra",
            "idPago",
            "idBilletera",
            "idMovimiento",
            "idBloqueo",
            "idHorario",
            "idHistorial",
            "idReserva",
            "idNotificacion",
            "idConfiguracion",
            "idTipoGasto",
            "stock",
            "saldo",
            "monto",
            "estado",
            "estadoTurno",
            "estadoCompra",
            "estadoPago",
            "estadoEmpleado",
            "estadoProducto"
        );

    public AuditoriaOperacionAspect(
        AuditoriaOperacionAutomaticaService registroService
    ) {
        this.registroService = registroService;
    }

    // ======================================================
    // INTERCEPTAR OPERACIONES
    // ======================================================

    @Around(
        "execution(public * " +
        "com.example.beautymanager.Servicio.servicioImpl." +
        "*ServiceImpl.*(..))"
    )
    public Object auditarOperacion(
        ProceedingJoinPoint punto
    ) throws Throwable {

        String clase =
            punto.getTarget().getClass().getSimpleName();

        String metodo =
            ((MethodSignature) punto.getSignature())
                .getMethod().getName();

        Set<String> metodosAuditables =
            OPERACIONES.get(clase);

        if (metodosAuditables == null
                || !metodosAuditables.contains(metodo)) {

            return punto.proceed();
        }

        Long idAutenticado = obtenerUsuarioAutenticado();

        String modulo = normalizarNombre(
            clase.replace("ServiceImpl", "")
        );

        String accion = normalizarNombre(metodo);

        String tipoEvento =
            determinarTipoEvento(clase, metodo);

        Long idArgumento =
            obtenerPrimerId(punto.getArgs());

        long inicio = System.nanoTime();

        try {

            Object respuesta = punto.proceed();

            Map<String, Object> metadatos =
                obtenerMetadatos(respuesta);

            metadatos.put("servicio", clase);
            metadatos.put("metodo", metodo);

            metadatos.put(
                "duracionMs",
                (System.nanoTime() - inicio) / 1_000_000
            );

            Long idRespuesta =
                obtenerIdRespuesta(metadatos, respuesta);

            Long idRegistro = idRespuesta != null
                ? idRespuesta
                : idArgumento;

            Long responsable = idAutenticado;

            // Login exitoso: todavía no hay JWT en el
            // contexto, pero el resultado identifica la cuenta.
            if ("AuthServiceImpl".equals(clase)
                    && "login".equals(metodo)
                    && idRespuesta != null) {

                responsable = idRespuesta;
            }

            final Long responsableFinal = responsable;
            final Long idRegistroFinal = idRegistro;

            String origen = determinarOrigen(
                clase,
                responsableFinal
            );

            String detalle =
                "Operación completada: " + clase + "." + metodo;

            // Si hay una transacción activa, esperar al commit
            // para no registrar un éxito que fue revertido.
            if (
                TransactionSynchronizationManager
                    .isActualTransactionActive()
                &&
                TransactionSynchronizationManager
                    .isSynchronizationActive()
            ) {

                TransactionSynchronizationManager
                    .registerSynchronization(
                        new TransactionSynchronization() {

                            @Override
                            public void afterCommit() {

                                guardarDeFormaSegura(
                                    accion,
                                    modulo,
                                    idRegistroFinal,
                                    detalle,
                                    tipoEvento,
                                    origen,
                                    "EXITO",
                                    responsableFinal,
                                    metadatos
                                );
                            }
                        }
                    );

            } else {

                guardarDeFormaSegura(
                    accion,
                    modulo,
                    idRegistroFinal,
                    detalle,
                    tipoEvento,
                    origen,
                    "EXITO",
                    responsableFinal,
                    metadatos
                );
            }

            return respuesta;

        } catch (Throwable error) {

            Map<String, Object> metadatos =
                new LinkedHashMap<>();

            metadatos.put("servicio", clase);
            metadatos.put("metodo", metodo);

            metadatos.put(
                "tipoError",
                error.getClass().getSimpleName()
            );

            metadatos.put(
                "duracionMs",
                (System.nanoTime() - inicio) / 1_000_000
            );

            guardarDeFormaSegura(
                accion,
                modulo,
                idArgumento,
                "La operación terminó con un error",
                tipoEvento,
                determinarOrigen(clase, idAutenticado),
                "ERROR",
                idAutenticado,
                metadatos
            );

            // Conservar la excepción original.
            throw error;
        }
    }

    // ======================================================
    // GUARDAR SIN MODIFICAR EL RESULTADO DE LA OPERACION
    // ======================================================

    private void guardarDeFormaSegura(
        String accion,
        String modulo,
        Long idRegistro,
        String detalle,
        String tipoEvento,
        String origen,
        String resultado,
        Long idUsuario,
        Map<String, Object> metadatos
    ) {

        try {

            registroService.registrar(
                accion,
                modulo,
                idRegistro,
                detalle,
                tipoEvento,
                origen,
                resultado,
                idUsuario,
                metadatos
            );

        } catch (Exception error) {

            logger.error(
                "No se pudo registrar la auditoría de {}.{}",
                modulo,
                accion,
                error
            );
        }
    }

    // ======================================================
    // OBTENER ID DE LA SESION JWT
    // ======================================================

    private Long obtenerUsuarioAutenticado() {

        Authentication autenticacion =
            SecurityContextHolder.getContext()
                .getAuthentication();

        if (autenticacion == null
                || !autenticacion.isAuthenticated()) {
            return null;
        }

        Object principal = autenticacion.getPrincipal();

        return principal instanceof Long id
            ? id
            : null;
    }

    // ======================================================
    // METADATOS CONTROLADOS DE RESPUESTA
    // ======================================================

    private Map<String, Object> obtenerMetadatos(
        Object respuesta
    ) {

        Map<String, Object> datos =
            new LinkedHashMap<>();

        if (respuesta == null) {
            return datos;
        }

        if (respuesta instanceof Long id) {
            datos.put("idResultado", id);
            return datos;
        }

        // No inspeccionar entidades JPA, listas ni objetos
        // arbitrarios: pueden contener datos sensibles.
        if (!respuesta.getClass()
                .getSimpleName().endsWith("DTO")) {

            return datos;
        }

        BeanWrapper propiedades =
            new BeanWrapperImpl(respuesta);

        for (String propiedad : PROPIEDADES_SEGURAS) {

            if (!propiedades.isReadableProperty(propiedad)) {
                continue;
            }

            Object valor =
                propiedades.getPropertyValue(propiedad);

            if (valor == null) {
                continue;
            }

            if (valor instanceof Number
                    || valor instanceof Boolean) {

                datos.put(propiedad, valor);

            } else if (valor instanceof Enum<?> enumerado) {

                datos.put(
                    propiedad,
                    enumerado.name()
                );
            }
        }

        return datos;
    }

    // ======================================================
    // ID PRINCIPAL DEL REGISTRO RESULTANTE
    // ======================================================

    private Long obtenerIdRespuesta(
        Map<String, Object> metadatos,
        Object respuesta
    ) {

        if (respuesta instanceof Long id) {
            return id;
        }

        String[] propiedadesId = {
            "idCompra",
            "idPago",
            "idTurno",
            "idReserva",
            "idHistorial",
            "idServicio",
            "idCategoria",
            "idProducto",
            "idEmpleado",
            "idCliente",
            "idUsuario",
            "idBilletera",
            "idMovimiento",
            "idBloqueo",
            "idHorario",
            "idNotificacion",
            "idConfiguracion",
            "idTipoGasto"
        };

        for (String propiedad : propiedadesId) {

            Object valor = metadatos.get(propiedad);

            if (valor instanceof Number numero) {
                return numero.longValue();
            }
        }

        return null;
    }

    // ======================================================
    // PRIMER IDENTIFICADOR NUMERICO DE ENTRADA
    // ======================================================

    private Long obtenerPrimerId(Object[] argumentos) {

        for (Object argumento : argumentos) {

            if (argumento instanceof Long id) {
                return id;
            }
        }

        return null;
    }

    // ======================================================
    // CLASIFICAR TIPO DE EVENTO
    // ======================================================

    private String determinarTipoEvento(
        String clase,
        String metodo
    ) {

        if (clase.equals("AuthServiceImpl")
                || clase.equals("RecuperacionPasswordServiceImpl")
                || metodo.toLowerCase(Locale.ROOT)
                    .contains("password")) {

            return "SEGURIDAD";
        }

        if (clase.equals("RecordatorioServiceImpl")
                || clase.equals("EmailServiceImpl")) {

            return "SISTEMA";
        }

        String nombre =
            metodo.toLowerCase(Locale.ROOT);

        if (nombre.startsWith("eliminar")
                || nombre.startsWith("dardebaja")) {

            return "ELIMINACION";
        }

        if (nombre.startsWith("crear")
                || nombre.startsWith("registrar")
                || nombre.startsWith("inicializar")) {

            return "CREACION";
        }

        if (nombre.startsWith("cancelar")
                || nombre.startsWith("aceptar")
                || nombre.startsWith("rechazar")
                || nombre.startsWith("confirmar")
                || nombre.startsWith("iniciar")
                || nombre.startsWith("finalizar")
                || nombre.startsWith("reactivar")
                || nombre.startsWith("marcar")
                || nombre.startsWith("reprogramar")) {

            return "CAMBIO_ESTADO";
        }

        if (nombre.startsWith("actualizar")
                || nombre.startsWith("cambiar")
                || nombre.startsWith("debitar")
                || nombre.startsWith("recargar")
                || nombre.startsWith("transferir")
                || nombre.startsWith("descontar")
                || nombre.startsWith("aumentar")) {

            return "MODIFICACION";
        }

        return "OPERACION";
    }

    // ======================================================
    // DISTINGUIR OPERACIONES SIN JWT
    // ======================================================

    private String determinarOrigen(
        String clase,
        Long idUsuario
    ) {

        if (idUsuario != null) {
            return "USUARIO";
        }

        if (
            clase.equals("AuthServiceImpl")
            || clase.equals("RegistroUsuarioServiceImpl")
            || clase.equals("RecuperacionPasswordServiceImpl")
        ) {
            return "NO_AUTENTICADO";
        }

        return "SISTEMA";
    }

    // ======================================================
    // CAMELCASE A SNAKE_CASE
    // ======================================================

    private String normalizarNombre(String valor) {

        return valor
            .replaceAll(
                "([a-z0-9])([A-Z])",
                "$1_$2"
            )
            .toUpperCase(Locale.ROOT);
    }
}
