package com.example.beautymanager.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ActualizarPagoDTO;
import com.example.beautymanager.Modelo.DTO.CrearPagoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerPagoDTO;
import com.example.beautymanager.Modelo.Enums.EstadoPagoEnums;
import com.example.beautymanager.Servicio.PagoService;

@RestController
@RequestMapping("/pago")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    //=======================
    //Crear pago de turno
    //=======================
    @PostMapping("/crear-turno")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ObtenerPagoDTO> crearPago(@RequestBody CrearPagoDTO crearPago){

        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.crearPago(crearPago));
    }

    //=======================
    //Crear pago de compra
    //=======================
    @PostMapping("/crear-compra")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ObtenerPagoDTO> crearPagoCompra(@RequestBody CrearPagoDTO crearPago){

        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.crearPago(crearPago));
    }

    //=======================
    //Pagar
    //=======================
    @PutMapping("/pagar/{idPago}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ObtenerPagoDTO> pagar(@PathVariable Long idPago){
        
        Long idUsuario = (Long) SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(pagoService.pagar(idUsuario, idPago));
    }

    //=======================
    //Actualizar pago
    //=======================
    @PutMapping("/actualizar/{idPago}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ObtenerPagoDTO> actualizarPago(@PathVariable Long idPago, @RequestBody ActualizarPagoDTO actualizarPago){
        return ResponseEntity.ok(pagoService.actualizarPago(idPago, actualizarPago));
    }

    //=======================
    //Obtener por id
    //=======================
    @GetMapping("/id/{idPago}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<ObtenerPagoDTO> obtenerPorId(@PathVariable Long idPago){
        return ResponseEntity.ok(pagoService.obtenerPorId(idPago));
    }

    //=======================
    //Obtener por turno
    //=======================
    @GetMapping("/turno/{idTurno}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<ObtenerPagoDTO> obtenerPorTurno(@PathVariable Long idTurno){
        return ResponseEntity.ok(pagoService.obtenerPorTurno(idTurno));
    }

    //=======================
    //Listar por estado
    //=======================
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<List<ObtenerPagoDTO>> listarPorEstado(@PathVariable EstadoPagoEnums estado){
        return ResponseEntity.ok(pagoService.listarPorEstado(estado));
    }

    //=======================
    //Mis pagos
    //=======================
    @GetMapping("/mis-pagos")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<ObtenerPagoDTO>> obtenerMisPagos(){

        Long idUsuario = (Long) SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(pagoService.obtenerMisPagos(idUsuario));
    }
}
