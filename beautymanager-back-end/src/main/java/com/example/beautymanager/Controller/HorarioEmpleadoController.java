package com.example.beautymanager.Controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ActualizarHorarioEmpleadoDTO;
import com.example.beautymanager.Modelo.DTO.CrearHorarioEmpleadoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerHorarioEmpleadoDTO;
import com.example.beautymanager.Servicio.HorarioEmpleadoService;

@RestController
@RequestMapping("/horario-empleado")
public class HorarioEmpleadoController {

    @Autowired
    private HorarioEmpleadoService horarioEmpleadoService;

    //=======================
    //Crear horario
    //=======================
    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ObtenerHorarioEmpleadoDTO> crearHorario(@RequestBody CrearHorarioEmpleadoDTO crearHorario){

        ObtenerHorarioEmpleadoDTO response = horarioEmpleadoService.crearHorario(crearHorario);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //=======================
    //Actualizar horario
    //=======================
    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ObtenerHorarioEmpleadoDTO> actualizarHorario(@PathVariable Long id, @RequestBody ActualizarHorarioEmpleadoDTO actualizarHorario){
        return ResponseEntity.ok(horarioEmpleadoService.actualizarHorario(id, actualizarHorario));
    }

    //=======================
    //Obtener por id
    //=======================
    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<ObtenerHorarioEmpleadoDTO> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(horarioEmpleadoService.obtenerPorId(id));
    }

    //=======================
    //Listar todos
    //=======================
    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('CLIENTE', 'EMPLEADO', 'ADMIN')")
    public ResponseEntity<List<ObtenerHorarioEmpleadoDTO>> obtenerTodos(){
        return ResponseEntity.ok(horarioEmpleadoService.obtenerTodos());
    }

    //=======================
    //Buscar por empleado
    //=======================
    @GetMapping("/empleado/{idEmpleado}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'EMPLEADO', 'ADMIN')")
    public ResponseEntity<List<ObtenerHorarioEmpleadoDTO>> obtenerPorEmpleado(@PathVariable Long idEmpleado){
        return ResponseEntity.ok(horarioEmpleadoService.obtenerPorEmpleado(idEmpleado));
    }

    //============================
    //Buscar por empleado y dia
    //============================
    @GetMapping("/empleado-dia/{idEmpleado}/{diaSemana}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'EMPLEADO', 'ADMIN')")
    public ResponseEntity<List<ObtenerHorarioEmpleadoDTO>> obtenerPorEmpleadoYDia(@PathVariable Long idEmpleado, @PathVariable Integer diaSemana){
        return ResponseEntity.ok(horarioEmpleadoService.obtenerPorEmpleadoYDia(idEmpleado, diaSemana));
    }

    //=======================
    //Eliminar horario
    //=======================
    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminarHorario(@PathVariable Long id){

        horarioEmpleadoService.eliminar(id);

        return ResponseEntity.ok("Horario eliminado correctamente");
    }
}
