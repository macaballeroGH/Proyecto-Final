package com.example.beautymanager.Controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.DebitarSaldoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerBilleteraDTO;
import com.example.beautymanager.Modelo.DTO.RecargarSaldoDTO;
import com.example.beautymanager.Servicio.BilleteraService;

@RestController
@RequestMapping("/billetera")
public class BilleteraController {

    @Autowired
    private BilleteraService billeteraService;

    //=======================
    //Consultar saldo
    //=======================
    @GetMapping("/saldo")
    public ResponseEntity<BigDecimal> consultarSaldo(){

        Long idUsuario = (Long) SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getPrincipal();
    
        return ResponseEntity.ok(billeteraService.consultarSaldo(idUsuario));
    }

    //=======================
    //Obtener por usuario
    //=======================
    @GetMapping
    public ResponseEntity<ObtenerBilleteraDTO> obtenerMiBilletera(){

        Long idUsuario = (Long) SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(billeteraService.obtenerPorUsuario(idUsuario));
    }

    //=======================
    //Recargar saldo
    //=======================
    @PutMapping("/recargar")
    public ResponseEntity<ObtenerBilleteraDTO> recargarSaldo(@RequestBody RecargarSaldoDTO recargarSaldo){

        Long idUsuario = (Long) SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getPrincipal();

        recargarSaldo.setIdUsuario(idUsuario);

        return ResponseEntity.ok(billeteraService.recargarSaldo(recargarSaldo));
    }

    //=======================
    //Debitar saldo
    //=======================
    @PutMapping("/debitar")
    public ResponseEntity<ObtenerBilleteraDTO> debitarSaldo(@RequestBody DebitarSaldoDTO debitarSaldo){

        Long idUsuario = (Long) SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getPrincipal();
        
        debitarSaldo.setIdUsuario(idUsuario);
        return ResponseEntity.ok(billeteraService.debitarSaldo(debitarSaldo));
    }
}
