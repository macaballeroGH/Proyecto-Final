package com.example.beautymanager.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ActualizarClienteDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerClienteDTO;
import com.example.beautymanager.Servicio.ClienteService;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    //=======================
    //Actualizar cliente
    //=======================
    @PutMapping("/mi-perfil")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ObtenerClienteDTO> actualizarCliente(@RequestBody ActualizarClienteDTO actualizarCliente){

        String email = SecurityContextHolder
        
                .getContext()
                .getAuthentication()
                .getName();

        return ResponseEntity.ok(clienteService.actualizarCliente(email, actualizarCliente));
    }

    //=======================
    //Eliminar cliente
    //=======================
    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminarCliente(@PathVariable Long id){

        clienteService.eliminarCliente(id);

        return ResponseEntity.ok("Cliente eliminado correctamente");
    }

    //=======================
    //Buscar por id
    //=======================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<ObtenerClienteDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    //=======================
    //Buscar por usuario
    //=======================
    @GetMapping("mi-perfil")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ObtenerClienteDTO> miPerfil(){

        String email = SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getName();

        return ResponseEntity.ok(clienteService.buscarPorEmail(email));
    }

    //=======================
    //Buscar por email
    //=======================
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<ObtenerClienteDTO> buscarPorEmail(@PathVariable String email){
        return ResponseEntity.ok(clienteService.buscarPorEmail(email));
    }

    //=======================
    //Listar todos
    //=======================
    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<List<ObtenerClienteDTO>> listarTodos(){
        return ResponseEntity.ok(clienteService.listarTodos());
    }
}
