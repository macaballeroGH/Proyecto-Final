package com.example.beautymanager.Controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.HistorialClienteDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerHistorialTratamientoDTO;
import com.example.beautymanager.Modelo.DTO.RegistrarTratamientoDTO;
import com.example.beautymanager.Servicio.HistorialTratamientoService;

@RestController
@RequestMapping("/historial-tratamiento")
public class HistorialTratamientoController {

    @Autowired
    private HistorialTratamientoService historialTratamientoService;

    //=======================
    //Registrar tratamiento
    //=======================
    @PostMapping("/registrar")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<ObtenerHistorialTratamientoDTO> registrarTratamiento(@RequestBody RegistrarTratamientoDTO registrarTratamiento){

        Long idUsuario = (Long) SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.status(HttpStatus.CREATED).body(historialTratamientoService.registrarTratamiento(idUsuario, registrarTratamiento));
    }

    //=======================
    //Buscar por id
    //=======================
    @GetMapping("/id/{idHistorial}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<ObtenerHistorialTratamientoDTO> buscarPorId(@PathVariable Long idHistorial){
        return ResponseEntity.ok(historialTratamientoService.buscarPorId(idHistorial));
    }

    //=======================
    //Listar todos
    //=======================
    @GetMapping("/listar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ObtenerHistorialTratamientoDTO>> listarTodos(){
        return ResponseEntity.ok(historialTratamientoService.listarTodos());
    }

    //=======================
    //Consultar mi historial
    //=======================
    @GetMapping("/mis-tratamientos")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<ObtenerHistorialTratamientoDTO>> consultarMiHistorial(){

        Long idUsuario = (Long) SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(historialTratamientoService.listarPorCliente(idUsuario));
    }
    

    //=======================
    //Listar por empleado
    //=======================
    @GetMapping("/empleado/{idEmpleado}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<List<ObtenerHistorialTratamientoDTO>> listarPorEmpleado(@PathVariable Long idEmpleado){
        return ResponseEntity.ok(historialTratamientoService.listarPorEmpleado(idEmpleado));
    }

    //=======================
    //Listar por fecha
    //=======================
    @GetMapping("/fecha")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<List<ObtenerHistorialTratamientoDTO>> listarPorFecha(@RequestParam LocalDateTime fecha){
        return ResponseEntity.ok(historialTratamientoService.listarPorFecha(fecha));
    }

    //===========================
    //Listar por rango fechas
    //===========================
    @GetMapping("/rango-fechas")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<List<ObtenerHistorialTratamientoDTO>> listarPorRangoFechas(@RequestParam LocalDateTime fechaInicio, @RequestParam LocalDateTime fechaFin){
        return ResponseEntity.ok(historialTratamientoService.listarPorRangoFechas(fechaInicio, fechaFin));
    }

    //=======================
    //Listar por turno
    //=======================
    @GetMapping("/turno/{idTurno}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<List<ObtenerHistorialTratamientoDTO>> listarPorTurno(@PathVariable Long idTurno){
        return ResponseEntity.ok(historialTratamientoService.listarPorTurno(idTurno));
    }

    //=======================
    //Buscar por texto
    //=======================
    @GetMapping("/texto/{texto}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<List<ObtenerHistorialTratamientoDTO>> buscarPorTexto(@PathVariable String texto){
        return ResponseEntity.ok(historialTratamientoService.buscarPorTexto(texto));
    }

    //=======================
    //Ver historial cliente
    //=======================
    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<List<ObtenerHistorialTratamientoDTO>> verHistorialCliente(@PathVariable Long idCliente){

        return ResponseEntity.ok(historialTratamientoService.verHistorialCliente(idCliente));
    }

    //=================================
    //Ver historial completo cliente
    //=================================
    @GetMapping("/historial-completo/{idCliente}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HistorialClienteDTO> obtenerHistorialCompletoCliente(@PathVariable Long idCliente){

        return ResponseEntity.ok(historialTratamientoService.obtenerHistorialCompletoCliente(idCliente));
    }

    //=======================
    //Eliminar historial
    //=======================
    @DeleteMapping("/eliminar/{idHistorial}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable Long idHistorial){

        historialTratamientoService.eliminar(idHistorial);

        return ResponseEntity.ok("Historial eliminado correctamente");
    }
}
