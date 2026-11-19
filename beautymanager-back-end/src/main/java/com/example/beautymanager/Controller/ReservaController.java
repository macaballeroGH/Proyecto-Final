package com.example.beautymanager.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.CrearReservaDTO;
import com.example.beautymanager.Servicio.ReservaPagoService;
import com.example.beautymanager.Servicio.ReservaService;
import com.example.beautymanager.exception.BusinessException;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    private final ReservaPagoService reservaPagoService;

    public ReservaController(
            ReservaService reservaService,
            ReservaPagoService reservaPagoService) {

        this.reservaService = reservaService;
        this.reservaPagoService = reservaPagoService;
    }

    //==========================
    //Crear reserva ADMIN
    //==========================
    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> crearReserva(
            @RequestBody CrearReservaDTO crearReserva) {

        Long idReserva = reservaService.crearReserva(crearReserva);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(idReserva);
    }

    //==========================
    //Confirmar y pagar CLIENTE
    //==========================
    @PostMapping("/confirmar-y-pagar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Long> confirmarYPagar(
            @RequestBody CrearReservaDTO crearReserva,
            Authentication authentication) {

        if(authentication == null ||
           !authentication.isAuthenticated()) {

            throw new BusinessException(
                "La clienta debe iniciar sesion"
            );
        }

        //==========================
        //Obtener usuario autenticado
        //==========================
        if(!(authentication.getPrincipal() instanceof Long idUsuario)){

            throw new BusinessException(
                "No se pudo identificar a la clienta autenticada"
            );
        }

        //==========================
        //Confirmar y pagar
        //==========================
        Long idReserva = reservaPagoService.confirmarYPagar(
            idUsuario,
            crearReserva
        );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(idReserva);
    }
}
