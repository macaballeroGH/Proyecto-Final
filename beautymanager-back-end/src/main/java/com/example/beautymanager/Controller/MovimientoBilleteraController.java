package com.example.beautymanager.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<ObtenerMovimientoBilleteraDTO>> obtenerMisMovimientos(){
        
        Long idUsuario = (Long) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

        return ResponseEntity.ok(movimientoBilleteraService.obtenerMovimientosPorUsuario(idUsuario));
       
    }
}