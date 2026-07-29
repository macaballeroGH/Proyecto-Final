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

import com.example.beautymanager.Modelo.DTO.ActualizarEmpleadoDTO;
import com.example.beautymanager.Modelo.DTO.CrearEmpleadoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerEmpleadoDTO;
import com.example.beautymanager.Modelo.Enums.EspecialidadEmpleadoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoEmpleadoEnums;
import com.example.beautymanager.Servicio.EmpleadoService;

@RestController
@RequestMapping("/empleados")
@PreAuthorize("hasRole('ADMIN')")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    //=======================
    //Crear empleado
    //=======================
    @PostMapping("/crear")
    public ResponseEntity<ObtenerEmpleadoDTO> crearEmpleado(@RequestBody CrearEmpleadoDTO crearEmpleado){

        ObtenerEmpleadoDTO response = empleadoService.crearEmpleado(crearEmpleado);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //=======================
    //Actualizar empleado
    //=======================
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ObtenerEmpleadoDTO> actualizarEmpleado(@PathVariable Long id, @RequestBody ActualizarEmpleadoDTO actualizarEmpleado){
        return ResponseEntity.ok(empleadoService.actualizarEmpleado(id, actualizarEmpleado));
    }

    //=======================
    //Eliminar empleado
    //=======================
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarEmpleado(@PathVariable Long id){

        empleadoService.eliminarEmpleado(id);

        return ResponseEntity.ok("Empleado eliminado correctamente");
    }

    //=======================
    //Obtener por id
    //=======================
    @GetMapping("/id/{id}")
    public ResponseEntity<ObtenerEmpleadoDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(empleadoService.buscarPorId(id));
    }

    //=======================
    //Listar todos
    //=======================
    @GetMapping("/listar")
    public ResponseEntity<List<ObtenerEmpleadoDTO>> listarTodos(){
        return ResponseEntity.ok(empleadoService.listarTodos());
    }

    //=======================
    //Buscar especialidad
    //=======================
    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<ObtenerEmpleadoDTO>> buscarPorEspecialidad(@PathVariable EspecialidadEmpleadoEnums especialidad){
        return ResponseEntity.ok(empleadoService.buscarPorEspecialidad(especialidad));
    }

    //=======================
    //Buscar por estado
    //=======================
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ObtenerEmpleadoDTO>> buscarPorEstado(@PathVariable EstadoEmpleadoEnums estado){
        return ResponseEntity.ok(empleadoService.buscarPorEstado(estado));
    }

    //==================================
    //Buscar por especialidad y estado
    //==================================
    @GetMapping("/especialidad-estado/{especialidad}/{estado}")
    public ResponseEntity<List<ObtenerEmpleadoDTO>> buscarPorEspecialidadYEstado(@PathVariable EspecialidadEmpleadoEnums especialidad, @PathVariable EstadoEmpleadoEnums estado){
        return ResponseEntity.ok(empleadoService.buscarPorEspecialidadAndEstado(especialidad, estado));
    }

    //=======================
    //Buscar por email
    //=======================
    @GetMapping("/email/{email}")
    public ResponseEntity<ObtenerEmpleadoDTO> buscarPorEmail(@PathVariable String email){
        return ResponseEntity.ok(empleadoService.buscarPorEmail(email));
    }

    //=======================
    //Buscar por usuario
    //=======================
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<ObtenerEmpleadoDTO> buscarPorUsuarioId(@PathVariable Long idUsuario){
        return ResponseEntity.ok(empleadoService.buscarPorUsuarioId(idUsuario));
    }

    //=======================
    //Existe por usuario
    //=======================
    @GetMapping("/existe-usuario/{idUsuario}")
    public ResponseEntity<Boolean> existePorUsuario(@PathVariable Long idUsuario){
        return ResponseEntity.ok(empleadoService.existePorUsuario(idUsuario));
    }

    //=======================
    //Cambiar estado
    //=======================
    @PutMapping("/cambiar-estado/{idEmpleado}/{estado}")
    public ResponseEntity<ObtenerEmpleadoDTO> cambiarEstado(@PathVariable Long idEmpleado, @PathVariable EstadoEmpleadoEnums estado){
        return ResponseEntity.ok(empleadoService.cambiarEstado(idEmpleado, estado));
    }

    //=======================
    //Dar de baja
    //=======================
    @PutMapping("/dar-baja/{idEmpleado}")
    public ResponseEntity<ObtenerEmpleadoDTO> darDeBaja(@PathVariable Long idEmpleado){
        return ResponseEntity.ok(empleadoService.darDeBaja(idEmpleado));
    }

    //=======================
    //Reactivar empleado
    //=======================
    @PutMapping("/reactivar/{idEmpleado}")
    public ResponseEntity<ObtenerEmpleadoDTO> reactivarEmpleado(@PathVariable Long idEmpleado){
        return ResponseEntity.ok(empleadoService.reactivarEmpleado(idEmpleado));
    }

    //=======================
    //Empleados activos
    //=======================
    @GetMapping("/activos")
    public ResponseEntity<List<ObtenerEmpleadoDTO>> empleadosActivos(){
        return ResponseEntity.ok(empleadoService.empleadosActivos());
    }

    //=======================
    //Empleados inactivos
    //=======================
    @GetMapping("/inactivos")
    public ResponseEntity<List<ObtenerEmpleadoDTO>> empleadosInactivos(){
        return ResponseEntity.ok(empleadoService.empleadosInactivos());
    }

    //=======================
    //Altas entre fechas
    //=======================
    @GetMapping("/altas")
    public ResponseEntity<List<ObtenerEmpleadoDTO>> altasEntreFechas(@RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fin){
        return ResponseEntity.ok(empleadoService.altasEntreFechas(inicio, fin));
    }

    //=======================
    //Bajas entre fechas
    //=======================
    @GetMapping("/bajas")
    public ResponseEntity<List<ObtenerEmpleadoDTO>> bajasEntreFechas(@RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fin){
        return ResponseEntity.ok(empleadoService.bajasEntreFechas(inicio, fin));
    }
}
