package com.example.beautymanager.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ObtenerMovimientoBilleteraDTO;
import com.example.beautymanager.Servicio.MovimientoBilleteraService;

@RestController
@RequestMapping("/movimiento-billetera")
public class MovimientoBilleteraController {

    @Autowired
    private MovimientoBilleteraService movimientoBilleteraService;

    //===========================
    //Movimientos por usuario
    //===========================
    @GetMapping("/mis-movimientos")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<ObtenerMovimientoBilleteraDTO>> obtenerMisMovimientos(){
        
        String email = SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getName();

        return ResponseEntity.ok(movimientoBilleteraService.obtenerMovimientosPorUsuario(email));
    }
}