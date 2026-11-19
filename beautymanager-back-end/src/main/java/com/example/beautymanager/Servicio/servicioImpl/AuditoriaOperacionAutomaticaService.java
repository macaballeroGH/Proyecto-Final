package com.example.beautymanager.Servicio.servicioImpl;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.beautymanager.Modelo.Entidad.AuditoriaEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Repositorio.AuditoriaRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;

import tools.jackson.databind.json.JsonMapper;

@Service
public class AuditoriaOperacionAutomaticaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final JsonMapper jsonMapper;

    public AuditoriaOperacionAutomaticaService(
        AuditoriaRepository auditoriaRepository,
        UsuarioRepository usuarioRepository,
        JsonMapper jsonMapper
    ) {
        this.auditoriaRepository = auditoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.jsonMapper = jsonMapper;
    }

    // ======================================================
    // PERSISTIR UN EVENTO EN UNA TRANSACCION INDEPENDIENTE
    // ======================================================

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(
        String accion,
        String tablaAfectada,
        Long idRegistro,
        String detalle,
        String tipoEvento,
        String origen,
        String resultado,
        Long idUsuario,
        Map<String, Object> metadatos
    ) {

        AuditoriaEntity auditoria = new AuditoriaEntity();

        auditoria.setAccion(accion);
        auditoria.setTablaAfectada(tablaAfectada);
        auditoria.setIdRegistro(idRegistro);
        auditoria.setDetalle(detalle);

        auditoria.setTipoEvento(tipoEvento);
        auditoria.setOrigen(origen);
        auditoria.setResultado(resultado);

        // ==================================================
        // IDENTIFICAR A LA PERSONA RESPONSABLE
        // ==================================================

        if (idUsuario != null && idUsuario > 0) {

            UsuarioEntity usuario = usuarioRepository
                .findById(idUsuario)
                .orElse(null);

            if (usuario != null) {

                auditoria.setUsuario(usuario);

                auditoria.setUsuarioNombre(
                    usuario.getNombre()
                );

                auditoria.setUsuarioApellido(
                    usuario.getApellido()
                );

                auditoria.setUsuarioEmail(
                    usuario.getEmail()
                );

                if (usuario.getRol() != null
                        && usuario.getRol().getNombre() != null) {

                    auditoria.setUsuarioRol(
                        "ROLE_" + usuario.getRol()
                            .getNombre().name()
                    );
                }
            }
        }

        // ==================================================
        // GUARDAR SOLO METADATOS CONTROLADOS
        // ==================================================

        if (metadatos != null && !metadatos.isEmpty()) {

            auditoria.setValoresNuevos(
                jsonMapper.writeValueAsString(metadatos)
            );
        }

        auditoriaRepository.saveAndFlush(auditoria);
    }
}
