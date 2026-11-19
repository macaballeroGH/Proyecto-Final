package com.example.beautymanager.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ObtenerAuditoriaDTO;
import com.example.beautymanager.Servicio.AuditoriaService;

@RestController
@RequestMapping("/auditoria")
@PreAuthorize("hasRole('ADMIN')")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(
        AuditoriaService auditoriaService
    ) {
        this.auditoriaService = auditoriaService;
    }

    // ======================================================
    // OBTENER POR ID
    // ======================================================

    @GetMapping("/id/{idAuditoria}")
    public ResponseEntity<ObtenerAuditoriaDTO> obtenerPorId(
        @PathVariable Long idAuditoria
    ) {

        return ResponseEntity.ok(
            auditoriaService.obtenerPorId(idAuditoria)
        );
    }

    // ======================================================
    // LISTADO ORIGINAL - COMPATIBILIDAD
    // ======================================================

    @GetMapping("/listar")
    public ResponseEntity<List<ObtenerAuditoriaDTO>> obtenerTodas() {

        return ResponseEntity.ok(
            auditoriaService.obtenerTodas()
        );
    }

    // ======================================================
    // OBTENER POR USUARIO
    // ======================================================

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ObtenerAuditoriaDTO>> obtenerPorUsuario(
        @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
            auditoriaService.obtenerPorUsuario(idUsuario)
        );
    }

    // ======================================================
    // BÚSQUEDA AVANZADA Y PAGINACIÓN
    // ======================================================

    @GetMapping("/buscar")
    public ResponseEntity<Page<ObtenerAuditoriaDTO>> buscarAuditorias(

        @RequestParam(required = false)
        Long idUsuario,

        @RequestParam(required = false)
        String accion,

        @RequestParam(required = false)
        String tablaAfectada,

        @RequestParam(required = false)
        String tipoEvento,

        @RequestParam(required = false)
        String resultado,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate fechaDesde,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate fechaHasta,

        @RequestParam(defaultValue = "0")
        int pagina,

        @RequestParam(defaultValue = "20")
        int tamanio
    ) {

        return ResponseEntity.ok(
            auditoriaService.buscarAuditorias(
                idUsuario,
                accion,
                tablaAfectada,
                tipoEvento,
                resultado,
                fechaDesde,
                fechaHasta,
                pagina,
                tamanio
            )
        );
    }
}
