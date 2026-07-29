package com.example.beautymanager.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ObtenerCompraDTO;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Servicio.CompraService;

@RestController
@RequestMapping("/compra")
public class CompraController {

    @Autowired
    private CompraService compraService;

    //=======================
    //Realizar compra
    //=======================
    @PostMapping("/realizar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ObtenerCompraDTO> realizarCompra(Authentication authentication){

        Long idCliente = ((UsuarioEntity) authentication.getPrincipal())

                .getCliente()
                .getId();

        ObtenerCompraDTO response = compraService.realizarCompra(idCliente);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
