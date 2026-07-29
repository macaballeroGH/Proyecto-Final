package com.example.beautymanager.Controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ActualizarBloqueoAgendaDTO;
import com.example.beautymanager.Modelo.DTO.CrearBloqueoAgendaDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerBloqueoAgendaDTO;
import com.example.beautymanager.Servicio.BloqueoAgendaService;

@RestController
@RequestMapping("/bloqueo-agenda")
@PreAuthorize("hasRole('ADMIN')")
public class BloqueoAgendaController {

    @Autowired
    private BloqueoAgendaService bloqueoAgendaService;

    //=======================
    //Crear bloqueo
    //=======================
    @PostMapping("/crear")
    public ResponseEntity<ObtenerBloqueoAgendaDTO> crearBloqueo(@RequestBody CrearBloqueoAgendaDTO crearBloqueo){

        ObtenerBloqueoAgendaDTO response = bloqueoAgendaService.crearBloqueo(crearBloqueo);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //=======================
    //Actualizar bloqueo
    //=======================
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ObtenerBloqueoAgendaDTO> actualizarBloqueo(@PathVariable Long id, @RequestBody ActualizarBloqueoAgendaDTO actualizarBloqueo){
        return ResponseEntity.ok(bloqueoAgendaService.actualizarBloqueo(id, actualizarBloqueo));
    }

    //=======================
    //Obtener por id
    //=======================
    @GetMapping("/id/{id}")
    public ResponseEntity<ObtenerBloqueoAgendaDTO> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(bloqueoAgendaService.obtenerPorId(id));
    }

    //=======================
    //Listar todos
    //=======================
    @GetMapping("/listar")
    public ResponseEntity<List<ObtenerBloqueoAgendaDTO>> obtenerTodos(){
        return ResponseEntity.ok(bloqueoAgendaService.obtenerTodos());
    }

    //=======================
    //Buscar por empleado
    //=======================
    @GetMapping("/empleado/{idEmpleado}")
    public ResponseEntity<List<ObtenerBloqueoAgendaDTO>> obtenerPorEmpleado(@PathVariable Long idEmpleado){
        return ResponseEntity.ok(bloqueoAgendaService.obtenerPorEmpleado(idEmpleado));
    }

    //=======================
    //Bloqueos superpuestos
    //=======================
    @GetMapping("/superpuestos/{idEmpleado}")
    public ResponseEntity<List<ObtenerBloqueoAgendaDTO>> obtenerBloqueosSuperpuestos(@PathVariable Long idEmpleado, @RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fin){
        return ResponseEntity.ok(bloqueoAgendaService.obtenerBloqueosSuperpuestos(idEmpleado, inicio, fin));
    }

    //=======================
    //Eliminar bloqueo
    //=======================
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id){

        bloqueoAgendaService.eliminar(id);

        return ResponseEntity.ok("Bloqueo eliminado correctamente");
    }
}
