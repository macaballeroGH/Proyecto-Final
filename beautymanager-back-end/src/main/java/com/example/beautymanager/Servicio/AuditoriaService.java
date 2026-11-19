package com.example.beautymanager.Servicio;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.example.beautymanager.Modelo.DTO.ObtenerAuditoriaDTO;
import com.example.beautymanager.Modelo.Entidad.AuditoriaEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;

public interface AuditoriaService {

    ObtenerAuditoriaDTO toMap(
        AuditoriaEntity auditoria
    );

    // Compatible con los servicios existentes
    ObtenerAuditoriaDTO guardarAuditoria(
        String accion,
        String tablaAfectada,
        Long idRegistro,
        UsuarioEntity usuario,
        String detalle
    );

    // Registro de modificaciones con comparación
    ObtenerAuditoriaDTO registrarCambio(
        String accion,
        String tablaAfectada,
        Long idRegistro,
        UsuarioEntity usuario,
        String detalle,
        Map<String, ?> valoresAnteriores,
        Map<String, ?> valoresNuevos
    );

    // Procesos automáticos
    ObtenerAuditoriaDTO registrarEventoSistema(
        String accion,
        String tablaAfectada,
        Long idRegistro,
        String detalle
    );

    // Eventos de seguridad sin autenticación
    ObtenerAuditoriaDTO registrarEventoSeguridad(
        String accion,
        String identificadorExterno,
        String detalle
    );

    ObtenerAuditoriaDTO obtenerPorId(
        Long idAuditoria
    );

    List<ObtenerAuditoriaDTO> obtenerTodas();

    List<ObtenerAuditoriaDTO> obtenerPorUsuario(
        Long idUsuario
    );

    List<ObtenerAuditoriaDTO> obtenerCambiosTratamiento(
        Long idHistorial
    );

    Page<ObtenerAuditoriaDTO> buscarAuditorias(
        Long idUsuario,
        String accion,
        String tablaAfectada,
        String tipoEvento,
        String resultado,
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        int pagina,
        int tamanio
    );
}
