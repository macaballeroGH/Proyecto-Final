package com.example.beautymanager.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ActualizarNotificacionDTO;
import com.example.beautymanager.Modelo.DTO.CrearNotificacionDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerNotificacionDTO;
import com.example.beautymanager.Servicio.NotificacionService;

@RestController
@RequestMapping("/notificacion")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    //=======================
    //Crear notificacion
    //=======================
    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<ObtenerNotificacionDTO> crearNotificacion(@RequestBody CrearNotificacionDTO crearNotificacion){
        return ResponseEntity.ok(notificacionService.crearNotificacion(crearNotificacion));
    }

    //===========================
    //Actualizar notificacion
    //===========================
    @PutMapping("/actualizar/{idNotificacion}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ObtenerNotificacionDTO> actualizarNotificacion(@PathVariable Long idNotificacion, @RequestBody ActualizarNotificacionDTO actualizarNotificacion){

        Long idUsuario = (Long) SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(notificacionService.actualizarNotificacion(idUsuario, idNotificacion, actualizarNotificacion));
    }

    //=======================
    //Buscar por ID
    //=======================
    @GetMapping("/id/{idNotificacion}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<ObtenerNotificacionDTO> buscarPorId(@PathVariable Long idNotificacion){
        return ResponseEntity.ok(notificacionService.buscarPorId(idNotificacion));
    }

    //=======================
    //Listar todas
    //=======================
    @GetMapping("/listar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ObtenerNotificacionDTO>> listarTodas(){
        return ResponseEntity.ok(notificacionService.listarTodas());
    }

    //=======================
    //Listar por usuario
    //=======================
    @GetMapping("/mis-notificaciones")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<ObtenerNotificacionDTO>> misNotificaciones(){

        Long idUsuario = (Long) SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(notificacionService.listarPorUsuario(idUsuario));
    }

    //=======================
    //Listar no leidas
    //=======================
    @GetMapping("/mis-no-leidas")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<ObtenerNotificacionDTO>> listarNoLeidas(){

        Long idUsuario = (Long) SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(notificacionService.listarNoLeidas(idUsuario));
    }

    //=======================
    //Eliminar notificacion
    //=======================
    @DeleteMapping("/eliminar/{idNotificacion}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable Long idNotificacion){
        
        notificacionService.eliminar(idNotificacion);

        return ResponseEntity.ok("Notificacion eliminada correctamente");
    }
}
