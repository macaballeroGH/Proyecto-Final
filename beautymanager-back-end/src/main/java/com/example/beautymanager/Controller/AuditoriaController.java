package com.example.beautymanager.Controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ObtenerAuditoriaDTO;
import com.example.beautymanager.Servicio.AuditoriaService;

@RestController
@RequestMapping("/auditoria")
@PreAuthorize("hasRole('ADMIN')")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    //=======================
    //Buscar por ID
    //=======================
    @GetMapping("/id/{idAuditoria}")
    public ResponseEntity<ObtenerAuditoriaDTO> obtenerPorId(@PathVariable Long idAuditoria){
        return ResponseEntity.ok(auditoriaService.obtenerPorId(idAuditoria));
    }

    //=======================
    //Listar todas
    //=======================
    @GetMapping("/listar")
    public ResponseEntity<List<ObtenerAuditoriaDTO>> obtenerTodas(){
        return ResponseEntity.ok(auditoriaService.obtenerTodas());
    }

    //=======================
    //Buscar por usuario
    //=======================
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ObtenerAuditoriaDTO>> obtenerPorUsuario(@PathVariable Long idUsuario){
        return ResponseEntity.ok(auditoriaService.obtenerPorUsuario(idUsuario));
    }
}
