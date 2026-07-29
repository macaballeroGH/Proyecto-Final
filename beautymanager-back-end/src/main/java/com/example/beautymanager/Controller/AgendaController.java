package com.example.beautymanager.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Servicio.AgendaService;

@RestController
@RequestMapping("/agenda")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
public class AgendaController {

    @Autowired
    private AgendaService agendaService;

    //=========================
    //Obtener disponibilidad
    //=========================
    @GetMapping("/disponibilidad/{idEmpleado}")
    public ResponseEntity<List<LocalDateTime>> obtenerDisponibilidad(@PathVariable Long idEmpleado, @RequestParam List<Long> serviciosIds, @RequestParam LocalDate fecha){
        return ResponseEntity.ok(agendaService.obtenerDisponibilidad(idEmpleado, serviciosIds, fecha));
    }

    //===============================
    //Obtener disponibilidad rango
    //===============================
    @GetMapping("/disponibilidad-rango/{idEmpleado}")
    public ResponseEntity<List<LocalDateTime>> obtenerDisponibilidadRango(@PathVariable Long idEmpleado, @RequestParam List<Long> serviciosIds, @RequestParam LocalDate inicio, @RequestParam LocalDate fin){
        return ResponseEntity.ok(agendaService.obtenerDisponibilidadRango(idEmpleado, serviciosIds, inicio, fin));
    }

    //======================================
    //Obtener disponibilidad por empleado
    //======================================
    @GetMapping("/disponibilidad-empleado")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<List<LocalDateTime>> agendaEmpleado(@RequestParam List<Long> serviciosIds, @RequestParam LocalDate fecha){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Long idUsuario = Long.valueOf(auth.getName());

        return ResponseEntity.ok(agendaService.obtenerDisponibilidadPorEmpleado(idUsuario, serviciosIds, fecha));
    }

    //=========================
    //Validar disponibilidad
    //=========================
    @GetMapping("/validar/{idEmpleado}")
    public ResponseEntity<Boolean> validarDisponibilidad(@PathVariable Long idEmpleado, @RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fin){
        return ResponseEntity.ok(agendaService.validarDisponibilidad(idEmpleado, inicio, fin));
    }

    //===========================
    //Calcular duracion total
    //===========================
    @GetMapping("/duracion-total")
    public ResponseEntity<Integer> calcularDuracionTotal(@RequestParam List<Long> serviciosIds){
        return ResponseEntity.ok(agendaService.calcularDuracionTotal(serviciosIds));
    }
}
