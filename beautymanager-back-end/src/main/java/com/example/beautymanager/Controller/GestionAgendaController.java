package com.example.beautymanager.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ActualizarTurnoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerTurnoDTO;
import com.example.beautymanager.Servicio.GestionAgendaService;

@RestController
@RequestMapping("/admin/agenda")
@PreAuthorize("hasRole('ADMIN')")
public class GestionAgendaController {

    @Autowired
    private GestionAgendaService gestionAgendaService;

    //=======================
    //Listar todos los turnos
    //=======================
    @GetMapping("/turnos")
    public ResponseEntity<List<ObtenerTurnoDTO>> listarTodos(){
        return ResponseEntity.ok(gestionAgendaService.listarTodos());
    }

    //=======================
    //Obtener por id
    //=======================
    @GetMapping("/turnos/{id}")
    public ResponseEntity<ObtenerTurnoDTO> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(gestionAgendaService.obtenerPorId(id));
    }

    //=======================
    //Listar por cliente 
    //=======================
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<ObtenerTurnoDTO>> listarPorCliente(@PathVariable Long idCliente){
        return ResponseEntity.ok(gestionAgendaService.listarPorCliente(idCliente));
    }

    //=======================
    //Listar por empleado
    //=======================
    @GetMapping("/empleado/{idEmpleado}")
    public ResponseEntity<List<ObtenerTurnoDTO>> listarPorEmpleado(@PathVariable Long idEmpleado){
        return ResponseEntity.ok(gestionAgendaService.listarPorEmpleado(idEmpleado));
    }

    //=======================
    //Aceptar turno
    //=======================
    @PutMapping("/aceptar/{idTurno}")
    public ResponseEntity<ObtenerTurnoDTO> aceptarTurno(@PathVariable Long idTurno){
        return ResponseEntity.ok(gestionAgendaService.aceptarTurno(idTurno));
    }

    //=======================
    //Rechazar turno
    //=======================
    @PutMapping("/rechazar/{idTurno}")
    public ResponseEntity<ObtenerTurnoDTO> rechazarTurno(@PathVariable Long idTurno){
        return ResponseEntity.ok(gestionAgendaService.rechazarTurno(idTurno));
    }

    //=======================
    //Cancelar turno
    //=======================
    @PutMapping("/cancelar/{idTurno}")
    public ResponseEntity<ObtenerTurnoDTO> cancelarTurno(@PathVariable Long idTurno){
        return ResponseEntity.ok(gestionAgendaService.cancelarTurno(idTurno));
    }

    //=======================
    //Reprogramar turno
    //=======================
    @PutMapping("/reprogramar/{idTurno}")
    public ResponseEntity<ObtenerTurnoDTO> reprogramarTurno(@PathVariable Long idTurno, @RequestBody ActualizarTurnoDTO actualizarTurno){
        return ResponseEntity.ok(gestionAgendaService.reprogramarTurno(idTurno, actualizarTurno));
    }

    //=======================
    //Cambiar empleado
    //=======================
    @PutMapping("/cambiar-empleado/{id}")
    public ResponseEntity<ObtenerTurnoDTO> cambiarEmpleado(@PathVariable Long id, @RequestBody Long idEmpleado){
        return ResponseEntity.ok(gestionAgendaService.cambiarEmpleado(id, idEmpleado));
    }
}
