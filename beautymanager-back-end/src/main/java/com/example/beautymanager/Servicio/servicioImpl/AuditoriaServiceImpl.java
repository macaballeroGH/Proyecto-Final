package com.example.beautymanager.Servicio.servicioImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.beautymanager.Modelo.DTO.ObtenerAuditoriaDTO;
import com.example.beautymanager.Modelo.Entidad.AuditoriaEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Repositorio.AuditoriaRepository;
import com.example.beautymanager.Servicio.AuditoriaService;
import com.example.beautymanager.exception.BusinessException;

import jakarta.persistence.criteria.Predicate;
import tools.jackson.databind.json.JsonMapper;
@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final JsonMapper jsonMapper;

    private static final Set<String> CAMPOS_SENSIBLES = Set.of(
        "password",
        "contrasena",
        "contraseña",
        "clave",
        "token",
        "accesstoken",
        "refreshtoken",
        "jwt",
        "secret",
        "secreto",
        "authorization",
        "cookie",
        "cvv",
        "numerotarjeta",
        "tarjeta",
        "credenciales"
    );

    public AuditoriaServiceImpl(
        AuditoriaRepository auditoriaRepository,
        JsonMapper jsonMapper
    ) {
        this.auditoriaRepository = auditoriaRepository;
        this.jsonMapper = jsonMapper;
    }

    // ======================================================
    // VALIDACIONES
    // ======================================================

    private String validarTexto(
        String texto,
        String nombre,
        int maximo
    ) {

        if (texto == null || texto.isBlank()) {
            throw new BusinessException(
                nombre + " es obligatorio"
            );
        }

        String limpio = texto.trim();

        if (limpio.length() > maximo) {
            throw new BusinessException(
                nombre + " supera el máximo de "
                    + maximo + " caracteres"
            );
        }

        return limpio;
    }

    private void validarUsuario(UsuarioEntity usuario) {

        if (usuario == null || usuario.getId() == null) {
            throw new BusinessException(
                "El usuario responsable es obligatorio"
            );
        }
    }

    // ======================================================
    // INFERIR TIPO DE EVENTO EXISTENTE
    // ======================================================

    private String inferirTipoEvento(String accion) {

        String valor = accion.toUpperCase(Locale.ROOT);

        if (valor.contains("ELIMINAR")
                || valor.contains("BAJA")) {
            return "ELIMINACION";
        }

        if (valor.contains("ESTADO")
                || valor.contains("ACTIVAR")
                || valor.contains("DESACTIVAR")
                || valor.contains("SUSPENDER")) {
            return "CAMBIO_ESTADO";
        }

        if (valor.contains("MODIFICAR")
                || valor.contains("ACTUALIZAR")
                || valor.contains("EDITAR")
                || valor.contains("REPROGRAMAR")) {
            return "MODIFICACION";
        }

        if (valor.contains("CREAR")
                || valor.contains("REGISTRAR")
                || valor.contains("ALTA")) {
            return "CREACION";
        }

        return "OPERACION";
    }

    // ======================================================
    // OBTENER ROL DE LA SESIÓN ACTUAL
    // ======================================================

    private String obtenerRolAutenticado() {

        Authentication autenticacion =
            SecurityContextHolder.getContext()
                .getAuthentication();

        if (autenticacion == null
                || !autenticacion.isAuthenticated()) {
            return null;
        }

        String roles = autenticacion.getAuthorities()
            .stream()
            .map(autoridad -> autoridad.getAuthority())
            .collect(Collectors.joining(", "));

        return roles.isBlank() ? null : roles;
    }

    // ======================================================
    // GUARDAR DATOS HISTÓRICOS DEL RESPONSABLE
    // ======================================================

    private void establecerResponsable(
        AuditoriaEntity auditoria,
        UsuarioEntity usuario
    ) {

        if (usuario == null) {
            return;
        }

        auditoria.setUsuario(usuario);
        auditoria.setUsuarioNombre(usuario.getNombre());
        auditoria.setUsuarioApellido(usuario.getApellido());
        auditoria.setUsuarioEmail(usuario.getEmail());
        auditoria.setUsuarioRol(obtenerRolAutenticado());
    }

    // ======================================================
    // IDENTIFICAR CAMPOS SENSIBLES
    // ======================================================

    private boolean esCampoSensible(String campo) {

        String normalizado = campo
            .toLowerCase(Locale.ROOT)
            .replace("_", "")
            .replace("-", "")
            .replace(" ", "");

        for (String sensible : CAMPOS_SENSIBLES) {

            if (normalizado.contains(sensible)) {
                return true;
            }
        }

        return false;
    }

    // ======================================================
    // NORMALIZAR VALORES PARA AUDITORÍA
    // ======================================================

    private Map<String, Object> normalizarValores(
        Map<String, ?> valores
    ) {

        Map<String, Object> resultado =
            new LinkedHashMap<>();

        if (valores == null) {
            return resultado;
        }

        for (Map.Entry<String, ?> entrada
                : valores.entrySet()) {

            String campo = entrada.getKey();

            if (campo == null || campo.isBlank()) {
                continue;
            }

            if (esCampoSensible(campo)) {
                continue;
            }

            Object valor = entrada.getValue();

            if (valor == null
                    || valor instanceof String
                    || valor instanceof Number
                    || valor instanceof Boolean) {

                resultado.put(campo, valor);

            } else if (valor instanceof Enum<?> enumerado) {

                resultado.put(campo, enumerado.name());

            } else if (valor instanceof TemporalAccessor) {

                resultado.put(campo, valor.toString());

            } else {

                throw new BusinessException(
                    "El campo " + campo
                        + " contiene un valor no admitido "
                        + "para la auditoría. Usá datos simples."
                );
            }
        }

        return resultado;
    }

    private String convertirAJson(
        Map<String, Object> valores
    ) {

        if (valores == null || valores.isEmpty()) {
            return null;
        }

        return jsonMapper.writeValueAsString(valores);
    }

    // ======================================================
    // CONSTRUIR Y PERSISTIR UN EVENTO
    // ======================================================

    private ObtenerAuditoriaDTO guardarEvento(
        String accion,
        String tablaAfectada,
        Long idRegistro,
        UsuarioEntity usuario,
        String detalle,
        String tipoEvento,
        String origen,
        String resultado,
        String identificadorExterno,
        String valoresAnteriores,
        String valoresNuevos
    ) {

        AuditoriaEntity auditoria = new AuditoriaEntity();

        auditoria.setAccion(
            validarTexto(accion, "La acción", 150)
        );

        auditoria.setTablaAfectada(
            validarTexto(
                tablaAfectada,
                "La tabla afectada",
                100
            )
        );

        auditoria.setIdRegistro(idRegistro);

        auditoria.setDetalle(detalle);

        auditoria.setTipoEvento(tipoEvento);
        auditoria.setOrigen(origen);
        auditoria.setResultado(resultado);

        auditoria.setIdentificadorExterno(
            identificadorExterno
        );

        auditoria.setValoresAnteriores(
            valoresAnteriores
        );

        auditoria.setValoresNuevos(
            valoresNuevos
        );

        establecerResponsable(auditoria, usuario);

        AuditoriaEntity guardada =
            auditoriaRepository.saveAndFlush(auditoria);

        return toMap(guardada);
    }

    // ======================================================
    // MÉTODO ORIGINAL - CONSERVA COMPATIBILIDAD
    // ======================================================

    @Override
    @Transactional
    public ObtenerAuditoriaDTO guardarAuditoria(
        String accion,
        String tablaAfectada,
        Long idRegistro,
        UsuarioEntity usuario,
        String detalle
    ) {

        validarUsuario(usuario);

        String accionValidada =
            validarTexto(accion, "La acción", 150);

        return guardarEvento(
            accionValidada,
            tablaAfectada,
            idRegistro,
            usuario,
            detalle,
            inferirTipoEvento(accionValidada),
            "USUARIO",
            "EXITO",
            null,
            null,
            null
        );
    }

    // ======================================================
    // REGISTRAR CAMBIOS ANTES / DESPUÉS
    // ======================================================

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public ObtenerAuditoriaDTO registrarCambio(
        String accion,
        String tablaAfectada,
        Long idRegistro,
        UsuarioEntity usuario,
        String detalle,
        Map<String, ?> valoresAnteriores,
        Map<String, ?> valoresNuevos
    ) {

        validarUsuario(usuario);

        Map<String, Object> anteriores =
            normalizarValores(valoresAnteriores);

        Map<String, Object> nuevos =
            normalizarValores(valoresNuevos);

        // No crear un evento de modificación
        // cuando los datos son idénticos.
        if (Objects.equals(anteriores, nuevos)) {
            return null;
        }

        return guardarEvento(
            accion,
            tablaAfectada,
            idRegistro,
            usuario,
            detalle,
            inferirTipoEvento(accion),
            "USUARIO",
            "EXITO",
            null,
            convertirAJson(anteriores),
            convertirAJson(nuevos)
        );
    }

    // ======================================================
    // EVENTOS AUTOMÁTICOS DEL SISTEMA
    // ======================================================

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ObtenerAuditoriaDTO registrarEventoSistema(
        String accion,
        String tablaAfectada,
        Long idRegistro,
        String detalle
    ) {

        return guardarEvento(
            accion,
            tablaAfectada,
            idRegistro,
            null,
            detalle,
            "SISTEMA",
            "SISTEMA",
            "EXITO",
            null,
            null,
            null
        );
    }

    // ======================================================
    // EVENTOS DE SEGURIDAD SIN AUTENTICACIÓN
    // ======================================================

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ObtenerAuditoriaDTO registrarEventoSeguridad(
        String accion,
        String identificadorExterno,
        String detalle
    ) {

        String identificador = identificadorExterno;

        if (identificador != null
                && identificador.length() > 255) {

            identificador = identificador.substring(0, 255);
        }

        return guardarEvento(
            accion,
            "seguridad",
            null,
            null,
            detalle,
            "SEGURIDAD",
            "NO_AUTENTICADO",
            "ERROR",
            identificador,
            null,
            null
        );
    }

    // ======================================================
    // OBTENER POR ID
    // ======================================================

    @Override
    @Transactional(readOnly = true)
    public ObtenerAuditoriaDTO obtenerPorId(
        Long idAuditoria
    ) {

        if (idAuditoria == null || idAuditoria <= 0) {
            throw new BusinessException(
                "ID de auditoría inválido"
            );
        }

        AuditoriaEntity auditoria =
            auditoriaRepository.findById(idAuditoria)
                .orElseThrow(() -> new BusinessException(
                    "Auditoría no encontrada"
                ));

        return toMap(auditoria);
    }

    // ======================================================
    // LISTADO ORIGINAL
    // ======================================================

    @Override
    @Transactional(readOnly = true)
    public List<ObtenerAuditoriaDTO> obtenerTodas() {

        return auditoriaRepository
            .findAllByOrderByFechaHoraDescIdDesc()
            .stream()
            .map(this::toMap)
            .toList();
    }

    // ======================================================
    // OBTENER POR USUARIO
    // ======================================================

    @Override
    @Transactional(readOnly = true)
    public List<ObtenerAuditoriaDTO> obtenerPorUsuario(
        Long idUsuario
    ) {

        if (idUsuario == null || idUsuario <= 0) {
            throw new BusinessException(
                "ID de usuario inválido"
            );
        }

        return auditoriaRepository
            .findByUsuarioIdOrderByFechaHoraDescIdDesc(
                idUsuario
            )
            .stream()
            .map(this::toMap)
            .toList();
    }

    // ======================================================
    // CAMBIOS DE TRATAMIENTO
    // ======================================================

    @Override
    @Transactional(readOnly = true)
    public List<ObtenerAuditoriaDTO> obtenerCambiosTratamiento(
        Long idHistorial
    ) {

        if (idHistorial == null || idHistorial <= 0) {
            throw new BusinessException(
                "El identificador del tratamiento es inválido"
            );
        }

        return auditoriaRepository
            .findByTablaAfectadaAndIdRegistroAndAccionOrderByFechaHoraDescIdDesc(
                "historial_tratamiento",
                idHistorial,
                "MODIFICAR_TRATAMIENTO"
            )
            .stream()
            .map(this::toMap)
            .toList();
    }

    // ======================================================
    // FILTROS AVANZADOS Y PAGINACIÓN
    // ======================================================

    @Override
    @Transactional(readOnly = true)
    public Page<ObtenerAuditoriaDTO> buscarAuditorias(
        Long idUsuario,
        String accion,
        String tablaAfectada,
        String tipoEvento,
        String resultado,
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        int pagina,
        int tamanio
    ) {

        if (pagina < 0) {
            throw new BusinessException(
                "La página no puede ser negativa"
            );
        }

        if (tamanio < 1 || tamanio > 100) {
            throw new BusinessException(
                "El tamaño de página debe estar entre 1 y 100"
            );
        }

        if (idUsuario != null && idUsuario <= 0) {
            throw new BusinessException(
                "ID de usuario inválido"
            );
        }

        if (fechaDesde != null
                && fechaHasta != null
                && fechaDesde.isAfter(fechaHasta)) {

            throw new BusinessException(
                "La fecha desde no puede superar la fecha hasta"
            );
        }

        Specification<AuditoriaEntity> especificacion =
            (root, query, cb) -> {

                List<Predicate> condiciones =
                    new ArrayList<>();

                if (idUsuario != null) {

                    condiciones.add(
                        cb.equal(
                            root.get("usuario").get("id"),
                            idUsuario
                        )
                    );
                }

                if (accion != null && !accion.isBlank()) {

                    condiciones.add(
                        cb.like(
                            cb.lower(
                                root.<String>get("accion")
                            ),
                            "%" + accion.trim()
                                .toLowerCase(Locale.ROOT) + "%"
                        )
                    );
                }

                if (tablaAfectada != null
                        && !tablaAfectada.isBlank()) {

                    condiciones.add(
                        cb.like(
                            cb.lower(
                                root.<String>get("tablaAfectada")
                            ),
                            "%" + tablaAfectada.trim()
                                .toLowerCase(Locale.ROOT) + "%"
                        )
                    );
                }

                if (tipoEvento != null
                        && !tipoEvento.isBlank()) {

                    condiciones.add(
                        cb.equal(
                            cb.upper(
                                root.<String>get("tipoEvento")
                            ),
                            tipoEvento.trim()
                                .toUpperCase(Locale.ROOT)
                        )
                    );
                }

                if (resultado != null
                        && !resultado.isBlank()) {

                    condiciones.add(
                        cb.equal(
                            cb.upper(
                                root.<String>get("resultado")
                            ),
                            resultado.trim()
                                .toUpperCase(Locale.ROOT)
                        )
                    );
                }

                if (fechaDesde != null) {

                    condiciones.add(
                        cb.greaterThanOrEqualTo(
                            root.<LocalDateTime>get("fechaHora"),
                            fechaDesde.atStartOfDay()
                        )
                    );
                }

                if (fechaHasta != null) {

                    condiciones.add(
                        cb.lessThan(
                            root.<LocalDateTime>get("fechaHora"),
                            fechaHasta.plusDays(1).atStartOfDay()
                        )
                    );
                }

                return cb.and(
                    condiciones.toArray(Predicate[]::new)
                );
            };

        PageRequest paginacion = PageRequest.of(
            pagina,
            tamanio,
            Sort.by(
                Sort.Order.desc("fechaHora"),
                Sort.Order.desc("id")
            )
        );

        return auditoriaRepository
            .findAll(especificacion, paginacion)
            .map(this::toMap);
    }

    // ======================================================
    // CONVERTIR ENTIDAD A DTO
    // ======================================================

    @Override
    public ObtenerAuditoriaDTO toMap(
        AuditoriaEntity auditoria
    ) {

        ObtenerAuditoriaDTO dto =
            new ObtenerAuditoriaDTO();

        dto.setIdAuditoria(auditoria.getId());

        dto.setAccion(auditoria.getAccion());
        dto.setTablaAfectada(auditoria.getTablaAfectada());
        dto.setIdRegistro(auditoria.getIdRegistro());

        dto.setDetalle(auditoria.getDetalle());
        dto.setFechaHora(auditoria.getFechaHora());

        dto.setTipoEvento(auditoria.getTipoEvento());
        dto.setOrigen(auditoria.getOrigen());
        dto.setResultado(auditoria.getResultado());

        dto.setIdentificadorExterno(
            auditoria.getIdentificadorExterno()
        );

        dto.setValoresAnteriores(
            auditoria.getValoresAnteriores()
        );

        dto.setValoresNuevos(
            auditoria.getValoresNuevos()
        );

        // Primero utilizar datos históricos.
        dto.setNombreUsuario(auditoria.getUsuarioNombre());
        dto.setApellidoUsuario(auditoria.getUsuarioApellido());
        dto.setEmailUsuario(auditoria.getUsuarioEmail());
        dto.setRolUsuario(auditoria.getUsuarioRol());

        if (auditoria.getUsuario() != null) {

            UsuarioEntity usuario = auditoria.getUsuario();

            dto.setIdUsuario(usuario.getId());

            // Compatibilidad con registros antiguos:
            // antes no existían datos históricos.
            if (dto.getNombreUsuario() == null) {
                dto.setNombreUsuario(usuario.getNombre());
            }

            if (dto.getApellidoUsuario() == null) {
                dto.setApellidoUsuario(usuario.getApellido());
            }

            if (dto.getEmailUsuario() == null) {
                dto.setEmailUsuario(usuario.getEmail());
            }
        }

        return dto;
    }
}