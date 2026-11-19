package com.example.beautymanager.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ArticuloBellezaDTO;
import com.example.beautymanager.Servicio.ArticuloBellezaService;

@RestController
@RequestMapping("/articulos-belleza")
@PreAuthorize("hasRole('ADMIN')")
public class ArticuloBellezaController {

    private final ArticuloBellezaService articuloBellezaService;

    public ArticuloBellezaController(ArticuloBellezaService articuloBellezaService) {
        this.articuloBellezaService = articuloBellezaService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ArticuloBellezaDTO>> obtenerArticulos() {
        return ResponseEntity.ok(articuloBellezaService.obtenerArticulos());
    }
}
